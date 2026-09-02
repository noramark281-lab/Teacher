package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color constants matching the screenshot styling
val MetalBgTop = Color(0xFFE2E8F0)
val MetalBgCenter = Color(0xFFF1F5F9)
val MetalBgBottom = Color(0xFFCBD5E1)

val GoldBorderLight = Color(0xFFE5C158)
val GoldBorderDark = Color(0xFFB38927)

val BlueButtonTop = Color(0xFF4A90E2)
val BlueButtonBottom = Color(0xFF2C5E9E)
val BlueButtonBorder = Color(0xFF78A9E7)

val GreenButtonTop = Color(0xFF7CB342)
val GreenButtonBottom = Color(0xFF558B2F)
val GreenButtonBorder = Color(0xFFA2CF6E)

val AmberButtonTop = Color(0xFFD49E34)
val AmberButtonBottom = Color(0xFF9E6B15)
val AmberButtonBorder = Color(0xFFF3C769)

val SmallPdfBlueTop = Color(0xFF3B82F6)
val SmallPdfBlueBottom = Color(0xFF1D4ED8)

val SmallExcelGreenTop = Color(0xFF22C55E)
val SmallExcelGreenBottom = Color(0xFF15803D)

@Composable
fun GoldBorderCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x40000000)
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.verticalGradient(
                colors = listOf(GoldBorderLight, GoldBorderDark, GoldBorderLight)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun TopAppTitleBanner(
    title: String = "برنامج كشف درجات اعمال السنة",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x40000000),
                spotColor = Color(0x50000000)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(GoldBorderLight, GoldBorderDark, GoldBorderLight)
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFECEFF1))
                    )
                )
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF8D6E28),
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("app_title_text")
            )
        }
    }
}

@Composable
fun DottedInfoField(
    prefix: String,
    value: String,
    suffix: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color(0xFF1E3A8A)),
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (prefix.isNotBlank()) {
            Text(
                text = prefix,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F2942)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = if (value.isBlank()) ".................." else value,
            fontWeight = if (value.isBlank()) FontWeight.Normal else FontWeight.Bold,
            fontSize = 14.sp,
            color = if (value.isBlank()) Color(0xFF64748B) else Color(0xFF1E40AF),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (suffix.isNotBlank()) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = suffix,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F2942)
            )
        }
    }
}

@Composable
fun BigSemesterButton(
    title: String,
    gradientTop: Color,
    gradientBottom: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0x44000000),
                spotColor = Color(0x66000000)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientTop, gradientBottom)
                )
            )
            .drawBehind {
                // Gold / lighter rim border
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(GoldBorderLight.copy(alpha = 0.8f), GoldBorderDark)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White),
                onClick = onClick
            )
            .defaultMinSize(minHeight = 110.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Inner highlight cushion
        Box(
            modifier = Modifier
                .padding(6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(100f, 60f),
                        radius = 200f
                    )
                )
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SmallActionButton3D(
    title: String,
    gradientTop: Color,
    gradientBottom: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x4D000000)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientTop, gradientBottom)
                )
            )
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(GoldBorderLight.copy(alpha = 0.7f), GoldBorderDark.copy(alpha = 0.9f))
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White),
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BigFinalOutcomeButton(
    title: String = "المحصلة النهائية",
    subtitle: String = "جمع محصلة الفصل الأول + الفصل الثاني ومحرك البحث",
    modifier: Modifier = Modifier,
    testTag: String = "btn_final_outcome",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x44000000),
                spotColor = Color(0x66000000)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                )
            )
            .drawBehind {
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(GoldBorderLight, GoldBorderDark)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFDE047))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "عرض الكشف",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }
    }
}

