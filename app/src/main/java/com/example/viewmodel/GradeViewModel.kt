package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.SchoolInfo
import com.example.data.model.StudentGradeEntity
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

    // Navigation & Selected Semester (0: Home, 1: Semester 1, 2: Semester 2)
    private val _currentScreen = MutableStateFlow<Int>(0)
    val currentScreen = _currentScreen.asStateFlow()

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
        val actualSem = if (sem == 1 || sem == 2) sem else 1
        repository.getStudents(actualSem, month, section, subject)
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSemesterStudents: StateFlow<List<StudentGradeEntity>> = _currentScreen
        .flatMapLatest { sem ->
            val actualSem = if (sem == 1 || sem == 2) sem else 1
            repository.getAllStudentsForSemester(actualSem)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screenId: Int) {
        _currentScreen.value = screenId
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
        order: Int? = null
    ) {
        viewModelScope.launch {
            val sem = if (_currentScreen.value == 2) 2 else 1
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
                _userMessage.emit("تم إضافة الطالب ${entity.studentName} بنجاح")
            } else {
                repository.updateStudent(entity)
                _userMessage.emit("تم تعديل درجات الطالب ${entity.studentName}")
            }
            closeAddStudentDialog()
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

    fun exportClassPdf(context: Context) {
        viewModelScope.launch {
            val sem = if (_currentScreen.value == 2) 2 else 1
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
            val sem = if (_currentScreen.value == 2) 2 else 1
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
            val sem = if (_currentScreen.value == 2) 2 else 1
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
            val sem = if (_currentScreen.value == 2) 2 else 1
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
