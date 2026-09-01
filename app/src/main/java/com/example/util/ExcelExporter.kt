package com.example.util

import android.content.Context
import com.example.data.model.SchoolInfo
import com.example.data.model.StudentGradeEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object ExcelExporter {

    fun generateClassSheetCsv(
        context: Context,
        schoolInfo: SchoolInfo,
        semester: Int,
        month: String,
        section: String,
        subject: String,
        students: List<StudentGradeEntity>
    ): File? {
        try {
            val semText = if (semester == 1) "الفصل الأول" else "الفصل الثاني"
            val fileName = "كشف_اعمال_السنة_${semText}_${month.replace(" ", "_")}.csv"
            val file = File(context.cacheDir, fileName)

            val fos = FileOutputStream(file)
            val writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)
            // UTF-8 BOM so MS Excel displays Arabic correctly
            writer.write("\uFEFF")

            // Title & Info
            writer.write("برنامج كشف درجات أعمال السنة - $semText\n")
            writer.write("المدرسة / المجمع,\"${schoolInfo.schoolName}\",العام الدراسي,\"${schoolInfo.academicYearAD}م / ${schoolInfo.academicYearHijri}هـ\"\n")
            writer.write("الأستاذ,\"${schoolInfo.teacherName}\",المادة,\"$subject\",الصف,\"${schoolInfo.gradeLevels}\",الشهر,\"$month\",الشعبة,\"$section\"\n\n")

            // Table Headers
            writer.write("م,اسم الطالب,المواظبة,الواجبات,الشفوي,التحريري,المجموع,النسبة %,التقدير,ملاحظات\n")

            val maxTotal = schoolInfo.maxTotalScore
            for (s in students) {
                val total = s.totalScore
                val pct = s.calculatePercentage(maxTotal)
                val grade = s.getGradeSymbol(maxTotal)
                writer.write("${s.studentOrder},\"${s.studentName}\",${s.attendance},${s.homework},${s.oral},${s.written},$total,$pct%,$grade,\"${s.notes}\"\n")
            }

            writer.write("\n\"برمجة الدكتور/ مالك الرميمة 🦷هاتف 771134103\",,,,,,,,,\n")

            writer.flush()
            writer.close()
            fos.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun generateSingleStudentCsv(
        context: Context,
        schoolInfo: SchoolInfo,
        semester: Int,
        student: StudentGradeEntity
    ): File? {
        try {
            val semText = if (semester == 1) "الفصل الأول" else "الفصل الثاني"
            val fileName = "درجات_الطالب_${student.studentName.replace(" ", "_")}.csv"
            val file = File(context.cacheDir, fileName)

            val fos = FileOutputStream(file)
            val writer = OutputStreamWriter(fos, StandardCharsets.UTF_8)
            writer.write("\uFEFF")

            writer.write("بطاقة درجات الطالب - أعمال السنة\n")
            writer.write("اسم الطالب,\"${student.studentName}\"\n")
            writer.write("المدرسة,\"${schoolInfo.schoolName}\"\n")
            writer.write("المعلم,\"${schoolInfo.teacherName}\"\n")
            writer.write("الصف,\"${schoolInfo.gradeLevels}\",الشعبة,\"${student.section}\"\n")
            writer.write("المادة,\"${student.subject}\",الفصل,\"$semText\",الشهر,\"${student.month}\"\n\n")

            writer.write("البند,الدرجة المستحقة,الدرجة العظمى\n")
            writer.write("المواظبة والحضور,${student.attendance},${schoolInfo.maxAttendance}\n")
            writer.write("الواجبات المدرسية,${student.homework},${schoolInfo.maxHomework}\n")
            writer.write("الاختبار الشفوي,${student.oral},${schoolInfo.maxOral}\n")
            writer.write("الاختبار التحريري,${student.written},${schoolInfo.maxWritten}\n")

            val maxTotal = schoolInfo.maxTotalScore
            val total = student.totalScore
            val pct = student.calculatePercentage(maxTotal)
            val grade = student.getGradeSymbol(maxTotal)

            writer.write("المجموع الكلي,$total,$maxTotal\n")
            writer.write("النسبة المئوية,$pct%,\n")
            writer.write("التقدير العام,$grade,\n\n")
            writer.write("\"برمجة الدكتور/ مالك الرميمة 🦷هاتف 771134103\",,\n")

            writer.flush()
            writer.close()
            fos.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
