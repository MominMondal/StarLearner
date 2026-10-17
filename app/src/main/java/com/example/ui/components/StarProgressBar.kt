package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.EnglishColor
import com.example.ui.theme.EnglishColorLight
import com.example.ui.theme.MathColor
import com.example.ui.theme.MathColorLight
import com.example.ui.theme.ScienceColor
import com.example.ui.theme.ScienceColorLight
import com.example.ui.theme.TertiaryAmber

@Composable
fun StarProgressBar(
    profile: StudentProfile,
    modifier: Modifier = Modifier
) {
    // Level calculation: Every 100 stars is a new rank level
    val currentLevel = (profile.totalStars / 100) + 1
    val starsInCurrentLevel = profile.totalStars % 100
    val targetLevelStars = 100
    val progressRatio = (starsInCurrentLevel.toFloat() / targetLevelStars).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "star_progress_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("star_progress_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Level badge & total stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(TertiaryAmber, AmberGlow))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star Level",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Level $currentLevel Explorer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile.completedLessons} Lessons • ${profile.quizzesCompleted} Quizzes",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Total Stars Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile.totalStars}",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB45309),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Gradient Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFF59E0B),
                                    Color(0xFFFBBF24),
                                    Color(0xFF10B981)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress text description
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$starsInCurrentLevel / $targetLevelStars Stars to Level ${currentLevel + 1}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFB45309)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Breakdown by subject stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubjectStarChip(
                    emoji = "🔢",
                    subjectName = "Math",
                    stars = profile.mathStars,
                    bgColor = MathColorLight,
                    textColor = MathColor,
                    modifier = Modifier.weight(1f)
                )
                SubjectStarChip(
                    emoji = "🔤",
                    subjectName = "English",
                    stars = profile.englishStars,
                    bgColor = EnglishColorLight,
                    textColor = EnglishColor,
                    modifier = Modifier.weight(1f)
                )
                SubjectStarChip(
                    emoji = "🌱",
                    subjectName = "Science",
                    stars = profile.scienceStars,
                    bgColor = ScienceColorLight,
                    textColor = ScienceColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SubjectStarChip(
    emoji: String,
    subjectName: String,
    stars: Int,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$emoji $subjectName",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$stars",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "⭐", fontSize = 10.sp)
            }
        }
    }
}
