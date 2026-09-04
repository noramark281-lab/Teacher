package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.dao.GradeDao
import com.example.data.model.SchoolInfo
import com.example.data.model.StudentGradeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GradeRepository(
    private val gradeDao: GradeDao,
    private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("school_grade_prefs", Context.MODE_PRIVATE)

    private val _schoolInfoState = MutableStateFlow(loadSchoolInfo())
    val schoolInfoState = _schoolInfoState.asStateFlow()

    fun getStudents(gradeLevel: String, semester: Int, month: String, section: String, subject: String): Flow<List<StudentGradeEntity>> {
        return gradeDao.getStudents(gradeLevel, semester, month, section, subject)
    }

    fun getAllStudentsForSemester(gradeLevel: String, semester: Int): Flow<List<StudentGradeEntity>> {
        return gradeDao.getAllStudentsForSemester(gradeLevel, semester)
    }

    fun getAllStudentsForSemesterGlobal(semester: Int): Flow<List<StudentGradeEntity>> {
        return gradeDao.getAllStudentsForSemesterGlobal(semester)
    }

    fun getAllStudentsForGrade(gradeLevel: String): Flow<List<StudentGradeEntity>> {
        return gradeDao.getAllStudentsForGrade(gradeLevel)
    }

    fun getAllStudents(): Flow<List<StudentGradeEntity>> {
        return gradeDao.getAllStudents()
    }

    fun getDistinctMonths(gradeLevel: String, semester: Int): Flow<List<String>> {
        return gradeDao.getDistinctMonths(gradeLevel, semester)
    }

    fun getDistinctSections(gradeLevel: String, semester: Int): Flow<List<String>> {
        return gradeDao.getDistinctSections(gradeLevel, semester)
    }

    fun getDistinctSubjects(gradeLevel: String, semester: Int): Flow<List<String>> {
        return gradeDao.getDistinctSubjects(gradeLevel, semester)
    }

    fun getAllDistinctSubjects(): Flow<List<String>> {
        return gradeDao.getAllDistinctSubjects()
    }

    suspend fun insertStudent(student: StudentGradeEntity): Long = withContext(Dispatchers.IO) {
        gradeDao.insertStudent(student)
    }

    suspend fun insertStudents(students: List<StudentGradeEntity>) = withContext(Dispatchers.IO) {
        gradeDao.insertStudents(students)
    }

    suspend fun updateStudent(student: StudentGradeEntity) = withContext(Dispatchers.IO) {
        gradeDao.updateStudent(student)
    }

    suspend fun deleteStudent(student: StudentGradeEntity) = withContext(Dispatchers.IO) {
        gradeDao.deleteStudent(student)
    }

    suspend fun deleteStudentById(id: Long) = withContext(Dispatchers.IO) {
        gradeDao.deleteStudentById(id)
    }

    suspend fun clearStudentScores(id: Long) = withContext(Dispatchers.IO) {
        gradeDao.clearStudentScores(id)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        gradeDao.clearAllData()
        resetSchoolInfoToDefault()
    }

    fun loadSchoolInfo(): SchoolInfo {
        return SchoolInfo(
            schoolName = prefs.getString("school_name", "مجمع النور التربوي الحديث") ?: "مجمع النور التربوي الحديث",
            academicYearAD = prefs.getString("academic_year_ad", "2025 - 2026") ?: "2025 - 2026",
            academicYearHijri = prefs.getString("academic_year_hijri", "1447") ?: "1447",
            teacherName = prefs.getString("teacher_name", "أ. محمد عبدالقوي الرميمة") ?: "أ. محمد عبدالقوي الرميمة",
            gradeLevels = prefs.getString("grade_levels", "الأول الثانوي") ?: "الأول الثانوي",
            defaultSubject = prefs.getString("default_subject", "أحياء") ?: "أحياء",
            maxAttendance = prefs.getFloat("max_attendance", 10.0f).toDouble(),
            maxHomework = prefs.getFloat("max_homework", 10.0f).toDouble(),
            maxOral = prefs.getFloat("max_oral", 10.0f).toDouble(),
            maxWritten = prefs.getFloat("max_written", 20.0f).toDouble()
        )
    }

    fun saveSchoolInfo(info: SchoolInfo) {
        prefs.edit().apply {
            putString("school_name", info.schoolName)
            putString("academic_year_ad", info.academicYearAD)
            putString("academic_year_hijri", info.academicYearHijri)
            putString("teacher_name", info.teacherName)
            putString("grade_levels", info.gradeLevels)
            putString("default_subject", info.defaultSubject)
            putFloat("max_attendance", info.maxAttendance.toFloat())
            putFloat("max_homework", info.maxHomework.toFloat())
            putFloat("max_oral", info.maxOral.toFloat())
            putFloat("max_written", info.maxWritten.toFloat())
            apply()
        }
        _schoolInfoState.value = info
    }

    private fun resetSchoolInfoToDefault() {
        val defaultInfo = SchoolInfo()
        saveSchoolInfo(defaultInfo)
    }

    suspend fun prePopulateIfEmpty() = withContext(Dispatchers.IO) {
        val isPopulated = prefs.getBoolean("is_pre_populated", false)
        if (!isPopulated) {
            val sampleStudentsSem1 = listOf(
                StudentGradeEntity(
                    gradeLevel = "الصف الأول الابتدائي",
                    semester = 1,
                    month = "الشهر الأول",
                    section = "أ",
                    subject = "أحياء",
                    studentOrder = 1,
                    studentName = "أحمد محمد سالم",
                    attendance = 10.0,
                    homework = 9.5,
                    oral = 10.0,
                    written = 19.0
                ),
                StudentGradeEntity(
                    gradeLevel = "الصف الأول الابتدائي",
                    semester = 1,
                    month = "الشهر الأول",
                    section = "أ",
                    subject = "أحياء",
                    studentOrder = 2,
                    studentName = "خالد عبدالله سعيد",
                    attendance = 9.0,
                    homework = 8.5,
                    oral = 9.0,
                    written = 17.5
                ),
                StudentGradeEntity(
                    gradeLevel = "الصف الأول الابتدائي",
                    semester = 1,
                    month = "الشهر الأول",
                    section = "أ",
                    subject = "أحياء",
                    studentOrder = 3,
                    studentName = "عمر فاروق المنصوري",
                    attendance = 10.0,
                    homework = 10.0,
                    oral = 9.5,
                    written = 18.0
                ),
                StudentGradeEntity(
                    gradeLevel = "الصف الأول الابتدائي",
                    semester = 1,
                    month = "الشهر الأول",
                    section = "أ",
                    subject = "أحياء",
                    studentOrder = 4,
                    studentName = "يوسف إبراهيم القحطاني",
                    attendance = 8.5,
                    homework = 9.0,
                    oral = 8.0,
                    written = 15.0
                ),
                StudentGradeEntity(
                    gradeLevel = "الصف الأول الابتدائي",
                    semester = 1,
                    month = "الشهر الأول",
                    section = "أ",
                    subject = "أحياء",
                    studentOrder = 5,
                    studentName = "عبدالرحمن علي الرميمة",
                    attendance = 10.0,
                    homework = 10.0,
                    oral = 10.0,
                    written = 20.0
                )
            )

            val sampleStudentsSem2 = sampleStudentsSem1.map {
                it.copy(id = 0, semester = 2, month = "الشهر الأول")
            }

            gradeDao.insertStudents(sampleStudentsSem1 + sampleStudentsSem2)
            prefs.edit().putBoolean("is_pre_populated", true).apply()
        }
    }

    suspend fun exportBackupJson(students: List<StudentGradeEntity>): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        val info = loadSchoolInfo()
        val infoObj = JSONObject().apply {
            put("schoolName", info.schoolName)
            put("academicYearAD", info.academicYearAD)
            put("academicYearHijri", info.academicYearHijri)
            put("teacherName", info.teacherName)
            put("gradeLevels", info.gradeLevels)
            put("defaultSubject", info.defaultSubject)
            put("maxAttendance", info.maxAttendance)
            put("maxHomework", info.maxHomework)
            put("maxOral", info.maxOral)
            put("maxWritten", info.maxWritten)
        }
        root.put("schoolInfo", infoObj)

        val array = JSONArray()
        for (s in students) {
            val obj = JSONObject().apply {
                put("gradeLevel", s.gradeLevel)
                put("semester", s.semester)
                put("month", s.month)
                put("section", s.section)
                put("subject", s.subject)
                put("studentOrder", s.studentOrder)
                put("studentName", s.studentName)
                put("attendance", s.attendance)
                put("homework", s.homework)
                put("oral", s.oral)
                put("written", s.written)
                put("notes", s.notes)
            }
            array.put(obj)
        }
        root.put("grades", array)
        root.put("exportedAt", System.currentTimeMillis())
        root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.has("schoolInfo")) {
                val infoObj = root.getJSONObject("schoolInfo")
                val importedInfo = SchoolInfo(
                    schoolName = infoObj.optString("schoolName", "مجمع النور"),
                    academicYearAD = infoObj.optString("academicYearAD", "2025 - 2026"),
                    academicYearHijri = infoObj.optString("academicYearHijri", "1447"),
                    teacherName = infoObj.optString("teacherName", "أ. محمد عبدالقوي"),
                    gradeLevels = infoObj.optString("gradeLevels", "الأول الثانوي"),
                    defaultSubject = infoObj.optString("defaultSubject", "أحياء"),
                    maxAttendance = infoObj.optDouble("maxAttendance", 10.0),
                    maxHomework = infoObj.optDouble("maxHomework", 10.0),
                    maxOral = infoObj.optDouble("maxOral", 10.0),
                    maxWritten = infoObj.optDouble("maxWritten", 20.0)
                )
                saveSchoolInfo(importedInfo)
            }

            if (root.has("grades")) {
                val array = root.getJSONArray("grades")
                val list = mutableListOf<StudentGradeEntity>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        StudentGradeEntity(
                            gradeLevel = obj.optString("gradeLevel", "الصف الأول الابتدائي"),
                            semester = obj.optInt("semester", 1),
                            month = obj.optString("month", "الشهر الأول"),
                            section = obj.optString("section", "أ"),
                            subject = obj.optString("subject", "أحياء"),
                            studentOrder = obj.optInt("studentOrder", i + 1),
                            studentName = obj.optString("studentName", "طالب"),
                            attendance = obj.optDouble("attendance", 0.0),
                            homework = obj.optDouble("homework", 0.0),
                            oral = obj.optDouble("oral", 0.0),
                            written = obj.optDouble("written", 0.0),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    gradeDao.clearAllData()
                    gradeDao.insertStudents(list)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
