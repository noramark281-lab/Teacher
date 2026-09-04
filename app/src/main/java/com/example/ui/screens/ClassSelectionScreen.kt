package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentSemesterOutcome
import com.example.data.model.formatPrecise
import com.example.data.model.formatScore
import com.example.ui.components.GoldBorderCard
import com.example.ui.components.MetalBgBottom
import com.example.ui.components.MetalBgCenter
import com.example.ui.components.MetalBgTop
import com.example.viewmodel.GradeViewModel

data class GradeLevelItem(
    val id: String,
    val title: String,
    val stage: String,
    val stageCategory: String, // primary, prep, secondary
    val orderNumber: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val borderColor: Color
)

val GRADE_LEVELS_LIST = listOf(
    // 1. المرحلة الابتدائية (1 - 6)
    GradeLevelItem(
        id = "prim_1",
        title = "الصف الأول الابتدائي",
        stage = "المرحلة الابتدائية",
        stageCategory = "primary",
        orderNumber = "١",
        icon = Icons.Default.AutoStories,
        primaryColor = Color(0xFF1E40AF),
        secondaryColor = Color(0xFF3B82F6),
        borderColor = Color(0xFF93C5FD)
    ),
    GradeLevelItem(
        id = "prim_2",
        title = "الصف الثاني الابتدائي",
        stage = "المرحلة الابتدائية",
        stageCategory = "primary",
        orderNumber = "٢",
        icon = Icons.Default.AutoStories,
        primaryColor = Color(0xFF1E40AF),
        secondaryColor = Color(0xFF3B82F6),
        borderColor = Color(0xFF93C5FD)
    ),
    GradeLevelItem(
        id = "prim_3",
        title = "الصف الثالث الابتدائي",
        stage = "المرحلة الابتدائية",
        stageCategory = "primary",
        orderNumber = "٣",
        icon = Icons.Default.AutoStories,
        primaryColor = Color(0xFF1E40AF),
        secondaryColor = Color(0xFF3B82F6),
        borderColor = Color(0xFF93C5FD)
    ),
    GradeLevelItem(
        id = "prim_4",
        title = "الصف الرابع الابتدائي",
        stage = "المرحلة الابتدائية",
        stageCategory = "primary",
        orderNumber = "٤",
        icon = Icons.Default.AutoStories,
        primaryColor = Color(0xFF1E40AF),
        secondaryColor = Color(0xFF3B82F6),
        borderColor = Color(0xFF93C5FD)
    ),
    GradeLevelItem(
        id = "prim_5",
        title = "الصف الخامس الابتدائي",
        stage = "المرحلة الابتدائية",
        stageCategory = "primary",
        orderNumber = "٥",
        icon = Icons.Default.AutoStories,
        primaryColor = Color(0xFF1E40AF),
        secondaryColor = Color(0xFF3B82F6),
        borderColor = Color(0xFF93C5FD)
    ),
    GradeLevelItem(
        id = "prim_6",
        title = "الصف السادس الابتدائي",
        stage = "المرحلة الابتدائية",
        stageCategory = "primary",
        orderNumber = "٦",
        icon = Icons.Default.AutoStories,
        primaryColor = Color(0xFF1E40AF),
        secondaryColor = Color(0xFF3B82F6),
        borderColor = Color(0xFF93C5FD)
    ),

    // 2. المرحلة الإعدادية (1 - 3)
    GradeLevelItem(
        id = "prep_1",
        title = "الصف الأول الإعدادي",
        stage = "المرحلة الإعدادية",
        stageCategory = "prep",
        orderNumber = "٧",
        icon = Icons.Default.Class,
        primaryColor = Color(0xFF0D9488),
        secondaryColor = Color(0xFF14B8A6),
        borderColor = Color(0xFF99F6E4)
    ),
    GradeLevelItem(
        id = "prep_2",
        title = "الصف الثاني الإعدادي",
        stage = "المرحلة الإعدادية",
        stageCategory = "prep",
        orderNumber = "٨",
        icon = Icons.Default.Class,
        primaryColor = Color(0xFF0D9488),
        secondaryColor = Color(0xFF14B8A6),
        borderColor = Color(0xFF99F6E4)
    ),
    GradeLevelItem(
        id = "prep_3",
        title = "الصف الثالث الإعدادي",
        stage = "المرحلة الإعدادية",
        stageCategory = "prep",
        orderNumber = "٩",
        icon = Icons.Default.Class,
        primaryColor = Color(0xFF0D9488),
        secondaryColor = Color(0xFF14B8A6),
        borderColor = Color(0xFF99F6E4)
    ),

    // 3. المرحلة الثانوية (1 - 3)
    GradeLevelItem(
        id = "sec_1",
        title = "الصف الأول الثانوي",
        stage = "المرحلة الثانوية",
        stageCategory = "secondary",
        orderNumber = "١٠",
        icon = Icons.Default.School,
        primaryColor = Color(0xFF6D28D9),
        secondaryColor = Color(0xFF8B5CF6),
        borderColor = Color(0xFFDDD6FE)
    ),
    GradeLevelItem(
        id = "sec_2",
        title = "الصف الثاني الثانوي",
        stage = "المرحلة الثانوية",
        stageCategory = "secondary",
        orderNumber = "١١",
        icon = Icons.Default.School,
        primaryColor = Color(0xFF6D28D9),
        secondaryColor = Color(0xFF8B5CF6),
        borderColor = Color(0xFFDDD6FE)
    ),
    GradeLevelItem(
        id = "sec_3",
        title = "الصف الثالث الثانوي",
        stage = "المرحلة الثانوية",
        stageCategory = "secondary",
        orderNumber = "١٢",
        icon = Icons.Default.WorkspacePremium,
        primaryColor = Color(0xFF6D28D9),
        secondaryColor = Color(0xFF8B5CF6),
        borderColor = Color(0xFFDDD6FE)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSelectionScreen(
    semester: Int,
    viewModel: GradeViewModel,
    modifier: Modifier = Modifier
) {
    val schoolInfo by viewModel.schoolInfo.collectAsState()
    val semesterTitle = if (semester == 1) "الفصل الأول" else "الفصل الثاني"
    val themeColorTop = if (semester == 1) Color(0xFF1E3A8A) else Color(0xFF14532D)

    val allStudents by viewModel.allDatabaseStudents.collectAsState()
    val searchQuery by viewModel.semesterSearchQuery.collectAsState()
    var searchClassFilter by remember { mutableStateOf<String?>(null) }

    val semesterOutcomes: List<StudentSemesterOutcome> = remember(allStudents, semester) {
        viewModel.getSemesterOutcomes(allGrades = allStudents, semester = semester)
    }

    val filteredOutcomes: List<StudentSemesterOutcome> = remember(semesterOutcomes, searchQuery, searchClassFilter) {
        if (searchQuery.isBlank()) {
            emptyList<StudentSemesterOutcome>()
        } else {
            semesterOutcomes.filter {
                it.studentName.contains(searchQuery.trim(), ignoreCase = true) &&
                (searchClassFilter == null || it.gradeLevel == searchClassFilter)
            }
        }
    }

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
                                text = "اختيار الصف الدراسي - $semesterTitle",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${schoolInfo.schoolName} | كشف درجات أعمال السنة",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateTo(0) },
                            modifier = Modifier.testTag("btn_back_to_home_from_classes")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع للرئيسية",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = themeColorTop
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MetalBgTop, MetalBgCenter, MetalBgBottom)
                        )
                    )
                    .drawBehind {
                        val strokeColor = Color(0x18000000)
                        for (y in 0 until size.height.toInt() step 8) {
                            drawLine(
                                color = strokeColor,
                                start = Offset(0f, y.toFloat()),
                                end = Offset(size.width, y.toFloat()),
                                strokeWidth = 0.8f
                            )
                        }
                    }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Search Bar for Student Outcome in this Semester
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(3.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSemesterSearchQuery(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp)
                                    .testTag("input_search_student_semester_outcome"),
                                placeholder = {
                                    Text(
                                        text = "ابحث باسم الطالب للحصول على محصلته (مجموع 3 أشهر ÷ 3)...",
                                        fontSize = 13.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "بحث",
                                        tint = themeColorTop
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.setSemesterSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "مسح",
                                                tint = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = themeColorTop,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                )
                            )

                            // Quick filter by specific class
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                item {
                                    FilterChip(
                                        selected = searchClassFilter == null,
                                        onClick = { searchClassFilter = null },
                                        label = { Text("جميع الصفوف", fontSize = 11.5.sp) }
                                    )
                                }
                                items(GRADE_LEVELS_LIST) { item ->
                                    FilterChip(
                                        selected = searchClassFilter == item.title,
                                        onClick = {
                                            searchClassFilter = if (searchClassFilter == item.title) null else item.title
                                        },
                                        label = { Text(item.title, fontSize = 11.5.sp) }
                                    )
                                }
                            }
                        }
                    }

                    // 2. Search Results if Query entered
                    if (searchQuery.isNotBlank()) {
                        val filterLabel = if (searchClassFilter != null) "في $searchClassFilter" else "في $semesterTitle"
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "نتائج البحث عن محصلة الطالب $filterLabel (${filteredOutcomes.size}):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        if (filteredOutcomes.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "لم يتم العثور على طالب بهذا الاسم $filterLabel",
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        } else {
                            items(
                                items = filteredOutcomes,
                                key = { "search_${it.gradeLevel}_${it.studentName}_${it.subject}_${it.section}" },
                                span = { GridItemSpan(maxLineSpan) }
                            ) { res ->
                                SemesterStudentOutcomeResultCard(
                                    outcome = res,
                                    themeColor = themeColorTop
                                )
                            }
                        }
                    }

                    // 3. Header Banner
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        GoldBorderCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 600.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "اختر الصف الدراسي لرصد وإدخال الدرجات",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "انقر على أي صف للانتقال مباشرة لجدول رصد أعمال السنة لـ $semesterTitle",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF475569),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // 12 Class Boxes
                    items(GRADE_LEVELS_LIST, key = { it.id }) { item ->
                        ClassLevelCard(
                            item = item,
                            onClick = {
                                viewModel.selectGradeLevelAndOpenSemester(
                                    gradeLevel = item.title,
                                    semester = semester
                                )
                            }
                        )
                    }

                    // Bottom info note
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xCCF1F5F9))
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "برمجة الدكتور / مالك الرميمة 🦷 هاتف: 771134103",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SemesterStudentOutcomeResultCard(
    outcome: StudentSemesterOutcome,
    themeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .border(1.2.dp, themeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(themeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = outcome.studentName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${outcome.gradeLevel} | الشعبة: ${outcome.section} | المادة: ${outcome.subject}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Outcome divided by 3 Box next to the name
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColor)
                        .border(1.dp, Color(0xFFFDE047), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "المحصلة (÷ 3)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFEF08A)
                        )
                        Text(
                            text = outcome.semesterOutcomeDisplay(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3 Months Mini Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MonthResultItem(
                    monthTitle = "الشهر 1",
                    total = outcome.month1.totalScore,
                    outcome = outcome.month1.outcome,
                    has = outcome.month1.hasRecord,
                    modifier = Modifier.weight(1f)
                )
                MonthResultItem(
                    monthTitle = "الشهر 2",
                    total = outcome.month2.totalScore,
                    outcome = outcome.month2.outcome,
                    has = outcome.month2.hasRecord,
                    modifier = Modifier.weight(1f)
                )
                MonthResultItem(
                    monthTitle = "الشهر 3",
                    total = outcome.month3.totalScore,
                    outcome = outcome.month3.outcome,
                    has = outcome.month3.hasRecord,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MonthResultItem(
    monthTitle: String,
    total: Double,
    outcome: Double,
    has: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = monthTitle, fontSize = 9.5.sp, color = Color(0xFF64748B))
            if (has) {
                Text(
                    text = "درجة: ${total.formatScore()}",
                    fontSize = 10.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "محصلة: ${outcome.formatScore()}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )
            } else {
                Text(text = "-", fontSize = 10.sp, color = Color(0xFFCBD5E1))
            }
        }
    }
}

@Composable
fun ClassLevelCard(
    item: GradeLevelItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("card_class_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, item.borderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle Stage color top band
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(item.primaryColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: Stage Tag & Number badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stage Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(item.primaryColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.stage,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = item.primaryColor
                        )
                    }

                    // Order badge circle
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(item.primaryColor, item.secondaryColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.orderNumber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Middle: Class Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(item.primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = item.title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 17.sp,
                        maxLines = 2
                    )
                }

                // Bottom row: Enter prompt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رصد الدرجات",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = item.primaryColor
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = item.primaryColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
