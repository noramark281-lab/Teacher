package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthGradeSummary
import com.example.data.model.StudentFinalOutcome
import com.example.data.model.StudentSemesterOutcome
import com.example.data.model.formatPrecise
import com.example.data.model.formatScore
import com.example.ui.components.GoldBorderCard
import com.example.ui.components.GoldBorderDark
import com.example.ui.components.GoldBorderLight
import com.example.ui.components.MetalBgBottom
import com.example.ui.components.MetalBgCenter
import com.example.ui.components.MetalBgTop
import com.example.viewmodel.GradeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalOutcomeScreen(
    viewModel: GradeViewModel,
    modifier: Modifier = Modifier
) {
    val schoolInfo by viewModel.schoolInfo.collectAsState()
    val allStudents by viewModel.allDatabaseStudents.collectAsState()
    val searchQuery by viewModel.finalOutcomeSearchQuery.collectAsState()
    val subjectFilter by viewModel.finalOutcomeSubjectFilter.collectAsState()
    val focusManager = LocalFocusManager.current

    val finalOutcomes: List<StudentFinalOutcome> = remember(allStudents) {
        viewModel.getFinalOutcomes(allStudents)
    }

    val availableSubjects = remember(finalOutcomes) {
        finalOutcomes.map { it.subject }.filter { it.isNotBlank() }.distinct()
    }

    val filteredOutcomes: List<StudentFinalOutcome> = remember(finalOutcomes, searchQuery, subjectFilter) {
        var list = finalOutcomes
        if (!subjectFilter.isNullOrBlank()) {
            list = list.filter { it.subject == subjectFilter }
        }
        if (searchQuery.isNotBlank()) {
            list = list.filter { it.studentName.contains(searchQuery.trim(), ignoreCase = true) }
        }
        list
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
                                text = "المحصلة النهائية للعام الدراسي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${schoolInfo.schoolName} | مجموع محصلة الفصل الأول + الفصل الثاني",
                                fontSize = 11.5.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateTo(0) },
                            modifier = Modifier.testTag("btn_back_to_home_from_final")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع للرئيسية",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E3A8A)
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Formula Explanation Card
                    item {
                        GoldBorderCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 600.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = Color(0xFF1E3A8A),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "معادلة احتساب المحصلة النهائية",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEFF6FF))
                                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("محصلة الشهر", fontSize = 11.sp, color = Color(0xFF475569))
                                        Text("مجموع الدرجات ÷ 5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                                    }
                                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("محصلة الفصل", fontSize = 11.sp, color = Color(0xFF475569))
                                        Text("مجموع 3 أشهر ÷ 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    }
                                    Text("=", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("المحصلة النهائية", fontSize = 11.sp, color = Color(0xFF475569))
                                        Text("فصل 1 + فصل 2", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                                    }
                                }
                            }
                        }
                    }

                    // 2. Search Input Box & Subject Filter
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 600.dp)
                                .shadow(3.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.setFinalOutcomeSearchQuery(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_search_final_outcome"),
                                    placeholder = {
                                        Text(
                                            text = if (subjectFilter.isNullOrBlank()) "اكتب اسم الطالب للبحث عن محصلته النهائية..." else "بحث عن طالب في مادة ($subjectFilter)...",
                                            fontSize = 13.5.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "بحث",
                                            tint = Color(0xFF1E3A8A)
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.setFinalOutcomeSearchQuery("") }) {
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
                                        focusedBorderColor = Color(0xFF1E3A8A),
                                        unfocusedBorderColor = Color(0xFFE2E8F0)
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                                )

                                // Subject Filter Chips
                                if (availableSubjects.size > 1 || (availableSubjects.isNotEmpty() && !subjectFilter.isNullOrBlank())) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "تصفية بالمادة:",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B)
                                        )

                                        // All subjects chip
                                        val isAllSelected = subjectFilter.isNullOrBlank()
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isAllSelected) Color(0xFF1E3A8A) else Color(0xFFF1F5F9))
                                                .clickable { viewModel.setFinalOutcomeSubjectFilter(null) }
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "جميع المواد",
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isAllSelected) Color.White else Color(0xFF334155)
                                            )
                                        }

                                        availableSubjects.forEach { subj ->
                                            val isSelected = subj == subjectFilter
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(if (isSelected) Color(0xFF1E3A8A) else Color(0xFFEFF6FF))
                                                    .border(1.dp, if (isSelected) Color(0xFF1E3A8A) else Color(0xFFBFDBFE), RoundedCornerShape(14.dp))
                                                    .clickable { viewModel.setFinalOutcomeSubjectFilter(subj) }
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = subj,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else Color(0xFF1E40AF)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Results Header Info
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 600.dp)
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نتائج الطلاب المسجلين (${filteredOutcomes.size})",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                            if (searchQuery.isNotBlank() || !subjectFilter.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (!subjectFilter.isNullOrBlank()) {
                                        Text(
                                            text = "مادة: $subjectFilter",
                                            fontSize = 12.sp,
                                            color = Color(0xFF047857),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    if (searchQuery.isNotBlank()) {
                                        Text(
                                            text = "\"$searchQuery\"",
                                            fontSize = 12.sp,
                                            color = Color(0xFF1E40AF),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Student Final Outcome Cards
                    if (filteredOutcomes.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 600.dp)
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (searchQuery.isBlank() && subjectFilter.isNullOrBlank()) "لا توجد درجات مسجلة للطلاب بعد" else "لا توجد نتائج مطابقة لبحثك",
                                        fontSize = 14.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    } else {
                        items(items = filteredOutcomes, key = { "${it.studentName}_${it.subject}_${it.section}" }) { item ->
                            StudentFinalOutcomeCard(
                                item = item,
                                modifier = Modifier.widthIn(max = 600.dp)
                            )
                        }
                    }

                    // 5. Developer Signature Footer
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 600.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xEEF8FAFC))
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
fun StudentFinalOutcomeCard(
    item: StudentFinalOutcome,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp))
            .testTag("card_student_final_${item.studentName}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Top Row: Name + Combined Outcome Square
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right side: Student Name and Details
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.studentName,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.section.isNotBlank()) {
                                Text(
                                    text = "الشعبة: ${item.section}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (item.subject.isNotBlank()) {
                                Text(
                                    text = "المادة: ${item.subject}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Left side: Big Outcome Square (المحصلة النهائية)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E3A8A), Color(0xFF1E40AF))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFDE047), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "المحصلة النهائية",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFEF08A)
                        )
                        Text(
                            text = item.finalCombinedDisplay(),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            // Middle Comparison Summary: Sem 1 vs Sem 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sem 1 Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDBEAFE))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "فصل أول: ${item.sem1Data?.semesterOutcomeDisplay() ?: "0"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }

                Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))

                // Sem 2 Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "فصل ثاني: ${item.sem2Data?.semesterOutcomeDisplay() ?: "0"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }

                // Expand/Collapse Details Toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "إخفاء التفاصيل" else "عرض الأشهر",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expandable breakdown for 3 months of Sem 1 & Sem 2
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Semester 1 Months
                    SemesterMonthsBreakdown(
                        title = "الفصل الأول (محصلات الـ 3 أشهر ÷ 3)",
                        headerColor = Color(0xFF1E40AF),
                        bgColor = Color(0xFFEFF6FF),
                        data = item.sem1Data
                    )

                    // Semester 2 Months
                    SemesterMonthsBreakdown(
                        title = "الفصل الثاني (محصلات الـ 3 أشهر ÷ 3)",
                        headerColor = Color(0xFF166534),
                        bgColor = Color(0xFFF0FDF4),
                        data = item.sem2Data
                    )
                }
            }
        }
    }
}

@Composable
fun SemesterMonthsBreakdown(
    title: String,
    headerColor: Color,
    bgColor: Color,
    data: StudentSemesterOutcome?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, headerColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerColor
                )
                Text(
                    text = "المحصلة = ${data?.semesterOutcomeDisplay() ?: "0"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = headerColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (data == null) {
                Text(
                    text = "لم يتم رصد درجات لهذا الفصل بعد",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MonthMiniBadge(
                        title = "شهر 1",
                        total = data.month1.totalScore,
                        outcome = data.month1.outcome,
                        hasRecord = data.month1.hasRecord,
                        modifier = Modifier.weight(1f)
                    )
                    MonthMiniBadge(
                        title = "شهر 2",
                        total = data.month2.totalScore,
                        outcome = data.month2.outcome,
                        hasRecord = data.month2.hasRecord,
                        modifier = Modifier.weight(1f)
                    )
                    MonthMiniBadge(
                        title = "شهر 3",
                        total = data.month3.totalScore,
                        outcome = data.month3.outcome,
                        hasRecord = data.month3.hasRecord,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MonthMiniBadge(
    title: String,
    total: Double,
    outcome: Double,
    hasRecord: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(0.8.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 10.sp, color = Color(0xFF64748B))
            if (hasRecord) {
                Text(
                    text = "درجة: ${total.formatScore()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "محصلة: ${outcome.formatScore()}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857)
                )
            } else {
                Text(
                    text = "-",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
