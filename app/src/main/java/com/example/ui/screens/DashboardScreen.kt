package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.LinearProgressIndicator
import com.example.data.model.ClassLevel
import com.example.data.model.CurriculumMedium
import com.example.data.model.DailyMission
import com.example.data.model.LessonBookmark
import com.example.data.model.StudentProfile
import com.example.data.model.SubjectType
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.QuizPerformanceChart
import com.example.ui.components.StarProgressBar
import com.example.ui.theme.BengaliColor
import com.example.ui.theme.BengaliColorLight
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun DashboardScreen(
    profile: StudentProfile,
    selectedClass: ClassLevel,
    selectedMedium: CurriculumMedium = CurriculumMedium.CBSE_ENGLISH,
    dailyMissions: List<DailyMission> = emptyList(),
    onClaimMission: (DailyMission) -> Unit = {},
    onResumeBookmark: (LessonBookmark) -> Unit = {},
    onClassSelected: (ClassLevel) -> Unit,
    onMediumSelected: (CurriculumMedium) -> Unit = {},
    onSubjectClick: (SubjectType) -> Unit,
    onOpenPhotoLearning: () -> Unit = {},
    onOpenBengaliLessons: () -> Unit = {},
    onOpenMediumLessons: () -> Unit = onOpenBengaliLessons,
    onStartQuiz: () -> Unit = {},
    onOpenAwards: () -> Unit = {},
    onOpenLeaderboard: () -> Unit,
    onOpenBadges: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenParentDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentAvatar = CurriculumRepository.getAvatarById(profile.avatarId)
    val context = androidx.compose.ui.platform.LocalContext.current
    var showConfetti by remember { mutableStateOf(false) }

    // Trigger celebratory confetti animation whenever student maintains or increases daily streak
    LaunchedEffect(profile.dailyStreak) {
        if (profile.dailyStreak > 0) {
            showConfetti = true
            com.example.service.SoundManagerService.playCorrect(context)
            delay(3500)
            showConfetti = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp)
                .testTag("dashboard_screen")
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Student Welcome & Daily Streak Header
                StudentWelcomeHeader(
                    profile = profile,
                    avatarEmoji = currentAvatar.emoji,
                    onAvatarClick = onOpenProfile
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Daily Streak Card with Flame Icon & Consecutive Day Tracker
                DailyStreakCard(
                    streakDays = profile.dailyStreak,
                    hasCompletedQuizToday = profile.hasCompletedQuizToday,
                    onStartQuizClick = onStartQuiz,
                    onCelebrateClick = {
                        showConfetti = true
                        com.example.service.SoundManagerService.playCorrect(context)
                    },
                    modifier = Modifier.testTag("daily_streak_card")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Continue Reading Card (allows students to bookmark & jump back with a single tap)
                profile.activeBookmark?.let { bookmark ->
                    ContinueReadingCard(
                        bookmark = bookmark,
                        onResume = { onResumeBookmark(bookmark) },
                        modifier = Modifier.testTag("continue_reading_card")
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Daily Missions System (3 small achievable tasks)
                if (dailyMissions.isNotEmpty()) {
                    DailyMissionsCard(
                        missions = dailyMissions,
                        onClaimReward = onClaimMission,
                        modifier = Modifier.testTag("daily_missions_section")
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Star Progress visual element
                StarProgressBar(
                    profile = profile,
                    modifier = Modifier.testTag("dashboard_star_progress")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 7-Day Quiz Performance Visualization (Recharts-style analytics)
                QuizPerformanceChart(
                    modifier = Modifier.testTag("dashboard_quiz_performance_chart")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Class Level Selector Row (Classes 1 - 4)
                Text(
                    text = "Choose Class Level:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                ClassLevelSelector(
                    selectedClass = selectedClass,
                    onClassSelected = onClassSelected
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Curriculum Medium Selector Row (Bangla, Hindi, CBSE English)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Learning Medium (পড়াশোনার মাধ্যম / माध्यम):",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CurriculumMediumSelector(
                    selectedMedium = selectedMedium,
                    onMediumSelected = onMediumSelected
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Medium Lessons Hero Card
                MediumLessonsHeroCard(
                    selectedMedium = selectedMedium,
                    selectedClass = selectedClass,
                    onOpenLessons = onOpenMediumLessons
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Photo-Based Learning Hero Section
                PhotoBasedLearningHeroCard(
                    onExploreClick = onOpenPhotoLearning
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section Title: Subjects Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore Subjects (${selectedClass.label} • ${selectedMedium.shortCode})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

        // Grid layout of Subjects (Math, English, Science, Bengali)
        items(count = 1) {
            SubjectCardsGrid(
                selectedClass = selectedClass,
                selectedMedium = selectedMedium,
                profile = profile,
                onSubjectClick = onSubjectClick,
                onOpenMediumLessons = onOpenMediumLessons
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Quick Navigation Hub
            QuickNavBanner(
                onOpenAwards = onOpenAwards,
                onOpenLeaderboard = onOpenLeaderboard,
                onOpenBadges = onOpenBadges,
                onOpenProfile = onOpenProfile,
                onOpenParentDashboard = onOpenParentDashboard
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Celebratory Confetti Animation Overlay
    AnimatedVisibility(
        visible = showConfetti,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        ConfettiCelebrationOverlay()
    }
}
}

@Composable
private fun StudentWelcomeHeader(
    profile: StudentProfile,
    avatarEmoji: String,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_student_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back, 🌟",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B)
            )
            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = Color(0xFF0F172A)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${profile.classLevel.label} • Active Student",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "📶 Offline Ready",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )
                }
            }
        }

        // Avatar Bubble
        Surface(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .clickable { onAvatarClick() }
                .testTag("dashboard_avatar_button"),
            shape = CircleShape,
            color = Color(0xFFEFF6FF),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = avatarEmoji, fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun DailyStreakCard(
    streakDays: Int,
    hasCompletedQuizToday: Boolean = false,
    onStartQuizClick: () -> Unit = {},
    onCelebrateClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7ED)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flame Icon Container
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(FlameYellow, FlameOrange)
                            )
                        )
                        .clickable { onCelebrateClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Daily Streak Flame",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$streakDays Day Streak!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color(0xFFC2410C)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🔥", fontSize = 16.sp)
                        }

                        // Celebrate button
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onCelebrateClick() }
                                .testTag("celebrate_streak_button"),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEDD5)
                        ) {
                            Text(
                                text = "Celebrate 🎉",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC2410C),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // First-Quiz-of-the-Day Streak Status
                    if (hasCompletedQuizToday) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("streak_claimed_pill")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "✓",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF15803D),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "1st quiz completed today! Streak advanced 🔥",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("streak_pending_pill")
                            ) {
                                Text(
                                    text = "Finish 1st quiz today to reach ${streakDays + 1} days!",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onStartQuizClick() }
                                    .testTag("start_first_quiz_button"),
                                color = Color(0xFFEA580C),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Quiz ➔",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7-Day Streak Habit Tracker Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today")
                weekDays.forEachIndexed { idx, day ->
                    val isPastActive = idx < 6
                    val isTodayActive = hasCompletedQuizToday
                    val isActive = if (idx == 6) isTodayActive else isPastActive

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                            .background(if (isActive) Color(0xFFEA580C) else Color(0xFFFED7AA))
                            .border(
                                width = if (idx == 6) 2.dp else 0.dp,
                                color = if (idx == 6) Color(0xFF9A3412) else Color.Transparent,
                                shape = CircleShape
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isActive) "🔥" else "•",
                                fontSize = if (isActive) 12.sp else 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = day,
                            fontSize = 9.sp,
                            fontWeight = if (idx == 6) FontWeight.Bold else FontWeight.Medium,
                            color = if (idx == 6) Color(0xFF9A3412) else Color(0xFF78350F)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContinueReadingCard(
    bookmark: LessonBookmark,
    onResume: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onResume() }
            .testTag("continue_reading_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF6FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarked Lesson",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Continue Reading",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "${bookmark.subject.title} • ${bookmark.classLevel.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF3B82F6),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Resume Pill Button
                Button(
                    onClick = onResume,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("resume_reading_button")
                ) {
                    Text(
                        text = "Resume",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Jump back",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = bookmark.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar showing where student left off
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved bookmark: ${bookmark.progressPercentage}% completed",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "~${bookmark.durationMinutes} min lesson",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { bookmark.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFFDBEAFE)
            )
        }
    }
}

@Composable
private fun DailyMissionsCard(
    missions: List<DailyMission>,
    onClaimReward: (DailyMission) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Daily Missions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Complete 3 tasks for bonus stars!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                val completedCount = missions.count { it.isCompleted }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$completedCount / ${missions.size} Done",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                missions.forEach { mission ->
                    DailyMissionItem(
                        mission = mission,
                        onClaim = { onClaimReward(mission) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyMissionItem(
    mission: DailyMission,
    onClaim: () -> Unit
) {
    val progress = (mission.currentCount.toFloat() / mission.targetCount.toFloat()).coerceIn(0f, 1f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("daily_mission_${mission.id}"),
        shape = RoundedCornerShape(14.dp),
        color = if (mission.isClaimed) Color(0xFFF1F5F9) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (mission.isCompleted && !mission.isClaimed) Color(0xFF86EFAC) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (mission.isCompleted) Color(0xFFDCFCE7) else Color(0xFFEEF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mission.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mission.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (mission.isClaimed) Color(0xFF64748B) else Color(0xFF1E293B)
                    )
                    Text(
                        text = "+${mission.rewardStars} ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD97706)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = mission.description,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (mission.isCompleted) Color(0xFF16A34A) else PrimaryBlue,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Claim / Done button
            if (mission.isClaimed) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Claimed ✓",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            } else if (mission.isCompleted) {
                Button(
                    onClick = onClaim,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("claim_mission_${mission.id}")
                ) {
                    Text(
                        text = "Claim ⭐",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = "${mission.currentCount}/${mission.targetCount}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun ClassLevelSelector(
    selectedClass: ClassLevel,
    onClassSelected: (ClassLevel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("class_level_selector"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ClassLevel.values().forEach { level ->
            val isSelected = level == selectedClass
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryBlue else Color.White,
                label = "tab_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color(0xFF475569),
                label = "tab_text"
            )

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onClassSelected(level) }
                    .testTag("class_selector_${level.name}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = level.shortLabel,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = textColor
                    )
                    Text(
                        text = level.label,
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurriculumMediumSelector(
    selectedMedium: CurriculumMedium,
    onMediumSelected: (CurriculumMedium) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_medium_selector"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CurriculumMedium.values().forEach { medium ->
            val isSelected = medium == selectedMedium
            val activeColor = when (medium) {
                CurriculumMedium.BANGLA -> BengaliColor
                CurriculumMedium.HINDI -> Color(0xFFEA580C)
                CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
            }
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) activeColor else Color.White,
                label = "medium_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color(0xFF334155),
                label = "medium_text"
            )

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onMediumSelected(medium) }
                    .testTag("medium_chip_${medium.name.lowercase()}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
                border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = medium.flagEmoji,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = medium.shortCode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        maxLines = 1
                    )
                    Text(
                        text = medium.nativeLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF64748B),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun MediumLessonsHeroCard(
    selectedMedium: CurriculumMedium,
    selectedClass: ClassLevel,
    onOpenLessons: () -> Unit
) {
    val mediumColor = when (selectedMedium) {
        CurriculumMedium.BANGLA -> BengaliColor
        CurriculumMedium.HINDI -> Color(0xFFEA580C)
        CurriculumMedium.CBSE_ENGLISH -> PrimaryBlue
    }
    val subtitleText = when (selectedMedium) {
        CurriculumMedium.BANGLA -> "বাংলা স্বরবর্ণ, ব্যঞ্জনবর্ণ, ১-১০ গণনা ও মজার ছড়া পাঠমালা"
        CurriculumMedium.HINDI -> "हिंदी स्वर, व्यंजन, १-१० गिनती व सुंदर बालगीत पाठ"
        CurriculumMedium.CBSE_ENGLISH -> "Phonics, CVC blends, STEM Lab concepts & Moral stories"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onOpenLessons() }
            .testTag("medium_lessons_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, mediumColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(mediumColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📖", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${selectedMedium.flagEmoji} ${selectedMedium.label}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(mediumColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = selectedClass.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = mediumColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitleText,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onOpenLessons,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mediumColor),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("dashboard_open_medium_lessons_button")
            ) {
                Text("Study 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SubjectCardsGrid(
    selectedClass: ClassLevel,
    selectedMedium: CurriculumMedium,
    profile: StudentProfile,
    onSubjectClick: (SubjectType) -> Unit,
    onOpenMediumLessons: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SubjectType.values().forEach { subject ->
            val subjectStars = when (subject) {
                SubjectType.MATH -> profile.mathStars
                SubjectType.ENGLISH -> profile.englishStars
                SubjectType.SCIENCE -> profile.scienceStars
                SubjectType.BENGALI -> profile.bengaliStars
            }

            SubjectCardItem(
                subject = subject,
                selectedClass = selectedClass,
                selectedMedium = selectedMedium,
                subjectStars = subjectStars,
                onClick = { onSubjectClick(subject) },
                onOpenLessons = onOpenMediumLessons
            )
        }
    }
}

@Composable
private fun SubjectCardItem(
    subject: SubjectType,
    selectedClass: ClassLevel,
    selectedMedium: CurriculumMedium,
    subjectStars: Int,
    onClick: () -> Unit,
    onOpenLessons: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("subject_card_${subject.name.lowercase()}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vibrant Subject Icon Container
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(subject.lightBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subject.iconEmoji,
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = subject.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color(0xFF0F172A)
                            )

                            // Mastery Badge if 100% completion reached
                            val maxStarsGoal = when (subject) {
                                SubjectType.MATH -> 60
                                SubjectType.ENGLISH -> 50
                                SubjectType.SCIENCE -> 40
                                SubjectType.BENGALI -> 40
                            }
                            val isMastered = subjectStars >= maxStarsGoal

                            if (isMastered) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                                            )
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("mastery_badge_${subject.name.lowercase()}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "👑", fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "100% Mastered",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                }
                            }
                        }

                        // Stars Earned Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⭐", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "$subjectStars",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }

                    val mediumSubtitle = when (selectedMedium) {
                        CurriculumMedium.BANGLA -> when (subject) {
                            SubjectType.MATH -> "সংখ্যা গণনা, যোগ-বিয়োগ ও সমস্যা সমাধান"
                            SubjectType.ENGLISH -> "ধ্বনি (Phonics), নতুন শব্দ ও সহজ পাঠ"
                            SubjectType.SCIENCE -> "আমাদের পঞ্চইন্দ্রিয়, প্রকৃতি ও পরিবেশ"
                            SubjectType.BENGALI -> "বাংলা বর্ণমালা, যুক্তবর্ণ, ছড়া ও গল্প"
                        }
                        CurriculumMedium.HINDI -> when (subject) {
                            SubjectType.MATH -> "गिनती, दो अंकों का जोड़ व घटाव"
                            SubjectType.ENGLISH -> "ध्वनि (Phonics), शब्द रचना व अभ्यास"
                            SubjectType.SCIENCE -> "५ इंद्रियाँ, पौधे, सजीव व निर्जीव वस्तुएँ"
                            SubjectType.BENGALI -> "हिंदी वर्णमाला, बालगीत व सुंदर कहानियाँ"
                        }
                        CurriculumMedium.CBSE_ENGLISH -> subject.subtitle
                    }

                    Text(
                        text = mediumSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedClass.label} • ${selectedMedium.shortCode}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = subject.primaryColor
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val lessonBtnLabel = when (selectedMedium) {
                        CurriculumMedium.BANGLA -> "📖 পাঠ"
                        CurriculumMedium.HINDI -> "📖 पाठ"
                        CurriculumMedium.CBSE_ENGLISH -> "📖 Lessons"
                    }

                    Button(
                        onClick = onOpenLessons,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = subject.lightBgColor,
                            contentColor = subject.primaryColor
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("subject_lessons_button_${subject.name.lowercase()}")
                    ) {
                        Text(
                            text = lessonBtnLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = subject.primaryColor),
                        contentPadding = ButtonDefaults.ContentPadding,
                        modifier = Modifier.testTag("subject_quiz_button_${subject.name.lowercase()}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Quiz",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Take Quiz",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickNavBanner(
    onOpenAwards: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenBadges: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenParentDashboard: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEEF2FF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Student Club & Achievements",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF312E81)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickNavButton(
                    title = "Awards",
                    emoji = "🏆",
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAwards
                )
                QuickNavButton(
                    title = "Ranks",
                    emoji = "🎖️",
                    color = Color(0xFF4338CA),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenLeaderboard
                )
                QuickNavButton(
                    title = "Badges",
                    emoji = "⭐",
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenBadges
                )
                QuickNavButton(
                    title = "Profile",
                    emoji = "👤",
                    color = Color(0xFF059669),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenProfile
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Parent Zone Shortcut Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenParentDashboard() }
                    .testTag("parent_zone_button"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👨‍👩‍👧", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Parent Zone & Weekly Insights",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E1B4B)
                            )
                            Text(
                                text = "View study time, mastery & recommendations",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Text(
                        text = "View ➔",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4338CA)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickNavButton(
    title: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

private enum class ParticleShape { CIRCLE, RECT }

private data class ConfettiParticle(
    val xNorm: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val initialDelay: Float,
    val shape: ParticleShape
)

@Composable
fun ConfettiCelebrationOverlay(
    modifier: Modifier = Modifier
) {
    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD166), // Gold
            Color(0xFFEF476F), // Coral Red
            Color(0xFF06D6A0), // Mint Green
            Color(0xFF118AB2), // Sky Blue
            Color(0xFF8338EC), // Purple
            Color(0xFFFF70A6), // Pink
            Color(0xFFFF9F1C)  // Orange
        )
        List(65) { index ->
            ConfettiParticle(
                xNorm = Random.nextFloat(),
                speed = Random.nextFloat() * 0.7f + 0.3f,
                size = Random.nextFloat() * 10f + 6f,
                color = colors[index % colors.size],
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                initialDelay = Random.nextFloat() * 0.4f,
                shape = if (index % 3 == 0) ParticleShape.CIRCLE else ParticleShape.RECT
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3200, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("confetti_celebration_overlay"),
        contentAlignment = Alignment.TopCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val currentProgress = progress.value

            particles.forEach { p ->
                val adjustedProgress = ((currentProgress - p.initialDelay) / (1f - p.initialDelay)).coerceIn(0f, 1f)
                if (adjustedProgress > 0f) {
                    val y = adjustedProgress * (canvasH + 80f) - 40f
                    val sway = kotlin.math.sin(adjustedProgress * 12f + p.xNorm * 10f) * 35f
                    val x = (p.xNorm * canvasW) + sway

                    when (p.shape) {
                        ParticleShape.CIRCLE -> {
                            drawCircle(
                                color = p.color,
                                radius = p.size / 2f,
                                center = Offset(x, y)
                            )
                        }
                        ParticleShape.RECT -> {
                            drawRect(
                                color = p.color,
                                topLeft = Offset(x - p.size / 2f, y - p.size / 3f),
                                size = Size(p.size, p.size * 1.5f)
                            )
                        }
                    }
                }
            }
        }

        // Celebratory Floating Banner
        Card(
            modifier = Modifier
                .padding(top = 70.dp, start = 24.dp, end = 24.dp)
                .testTag("streak_celebration_banner"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1B4B)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "🎉", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Streak Alive & Growing! 🔥",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFFFDE047)
                    )
                    Text(
                        text = "Keep up the daily habit, Champion!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoBasedLearningHeroCard(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onExploreClick() }
            .testTag("photo_learning_hero_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF1F2),
                            Color.White
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF43F5E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📸", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ছবি দেখে শেখো",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF881337)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFE4E6))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "NEW SECTION",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE11D48)
                                )
                            }
                        }
                        Text(
                            text = "Photo-Based Learning & Picture Quiz",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sample photos preview strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "🥭" to "আম",
                    "🐅" to "বাঘ",
                    "🪷" to "শাপলা",
                    "🐘" to "হাতি",
                    "⛵" to "নৌকা"
                ).forEach { (emoji, label) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = emoji, fontSize = 22.sp)
                        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap photos to hear sound hints & learn words",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Button(
                    onClick = onExploreClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    contentPadding = ButtonDefaults.ContentPadding,
                    modifier = Modifier.testTag("explore_photo_learning_button")
                ) {
                    Text(
                        text = "Explore 📸",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


