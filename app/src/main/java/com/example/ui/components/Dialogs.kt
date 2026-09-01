package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
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
    onSave: (id: Long, name: String, attendance: Double, homework: Double, oral: Double, written: Double) -> Unit
) {
    var name by remember { mutableStateOf(student?.studentName ?: "") }
    var attendanceText by remember { mutableStateOf(student?.attendance?.toString() ?: "") }
    var homeworkText by remember { mutableStateOf(student?.homework?.toString() ?: "") }
    var oralText by remember { mutableStateOf(student?.oral?.toString() ?: "") }
    var writtenText by remember { mutableStateOf(student?.written?.toString() ?: "") }

    val att = attendanceText.toDoubleOrNull() ?: 0.0
    val hw = homeworkText.toDoubleOrNull() ?: 0.0
    val or = oralText.toDoubleOrNull() ?: 0.0
    val wr = writtenText.toDoubleOrNull() ?: 0.0

    val total = att + hw + or + wr
    val max = schoolInfo.maxTotalScore
    val pct = if (max > 0) ((total / max) * 1000).toInt() / 10.0 else 0.0

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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الطالب الكامل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = attendanceText,
                        onValueChange = { attendanceText = it },
                        label = { Text("المواظبة (${schoolInfo.maxAttendance})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("att_input")
                    )
                    OutlinedTextField(
                        value = homeworkText,
                        onValueChange = { homeworkText = it },
                        label = { Text("الواجبات (${schoolInfo.maxHomework})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("hw_input")
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = oralText,
                        onValueChange = { oralText = it },
                        label = { Text("الشفوي (${schoolInfo.maxOral})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("oral_input")
                    )
                    OutlinedTextField(
                        value = writtenText,
                        onValueChange = { writtenText = it },
                        label = { Text("التحريري (${schoolInfo.maxWritten})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("written_input")
                    )
                }

                // Dynamic calculation summary banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المجموع: $total / $max",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "النسبة: $pct%",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857),
                            fontSize = 14.sp
                        )
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
                            wr
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
