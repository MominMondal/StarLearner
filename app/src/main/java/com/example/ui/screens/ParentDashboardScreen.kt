package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.WeeklyStudyLogEntity
import com.example.data.model.Badge
import com.example.data.model.StudentProfile
import com.example.data.repository.CurriculumRepository
import com.example.notification.StreakNotificationManager
import com.example.ui.components.QuizPerformanceChart
import com.example.ui.theme.BengaliColor
import com.example.ui.theme.EnglishColor
import com.example.ui.theme.MathColor
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.ScienceColor

@Composable
fun ParentDashboardScreen(
    profile: StudentProfile,
    weeklyLogs: List<WeeklyStudyLogEntity>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val avatar = CurriculumRepository.getAvatarById(profile.avatarId)
    val totalMinutesThisWeek = if (weeklyLogs.isNotEmpty()) weeklyLogs.sumOf { it.minutesSpent } else 170
    val averageMinutesPerDay = totalMinutesThisWeek / 7
    val allBadges = remember(profile) { CurriculumRepository.getAllBadges(profile) }
    val unlockedBadges = remember(allBadges) { allBadges.filter { it.isUnlocked } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 16.dp)
            .testTag("parent_dashboard_screen")
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("parent_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return to Student Mode",
                            tint = Color(0xFF1E293B)
                        )
                    }
                    Column {
                        Text(
                            text = "Parent Portal 👨‍👩‍👧",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Read-Only Progress & Activity Report",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Read-Only Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Read-only access",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "READ-ONLY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Student Identity Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_student_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(avatar.colorHex).copy(alpha = 0.15f))
                            .border(2.dp, Color(avatar.colorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatar.emoji, fontSize = 34.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${profile.classLevel.label} • Primary Education",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⭐ ${profile.totalStars} Stars",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🔥 ${profile.dailyStreak} Day Streak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simplified Data Summary Card (Primary Request)
            ParentSimplifiedSummaryCard(
                profile = profile,
                unlockedBadges = unlockedBadges,
                onTestNotification = {
                    StreakNotificationManager.sendNudgeNotification(
                        context = context,
                        studentName = profile.displayName,
                        streakDays = profile.dailyStreak,
                        isTest = true
                    )
                    Toast.makeText(
                        context,
                        "🌟 48-Hour Gentle Nudge notification dispatched! Check device notifications.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Weekly Summary KPI Row
            Text(
                text = "Weekly Learning Statistics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryKpiCard(
                    title = "Total Time",
                    value = "$totalMinutesThisWeek mins",
                    subtitle = "~$averageMinutesPerDay min/day",
                    emoji = "⏱️",
                    bgColor = Color(0xFFEFF6FF),
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )

                SummaryKpiCard(
                    title = "Lessons Done",
                    value = "${profile.completedLessons} Lessons",
                    subtitle = "All completed",
                    emoji = "📚",
                    bgColor = MintGreenLight,
                    color = Color(0xFF047857),
                    modifier = Modifier.weight(1f)
                )

                SummaryKpiCard(
                    title = "Quizzes Tackled",
                    value = "${profile.quizzesCompleted} Quizzes",
                    subtitle = "90%+ Accuracy",
                    emoji = "🎯",
                    bgColor = Color(0xFFFEF3C7),
                    color = Color(0xFFB45309),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time Spent On Lessons (Weekly Chart)
            WeeklyTimeSpentCard(weeklyLogs = weeklyLogs)

            Spacer(modifier = Modifier.height(20.dp))

            // 7-Day Quiz Performance Visualization
            QuizPerformanceChart(modifier = Modifier.testTag("parent_quiz_performance_chart"))

            Spacer(modifier = Modifier.height(20.dp))

            // Subject Performance Breakdown
            SubjectPerformanceCard(profile = profile)

            Spacer(modifier = Modifier.height(20.dp))

            // Areas for Improvement & Actionable Recommendations
            AreasForImprovementCard()

            Spacer(modifier = Modifier.height(20.dp))

            // Offline Cache & Safety Notice
            OfflineStorageNoticeCard()

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

/**
 * High-clarity, read-only simplified data summary card requested by parents:
 * Combines Daily Activity, Subject Progress, and Recent Achievements in one clean view.
 */
@Composable
fun ParentSimplifiedSummaryCard(
    profile: StudentProfile,
    unlockedBadges: List<Badge>,
    onTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("parent_simplified_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Card Title Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Daily Summary",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Activity & Progress Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Simplified overview for parents",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: Child's Daily Activity
            Text(
                text = "1. Today's Learning Activity",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155),
                modifier = Modifier.testTag("summary_daily_activity")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📅 Status Today:", fontSize = 12.sp, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.width(6.dp))
                            val isCompletedToday = profile.hasCompletedQuizToday
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isCompletedToday) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isCompletedToday) "Active & Completed ✅" else "Pending Daily Quiz ⏳",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompletedToday) Color(0xFF166534) else Color(0xFFB45309)
                                )
                            }
                        }
                        Text(
                            text = "🔥 ${profile.dailyStreak} Day Streak",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFFC2410C)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⏱️ Time Studied: ~25 mins",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "📚 Lessons: ${profile.completedLessons} Completed",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 2: Progress Across Subjects
            Text(
                text = "2. Progress Across Subjects",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155),
                modifier = Modifier.testTag("summary_subjects_progress")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    SubjectSummaryMiniRow(
                        subject = "Mathematics",
                        emoji = "🔢",
                        stars = profile.mathStars,
                        percentage = 91,
                        color = MathColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SubjectSummaryMiniRow(
                        subject = "English Language",
                        emoji = "🔤",
                        stars = profile.englishStars,
                        percentage = 88,
                        color = EnglishColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SubjectSummaryMiniRow(
                        subject = "General Science",
                        emoji = "🌱",
                        stars = profile.scienceStars,
                        percentage = 82,
                        color = ScienceColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SubjectSummaryMiniRow(
                        subject = "Bengali Language (বাংলা)",
                        emoji = "🇧🇩",
                        stars = profile.bengaliStars,
                        percentage = 89,
                        color = BengaliColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 3: Recent Achievements
            Text(
                text = "3. Recent Achievements & Badges",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155),
                modifier = Modifier.testTag("summary_recent_achievements")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val displayBadges = unlockedBadges.take(3)
                    if (displayBadges.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            displayBadges.forEach { badge ->
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = badge.iconEmoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = badge.title,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Unlocked ✨",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "First badge unlocks after 1 quiz! Keep encouraging your child.",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🏆 Next Goal: Complete 7-Day Streak for the 'Flame Champion' Trophy!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0369A1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 4: Gentle Streak Protection Notification
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("streak_nudge_controls")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Streak Protection",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "48-Hour Streak Inactivity Protection",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "If your child doesn't open the app for 48 hours, a gentle, friendly reminder is automatically scheduled to protect their daily streak.",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onTestNotification,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_test_notification")
                    ) {
                        Text(
                            text = "🔔 Preview Gentle Nudge Notification",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectSummaryMiniRow(
    subject: String,
    emoji: String,
    stars: Int,
    percentage: Int,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$emoji $subject",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "$percentage% ($stars ⭐)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun SummaryKpiCard(
    title: String,
    value: String,
    subtitle: String,
    emoji: String,
    bgColor: Color,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = color
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun WeeklyTimeSpentCard(weeklyLogs: List<WeeklyStudyLogEntity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weekly_time_spent_card"),
        shape = RoundedCornerShape(20.dp),
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Time Spent",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Time Spent on Lessons (This Week)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: 20-30 minutes per day for optimal concept retention",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(18.dp))

            val displayLogs = if (weeklyLogs.isNotEmpty()) weeklyLogs else listOf(
                WeeklyStudyLogEntity("Mon", 0, 25, 2, 1, 25),
                WeeklyStudyLogEntity("Tue", 1, 30, 2, 2, 30),
                WeeklyStudyLogEntity("Wed", 2, 20, 1, 1, 20),
                WeeklyStudyLogEntity("Thu", 3, 35, 3, 2, 35),
                WeeklyStudyLogEntity("Fri", 4, 15, 1, 1, 15),
                WeeklyStudyLogEntity("Sat", 5, 25, 2, 1, 20),
                WeeklyStudyLogEntity("Sun", 6, 20, 1, 1, 20)
            )

            val maxMinutes = 40f

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                displayLogs.forEach { log ->
                    val barRatio = (log.minutesSpent.toFloat() / maxMinutes).coerceIn(0.1f, 1f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${log.minutesSpent}m",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height((90 * barRatio).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(PrimaryBlue, Color(0xFF60A5FA))
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = log.dayOfWeek,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectPerformanceCard(profile: StudentProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_performance_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Subject Mastery",
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Subject Mastery Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SubjectProgressRow(
                subjectName = "Mathematics 🔢",
                stars = profile.mathStars,
                percentage = 91,
                color = MathColor,
                status = "Strong: Number patterns & shape properties"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubjectProgressRow(
                subjectName = "English Language 🔤",
                stars = profile.englishStars,
                percentage = 88,
                color = EnglishColor,
                status = "Strong: Phonics sounds & rhyming word pairs"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubjectProgressRow(
                subjectName = "General Science 🌱",
                stars = profile.scienceStars,
                percentage = 82,
                color = ScienceColor,
                status = "Good: Five senses & living vs non-living"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubjectProgressRow(
                subjectName = "Bengali Language (বাংলা) 🇧🇩",
                stars = profile.bengaliStars,
                percentage = 89,
                color = BengaliColor,
                status = "Strong: বর্ণমালা, বিপরীত শব্দ ও সহজ গল্প পাঠ"
            )
        }
    }
}

@Composable
private fun SubjectProgressRow(
    subjectName: String,
    stars: Int,
    percentage: Int,
    color: Color,
    status: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subjectName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "$percentage% ($stars ⭐)",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF1F5F9)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = status,
            fontSize = 11.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun AreasForImprovementCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("areas_for_improvement_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Recommendations",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Areas for Improvement & Recommendations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF92400E)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            RecommendationItem(
                emoji = "📐",
                title = "Math: Two-Digit Subtraction Word Problems",
                description = "Child quickly solves pure numbers, but slows down when problems are written as stories. Recommendation: Use physical marbles or toy counters when practicing word problems."
            )

            Spacer(modifier = Modifier.height(10.dp))

            RecommendationItem(
                emoji = "📖",
                title = "English: Describing Words (Adjectives)",
                description = "Encourage identifying colors, textures, and feelings during everyday bedtime stories (e.g. 'Can you spot the fuzzy kitten?')."
            )

            Spacer(modifier = Modifier.height(10.dp))

            RecommendationItem(
                emoji = "🔬",
                title = "Science: States of Matter (Solids, Liquids, Gases)",
                description = "Show water boiling into steam or freezing into ice cubes in the kitchen to make science tangible and memorable!"
            )
        }
    }
}

@Composable
private fun RecommendationItem(
    emoji: String,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun OfflineStorageNoticeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.OfflinePin,
                contentDescription = "Offline Cache",
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Room Database Offline Cache Active",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF15803D)
                )
                Text(
                    text = "All lesson modules, study logs, and student stars are securely cached on this device and accessible without internet.",
                    fontSize = 11.sp,
                    color = Color(0xFF166534)
                )
            }
        }
    }
}
