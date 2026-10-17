package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizPerformanceDay
import com.example.data.model.SubjectType
import com.example.data.repository.CurriculumRepository
import com.example.ui.theme.BengaliColor
import com.example.ui.theme.EnglishColor
import com.example.ui.theme.MathColor
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.ScienceColor

enum class ChartSubjectFilter(val label: String, val icon: String, val subjectType: SubjectType?) {
    ALL("All", "📊", null),
    MATH("Math", "🔢", SubjectType.MATH),
    ENGLISH("English", "🔤", SubjectType.ENGLISH),
    SCIENCE("Science", "🌱", SubjectType.SCIENCE),
    BENGALI("Bengali", "🇧🇩", SubjectType.BENGALI)
}

@Composable
fun QuizPerformanceChart(
    daysData: List<QuizPerformanceDay> = remember { CurriculumRepository.getSevenDayQuizPerformance() },
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(ChartSubjectFilter.ALL) }
    var selectedDayIndex by remember { mutableStateOf(daysData.lastIndex.coerceAtLeast(0)) }

    val subjectMetrics = remember { CurriculumRepository.getSubjectPerformanceMetrics() }
    val selectedDay = daysData.getOrNull(selectedDayIndex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quiz_performance_chart"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "7-Day Quiz Performance",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "📈", fontSize = 16.sp)
                    }
                    Text(
                        text = "Visualized accuracy & improvement tracking",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }

                // Growth pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Improvement Trend",
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+24% Growth",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts-style Subject Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ChartSubjectFilter.values().forEach { filter ->
                    val isSelected = filter == selectedFilter
                    val activeColor = when (filter.subjectType) {
                        SubjectType.MATH -> MathColor
                        SubjectType.ENGLISH -> EnglishColor
                        SubjectType.SCIENCE -> ScienceColor
                        SubjectType.BENGALI -> BengaliColor
                        null -> PrimaryBlue
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) activeColor.copy(alpha = 0.12f) else Color(0xFFF1F5F9))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) activeColor else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(vertical = 6.dp)
                            .testTag("chart_filter_${filter.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = filter.icon, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = filter.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) activeColor else Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Recharts-like Canvas Bar Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(Color(0xFFFAFAFC), RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // Background Guide Lines (0%, 25%, 50%, 75%, 100%)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height - 24f // leave room for labels
                    val yLevels = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)

                    yLevels.forEach { lvl ->
                        val y = h - (lvl * h)
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(30f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }
                }

                // Y-Axis labels
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("100%", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                    Text("75%", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                    Text("50%", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                    Text("25%", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                    Text("0%", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.SemiBold)
                }

                // Bars Row for 7 Days
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    daysData.forEachIndexed { index, day ->
                        val scoreForFilter = when (selectedFilter) {
                            ChartSubjectFilter.ALL -> day.overallAveragePercent
                            ChartSubjectFilter.MATH -> day.mathScorePercent
                            ChartSubjectFilter.ENGLISH -> day.englishScorePercent
                            ChartSubjectFilter.SCIENCE -> day.scienceScorePercent
                            ChartSubjectFilter.BENGALI -> ((day.mathScorePercent + day.englishScorePercent) / 2 + 5).coerceIn(0, 100)
                        }

                        val barColor = when (selectedFilter) {
                            ChartSubjectFilter.ALL -> PrimaryBlue
                            ChartSubjectFilter.MATH -> MathColor
                            ChartSubjectFilter.ENGLISH -> EnglishColor
                            ChartSubjectFilter.SCIENCE -> ScienceColor
                            ChartSubjectFilter.BENGALI -> BengaliColor
                        }

                        val isDaySelected = (index == selectedDayIndex)
                        val animatedHeightFraction by animateFloatAsState(
                            targetValue = (scoreForFilter / 100f).coerceIn(0.05f, 1f),
                            animationSpec = tween(durationMillis = 600)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { selectedDayIndex = index }
                                .padding(horizontal = 2.dp)
                                .testTag("chart_day_col_$index")
                        ) {
                            // Upper area for bar
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Score badge above bar if selected
                                if (isDaySelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$scoreForFilter%",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                // The bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.55f)
                                        .fillMaxHeight(animatedHeightFraction * 0.82f)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (isDaySelected) {
                                                Brush.verticalGradient(
                                                    listOf(barColor, barColor.copy(alpha = 0.7f))
                                                )
                                            } else {
                                                Brush.verticalGradient(
                                                    listOf(barColor.copy(alpha = 0.55f), barColor.copy(alpha = 0.3f))
                                                )
                                            }
                                        )
                                        .border(
                                            width = if (isDaySelected) 1.5.dp else 0.dp,
                                            color = if (isDaySelected) Color(0xFF0F172A) else Color.Transparent,
                                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Day Label below bar
                            Text(
                                text = day.dayLabel,
                                fontSize = 10.sp,
                                fontWeight = if (isDaySelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isDaySelected) Color(0xFF0F172A) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Day Details Callout (Tooltip card)
            selectedDay?.let { day ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chart_day_inspection_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📅", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "${day.dayLabel} (${day.dateLabel})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "${day.totalQuizzesTaken} Quiz Attempt${if (day.totalQuizzesTaken > 1) "s" else ""}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Subject Score breakdown
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SubjectScorePill("Math", day.mathScorePercent, MathColor)
                            SubjectScorePill("Eng", day.englishScorePercent, EnglishColor)
                            SubjectScorePill("Sci", day.scienceScorePercent, ScienceColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subject Improvement Breakdown Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjectMetrics.forEach { metric ->
                    SubjectImprovementMetricCard(
                        metric = metric,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectScorePill(
    subjectName: String,
    scorePercent: Int,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$subjectName: $scorePercent%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun SubjectImprovementMetricCard(
    metric: com.example.data.model.SubjectPerformanceMetric,
    modifier: Modifier = Modifier
) {
    val subjectColor = when (metric.subject) {
        SubjectType.MATH -> MathColor
        SubjectType.ENGLISH -> EnglishColor
        SubjectType.SCIENCE -> ScienceColor
        SubjectType.BENGALI -> BengaliColor
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metric.subject.iconEmoji,
                    fontSize = 14.sp
                )
                Text(
                    text = "+${metric.improvementPercent}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF15803D)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = metric.subject.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "${metric.averageAccuracyPercent}% avg accuracy",
                fontSize = 9.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
