package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolInfo
import com.example.data.model.StudentGradeEntity
import com.example.data.model.formatScore

@Composable
fun GradeTable(
    students: List<StudentGradeEntity>,
    schoolInfo: SchoolInfo,
    onStudentClick: (StudentGradeEntity) -> Unit,
    onSendStudentReport: (StudentGradeEntity) -> Unit,
    onSaveStudentPdf: (StudentGradeEntity) -> Unit,
    onSaveStudentExcel: (StudentGradeEntity) -> Unit,
    onClearScores: (StudentGradeEntity) -> Unit,
    onDeleteStudent: (StudentGradeEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()
    val maxTotal = schoolInfo.maxTotalScore

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(listOf(GoldBorderLight, GoldBorderDark))
                ),
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
                .padding(vertical = 4.dp)
        ) {
            // Table Header Row
            Row(
                modifier = Modifier
                    .background(Color(0xFF1E3A8A))
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderCell(text = "م", width = 36.dp)
                TableHeaderCell(text = "اسم الطالب", width = 160.dp)
                TableHeaderCell(text = "المواظبة\n(${schoolInfo.maxAttendance.formatScore()})", width = 72.dp)
                TableHeaderCell(text = "الواجبات\n(${schoolInfo.maxHomework.formatScore()})", width = 72.dp)
                TableHeaderCell(text = "الشفوي\n(${schoolInfo.maxOral.formatScore()})", width = 72.dp)
                TableHeaderCell(text = "التحريري\n(${schoolInfo.maxWritten.formatScore()})", width = 74.dp)
                TableHeaderCell(text = "المجموع\n(${maxTotal.formatScore()})", width = 78.dp)
                TableHeaderCell(text = "نسبة %\nالتقدير", width = 84.dp)
                TableHeaderCell(text = "إرسال / حفظ / خيارات", width = 180.dp)
            }

            // Student Rows
            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(min = 788.dp)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا يوجد طلاب مسجلين في هذا الجدول حالياً\nاضغط على 'إضافة طالب' بالأسفل لبدء رصد الدرجات",
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                students.forEachIndexed { index, student ->
                    val isEven = index % 2 == 0
                    val total = student.totalScore
                    val pct = student.calculatePercentage(maxTotal)
                    val gradeSymbol = student.getGradeSymbol(maxTotal)

                    val gradeBadgeColor = when {
                        pct >= 90.0 -> Color(0xFF16A34A)
                        pct >= 80.0 -> Color(0xFF2563EB)
                        pct >= 65.0 -> Color(0xFFD97706)
                        pct >= 50.0 -> Color(0xFFCA8A04)
                        else -> Color(0xFFDC2626)
                    }

                    Row(
                        modifier = Modifier
                            .background(if (isEven) Color(0xFFF8FAFC) else Color.White)
                            .clickable { onStudentClick(student) }
                            .padding(vertical = 6.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Order number
                        TableCell(text = "${student.studentOrder}", width = 36.dp, isBold = true, color = Color(0xFF334155))

                        // Student Name
                        TableCell(
                            text = student.studentName,
                            width = 160.dp,
                            isBold = true,
                            color = Color(0xFF0F172A),
                            align = TextAlign.Right
                        )

                        // Attendance
                        TableCell(text = "${student.attendance}", width = 72.dp)

                        // Homework
                        TableCell(text = "${student.homework}", width = 72.dp)

                        // Oral
                        TableCell(text = "${student.oral}", width = 72.dp)

                        // Written
                        TableCell(text = "${student.written}", width = 74.dp)

                        // Total (Highlighted Blue)
                        TableCell(
                            text = "$total",
                            width = 78.dp,
                            isBold = true,
                            color = Color(0xFF1E40AF),
                            backgroundColor = Color(0xFFEFF6FF)
                        )

                        // Percentage & Grade Symbol
                        Box(
                            modifier = Modifier
                                .width(84.dp)
                                .padding(horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = student.percentageDisplay(maxTotal),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = gradeBadgeColor
                                )
                                Text(
                                    text = gradeSymbol,
                                    fontSize = 10.sp,
                                    color = gradeBadgeColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Actions Row: Send WhatsApp, PDF, Excel, Delete
                        Row(
                            modifier = Modifier.width(180.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Send to Phone / WhatsApp Button for single student
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF16A34A))
                                    .clickable { onSendStudentReport(student) }
                                    .testTag("send_student_whatsapp_${student.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "إرسال كشف الطالب لرقم هاتف",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            // PDF Button for single student
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF2563EB))
                                    .clickable { onSaveStudentPdf(student) }
                                    .testTag("save_student_pdf_${student.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "حفظ PDF للطالب",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            // Excel Button for single student
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0D9488))
                                    .clickable { onSaveStudentExcel(student) }
                                    .testTag("save_student_excel_${student.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = "حفظ Excel للطالب",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            // Delete / Clear marks button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEF4444))
                                    .clickable { onClearScores(student) }
                                    .testTag("clear_student_${student.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف درجات الطالب",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Divider line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.8.dp)
                            .background(Color(0xFFE2E8F0))
                    )
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 11.5.sp,
        textAlign = TextAlign.Center,
        lineHeight = 14.sp,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 2.dp)
    )
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    color: Color = Color(0xFF1E293B),
    backgroundColor: Color = Color.Transparent,
    align: TextAlign = TextAlign.Center
) {
    Box(
        modifier = Modifier
            .width(width)
            .then(
                if (backgroundColor != Color.Transparent) {
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(backgroundColor)
                        .padding(vertical = 2.dp)
                } else Modifier
            )
            .padding(horizontal = 4.dp),
        contentAlignment = when (align) {
            TextAlign.Right -> Alignment.CenterEnd
            TextAlign.Left -> Alignment.CenterStart
            else -> Alignment.Center
        }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color,
            textAlign = align,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
