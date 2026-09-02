package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.MonthGradeSummary
import com.example.data.model.SchoolInfo
import com.example.data.model.StudentFinalOutcome
import com.example.data.model.StudentGradeEntity
import com.example.data.model.StudentSemesterOutcome
import com.example.data.repository.GradeRepository
import com.example.util.ExcelExporter
import com.example.util.PdfExporter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GradeViewModel(
    application: Application,
    private val repository: GradeRepository
) : AndroidViewModel(application) {

    val schoolInfo: StateFlow<SchoolInfo> = repository.schoolInfoState

    // Navigation & Screen ID:
    // 0: HomeScreen
    // 1: ClassSelectionScreen(semester = 1)
    // 2: ClassSelectionScreen(semester = 2)
    // 3: SemesterGradeScreen(semester = 1)
    // 4: SemesterGradeScreen(semester = 2)
    private val _currentScreen = MutableStateFlow<Int>(0)
    val currentScreen = _currentScreen.asStateFlow()

    fun getActiveSemester(): Int {
        val s = _currentScreen.value
        return if (s == 2 || s == 4) 2 else 1
    }

    // Filter selectors
    private val _selectedMonth = MutableStateFlow("الشهر الأول")
    val selectedMonth = _selectedMonth.asStateFlow()

    private val _selectedSection = MutableStateFlow("أ")
    val selectedSection = _selectedSection.asStateFlow()

    private val _selectedSubject = MutableStateFlow("أحياء")
    val selectedSubject = _selectedSubject.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // User notification messages / SnackBar events
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Dialog control states
    private val _showEditSchoolDialog = MutableStateFlow(false)
    val showEditSchoolDialog = _showEditSchoolDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog = _showSettingsDialog.asStateFlow()

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog = _showResetDialog.asStateFlow()

    private val _showBackupDialog = MutableStateFlow(false)
    val showBackupDialog = _showBackupDialog.asStateFlow()

    private val _showAddStudentDialog = MutableStateFlow(false)
    val showAddStudentDialog = _showAddStudentDialog.asStateFlow()

    private val _editingStudent = MutableStateFlow<StudentGradeEntity?>(null)
    val editingStudent = _editingStudent.asStateFlow()

    // Send to Phone / WhatsApp states
    private val _sendingStudent = MutableStateFlow<StudentGradeEntity?>(null)
    val sendingStudent = _sendingStudent.asStateFlow()

    private val _showSendClassDialog = MutableStateFlow(false)
    val showSendClassDialog = _showSendClassDialog.asStateFlow()

    // Export By Grade & Subject Modal states
    private val _showExportModal = MutableStateFlow(false)
    val showExportModal = _showExportModal.asStateFlow()

    private val _exportModalFormat = MutableStateFlow("PDF") // "PDF" or "EXCEL"
    val exportModalFormat = _exportModalFormat.asStateFlow()

    init {
        viewModelScope.launch {
            repository.prePopulateIfEmpty()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentStudents: StateFlow<List<StudentGradeEntity>> = combine(
        _currentScreen,
        _selectedMonth,
        _selectedSection,
        _selectedSubject
    ) { sem, month, section, subject ->
        val actualSem = if (sem == 2 || sem == 4) 2 else 1
        repository.getStudents(actualSem, month, section, subject)
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSemesterStudents: StateFlow<List<StudentGradeEntity>> = _currentScreen
        .flatMapLatest { sem ->
            val actualSem = if (sem == 2 || sem == 4) 2 else 1
            repository.getAllStudentsForSemester(actualSem)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDatabaseStudents: StateFlow<List<StudentGradeEntity>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDistinctSubjects: StateFlow<List<String>> = combine(
        repository.getAllDistinctSubjects(),
        schoolInfo
    ) { dbSubjects, info ->
        val defaults = listOf("أحياء", "لغة عربية", "رياضيات", "فيزياء", "كيمياء", "قرآن كريم", "تربية إسلامية", "لغة إنجليزية", "علوم", "اجتماعيات", "حاسوب")
        (listOf(info.defaultSubject) + dbSubjects + defaults).filter { it.isNotBlank() }.distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("أحياء", "لغة عربية", "رياضيات"))

    // Search query for semester outcome lookup
    private val _semesterSearchQuery = MutableStateFlow("")
    val semesterSearchQuery = _semesterSearchQuery.asStateFlow()

    fun setSemesterSearchQuery(query: String) {
        _semesterSearchQuery.value = query
    }

    // Search query for final outcome lookup
    private val _finalOutcomeSearchQuery = MutableStateFlow("")
    val finalOutcomeSearchQuery = _finalOutcomeSearchQuery.asStateFlow()

    fun setFinalOutcomeSearchQuery(query: String) {
        _finalOutcomeSearchQuery.value = query
    }

    // Selected Subject filter for Final Outcomes screen (null means all subjects)
    private val _finalOutcomeSubjectFilter = MutableStateFlow<String?>(null)
    val finalOutcomeSubjectFilter = _finalOutcomeSubjectFilter.asStateFlow()

    fun setFinalOutcomeSubjectFilter(subject: String?) {
        _finalOutcomeSubjectFilter.value = subject
    }

    // Calculate Semester Outcomes for all students in a semester - isolated strictly by Subject + Section
    fun getSemesterOutcomes(allGrades: List<StudentGradeEntity>, semester: Int): List<StudentSemesterOutcome> {
        val semGrades = allGrades.filter { it.semester == semester }
        // Group by combination of (Student Name, Subject, Section)
        val grouped = semGrades.groupBy { Triple(it.studentName.trim(), it.subject.trim(), it.section.trim()) }

        return grouped.map { (key, records) ->
            val (studentName, subject, section) = key
            val sortedRecords = records.sortedBy { it.month }
            var m1Record = records.find { it.month.contains("الأول") || it.month.contains("1") || it.month.contains("محرم") }
            var m2Record = records.find { it.month.contains("الثاني") || it.month.contains("2") || it.month.contains("صفر") }
            var m3Record = records.find { it.month.contains("الثالث") || it.month.contains("3") || it.month.contains("ربيع") }

            if (m1Record == null && sortedRecords.isNotEmpty()) m1Record = sortedRecords.getOrNull(0)
            if (m2Record == null && sortedRecords.size > 1) m2Record = sortedRecords.getOrNull(1)
            if (m3Record == null && sortedRecords.size > 2) m3Record = sortedRecords.getOrNull(2)

            val m1Summary = MonthGradeSummary(
                monthName = m1Record?.month ?: "الشهر الأول",
                totalScore = m1Record?.totalScore ?: 0.0,
                outcome = (m1Record?.totalScore ?: 0.0) / 5.0,
                hasRecord = m1Record != null
            )
            val m2Summary = MonthGradeSummary(
                monthName = m2Record?.month ?: "الشهر الثاني",
                totalScore = m2Record?.totalScore ?: 0.0,
                outcome = (m2Record?.totalScore ?: 0.0) / 5.0,
                hasRecord = m2Record != null
            )
            val m3Summary = MonthGradeSummary(
                monthName = m3Record?.month ?: "الشهر الثالث",
                totalScore = m3Record?.totalScore ?: 0.0,
                outcome = (m3Record?.totalScore ?: 0.0) / 5.0,
                hasRecord = m3Record != null
            )

            val extraRecords = records.filter { it != m1Record && it != m2Record && it != m3Record }
            val extraSummaries = extraRecords.map {
                MonthGradeSummary(
                    monthName = it.month,
                    totalScore = it.totalScore,
                    outcome = it.totalScore / 5.0,
                    hasRecord = true
                )
            }

            StudentSemesterOutcome(
                studentName = studentName,
                section = section,
                subject = subject,
                semester = semester,
                month1 = m1Summary,
                month2 = m2Summary,
                month3 = m3Summary,
                extraMonths = extraSummaries
            )
        }.sortedWith(compareBy({ it.subject }, { it.studentName }))
    }

    // Calculate Final Outcomes (الفصل الأول + الفصل الثاني) - strictly separated by Subject
    fun getFinalOutcomes(allGrades: List<StudentGradeEntity>): List<StudentFinalOutcome> {
        val sem1Outcomes = getSemesterOutcomes(allGrades, 1)
        val sem2Outcomes = getSemesterOutcomes(allGrades, 2)

        val allKeys = (sem1Outcomes.map { Triple(it.studentName, it.subject, it.section) } +
                sem2Outcomes.map { Triple(it.studentName, it.subject, it.section) }).distinct()

        return allKeys.map { (name, subj, sec) ->
            val s1 = sem1Outcomes.find { it.studentName == name && it.subject == subj && it.section == sec }
            val s2 = sem2Outcomes.find { it.studentName == name && it.subject == subj && it.section == sec }
            StudentFinalOutcome(
                studentName = name,
                section = sec,
                subject = subj,
                sem1Data = s1,
                sem2Data = s2
            )
        }.sortedWith(compareBy({ it.subject }, { it.studentName }))
    }

    fun navigateTo(screenId: Int) {
        _currentScreen.value = screenId
    }

    fun selectGradeLevelAndOpenSemester(gradeLevel: String, semester: Int) {
        val currentInfo = schoolInfo.value
        val updatedInfo = currentInfo.copy(gradeLevels = gradeLevel)
        repository.saveSchoolInfo(updatedInfo)
        _currentScreen.value = if (semester == 2) 4 else 3
    }

    fun navigateBackFromSemesterGrade(semester: Int) {
        // Return to the class selection screen for this semester
        _currentScreen.value = if (semester == 2) 2 else 1
    }

    fun setSelectedMonth(month: String) {
        _selectedMonth.value = month
    }

    fun setSelectedSection(section: String) {
        _selectedSection.value = section
    }

    fun setSelectedSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openEditSchoolDialog() {
        _showEditSchoolDialog.value = true
    }

    fun closeEditSchoolDialog() {
        _showEditSchoolDialog.value = false
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun openResetDialog() {
        _showResetDialog.value = true
    }

    fun closeResetDialog() {
        _showResetDialog.value = false
    }

    fun openBackupDialog() {
        _showBackupDialog.value = true
    }

    fun closeBackupDialog() {
        _showBackupDialog.value = false
    }

    fun openAddStudentDialog(student: StudentGradeEntity? = null) {
        _editingStudent.value = student
        _showAddStudentDialog.value = true
    }

    fun closeAddStudentDialog() {
        _editingStudent.value = null
        _showAddStudentDialog.value = false
    }

    fun openSendStudentDialog(student: StudentGradeEntity) {
        _sendingStudent.value = student
    }

    fun closeSendStudentDialog() {
        _sendingStudent.value = null
    }

    fun openSendClassDialog() {
        _showSendClassDialog.value = true
    }

    fun closeSendClassDialog() {
        _showSendClassDialog.value = false
    }

    fun updateSchoolInfo(info: SchoolInfo) {
        repository.saveSchoolInfo(info)
        _selectedSubject.value = info.defaultSubject
        viewModelScope.launch {
            _userMessage.emit("تم حفظ بيانات المدرسة والمعلم بنجاح")
        }
    }

    fun saveStudent(
        id: Long,
        name: String,
        attendance: Double,
        homework: Double,
        oral: Double,
        written: Double,
        maxAtt: Double? = null,
        maxHw: Double? = null,
        maxOral: Double? = null,
        maxWritten: Double? = null,
        order: Int? = null
    ) {
        viewModelScope.launch {
            // If user updated max score boundaries in the dialog, persist them to schoolInfo
            if (maxAtt != null && maxHw != null && maxOral != null && maxWritten != null) {
                val currentInfo = schoolInfo.value
                if (currentInfo.maxAttendance != maxAtt ||
                    currentInfo.maxHomework != maxHw ||
                    currentInfo.maxOral != maxOral ||
                    currentInfo.maxWritten != maxWritten
                ) {
                    val updatedInfo = currentInfo.copy(
                        maxAttendance = maxAtt,
                        maxHomework = maxHw,
                        maxOral = maxOral,
                        maxWritten = maxWritten
                    )
                    repository.saveSchoolInfo(updatedInfo)
                }
            }

            val sem = if (_currentScreen.value == 2 || _currentScreen.value == 4) 2 else 1
            val nextOrder = order ?: ((currentStudents.value.maxOfOrNull { it.studentOrder } ?: 0) + 1)

            val entity = StudentGradeEntity(
                id = id,
                semester = sem,
                month = _selectedMonth.value,
                section = _selectedSection.value,
                subject = _selectedSubject.value,
                studentOrder = nextOrder,
                studentName = name.trim(),
                attendance = attendance,
                homework = homework,
                oral = oral,
                written = written
            )

            if (id == 0L) {
                repository.insertStudent(entity)
                _userMessage.emit("تم إضافة الطالب ${entity.studentName} في مادة ${_selectedSubject.value} بنجاح")
            } else {
                repository.updateStudent(entity)
                _userMessage.emit("تم تعديل درجات الطالب ${entity.studentName}")
            }
            closeAddStudentDialog()
        }
    }

    fun setSubjectAndSave(subject: String) {
        val clean = subject.trim()
        if (clean.isNotBlank()) {
            _selectedSubject.value = clean
            val currentInfo = schoolInfo.value
            val updatedInfo = currentInfo.copy(defaultSubject = clean)
            repository.saveSchoolInfo(updatedInfo)
            viewModelScope.launch {
                _userMessage.emit("تم تعيين المادة: $clean")
            }
        }
    }

    fun copyRosterFromPrevious(sourceSubject: String? = null) {
        viewModelScope.launch {
            val sem = if (_currentScreen.value == 2 || _currentScreen.value == 4) 2 else 1
            val currentList = currentStudents.value
            if (currentList.isNotEmpty()) {
                _userMessage.emit("يوجد طلاب مسجلين بالفعل في هذه المادة لهذا الشهر")
                return@launch
            }
            val allSemStudents = allSemesterStudents.value
            val sourceStudents = if (sourceSubject != null && sourceSubject.isNotBlank()) {
                allSemStudents.filter { it.subject == sourceSubject && it.section == _selectedSection.value }
            } else {
                allSemStudents.filter { it.section == _selectedSection.value }
            }

            val distinctNames = sourceStudents.map { it.studentName.trim() to it.studentOrder }.distinctBy { it.first }
            if (distinctNames.isEmpty()) {
                _userMessage.emit("لا توجد كشوفات سابقة لنفس الشعبة لنسخ أسماء الطلاب منها")
                return@launch
            }

            var orderCounter = 1
            val newEntities = distinctNames.map { (name, _) ->
                StudentGradeEntity(
                    id = 0L,
                    semester = sem,
                    month = _selectedMonth.value,
                    section = _selectedSection.value,
                    subject = _selectedSubject.value,
                    studentOrder = orderCounter++,
                    studentName = name,
                    attendance = 0.0,
                    homework = 0.0,
                    oral = 0.0,
                    written = 0.0
                )
            }
            repository.insertStudents(newEntities)
            _userMessage.emit("تم استيراد ${newEntities.size} طالب إلى مادة ${_selectedSubject.value} بنجاح")
        }
    }

    fun updateStudentGradesInline(
        student: StudentGradeEntity,
        attendance: Double,
        homework: Double,
        oral: Double,
        written: Double
    ) {
        viewModelScope.launch {
            val updated = student.copy(
                attendance = attendance,
                homework = homework,
                oral = oral,
                written = written
            )
            repository.updateStudent(updated)
        }
    }

    fun deleteStudent(student: StudentGradeEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            _userMessage.emit("تم حذف الطالب ${student.studentName}")
        }
    }

    fun clearStudentScores(student: StudentGradeEntity) {
        viewModelScope.launch {
            repository.clearStudentScores(student.id)
            _userMessage.emit("تم تصفير العملية الحسابية للطالب ${student.studentName}")
        }
    }

    fun closeMonthAndStartNew(newMonthName: String) {
        viewModelScope.launch {
            val currentList = currentStudents.value
            val sem = if (_currentScreen.value == 2) 2 else 1

            // Clone students to the new month with zero initial marks
            val newMonthStudents = currentList.map {
                it.copy(
                    id = 0,
                    month = newMonthName,
                    attendance = 0.0,
                    homework = 0.0,
                    oral = 0.0,
                    written = 0.0,
                    updatedAt = System.currentTimeMillis()
                )
            }

            for (s in newMonthStudents) {
                repository.insertStudent(s)
            }

            _selectedMonth.value = newMonthName
            _userMessage.emit("تم إغلاق الشهر السابق وبدء جدول $newMonthName بنجاح")
        }
    }

    fun factoryReset() {
        viewModelScope.launch {
            repository.clearAllData()
            _userMessage.emit("تمت إعادة ضبط المصنع ومسح كافة البيانات")
            closeResetDialog()
            _currentScreen.value = 0
        }
    }

    fun openExportModal(format: String = "PDF") {
        _exportModalFormat.value = format
        _showExportModal.value = true
    }

    fun closeExportModal() {
        _showExportModal.value = false
    }

    fun exportCustomGradeSubject(
        context: Context,
        format: String,
        gradeLevel: String,
        subject: String,
        semester: Int,
        month: String,
        section: String
    ) {
        viewModelScope.launch {
            val all = allDatabaseStudents.value
            val filtered = all.filter {
                it.semester == semester &&
                it.subject == subject &&
                it.month == month &&
                (section.isBlank() || it.section == section)
            }.sortedWith(compareBy({ it.studentOrder }, { it.id }))

            val targetStudents = if (filtered.isNotEmpty()) {
                filtered
            } else {
                // If not yet populated for this exact subject/month, copy roster of students in that semester
                val roster = all.filter { it.semester == semester && (section.isBlank() || it.section == section) }
                    .distinctBy { it.studentName }
                if (roster.isNotEmpty()) {
                    roster.mapIndexed { idx, st ->
                        StudentGradeEntity(
                            semester = semester,
                            month = month,
                            section = if (section.isNotBlank()) section else st.section,
                            subject = subject,
                            studentOrder = idx + 1,
                            studentName = st.studentName,
                            attendance = 0.0,
                            homework = 0.0,
                            oral = 0.0,
                            written = 0.0,
                            notes = ""
                        )
                    }
                } else {
                    emptyList()
                }
            }

            if (targetStudents.isEmpty()) {
                _userMessage.emit("لا توجد بيانات أو أسماء طلاب مسجلة لـ ($gradeLevel - مادة: $subject)")
                return@launch
            }

            val customSchoolInfo = schoolInfo.value.copy(
                gradeLevels = gradeLevel,
                defaultSubject = subject
            )
            val targetSection = if (section.isNotBlank()) section else targetStudents.firstOrNull()?.section ?: "أ"

            if (format.equals("EXCEL", ignoreCase = true)) {
                val file = ExcelExporter.generateClassSheetCsv(
                    context = context,
                    schoolInfo = customSchoolInfo,
                    semester = semester,
                    month = month,
                    section = targetSection,
                    subject = subject,
                    students = targetStudents
                )
                if (file != null) {
                    PdfExporter.shareFile(context, file, "text/csv", "كشف درجات $gradeLevel - مادة $subject - Excel")
                    _userMessage.emit("تم تصدير كشف Excel لـ $gradeLevel (مادة: $subject) بنجاح")
                    closeExportModal()
                } else {
                    _userMessage.emit("تعذر إنشاء ملف Excel")
                }
            } else {
                val file = PdfExporter.generateClassSheetPdf(
                    context = context,
                    schoolInfo = customSchoolInfo,
                    semester = semester,
                    month = month,
                    section = targetSection,
                    subject = subject,
                    students = targetStudents
                )
                if (file != null) {
                    PdfExporter.shareFile(context, file, "application/pdf", "كشف درجات $gradeLevel - مادة $subject")
                    _userMessage.emit("تم تصدير كشف PDF لـ $gradeLevel (مادة: $subject) بنجاح")
                    closeExportModal()
                } else {
                    _userMessage.emit("تعذر إنشاء ملف PDF")
                }
            }
        }
    }

    fun exportClassPdf(context: Context) {
        viewModelScope.launch {
            val sem = getActiveSemester()
            val list = currentStudents.value
            if (list.isEmpty()) {
                _userMessage.emit("لا توجد بيانات طلاب لتصديرها")
                return@launch
            }
            val file = PdfExporter.generateClassSheetPdf(
                context = context,
                schoolInfo = schoolInfo.value,
                semester = sem,
                month = _selectedMonth.value,
                section = _selectedSection.value,
                subject = _selectedSubject.value,
                students = list
            )
            if (file != null) {
                PdfExporter.shareFile(context, file, "application/pdf", "كشف درجات أعمال السنة")
                _userMessage.emit("تم إنشاء ملف PDF بنجاح")
            } else {
                _userMessage.emit("تعذر إنشاء ملف PDF")
            }
        }
    }

    fun exportSingleStudentPdf(context: Context, student: StudentGradeEntity) {
        viewModelScope.launch {
            val sem = getActiveSemester()
            val file = PdfExporter.generateSingleStudentPdf(
                context = context,
                schoolInfo = schoolInfo.value,
                semester = sem,
                student = student
            )
            if (file != null) {
                PdfExporter.shareFile(context, file, "application/pdf", "بطاقة درجات ${student.studentName}")
                _userMessage.emit("تم تصدير ملف PDF للطالب ${student.studentName}")
            }
        }
    }

    fun exportClassExcel(context: Context) {
        viewModelScope.launch {
            val sem = getActiveSemester()
            val list = currentStudents.value
            if (list.isEmpty()) {
                _userMessage.emit("لا توجد بيانات طلاب لتصديرها")
                return@launch
            }
            val file = ExcelExporter.generateClassSheetCsv(
                context = context,
                schoolInfo = schoolInfo.value,
                semester = sem,
                month = _selectedMonth.value,
                section = _selectedSection.value,
                subject = _selectedSubject.value,
                students = list
            )
            if (file != null) {
                PdfExporter.shareFile(context, file, "text/csv", "كشف درجات أعمال السنة Excel")
                _userMessage.emit("تم إنشاء وتصدير ملف Excel بنجاح")
            } else {
                _userMessage.emit("تعذر إنشاء ملف Excel")
            }
        }
    }

    fun exportSingleStudentExcel(context: Context, student: StudentGradeEntity) {
        viewModelScope.launch {
            val sem = getActiveSemester()
            val file = ExcelExporter.generateSingleStudentCsv(
                context = context,
                schoolInfo = schoolInfo.value,
                semester = sem,
                student = student
            )
            if (file != null) {
                PdfExporter.shareFile(context, file, "text/csv", "بيانات درجات ${student.studentName}")
                _userMessage.emit("تم تصدير ملف Excel للطالب ${student.studentName}")
            }
        }
    }

    fun sendSingleStudentReport(
        context: Context,
        student: StudentGradeEntity,
        format: String,
        phone: String,
        messageText: String
    ) {
        viewModelScope.launch {
            val sem = getActiveSemester()
            val file = if (format == "PDF") {
                PdfExporter.generateSingleStudentPdf(
                    context = context,
                    schoolInfo = schoolInfo.value,
                    semester = sem,
                    student = student
                )
            } else {
                ExcelExporter.generateSingleStudentCsv(
                    context = context,
                    schoolInfo = schoolInfo.value,
                    semester = sem,
                    student = student
                )
            }
            val mime = if (format == "PDF") "application/pdf" else "text/csv"
            PdfExporter.shareToWhatsApp(
                context = context,
                file = file,
                mimeType = mime,
                phoneNumber = phone,
                messageText = messageText
            )
            closeSendStudentDialog()
            _userMessage.emit("جاري فتح واتساب لإرسال كشف الطالب ${student.studentName}")
        }
    }

    fun sendClassSheetReport(
        context: Context,
        format: String,
        phone: String,
        messageText: String
    ) {
        viewModelScope.launch {
            val sem = getActiveSemester()
            val list = currentStudents.value
            if (list.isEmpty()) {
                _userMessage.emit("لا توجد بيانات طلاب لإرسالها")
                return@launch
            }
            val file = if (format == "PDF") {
                PdfExporter.generateClassSheetPdf(
                    context = context,
                    schoolInfo = schoolInfo.value,
                    semester = sem,
                    month = _selectedMonth.value,
                    section = _selectedSection.value,
                    subject = _selectedSubject.value,
                    students = list
                )
            } else {
                ExcelExporter.generateClassSheetCsv(
                    context = context,
                    schoolInfo = schoolInfo.value,
                    semester = sem,
                    month = _selectedMonth.value,
                    section = _selectedSection.value,
                    subject = _selectedSubject.value,
                    students = list
                )
            }
            val mime = if (format == "PDF") "application/pdf" else "text/csv"
            PdfExporter.shareToWhatsApp(
                context = context,
                file = file,
                mimeType = mime,
                phoneNumber = phone,
                messageText = messageText
            )
            closeSendClassDialog()
            _userMessage.emit("جاري فتح واتساب لإرسال كشف الفصل الكامل")
        }
    }

    fun exportBackup(context: Context) {
        viewModelScope.launch {
            try {
                val list = repository.getAllStudents().first()
                val json = repository.exportBackupJson(list)
                val file = java.io.File(context.cacheDir, "نسخة_احتياطية_اعمال_السنة.json")
                file.writeText(json)
                PdfExporter.shareFile(context, file, "application/json", "نسخة احتياطية - كشف أعمال السنة")
                _userMessage.emit("تم إنشاء ومشاركة النسخة الاحتياطية بنجاح")
            } catch (e: Exception) {
                _userMessage.emit("حدث خطأ أثناء إنشاء النسخة الاحتياطية")
            }
        }
    }

    fun importBackup(jsonString: String) {
        viewModelScope.launch {
            val success = repository.importBackupJson(jsonString)
            if (success) {
                _userMessage.emit("تم استرجاع النسخة الاحتياطية بنجاح")
            } else {
                _userMessage.emit("فشل في قراءة ملف النسخة الاحتياطية")
            }
            closeBackupDialog()
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AppDatabase.getDatabase(context)
                    val repo = GradeRepository(db.gradeDao(), context)
                    return GradeViewModel(context.applicationContext as Application, repo) as T
                }
            }
    }
}
