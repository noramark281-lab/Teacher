package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.SchoolInfo
import com.example.data.model.StudentGradeEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun generateClassSheetPdf(
        context: Context,
        schoolInfo: SchoolInfo,
        semester: Int,
        month: String,
        section: String,
        subject: String,
        students: List<StudentGradeEntity>
    ): File? {
        try {
            val pdfDoc = PdfDocument()
            val pageWidth = 595 // A4 standard width in points (72 dpi)
            val pageHeight = 842 // A4 standard height
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
            }

            // Background
            paint.color = Color.WHITE
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

            // Header Frame
            paint.color = Color.parseColor("#1E3A8A") // Deep Blue
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.5f
            canvas.drawRoundRect(20f, 20f, pageWidth - 20f, pageHeight - 20f, 8f, 8f, paint)

            // Title Box
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#F1F5F9")
            canvas.drawRoundRect(28f, 28f, pageWidth - 28f, 90f, 6f, 6f, paint)

            paint.color = Color.parseColor("#1E3A8A")
            paint.textSize = 17f
            paint.isFakeBoldText = true
            paint.textAlign = Paint.Align.CENTER
            val semText = if (semester == 1) "الفصل الدراسي الأول" else "الفصل الدراسي الثاني"
            canvas.drawText("كشف درجات أعمال السنة - $semText", pageWidth / 2f, 54f, paint)

            paint.textSize = 12f
            paint.color = Color.parseColor("#475569")
            paint.isFakeBoldText = false
            canvas.drawText("${schoolInfo.schoolName} | العام الدراسي: ${schoolInfo.academicYearAD}م / ${schoolInfo.academicYearHijri}هـ", pageWidth / 2f, 76f, paint)

            // Info Bar (Teacher, Subject, Grade, Month, Section)
            paint.textSize = 11f
            paint.color = Color.parseColor("#0F172A")
            paint.textAlign = Paint.Align.RIGHT

            val yInfo = 112f
            canvas.drawText("الأستاذ: ${schoolInfo.teacherName}", pageWidth - 36f, yInfo, paint)
            canvas.drawText("المادة: $subject", pageWidth - 230f, yInfo, paint)
            canvas.drawText("الصف: ${schoolInfo.gradeLevels}", pageWidth - 360f, yInfo, paint)
            canvas.drawText("الشهر: $month | الشعبة: $section", pageWidth - 460f, yInfo, paint)

            // Divider
            paint.color = Color.parseColor("#CBD5E1")
            paint.strokeWidth = 1.5f
            canvas.drawLine(28f, 125f, pageWidth - 28f, 125f, paint)

            // Table Setup
            val startY = 135f
            val rowHeight = 24f
            val maxTotal = schoolInfo.maxTotalScore

            // Table Column Widths (Sum = 539, from x=28 to x=567)
            // Order (RTL): [م: 25] [اسم الطالب: 130] [المواظبة: 45] [الواجبات: 45] [الشفوي: 45] [التحريري: 45] [المجموع: 50] [النسبة%: 50] [التقدير: 50]
            val colWidths = floatArrayOf(26f, 160f, 50f, 50f, 50f, 50f, 55f, 50f, 48f)
            val headers = arrayOf("م", "اسم الطالب", "مواظبة", "واجبات", "شفوي", "تحريري", "المجموع", "النسبة%", "التقدير")

            // Draw Table Header
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#1E40AF") // Dark Blue Header
            canvas.drawRect(28f, startY, pageWidth - 28f, startY + rowHeight, paint)

            paint.color = Color.WHITE
            paint.textSize = 10f
            paint.isFakeBoldText = true
            paint.textAlign = Paint.Align.CENTER

            var currentX = pageWidth - 28f
            for (i in headers.indices) {
                val w = colWidths[i]
                val cellCenterX = currentX - (w / 2f)
                canvas.drawText(headers[i], cellCenterX, startY + 16f, paint)
                currentX -= w
            }

            // Draw Table Rows
            var yPos = startY + rowHeight
            paint.isFakeBoldText = false
            paint.textSize = 9.5f

            for ((idx, student) in students.withIndex()) {
                if (yPos + rowHeight > pageHeight - 80f) {
                    break // Keep single page clean
                }

                // Row background (alternating)
                paint.style = Paint.Style.FILL
                if (idx % 2 == 0) {
                    paint.color = Color.parseColor("#F8FAFC")
                } else {
                    paint.color = Color.WHITE
                }
                canvas.drawRect(28f, yPos, pageWidth - 28f, yPos + rowHeight, paint)

                // Row Border line
                paint.style = Paint.Style.STROKE
                paint.color = Color.parseColor("#E2E8F0")
                paint.strokeWidth = 0.8f
                canvas.drawLine(28f, yPos + rowHeight, pageWidth - 28f, yPos + rowHeight, paint)

                // Cell values
                paint.style = Paint.Style.FILL
                paint.color = Color.parseColor("#1E293B")

                val total = student.totalScore
                val pct = student.calculatePercentage(maxTotal)
                val gradeSymbol = student.getGradeSymbol(maxTotal)

                val rowData = arrayOf(
                    "${student.studentOrder}",
                    student.studentName,
                    "${student.attendance}",
                    "${student.homework}",
                    "${student.oral}",
                    "${student.written}",
                    "$total",
                    "$pct%",
                    gradeSymbol
                )

                currentX = pageWidth - 28f
                for (i in rowData.indices) {
                    val w = colWidths[i]
                    val cellCenterX = currentX - (w / 2f)
                    
                    // Student name aligned right or center
                    if (i == 1) {
                        paint.textAlign = Paint.Align.RIGHT
                        canvas.drawText(rowData[i], currentX - 8f, yPos + 16f, paint)
                    } else {
                        paint.textAlign = Paint.Align.CENTER
                        if (i == 6) { // Total score bold
                            paint.isFakeBoldText = true
                            paint.color = Color.parseColor("#1D4ED8")
                        } else if (i == 7) { // Percentage
                            paint.isFakeBoldText = true
                            paint.color = Color.parseColor("#047857")
                        } else {
                            paint.isFakeBoldText = false
                            paint.color = Color.parseColor("#1E293B")
                        }
                        canvas.drawText(rowData[i], cellCenterX, yPos + 16f, paint)
                    }
                    currentX -= w
                }

                yPos += rowHeight
            }

            // Footer Statistics & Signatures
            val footerY = pageHeight - 65f
            paint.color = Color.parseColor("#CBD5E1")
            paint.strokeWidth = 1f
            canvas.drawLine(28f, footerY - 10f, pageWidth - 28f, footerY - 10f, paint)

            paint.color = Color.parseColor("#475569")
            paint.textSize = 9.5f
            paint.textAlign = Paint.Align.RIGHT
            paint.isFakeBoldText = true
            canvas.drawText("توقيع معلم المادة: ......................", pageWidth - 36f, footerY + 12f, paint)
            canvas.drawText("مدير المدرسة: ......................", pageWidth - 260f, footerY + 12f, paint)

            val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
            paint.textAlign = Paint.Align.LEFT
            paint.isFakeBoldText = false
            canvas.drawText("تاريخ الطباعة: $dateStr | كشف أعمال السنة الذكي", 36f, footerY + 12f, paint)

            pdfDoc.finishPage(page)

            // Save to Cache / Files
            val fileName = "كشف_اعمال_السنة_فصل_${semester}_${month.replace(" ", "_")}.pdf"
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDoc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDoc.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun generateSingleStudentPdf(
        context: Context,
        schoolInfo: SchoolInfo,
        semester: Int,
        student: StudentGradeEntity
    ): File? {
        try {
            val pdfDoc = PdfDocument()
            val pageWidth = 400
            val pageHeight = 500
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply { isAntiAlias = true }

            // Card background
            paint.color = Color.parseColor("#F8FAFC")
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

            // Border
            paint.color = Color.parseColor("#2563EB")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawRoundRect(15f, 15f, pageWidth - 15f, pageHeight - 15f, 12f, 12f, paint)

            // Header
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#1E3A8A")
            canvas.drawRoundRect(22f, 22f, pageWidth - 22f, 75f, 8f, 8f, paint)

            paint.color = Color.WHITE
            paint.textSize = 15f
            paint.isFakeBoldText = true
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("بطاقة درجات أعمال السنة للطالب", pageWidth / 2f, 46f, paint)

            paint.textSize = 11f
            paint.isFakeBoldText = false
            canvas.drawText(schoolInfo.schoolName, pageWidth / 2f, 65f, paint)

            // Student Info
            paint.color = Color.parseColor("#0F172A")
            paint.textAlign = Paint.Align.RIGHT
            paint.textSize = 12f
            paint.isFakeBoldText = true

            var y = 105f
            canvas.drawText("اسم الطالب: ${student.studentName}", pageWidth - 30f, y, paint)
            y += 22f
            canvas.drawText("الصف: ${schoolInfo.gradeLevels} | الشعبة: ${student.section}", pageWidth - 30f, y, paint)
            y += 22f
            val semText = if (semester == 1) "الفصل الأول" else "الفصل الثاني"
            canvas.drawText("الفصل: $semText | الشهر: ${student.month}", pageWidth - 30f, y, paint)
            y += 22f
            canvas.drawText("المادة: ${student.subject} | المعلم: ${schoolInfo.teacherName}", pageWidth - 30f, y, paint)

            // Grades Box
            y += 25f
            paint.color = Color.parseColor("#EFF6FF")
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(25f, y, pageWidth - 25f, y + 170f, 8f, 8f, paint)

            paint.color = Color.parseColor("#93C5FD")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(25f, y, pageWidth - 25f, y + 170f, 8f, 8f, paint)

            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#1E293B")
            paint.textSize = 12f
            paint.isFakeBoldText = false

            val maxTotal = schoolInfo.maxTotalScore
            val total = student.totalScore
            val pct = student.calculatePercentage(maxTotal)
            val gradeSymbol = student.getGradeSymbol(maxTotal)

            val gradesY = y + 25f
            canvas.drawText("درجة المواظبة والحضور:", pageWidth - 40f, gradesY, paint)
            canvas.drawText("${student.attendance} / ${schoolInfo.maxAttendance}", 60f, gradesY, paint)

            canvas.drawText("درجة الواجبات المدرسية:", pageWidth - 40f, gradesY + 25f, paint)
            canvas.drawText("${student.homework} / ${schoolInfo.maxHomework}", 60f, gradesY + 25f, paint)

            canvas.drawText("درجة الاختبار الشفوي:", pageWidth - 40f, gradesY + 50f, paint)
            canvas.drawText("${student.oral} / ${schoolInfo.maxOral}", 60f, gradesY + 50f, paint)

            canvas.drawText("درجة الاختبار التحريري:", pageWidth - 40f, gradesY + 75f, paint)
            canvas.drawText("${student.written} / ${schoolInfo.maxWritten}", 60f, gradesY + 75f, paint)

            // Line
            paint.color = Color.parseColor("#93C5FD")
            canvas.drawLine(35f, gradesY + 95f, pageWidth - 35f, gradesY + 95f, paint)

            // Total and %
            paint.color = Color.parseColor("#1D4ED8")
            paint.isFakeBoldText = true
            paint.textSize = 13f
            canvas.drawText("المجموع الكلي: $total / $maxTotal", pageWidth - 40f, gradesY + 120f, paint)

            paint.color = Color.parseColor("#047857")
            canvas.drawText("النسبة المئوية: $pct% ($gradeSymbol)", pageWidth - 40f, gradesY + 145f, paint)

            // Footer
            val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
            paint.color = Color.parseColor("#64748B")
            paint.textSize = 10f
            paint.isFakeBoldText = false
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("تاريخ الإصدار: $dateStr", pageWidth / 2f, pageHeight - 30f, paint)

            pdfDoc.finishPage(page)

            val fileName = "درجات_الطالب_${student.studentName.replace(" ", "_")}.pdf"
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDoc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDoc.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
