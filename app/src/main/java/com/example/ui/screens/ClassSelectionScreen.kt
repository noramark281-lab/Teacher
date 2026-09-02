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
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GoldBorderCard
import com.example.ui.components.GoldBorderDark
import com.example.ui.components.GoldBorderLight
import com.example.ui.components.MetalBgBottom
import com.example.ui.components.MetalBgCenter
import com.example.ui.components.MetalBgTop
import com.example.viewmodel.GradeViewModel

data class GradeLevelItem(
    val id: String,
    val title: String,
    val stage: String,
    val stageCategory: String, // "primary", "prep", "secondary"
    val orderNumber: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val borderColor: Color
)

val GRADE_LEVELS_LIST = listOf(
    // 1. المرحلة الابتدائية (1 - 6)
    GradeLevelItem(
        id = "primary_1",
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
        id = "primary_2",
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
        id = "primary_3",
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
        id = "primary_4",
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
        id = "primary_5",
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
        id = "primary_6",
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
    val themeColorBottom = if (semester == 1) Color(0xFF2563EB) else Color(0xFF16A34A)

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
                    // Header Banner
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
