package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_grades")
data class StudentGradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gradeLevel: String = "الصف الأول الابتدائي", // المرحلة والصف الدراسي (عزل تام بين الصفوف)
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

    val outcomeScore: Double
        get() = totalScore / 5.0

    fun outcomeDisplay(): String {
        return outcomeScore.formatScore()
    }

    fun normalizedScore(maxTotal: Double): Double {
        if (maxTotal <= 0.0) return 0.0
        val norm = totalScore / maxTotal
        return when {
            norm < 0.0 -> 0.0
            norm > 1.0 -> 1.0
            else -> Math.round(norm * 10000.0) / 10000.0
        }
    }

    fun calculatePercentage(maxTotal: Double): Double {
        if (maxTotal <= 0.0) return 0.0
        val p = (totalScore / maxTotal) * 100.0
        return Math.round(p * 100.0) / 100.0 // 2 decimal places precision
    }

    fun percentageDisplay(maxTotal: Double): String {
        return String.format(java.util.Locale.US, "%.2f%%", calculatePercentage(maxTotal))
    }

    fun getGradeSymbol(maxTotal: Double): String {
        val pct = calculatePercentage(maxTotal)
        return when {
            pct > 100.0 -> "تجاوز الحد"
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

fun Double.formatScore(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.1f", this)
    }
}

fun Double.formatPrecise(decimals: Int = 2): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        String.format(java.util.Locale.US, "%.${decimals}f", this)
    }
}

data class MonthGradeSummary(
    val monthName: String,
    val totalScore: Double = 0.0,
    val outcome: Double = 0.0, // مجموع درجات الشهر ÷ 5
    val hasRecord: Boolean = false
)

data class StudentSemesterOutcome(
    val studentName: String,
    val gradeLevel: String = "",
    val section: String = "",
    val subject: String = "",
    val semester: Int = 1,
    val month1: MonthGradeSummary = MonthGradeSummary("الشهر الأول"),
    val month2: MonthGradeSummary = MonthGradeSummary("الشهر الثاني"),
    val month3: MonthGradeSummary = MonthGradeSummary("الشهر الثالث"),
    val extraMonths: List<MonthGradeSummary> = emptyList()
) {
    // مجموع محصلة الثلاثة أشهر
    val sumOf3MonthsOutcome: Double
        get() = month1.outcome + month2.outcome + month3.outcome

    // محصلة الفصل = مجموع محصلات الثلاثة أشهر ÷ 3
    val semesterOutcomeDividedBy3: Double
        get() = sumOf3MonthsOutcome / 3.0

    fun semesterOutcomeDisplay(): String {
        return semesterOutcomeDividedBy3.formatPrecise(2)
    }
}

data class StudentFinalOutcome(
    val studentName: String,
    val gradeLevel: String = "",
    val section: String = "",
    val subject: String = "",
    val sem1Data: StudentSemesterOutcome? = null,
    val sem2Data: StudentSemesterOutcome? = null
) {
    val sem1Outcome: Double
        get() = sem1Data?.semesterOutcomeDividedBy3 ?: 0.0

    val sem2Outcome: Double
        get() = sem2Data?.semesterOutcomeDividedBy3 ?: 0.0

    // المحصلة النهائية = جمع محصلة الفصل الأول مع محصلة الفصل الثاني
    val finalCombinedOutcome: Double
        get() = sem1Outcome + sem2Outcome

    fun finalCombinedDisplay(): String {
        return finalCombinedOutcome.formatPrecise(2)
    }
}
