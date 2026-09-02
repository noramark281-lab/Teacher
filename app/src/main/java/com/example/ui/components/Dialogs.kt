package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolInfo
import com.example.data.model.StudentGradeEntity
import com.example.data.model.formatScore
import com.example.util.PdfExporter

@Composable
fun EditSchoolInfoDialog(
    currentInfo: SchoolInfo,
    onDismiss: () -> Unit,
    onSave: (SchoolInfo) -> Unit
) {
    var schoolName by remember { mutableStateOf(currentInfo.schoolName) }
    var yearAD by remember { mutableStateOf(currentInfo.academicYearAD) }
    var yearHijri by remember { mutableStateOf(currentInfo.academicYearHijri) }
    var teacherName by remember { mutableStateOf(currentInfo.teacherName) }
    var gradeLevels by remember { mutableStateOf(currentInfo.gradeLevels) }
    var defaultSubject by remember { mutableStateOf(currentInfo.defaultSubject) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تعديل بيانات المدرسة والمعلم",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1E3A8A)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("اسم المدرسة / المجمع") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_school_name")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = yearAD,
                        onValueChange = { yearAD = it },
                        label = { Text("العام الميلادي (م)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = yearHijri,
                        onValueChange = { yearHijri = it },
                        label = { Text("العام الهجري (هـ)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = teacherName,
                    onValueChange = { teacherName = it },
                    label = { Text("اسم الأستاذ / المعلم") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_teacher_name")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gradeLevels,
                        onValueChange = { gradeLevels = it },
                        label = { Text("الصفوف") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = defaultSubject,
                        onValueChange = { defaultSubject = it },
                        label = { Text("المادة") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentInfo.copy(
                            schoolName = schoolName.trim(),
                            academicYearAD = yearAD.trim(),
                            academicYearHijri = yearHijri.trim(),
                            teacherName = teacherName.trim(),
                            gradeLevels = gradeLevels.trim(),
                            defaultSubject = defaultSubject.trim()
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                modifier = Modifier.testTag("save_school_info_btn")
            ) {
                Text("حفظ التعديلات")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun AddEditStudentDialog(
    student: StudentGradeEntity?,
    schoolInfo: SchoolInfo,
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, attendance: Double, homework: Double, oral: Double, written: Double, maxAtt: Double, maxHw: Double, maxOral: Double, maxWritten: Double) -> Unit
) {
    var name by remember { mutableStateOf(student?.studentName ?: "") }

    // Max limits inputs (الحدود القصوى / الماكسيمام)
    var maxAttendanceText by remember { mutableStateOf(schoolInfo.maxAttendance.formatScore()) }
    var maxHomeworkText by remember { mutableStateOf(schoolInfo.maxHomework.formatScore()) }
    var maxOralText by remember { mutableStateOf(schoolInfo.maxOral.formatScore()) }
    var maxWrittenText by remember { mutableStateOf(schoolInfo.maxWritten.formatScore()) }

    val currentMaxAtt = maxAttendanceText.toDoubleOrNull() ?: 10.0
    val currentMaxHw = maxHomeworkText.toDoubleOrNull() ?: 10.0
    val currentMaxOral = maxOralText.toDoubleOrNull() ?: 10.0
    val currentMaxWr = maxWrittenText.toDoubleOrNull() ?: 20.0
    val calculatedSumMax = currentMaxAtt + currentMaxHw + currentMaxOral + currentMaxWr

    var maxTotalText by remember { mutableStateOf(schoolInfo.maxTotalScore.formatScore()) }

    // Keep maxTotal synced when sub-maxes change
    LaunchedEffect(currentMaxAtt, currentMaxHw, currentMaxOral, currentMaxWr) {
        maxTotalText = calculatedSumMax.formatScore()
    }

    // Student's actual grades
    var attendanceText by remember { mutableStateOf(if (student != null) student.attendance.formatScore() else "") }
    var homeworkText by remember { mutableStateOf(if (student != null) student.homework.formatScore() else "") }
    var oralText by remember { mutableStateOf(if (student != null) student.oral.formatScore() else "") }
    var writtenText by remember { mutableStateOf(if (student != null) student.written.formatScore() else "") }

    val att = attendanceText.toDoubleOrNull() ?: 0.0
    val hw = homeworkText.toDoubleOrNull() ?: 0.0
    val or = oralText.toDoubleOrNull() ?: 0.0
    val wr = writtenText.toDoubleOrNull() ?: 0.0

    val attExceeds = att > currentMaxAtt
    val hwExceeds = hw > currentMaxHw
    val oralExceeds = or > currentMaxOral
    val writtenExceeds = wr > currentMaxWr

    val maxTotal = maxTotalText.toDoubleOrNull() ?: calculatedSumMax
    val total = att + hw + or + wr
    val totalExceeds = total > maxTotal
    val anyFieldExceeds = attExceeds || hwExceeds || oralExceeds || writtenExceeds || totalExceeds

    val pct = if (maxTotal > 0.0) Math.round(((total / maxTotal) * 100.0) * 100.0) / 100.0 else 0.0
    val pctDisplay = String.format(java.util.Locale.US, "%.2f%%", pct)

    val evaluation = when {
        anyFieldExceeds -> "تجاوز الحد الأقصى"
        pct >= 90.0 -> "ممتاز"
        pct >= 80.0 -> "جيد جداً"
        pct >= 65.0 -> "جيد"
        pct >= 50.0 -> "مقبول"
        else -> "ضعيف"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (student == null) "إضافة طالب جديد" else "تعديل درجات الطالب",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1E3A8A)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Student Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الطالب الكامل") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_name_input")
                )

                // 2. Maximum limits settings box (الحدود القصوى للدرجات)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🎯 تحديد الحد الأعلى للدرجات (الماكسيمام):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = Color(0xFF1E40AF)
                            )
                        }

                        // Box 1: Total Max Score for the Subject
                        OutlinedTextField(
                            value = maxTotalText,
                            onValueChange = { maxTotalText = it },
                            label = { Text("إجمالي درجة المادة كاملاً (الماكسيمام)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1E40AF),
                                unfocusedBorderColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("max_total_input")
                        )

                        // Box 2 & 3: Attendance Max & Homework Max
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = maxAttendanceText,
                                onValueChange = { maxAttendanceText = it },
                                label = { Text("حد المواظبة") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("max_att_input")
                            )
                            OutlinedTextField(
                                value = maxHomeworkText,
                                onValueChange = { maxHomeworkText = it },
                                label = { Text("حد الواجبات") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("max_hw_input")
                            )
                        }

                        // Box 4 & 5: Oral Max & Written Max
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = maxOralText,
                                onValueChange = { maxOralText = it },
                                label = { Text("حد الشفوي") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("max_oral_input")
                            )
                            OutlinedTextField(
                                value = maxWrittenText,
                                onValueChange = { maxWrittenText = it },
                                label = { Text("حد التحريري") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("max_written_input")
                            )
                        }
                    }
                }

                // 3. Student's Actual Scores
                Text(
                    text = "📝 رصد درجات الطالب (وفقاً للحدود أعلاه):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = attendanceText,
                        onValueChange = { attendanceText = it },
                        label = { Text("المواظبة (${currentMaxAtt.formatScore()})") },
                        isError = attExceeds,
                        supportingText = if (attExceeds) {
                            { Text("الحد الأقصى ${currentMaxAtt.formatScore()}", color = Color.Red, fontSize = 10.sp) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("att_input")
                    )
                    OutlinedTextField(
                        value = homeworkText,
                        onValueChange = { homeworkText = it },
                        label = { Text("الواجبات (${currentMaxHw.formatScore()})") },
                        isError = hwExceeds,
                        supportingText = if (hwExceeds) {
                            { Text("الحد الأقصى ${currentMaxHw.formatScore()}", color = Color.Red, fontSize = 10.sp) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hw_input")
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = oralText,
                        onValueChange = { oralText = it },
                        label = { Text("الشفوي (${currentMaxOral.formatScore()})") },
                        isError = oralExceeds,
                        supportingText = if (oralExceeds) {
                            { Text("الحد الأقصى ${currentMaxOral.formatScore()}", color = Color.Red, fontSize = 10.sp) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("oral_input")
                    )
                    OutlinedTextField(
                        value = writtenText,
                        onValueChange = { writtenText = it },
                        label = { Text("التحريري (${currentMaxWr.formatScore()})") },
                        isError = writtenExceeds,
                        supportingText = if (writtenExceeds) {
                            { Text("الحد الأقصى ${currentMaxWr.formatScore()}", color = Color.Red, fontSize = 10.sp) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("written_input")
                    )
                }

                // 4. Dynamic calculation summary banner (المجموع والمحصلة والتقدير)
                val outcome = total / 5.0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (anyFieldExceeds) Color(0xFFFEF2F2) else Color(0xFFEFF6FF))
                        .border(
                            1.dp,
                            if (anyFieldExceeds) Color(0xFFFECACA) else Color(0xFFBFDBFE),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المجموع: ${total.formatScore()} / ${maxTotal.formatScore()}",
                                fontWeight = FontWeight.Bold,
                                color = if (anyFieldExceeds) Color(0xFFDC2626) else Color(0xFF1D4ED8),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "المحصلة (المجموع ÷ 5): ${outcome.formatScore()}",
                                fontWeight = FontWeight.Bold,
                                color = if (anyFieldExceeds) Color(0xFFDC2626) else Color(0xFF047857),
                                fontSize = 14.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "التقدير: $evaluation",
                                fontWeight = FontWeight.Medium,
                                color = if (anyFieldExceeds) Color(0xFFDC2626) else Color(0xFF475569),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "النسبة: $pctDisplay",
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            student?.id ?: 0L,
                            name,
                            att,
                            hw,
                            or,
                            wr,
                            currentMaxAtt,
                            currentMaxHw,
                            currentMaxOral,
                            currentMaxWr
                        )
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                modifier = Modifier.testTag("save_student_btn")
            ) {
                Text(if (student == null) "إضافة" else "حفظ التعديل")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun SettingsVariablesDialog(
    schoolInfo: SchoolInfo,
    onDismiss: () -> Unit,
    onSave: (SchoolInfo) -> Unit
) {
    var maxAttendance by remember { mutableStateOf(schoolInfo.maxAttendance.toString()) }
    var maxHomework by remember { mutableStateOf(schoolInfo.maxHomework.toString()) }
    var maxOral by remember { mutableStateOf(schoolInfo.maxOral.toString()) }
    var maxWritten by remember { mutableStateOf(schoolInfo.maxWritten.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF1E40AF))
                Spacer(modifier = Modifier.width(8.dp))
                Text("المتغيرات والرموز والدرجات العظمى", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "حدد الدرجة العظمى لكل بند لحساب المجموع والنسبة المئوية بدقة:",
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )

                OutlinedTextField(
                    value = maxAttendance,
                    onValueChange = { maxAttendance = it },
                    label = { Text("الدرجة العظمى للمواظبة والحضور") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxHomework,
                    onValueChange = { maxHomework = it },
                    label = { Text("الدرجة العظمى للواجبات المدرسية") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxOral,
                    onValueChange = { maxOral = it },
                    label = { Text("الدرجة العظمى للاختبار الشفوي") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxWritten,
                    onValueChange = { maxWritten = it },
                    label = { Text("الدرجة العظمى للاختبار التحريري") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                val sum = (maxAttendance.toDoubleOrNull() ?: 0.0) +
                        (maxHomework.toDoubleOrNull() ?: 0.0) +
                        (maxOral.toDoubleOrNull() ?: 0.0) +
                        (maxWritten.toDoubleOrNull() ?: 0.0)

                Text(
                    text = "إجمالي الدرجة العظمى للكشف: $sum",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E40AF),
                    fontSize = 14.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        schoolInfo.copy(
                            maxAttendance = maxAttendance.toDoubleOrNull() ?: 10.0,
                            maxHomework = maxHomework.toDoubleOrNull() ?: 10.0,
                            maxOral = maxOral.toDoubleOrNull() ?: 10.0,
                            maxWritten = maxWritten.toDoubleOrNull() ?: 20.0
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF))
            ) {
                Text("حفظ المتغيرات")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun FactoryResetDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
        },
        title = {
            Text(
                text = "إعادة ضبط المصنع ومسح البيانات",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFFDC2626),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "هل أنت متأكد من رغبتك في مسح كافة الكشوفات والدرجات والملفات في التطبيق وإعادة الضبط إلى الحالة الافتراضية؟ لا يمكن التراجع عن هذه العملية بعد التأكيد.",
                fontSize = 14.sp,
                color = Color(0xFF334155),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                modifier = Modifier.testTag("confirm_reset_btn")
            ) {
                Text("نعم، مسح كافة البيانات")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.testTag("cancel_reset_btn")) {
                Text("لا، إلغاء")
            }
        }
    )
}

@Composable
fun BackupRestoreDialog(
    onDismiss: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: (String) -> Unit
) {
    var importJsonText by remember { mutableStateOf("") }
    var isImportMode by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("النسخة الاحتياطية واسترجاع البيانات", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFFB45309))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isImportMode) {
                    Text(
                        text = "يمكنك إنشاء نسخة احتياطية لكافة بيانات الفصول والدرجات وحفظها على هاتفك أو مشاركتها عبر الواتساب والبريد.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )

                    Button(
                        onClick = onExportBackup,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        modifier = Modifier.fillMaxWidth().testTag("create_backup_file_btn")
                    ) {
                        Text("إنشاء ومشاركة نسخة احتياطية (JSON)")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = { isImportMode = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("استيراد نسخة احتياطية سابقة")
                    }
                } else {
                    Text(
                        text = "الصق نص النسخة الاحتياطية (JSON) هنا للاسترجاع:",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )

                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        label = { Text("بيانات النسخة الاحتياطية") },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        maxLines = 8
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (importJsonText.isNotBlank()) {
                                    onImportBackup(importJsonText)
                                }
                            },
                            enabled = importJsonText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("استعادة الآن")
                        }

                        OutlinedButton(
                            onClick = { isImportMode = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("رجوع")
                        }
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}

@Composable
fun NewMonthDialog(
    currentMonth: String,
    onDismiss: () -> Unit,
    onConfirm: (newMonth: String) -> Unit
) {
    val predefinedMonths = listOf(
        "الشهر الأول",
        "الشهر الثاني",
        "الشهر الثالث",
        "الشهر الرابع",
        "محرم",
        "صفر",
        "ربيع الأول",
        "ربيع الثاني",
        "جمادى الأولى",
        "جمادى الآخرة",
        "رجب",
        "شعبان",
        "رمضان",
        "شوال"
    )
    var selectedName by remember { mutableStateOf("الشهر الثاني") }
    var customName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إغلاق الشهر وبدء شهر جديد", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1E40AF))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "سيتم حفظ جدول $currentMonth وفتح جدول جديد بنفس قائمة الطلاب مع تصفير الدرجات للبدء بتسجيل الشهر الجديد.",
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )

                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("اسم الشهر الجديد (مثال: الشهر الثاني)") },
                    placeholder = { Text("الشهر الثاني") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_month_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (customName.isNotBlank()) customName.trim() else selectedName
                    onConfirm(finalName)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                modifier = Modifier.testTag("confirm_new_month_btn")
            ) {
                Text("فتح الشهر الجديد")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun SendStudentReportDialog(
    student: StudentGradeEntity,
    schoolInfo: SchoolInfo,
    semester: Int,
    onDismiss: () -> Unit,
    onSend: (format: String, phone: String, messageText: String) -> Unit,
    onShareGeneral: (format: String) -> Unit
) {
    val maxTotal = schoolInfo.maxTotalScore
    val total = student.totalScore
    val pctDisplay = student.percentageDisplay(maxTotal)
    val gradeSymbol = student.getGradeSymbol(maxTotal)

    var selectedFormat by remember { mutableStateOf("PDF") } // "PDF" or "EXCEL"
    var phoneNumber by remember { mutableStateOf("") }

    val defaultMotivationalMsg = remember(student, schoolInfo, semester) {
        PdfExporter.buildMotivationalMessage(schoolInfo, semester, student)
    }

    val defaultBriefMsg = remember(student, schoolInfo, semester) {
        "كشف درجات الطالب: ${student.studentName} | المجموع: $total من $maxTotal ($pctDisplay - $gradeSymbol) | مادة: ${student.subject} | مدرسة: ${schoolInfo.schoolName}"
    }

    var isMotivationalMode by remember { mutableStateOf(true) }
    var customMessage by remember(isMotivationalMode) {
        mutableStateOf(if (isMotivationalMode) defaultMotivationalMsg else defaultBriefMsg)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "إرسال كشف الطالب إلى رقم هاتف",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = student.studentName,
                        fontSize = 13.sp,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Grade Summary Chip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "المجموع: $total / $maxTotal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1E40AF)
                    )
                    Text(
                        text = "النسبة: $pctDisplay ($gradeSymbol)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF16A34A)
                    )
                }

                // 1. Choose Format (PDF / Excel)
                Text(
                    text = "١. اختر صيغة الملف المرفق:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // PDF Option
                    val isPdf = selectedFormat == "PDF"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isPdf) Color(0xFFEFF6FF) else Color.White)
                            .border(
                                1.5.dp,
                                if (isPdf) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFormat = "PDF" }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = if (isPdf) Color(0xFF2563EB) else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ملف PDF (بطاقة)",
                                fontSize = 12.sp,
                                fontWeight = if (isPdf) FontWeight.Bold else FontWeight.Normal,
                                color = if (isPdf) Color(0xFF1D4ED8) else Color(0xFF475569)
                            )
                        }
                    }

                    // Excel Option
                    val isExcel = selectedFormat == "EXCEL"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isExcel) Color(0xFFF0FDF4) else Color.White)
                            .border(
                                1.5.dp,
                                if (isExcel) Color(0xFF16A34A) else Color(0xFFCBD5E1),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFormat = "EXCEL" }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = null,
                                tint = if (isExcel) Color(0xFF16A34A) else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ملف Excel (جدول)",
                                fontSize = 12.sp,
                                fontWeight = if (isExcel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isExcel) Color(0xFF15803D) else Color(0xFF475569)
                            )
                        }
                    }
                }

                // 2. Phone Number Input
                Text(
                    text = "٢. رقم هاتف المستلم (واتساب):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = { Text("مثال: 967771234567 أو 0551234567") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF16A34A))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("send_student_phone_input")
                )

                // 3. Motivational / Encouraging Message Option
                Text(
                    text = "٣. نص الرسالة والعبارة التحفيزية:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Option 1: Motivational
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isMotivationalMode) Color(0xFFFEF3C7) else Color(0xFFF1F5F9))
                            .border(
                                1.dp,
                                if (isMotivationalMode) Color(0xFFD97706) else Color(0xFFCBD5E1),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                isMotivationalMode = true
                                customMessage = defaultMotivationalMsg
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isMotivationalMode) Color(0xFFB45309) else Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "رسالة تحفيز لولي الأمر",
                                fontSize = 11.sp,
                                fontWeight = if (isMotivationalMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (isMotivationalMode) Color(0xFF92400E) else Color(0xFF475569)
                            )
                        }
                    }

                    // Option 2: Brief
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isMotivationalMode) Color(0xFFEFF6FF) else Color(0xFFF1F5F9))
                            .border(
                                1.dp,
                                if (!isMotivationalMode) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                isMotivationalMode = false
                                customMessage = defaultBriefMsg
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "إشعار درجات رسمي",
                            fontSize = 11.sp,
                            fontWeight = if (!isMotivationalMode) FontWeight.Bold else FontWeight.Normal,
                            color = if (!isMotivationalMode) Color(0xFF1D4ED8) else Color(0xFF475569)
                        )
                    }
                }

                OutlinedTextField(
                    value = customMessage,
                    onValueChange = { customMessage = it },
                    label = { Text("معاينة وتعديل نص الرسالة المرفقة") },
                    maxLines = 6,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("send_student_message_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSend(selectedFormat, phoneNumber, customMessage)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_send_whatsapp_student")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إرسال عبر واتساب 📲", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { onShareGeneral(selectedFormat) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة عامة")
                }
                OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                    Text("إلغاء")
                }
            }
        }
    )
}

@Composable
fun SendClassSheetDialog(
    schoolInfo: SchoolInfo,
    semester: Int,
    month: String,
    section: String,
    subject: String,
    studentsCount: Int,
    onDismiss: () -> Unit,
    onSend: (format: String, phone: String, messageText: String) -> Unit,
    onShareGeneral: (format: String) -> Unit
) {
    var selectedFormat by remember { mutableStateOf("PDF") } // "PDF" or "EXCEL"
    var phoneNumber by remember { mutableStateOf("") }
    var recipientRole by remember { mutableStateOf("مدير المدرسة") }

    val rolesList = listOf("مدير المدرسة", "وكيل المدرسة", "الموجه التربوي", "مسؤول الطباعة", "معلم المادة")

    val defaultMessage = remember(schoolInfo, semester, month, section, subject, studentsCount, recipientRole) {
        PdfExporter.buildOfficialClassMessage(
            schoolInfo = schoolInfo,
            semester = semester,
            month = month,
            section = section,
            subject = subject,
            studentsCount = studentsCount,
            recipientRole = recipientRole
        )
    }

    var customMessage by remember(recipientRole) {
        mutableStateOf(defaultMessage)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color(0xFF1E40AF),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "إرسال كشف الفصل الكامل",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "$subject | الصف: ${schoolInfo.gradeLevels} (شعبة $section) | $month",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Recipient Role Selector
                Text(
                    text = "١. لمن تريد إرسال الكشف؟",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rolesList.forEach { role ->
                        val isSelected = role == recipientRole
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF1E40AF) else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF1E40AF) else Color(0xFFCBD5E1),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    recipientRole = role
                                    customMessage = PdfExporter.buildOfficialClassMessage(
                                        schoolInfo = schoolInfo,
                                        semester = semester,
                                        month = month,
                                        section = section,
                                        subject = subject,
                                        studentsCount = studentsCount,
                                        recipientRole = role
                                    )
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = role,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }

                // 2. Choose Format (PDF / Excel)
                Text(
                    text = "٢. صيغة الملف المرفق:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isPdf = selectedFormat == "PDF"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isPdf) Color(0xFFEFF6FF) else Color.White)
                            .border(
                                1.5.dp,
                                if (isPdf) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFormat = "PDF" }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = if (isPdf) Color(0xFF2563EB) else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ملف PDF (للطباعة)",
                                fontSize = 12.sp,
                                fontWeight = if (isPdf) FontWeight.Bold else FontWeight.Normal,
                                color = if (isPdf) Color(0xFF1D4ED8) else Color(0xFF475569)
                            )
                        }
                    }

                    val isExcel = selectedFormat == "EXCEL"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isExcel) Color(0xFFF0FDF4) else Color.White)
                            .border(
                                1.5.dp,
                                if (isExcel) Color(0xFF16A34A) else Color(0xFFCBD5E1),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedFormat = "EXCEL" }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = null,
                                tint = if (isExcel) Color(0xFF16A34A) else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ملف Excel (جدول)",
                                fontSize = 12.sp,
                                fontWeight = if (isExcel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isExcel) Color(0xFF15803D) else Color(0xFF475569)
                            )
                        }
                    }
                }

                // 3. Phone Number Input
                Text(
                    text = "٣. رقم هاتف المستلم (واتساب):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = { Text("أدخل رقم هاتف $recipientRole...") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF16A34A))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("send_class_phone_input")
                )

                // 4. Message Preview
                Text(
                    text = "٤. نص الرسالة المرفقة مع الكشف:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
                OutlinedTextField(
                    value = customMessage,
                    onValueChange = { customMessage = it },
                    label = { Text("نص الخطاب الرسمي المرفق") },
                    maxLines = 6,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("send_class_message_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSend(selectedFormat, phoneNumber, customMessage)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_send_whatsapp_class")
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إرسال الكشف لواتساب 📲", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = { onShareGeneral(selectedFormat) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة عامة")
                }
                OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                    Text("إلغاء")
                }
            }
        }
    )
}

@Composable
fun ExportByGradeAndSubjectDialog(
    exportFormat: String, // "PDF" or "EXCEL"
    schoolInfo: SchoolInfo,
    allDistinctSubjects: List<String>,
    allStudents: List<StudentGradeEntity>,
    onDismiss: () -> Unit,
    onExport: (format: String, gradeLevel: String, subject: String, semester: Int, month: String, section: String) -> Unit,
    onNavigateToClass: (gradeLevel: String, semester: Int, subject: String) -> Unit
) {
    var selectedFormat by remember { mutableStateOf(exportFormat) }
    var selectedGrade by remember { mutableStateOf(schoolInfo.gradeLevels) }
    var selectedSubject by remember { mutableStateOf(schoolInfo.defaultSubject) }
    var selectedSemester by remember { mutableStateOf(1) }
    var selectedMonth by remember { mutableStateOf("الشهر الأول") }
    var selectedSection by remember { mutableStateOf("أ") }
    var customSubjectInput by remember { mutableStateOf("") }

    val gradeLevelsList = listOf(
        "الصف الأول الابتدائي", "الصف الثاني الابتدائي", "الصف الثالث الابتدائي",
        "الصف الرابع الابتدائي", "الصف الخامس الابتدائي", "الصف السادس الابتدائي",
        "الصف الأول الإعدادي", "الصف الثاني الإعدادي", "الصف الثالث الإعدادي",
        "الصف الأول الثانوي", "الصف الثاني الثانوي", "الصف الثالث الثانوي"
    )

    val defaultSubjects = listOf(
        "أحياء", "رياضيات", "لغة عربية", "فيزياء", "كيمياء",
        "قرآن كريم", "تربية إسلامية", "لغة إنجليزية", "علوم", "اجتماعيات", "حاسوب"
    )

    val mergedSubjects = remember(allDistinctSubjects) {
        (defaultSubjects + allDistinctSubjects).distinct()
    }

    // Matching students from database
    val matchingStudents = remember(allStudents, selectedSemester, selectedSubject, selectedMonth, selectedSection) {
        allStudents.filter {
            it.semester == selectedSemester &&
            it.subject == selectedSubject &&
            it.month == selectedMonth &&
            it.section == selectedSection
        }.sortedWith(compareBy({ it.studentOrder }, { it.id }))
    }

    // Roster count of students in this semester and section across all records
    val rosterCount = remember(allStudents, selectedSemester, selectedSection) {
        allStudents.filter {
            it.semester == selectedSemester && it.section == selectedSection
        }.map { it.studentName }.distinct().size
    }

    val isExcel = selectedFormat.equals("EXCEL", ignoreCase = true)
    val themeColor = if (isExcel) Color(0xFF15803D) else Color(0xFF1E40AF)
    val headerBgColor = if (isExcel) Color(0xFFDCFCE7) else Color(0xFFEFF6FF)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isExcel) Icons.Default.TableChart else Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isExcel) "تصدير كشف Excel" else "تصدير كشف PDF",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = themeColor
                        )
                    }

                    // Format toggle chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isExcel) Color(0xFF1E40AF) else Color(0xFFF1F5F9))
                                .clickable { selectedFormat = "PDF" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PDF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isExcel) Color.White else Color(0xFF475569)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isExcel) Color(0xFF15803D) else Color(0xFFF1F5F9))
                                .clickable { selectedFormat = "EXCEL" }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Excel",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExcel) Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "حدد المرحلة الدراسية والمادة لتصدير كشف درجات الطلاب مباشرة",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Stage / Grade Level Selection
                Text(
                    text = "١. المرحلة الدراسية / الصف:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    gradeLevelsList.forEach { grade ->
                        val isSelected = grade == selectedGrade
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) themeColor else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) themeColor else Color(0xFFCBD5E1),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedGrade = grade }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = grade,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }

                // 2. Subject Selection
                Text(
                    text = "٢. المادة الدراسية:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    mergedSubjects.forEach { subj ->
                        val isSelected = subj == selectedSubject
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) themeColor else Color(0xFFF1F5F9))
                                .border(
                                    1.dp,
                                    if (isSelected) themeColor else Color(0xFFCBD5E1),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedSubject = subj
                                    customSubjectInput = ""
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = subj,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }

                // Custom Subject Input Option
                OutlinedTextField(
                    value = customSubjectInput,
                    onValueChange = {
                        customSubjectInput = it
                        if (it.isNotBlank()) selectedSubject = it.trim()
                    },
                    placeholder = { Text("أو اكتب اسم مادة أخرى مخصصة...") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("export_custom_subject_input")
                )

                // 3. Semester & Month & Section Row
                Text(
                    text = "٣. الفصل والشهر والشعبة:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Semester Selector
                    listOf(1 to "الفصل 1", 2 to "الفصل 2").forEach { (semId, label) ->
                        val isSelected = selectedSemester == semId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) themeColor else Color(0xFFF8FAFC))
                                .border(1.dp, if (isSelected) themeColor else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .clickable { selectedSemester = semId }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }

                    // Section Selector
                    listOf("أ", "ب", "ج").forEach { sec ->
                        val isSelected = selectedSection == sec
                        Box(
                            modifier = Modifier
                                .weight(0.7f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) themeColor else Color(0xFFF8FAFC))
                                .border(1.dp, if (isSelected) themeColor else Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .clickable { selectedSection = sec }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "شعبة $sec",
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }

                // Months row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("الشهر الأول", "الشهر الثاني", "الشهر الثالث", "الشهر الرابع").forEach { m ->
                        val isSelected = selectedMonth == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color(0xFF334155) else Color(0xFFF1F5F9))
                                .clickable { selectedMonth = m }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = m,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }

                // 4. Status and Students Count Summary Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(headerBgColor)
                        .border(1.dp, if (isExcel) Color(0xFF86EFAC) else Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "كشف: $selectedGrade",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColor
                            )
                            Text(
                                text = "مادة: $selectedSubject",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColor
                            )
                        }

                        if (matchingStudents.isNotEmpty()) {
                            Text(
                                text = "✅ تم العثور على (${matchingStudents.size}) طالب برصيد درجاتهم في هذا الكشف",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                            // Mini preview of names
                            val namesPreview = matchingStudents.take(4).joinToString("، ") { it.studentName } +
                                    if (matchingStudents.size > 4) " ...وغيرهم" else ""
                            Text(
                                text = "الطلاب: $namesPreview",
                                fontSize = 10.5.sp,
                                color = Color(0xFF475569),
                                maxLines = 1
                            )
                        } else if (rosterCount > 0) {
                            Text(
                                text = "📋 سيتم إدراج أسماء طلاب الشعبة ($rosterCount طالب) في كشف مادة $selectedSubject",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB45309)
                            )
                        } else {
                            Text(
                                text = "ℹ️ لا توجد أسماء مسجلة بعد لهذا الفصل والشعبة، انقر على زر (فتح جدول الرصد) لإدخالهم",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onExport(
                        selectedFormat,
                        selectedGrade,
                        selectedSubject,
                        selectedSemester,
                        selectedMonth,
                        selectedSection
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("btn_confirm_export_grade_subject")
            ) {
                Icon(
                    imageVector = if (isExcel) Icons.Default.TableChart else Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("تصدير كشف $selectedFormat 📥", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedButton(
                    onClick = {
                        onNavigateToClass(selectedGrade, selectedSemester, selectedSubject)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("فتح جدول الرصد", fontSize = 11.5.sp)
                }

                OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                    Text("إلغاء", fontSize = 11.5.sp)
                }
            }
        }
    )
}
