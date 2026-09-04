package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentGradeEntity
import com.example.ui.components.DottedInfoField
import com.example.ui.components.GoldBorderCard
import com.example.ui.components.GoldBorderDark
import com.example.ui.components.GoldBorderLight
import com.example.ui.components.GradeTable
import com.example.ui.components.MetalBgBottom
import com.example.ui.components.MetalBgCenter
import com.example.ui.components.MetalBgTop
import com.example.ui.components.NewMonthDialog
import com.example.viewmodel.GradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterGradeScreen(
    semester: Int,
    viewModel: GradeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schoolInfo by viewModel.schoolInfo.collectAsState()
    val students by viewModel.currentStudents.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedSection by viewModel.selectedSection.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val allDistinctSubjects by viewModel.allDistinctSubjects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showNewMonthDialog by remember { mutableStateOf(false) }
    var showSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectInput by remember { mutableStateOf("") }
    var studentToDelete by remember { mutableStateOf<StudentGradeEntity?>(null) }

    // Filter students by search query if present
    val filteredStudents = remember(students, searchQuery) {
        if (searchQuery.isBlank()) {
            students
        } else {
            students.filter { it.studentName.contains(searchQuery.trim(), ignoreCase = true) }
        }
    }

    val semesterTitle = if (semester == 1) "الفصل الأول" else "الفصل الثاني"
    val themeColor = if (semester == 1) Color(0xFF1E40AF) else Color(0xFF15803D)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "كشف درجات $semesterTitle - ${schoolInfo.gradeLevels}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${schoolInfo.schoolName} | مادة: $selectedSubject",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateBackFromSemesterGrade(semester) },
                            modifier = Modifier.testTag("back_to_classes_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع لاختيار الصف",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        // Export PDF
                        IconButton(
                            onClick = { viewModel.exportClassPdf(context) },
                            modifier = Modifier.testTag("top_export_pdf_btn")
                        ) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = "تصدير كشف PDF",
                                tint = Color.White
                            )
                        }
                        // Export Excel
                        IconButton(
                            onClick = { viewModel.exportClassExcel(context) },
                            modifier = Modifier.testTag("top_export_excel_btn")
                        ) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = "تصدير كشف Excel",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = themeColor)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openAddStudentDialog(null) },
                    containerColor = themeColor,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_student")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة طالب", fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            listOf(MetalBgTop, MetalBgCenter, MetalBgBottom)
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Summary Card (Two Spacious Rows so nothing is cut off)
                GoldBorderCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Row 1: Grade Level & Subject
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DottedInfoField(
                            prefix = "الصف:",
                            value = schoolInfo.gradeLevels,
                            onClick = { viewModel.navigateBackFromSemesterGrade(semester) },
                            modifier = Modifier.weight(1.1f).testTag("filter_grade_level_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        DottedInfoField(
                            prefix = "المادة:",
                            value = selectedSubject,
                            onClick = { showSubjectDialog = true },
                            modifier = Modifier.weight(1f).testTag("filter_subject_field")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 2: Month & Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DottedInfoField(
                            prefix = "الشهر:",
                            value = selectedMonth,
                            onClick = { showNewMonthDialog = true },
                            modifier = Modifier.weight(1.1f).testTag("filter_month_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        DottedInfoField(
                            prefix = "الشعبة:",
                            value = selectedSection,
                            onClick = { viewModel.openEditSchoolDialog() },
                            modifier = Modifier.weight(1f).testTag("filter_section_field")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Month Switching / Closing & Edit Cliché bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Change / Choose Subject button
                        OutlinedButton(
                            onClick = { showSubjectDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("btn_change_subject_quick")
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تغيير المادة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Close Month & Open New Month button
                        Button(
                            onClick = { showNewMonthDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.3f).testTag("btn_close_month_new")
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إغلاق وبدء شهر جديد", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        // Edit Info
                        OutlinedButton(
                            onClick = { viewModel.openEditSchoolDialog() },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("الكليشة", fontSize = 12.sp)
                        }
                    }
                }

                // 1. Subject Selector Bar (شريط اختيار المادة لفصل درجات المواد تماماً)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "المادة الحالية:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = selectedSubject,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E40AF)
                                )
                            }
                        }

                        Text(
                            text = "+ مادة أخرى",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB),
                            modifier = Modifier
                                .clickable { showSubjectDialog = true }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allDistinctSubjects.forEach { subj ->
                            val isSelected = subj == selectedSubject
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) Color(0xFF1E40AF) else Color(0xFFF1F5F9))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF1E40AF) else Color(0xFFCBD5E1),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.setSubjectAndSave(subj) }
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = subj,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                // 2. Month & Section Tabs (Quick Switcher)
                val availableMonths = listOf("الشهر الأول", "الشهر الثاني", "الشهر الثالث", "الشهر الرابع")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableMonths.forEach { m ->
                        val isSelected = m == selectedMonth
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) themeColor else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) themeColor else Color(0xFFCBD5E1),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setSelectedMonth(m) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = m,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }

                    // Sections Tabs (أ, ب, ج, د)
                    listOf("أ", "ب", "ج", "د").forEach { sec ->
                        val isSecSelected = sec == selectedSection
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSecSelected) Color(0xFF475569) else Color.White)
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                                .clickable { viewModel.setSelectedSection(sec) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "شعبة $sec",
                                fontSize = 13.sp,
                                fontWeight = if (isSecSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSecSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("بحث عن اسم طالب في مادة ($selectedSubject)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "مسح البحث")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_search_input")
                )

                // If no students in this subject, show a helper to copy roster from another subject
                if (students.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "كشف مادة: $selectedSubject فارغ لهذا الشهر",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "يمكنك إضافة الطلاب يدوياً أو استيراد أسماء طلاب الشعبة ($selectedSection) دفعة واحدة",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.openAddStudentDialog(null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إضافة طالب", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.copyRosterFromPrevious() },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("استيراد أسماء الطلاب", fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }

                // Grade Table
                GradeTable(
                    students = filteredStudents,
                    schoolInfo = schoolInfo,
                    onStudentClick = { student ->
                        viewModel.openAddStudentDialog(student)
                    },
                    onSendStudentReport = { student ->
                        viewModel.openSendStudentDialog(student)
                    },
                    onSaveStudentPdf = { student ->
                        viewModel.exportSingleStudentPdf(context, student)
                    },
                    onSaveStudentExcel = { student ->
                        viewModel.exportSingleStudentExcel(context, student)
                    },
                    onClearScores = { student ->
                        viewModel.clearStudentScores(student)
                    },
                    onDeleteStudent = { student ->
                        studentToDelete = student
                    }
                )

                // Statistics Summary Card
                val maxTotal = schoolInfo.maxTotalScore
                val totalStudents = filteredStudents.size
                val avgScore = if (totalStudents > 0) {
                    val sum = filteredStudents.sumOf { it.totalScore }
                    Math.round((sum / totalStudents) * 100.0) / 100.0
                } else 0.0
                val topScore = filteredStudents.maxOfOrNull { it.totalScore } ?: 0.0
                val passingCount = filteredStudents.count {
                    val pct = it.calculatePercentage(maxTotal)
                    pct >= 50.0
                }
                val passRate = if (totalStudents > 0) {
                    Math.round(((passingCount.toDouble() / totalStudents) * 100.0) * 100.0) / 100.0
                } else 0.0
                val passRateDisplay = String.format(java.util.Locale.US, "%.2f%%", passRate)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "إحصائيات الكشف الشهري",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(label = "إجمالي الطلاب", value = "$totalStudents")
                            StatItem(label = "المتوسط العام", value = "$avgScore / $maxTotal")
                            StatItem(label = "أعلى مجموع", value = "$topScore")
                            StatItem(label = "نسبة النجاح", value = passRateDisplay)
                        }
                    }
                }

                // WhatsApp Send Class Button (Full feature for المدير / المعلمين / الطباعة)
                Button(
                    onClick = { viewModel.openSendClassDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_class_whatsapp_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إرسال الكشف إلى رقم هاتف (واتساب للمدير / المعلم / الطباعة)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Bottom Export Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.exportClassPdf(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("bottom_pdf_export_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ الكشف PDF", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.exportClassExcel(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("bottom_excel_export_btn")
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ الكشف Excel", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showNewMonthDialog) {
        NewMonthDialog(
            currentMonth = selectedMonth,
            onDismiss = { showNewMonthDialog = false },
            onConfirm = { newMonthName ->
                viewModel.closeMonthAndStartNew(newMonthName)
            }
        )
    }

    if (showSubjectDialog) {
        var customSubject by remember { mutableStateOf("") }
        val quickSubjects = listOf(
            "لغة عربية", "رياضيات", "أحياء", "فيزياء", "كيمياء",
            "قرآن كريم", "تربية إسلامية", "لغة إنجليزية", "علوم", "اجتماعيات", "حاسوب"
        )

        AlertDialog(
            onDismissRequest = { showSubjectDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF1E40AF),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تحديد / اختيار المادة", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "المادة الحالية: $selectedSubject",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )

                    Text(
                        text = "اختر مادة سريعة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )

                    // Quick subject chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickSubjects.forEach { s ->
                            val isCurrent = s == selectedSubject
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isCurrent) Color(0xFF1E40AF) else Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.setSubjectAndSave(s)
                                        showSubjectDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = s,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else Color(0xFF1E40AF)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "أو اكتب اسم مادة مخصصة:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )

                    OutlinedTextField(
                        value = customSubject,
                        onValueChange = { customSubject = it },
                        placeholder = { Text("مثال: لغة عربية، تلاوة وتجويد...") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("custom_subject_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customSubject.isNotBlank()) {
                            viewModel.setSubjectAndSave(customSubject.trim())
                        }
                        showSubjectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("تعيين المادة")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showSubjectDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (studentToDelete != null) {
        val target = studentToDelete!!
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "حذف الطالب نهائياً",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من حذف الطالب (${target.studentName}) نهائياً؟\nسيتم حذف الاسم بالكامل مع كافة درجاته من ${schoolInfo.gradeLevels}.",
                    fontSize = 13.5.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(target)
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_delete_student_btn")
                ) {
                    Text("نعم، حذف نهائي")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { studentToDelete = null },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("cancel_delete_student_btn")
                ) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
    }
}
