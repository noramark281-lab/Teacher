package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_grades")
data class StudentGradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val semester: Int, // 1 for الفصل الأول, 2 for الفصل الثاني
    val month: String, // e.g. "الشهر الأول", "الشهر الثاني", "محرم"
    val section: String, // e.g. "أ", "ب", "شعبة 1"
    val subject: String, // e.g. "رياضيات", "أحياء"
    val studentOrder: Int, // م - 1, 2, 3...
    val studentName: String, // اسم الطالب
    val attendance: Double = 0.0, // المواظبة
    val homework: Double = 0.0, // الواجبات
    val oral: Double = 0.0, // الشفوي
    val written: Double = 0.0, // التحريري
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalScore: Double
        get() = attendance + homework + oral + written

    fun calculatePercentage(maxTotal: Double): Double {
        if (maxTotal <= 0) return 0.0
        val p = (totalScore / maxTotal) * 100.0
        return (p * 10).toInt() / 10.0 // 1 decimal place
    }

    fun getGradeSymbol(maxTotal: Double): String {
        val pct = calculatePercentage(maxTotal)
        return when {
            pct >= 90.0 -> "ممتاز"
            pct >= 80.0 -> "جيد جداً"
            pct >= 65.0 -> "جيد"
            pct >= 50.0 -> "مقبول"
            else -> "ضعيف"
        }
    }
}

data class SchoolInfo(
    val schoolName: String = "مجمع النور التربوي الحديث",
    val academicYearAD: String = "2025 - 2026",
    val academicYearHijri: String = "1447",
    val teacherName: String = "أ. محمد عبدالقوي الرميمة",
    val gradeLevels: String = "الأول الثانوي",
    val defaultSubject: String = "أحياء",
    val maxAttendance: Double = 10.0,
    val maxHomework: Double = 10.0,
    val maxOral: Double = 10.0,
    val maxWritten: Double = 20.0
) {
    val maxTotalScore: Double
        get() = maxAttendance + maxHomework + maxOral + maxWritten
}
