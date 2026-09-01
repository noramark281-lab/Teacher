package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmberButtonBottom
import com.example.ui.components.AmberButtonTop
import com.example.ui.components.BigSemesterButton
import com.example.ui.components.BlueButtonBorder
import com.example.ui.components.BlueButtonBottom
import com.example.ui.components.BlueButtonTop
import com.example.ui.components.DottedInfoField
import com.example.ui.components.GoldBorderCard
import com.example.ui.components.GoldBorderDark
import com.example.ui.components.GoldBorderLight
import com.example.ui.components.GreenButtonBorder
import com.example.ui.components.GreenButtonBottom
import com.example.ui.components.GreenButtonTop
import com.example.ui.components.MetalBgBottom
import com.example.ui.components.MetalBgCenter
import com.example.ui.components.MetalBgTop
import com.example.ui.components.SmallActionButton3D
import com.example.ui.components.SmallExcelGreenBottom
import com.example.ui.components.SmallExcelGreenTop
import com.example.ui.components.SmallPdfBlueBottom
import com.example.ui.components.SmallPdfBlueTop
import com.example.ui.components.TopAppTitleBanner
import com.example.viewmodel.GradeViewModel

@Composable
fun HomeScreen(
    viewModel: GradeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schoolInfo by viewModel.schoolInfo.collectAsState()
    val scrollState = rememberScrollState()

    // Enforce RTL layout for Arabic
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MetalBgTop, MetalBgCenter, MetalBgBottom)
                    )
                )
                .drawBehind {
                    // Subtle brushed metal horizontal lines texture
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
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Embossed Title Banner
                TopAppTitleBanner(
                    title = "برنامج كشف درجات اعمال السنة",
                    modifier = Modifier.widthIn(max = 500.dp)
                )

                // 1. First Info Box (School Name, AD Year, Hijri Year)
                GoldBorderCard(
                    modifier = Modifier.widthIn(max = 500.dp),
                    onClick = { viewModel.openEditSchoolDialog() }
                ) {
                    // School Name Row
                    DottedInfoField(
                        prefix = "مدرسة / مجمع :-",
                        value = schoolInfo.schoolName,
                        onClick = { viewModel.openEditSchoolDialog() },
                        modifier = Modifier.testTag("field_school_name")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // AD Year Row
                    DottedInfoField(
                        prefix = "العام الدراسي",
                        value = schoolInfo.academicYearAD,
                        suffix = "م",
                        onClick = { viewModel.openEditSchoolDialog() },
                        modifier = Modifier.testTag("field_year_ad")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Hijri Year Row
                    DottedInfoField(
                        prefix = "هجر",
                        value = schoolInfo.academicYearHijri,
                        suffix = "هـ",
                        onClick = { viewModel.openEditSchoolDialog() },
                        modifier = Modifier.testTag("field_year_hijri")
                    )
                }

                // 2. Second Info Box (Teacher, Classes, Semester, Subject)
                GoldBorderCard(
                    modifier = Modifier.widthIn(max = 500.dp),
                    onClick = { viewModel.openEditSchoolDialog() }
                ) {
                    // Teacher Name & Classes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DottedInfoField(
                            prefix = "الاستاذ /",
                            value = schoolInfo.teacherName,
                            onClick = { viewModel.openEditSchoolDialog() },
                            modifier = Modifier.weight(1.3f).testTag("field_teacher_name")
                        )

                        DottedInfoField(
                            prefix = "الصفوف",
                            value = schoolInfo.gradeLevels,
                            onClick = { viewModel.openEditSchoolDialog() },
                            modifier = Modifier.weight(1f).testTag("field_grade_levels")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Semester & Subject
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DottedInfoField(
                            prefix = "الفصل الدراسي",
                            value = "الأول والثاني",
                            onClick = { viewModel.openEditSchoolDialog() },
                            modifier = Modifier.weight(1.3f)
                        )

                        DottedInfoField(
                            prefix = "المادة",
                            value = schoolInfo.defaultSubject,
                            onClick = { viewModel.openEditSchoolDialog() },
                            modifier = Modifier.weight(1f).testTag("field_subject")
                        )
                    }
                }

                // 3. Main Semester Buttons (Large 3D Blue & Green Buttons)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Semester 1 (الفصل الاول) - Blue Button
                    BigSemesterButton(
                        title = "الفصل الاول",
                        gradientTop = BlueButtonTop,
                        gradientBottom = BlueButtonBottom,
                        borderColor = BlueButtonBorder,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_semester_1",
                        onClick = {
                            viewModel.navigateTo(1)
                        }
                    )

                    // Semester 2 (الفصل الثاني) - Green Button
                    BigSemesterButton(
                        title = "الفصل الثاني",
                        gradientTop = GreenButtonTop,
                        gradientBottom = GreenButtonBottom,
                        borderColor = GreenButtonBorder,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_semester_2",
                        onClick = {
                            viewModel.navigateTo(2)
                        }
                    )
                }

                // 4. Export & Backup Action Buttons Row (3D Buttons: Backup, Excel, PDF)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Backup Button (نسخة احتياطية) - Amber/Gold
                    SmallActionButton3D(
                        title = "نسخة\nاحتياطية",
                        gradientTop = AmberButtonTop,
                        gradientBottom = AmberButtonBottom,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_backup_restore",
                        onClick = {
                            viewModel.openBackupDialog()
                        }
                    )

                    // Excel Button - Green
                    SmallActionButton3D(
                        title = "Excel",
                        gradientTop = SmallExcelGreenTop,
                        gradientBottom = SmallExcelGreenBottom,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_export_excel",
                        onClick = {
                            viewModel.exportClassExcel(context)
                        }
                    )

                    // PDF Button - Blue
                    SmallActionButton3D(
                        title = "pdf",
                        gradientTop = SmallPdfBlueTop,
                        gradientBottom = SmallPdfBlueBottom,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_export_pdf",
                        onClick = {
                            viewModel.exportClassPdf(context)
                        }
                    )
                }

                // 5. Settings / Variables quick button
                Row(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { viewModel.openSettingsDialog() },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E40AF)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E40AF)),
                        modifier = Modifier.testTag("btn_variables_settings")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "المتغيرات والرموز والدرجات العظمى",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 6. Bottom Credits Box with Factory Reset Button
                GoldBorderCard(
                    modifier = Modifier.widthIn(max = 500.dp)
                ) {
                    Text(
                        text = "فكرة أ / محمد عبدالقوي الرميمة",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "برمجة وتصميم الدكتور / مالك الرميمة",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Phone row with Factory Reset button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Phone number with clickable dialer
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:771134103")
                                    }
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = "اتصال",
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "هاتف / 771134103",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Factory Reset button (زر صغير لإعادة ضبط المصنع)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEE2E2))
                                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(8.dp))
                                .clickable { viewModel.openResetDialog() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("btn_factory_reset"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.RestartAlt,
                                    contentDescription = "إعادة ضبط المصنع",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ضبط المصنع",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
