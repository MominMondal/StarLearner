package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.QuizAttemptEntity
import com.example.data.db.WeeklyStudyLogEntity
import com.example.data.model.Badge
import com.example.data.model.StudentProfile
import com.example.data.model.SubjectType
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ParentDashboardTab(val label: String, val icon: String) {
    OVERVIEW("Overview & Progress", "📊"),
    QUIZ_SCORES("Recent Quizzes", "🎯"),
    ACHIEVEMENTS("Achievements", "🏆"),
    RECOMMENDATIONS("Recommendations", "💡")
}

@Composable
fun ParentDashboardScreen(
    profile: StudentProfile,
    weeklyLogs: List<WeeklyStudyLogEntity> = emptyList(),
    quizAttempts: List<QuizAttemptEntity> = emptyList(),
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
    val lockedBadges = remember(allBadges) { allBadges.filter { !it.isUnlocked } }

    var selectedTab by remember { mutableStateOf(ParentDashboardTab.OVERVIEW) }
    var selectedQuizFilter by remember { mutableStateOf("ALL") }
    var selectedAchievementFilter by remember { mutableStateOf("ALL") } // "ALL", "UNLOCKED", "LOCKED"

    // Fallback recent quiz attempts if list is empty
    val displayQuizAttempts = remember(quizAttempts) {
        if (quizAttempts.isNotEmpty()) {
            quizAttempts
        } else {
            val now = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val day = 24 * hour
            listOf(
                QuizAttemptEntity(subject = SubjectType.MATH.name, classLevel = profile.classLevel.name, score = 5, total = 5, starsEarned = 25, timestamp = now - 2 * hour),
                QuizAttemptEntity(subject = SubjectType.ENGLISH.name, classLevel = profile.classLevel.name, score = 4, total = 5, starsEarned = 20, timestamp = now - 7 * hour),
                QuizAttemptEntity(subject = SubjectType.SCIENCE.name, classLevel = profile.classLevel.name, score = 5, total = 5, starsEarned = 25, timestamp = now - 1 * day),
                QuizAttemptEntity(subject = SubjectType.BENGALI.name, classLevel = profile.classLevel.name, score = 4, total = 5, starsEarned = 20, timestamp = now - 2 * day),
                QuizAttemptEntity(subject = SubjectType.MATH.name, classLevel = profile.classLevel.name, score = 5, total = 5, starsEarned = 30, timestamp = now - 3 * day)
            )
        }
    }

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
                            text = "Parent Dashboard 👨‍👩‍👧",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Monitor learning progress, scores & achievements",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Share Report Button
                    IconButton(
                        onClick = {
                            shareProgressReport(context, profile, unlockedBadges, displayQuizAttempts)
                        },
                        modifier = Modifier.testTag("parent_share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Progress Report",
                            tint = PrimaryBlue
                        )
                    }

                    // Read-Only Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Read-only access",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "READ-ONLY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Student Identity Summary Card
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
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(Color(avatar.colorHex).copy(alpha = 0.15f))
                            .border(2.dp, Color(avatar.colorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatar.emoji, fontSize = 34.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = profile.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF0F172A)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Grade A+ • Excellent",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                        Text(
                            text = "${profile.classLevel.label} • Primary Curriculum",
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
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "🏆 ${unlockedBadges.size}/${allBadges.size} Badges",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs (Overview, Quiz Scores, Achievements, Recommendations)
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.White,
                contentColor = PrimaryBlue,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = PrimaryBlue
                    )
                }
            ) {
                ParentDashboardTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = tab.icon, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (isSelected) PrimaryBlue else Color(0xFF64748B)
                                )
                            }
                        },
                        modifier = Modifier.testTag("parent_tab_${tab.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // TAB 1: OVERVIEW & PROGRESS
        if (selectedTab == ParentDashboardTab.OVERVIEW) {
            item {
                // Simplified Data Summary Card
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

                // 7-Day Quiz & Lesson Performance Visualization
                QuizPerformanceChart(modifier = Modifier.testTag("parent_quiz_performance_chart"))

                Spacer(modifier = Modifier.height(20.dp))

                // Subject Mastery Breakdown
                SubjectPerformanceCard(profile = profile)

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // TAB 2: RECENT QUIZ SCORES
        if (selectedTab == ParentDashboardTab.QUIZ_SCORES) {
            item {
                // Section Header & Filters
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_recent_quiz_scores_section"),
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
                            Column {
                                Text(
                                    text = "Recent Quiz Scores 🎯",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Inspect accuracy, question counts, and star earnings",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // Average accuracy pill
                            val avgScore = if (displayQuizAttempts.isNotEmpty()) {
                                (displayQuizAttempts.sumOf { it.score * 100 / it.total.coerceAtLeast(1) } / displayQuizAttempts.size)
                            } else 90
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Avg: $avgScore%",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Subject Filter Pills
                        val filterOptions = listOf(
                            "ALL" to "All Subjects",
                            SubjectType.MATH.name to "🔢 Math",
                            SubjectType.ENGLISH.name to "🔤 English",
                            SubjectType.SCIENCE.name to "🌱 Science",
                            SubjectType.BENGALI.name to "🇧🇩 Bengali"
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filterOptions) { (key, label) ->
                                val isFilterActive = selectedQuizFilter == key
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isFilterActive) PrimaryBlue else Color(0xFFF1F5F9))
                                        .clickable { selectedQuizFilter = key }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("quiz_filter_$key")
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isFilterActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isFilterActive) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            val filteredAttempts = displayQuizAttempts.filter {
                if (selectedQuizFilter == "ALL") true else it.subject.equals(selectedQuizFilter, ignoreCase = true)
            }

            if (filteredAttempts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📝", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No Quiz Attempts Found",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "No recent quiz attempts match this filter. Take a quiz to record scores!",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredAttempts) { attempt ->
                    ParentQuizScoreItem(attempt = attempt)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // TAB 3: ACHIEVEMENTS TRACKER
        if (selectedTab == ParentDashboardTab.ACHIEVEMENTS) {
            item {
                // Achievements Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_achievements_header_card"),
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
                            Column {
                                Text(
                                    text = "Achievement & Trophy Tracker 🏆",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${unlockedBadges.size} of ${allBadges.size} milestones unlocked",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }

                            // Completion percentage
                            val pct = (unlockedBadges.size * 100 / allBadges.size.coerceAtLeast(1))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$pct% Done",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { unlockedBadges.size.toFloat() / allBadges.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFFD97706),
                            trackColor = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Filter Pills (All, Unlocked, In Progress)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "ALL" to "All (${allBadges.size})",
                                "UNLOCKED" to "Unlocked (${unlockedBadges.size}) ✨",
                                "LOCKED" to "In Progress (${lockedBadges.size}) 🔒"
                            ).forEach { (key, label) ->
                                val isSelected = selectedAchievementFilter == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                                        .clickable { selectedAchievementFilter = key }
                                        .padding(vertical = 6.dp)
                                        .testTag("badge_filter_$key"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            val filteredBadges = when (selectedAchievementFilter) {
                "UNLOCKED" -> unlockedBadges
                "LOCKED" -> lockedBadges
                else -> allBadges
            }

            items(filteredBadges) { badge ->
                ParentBadgeDetailItem(badge = badge, profile = profile)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // TAB 4: RECOMMENDATIONS & OFFLINE CACHE
        if (selectedTab == ParentDashboardTab.RECOMMENDATIONS) {
            item {
                AreasForImprovementCard()

                Spacer(modifier = Modifier.height(16.dp))

                // Offline Cache & Safety Notice
                OfflineStorageNoticeCard()

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

/**
 * Quiz Score Item for Parent Review
 */
@Composable
private fun ParentQuizScoreItem(attempt: QuizAttemptEntity) {
    val percentage = ((attempt.score.toFloat() / attempt.total.coerceAtLeast(1)) * 100).toInt()
    val isPerfect = percentage == 100

    val subjectThemeColor = when (attempt.subject.uppercase()) {
        "MATH" -> MathColor
        "ENGLISH" -> EnglishColor
        "SCIENCE" -> ScienceColor
        "BENGALI" -> BengaliColor
        else -> PrimaryBlue
    }

    val subjectEmoji = when (attempt.subject.uppercase()) {
        "MATH" -> "🔢"
        "ENGLISH" -> "🔤"
        "SCIENCE" -> "🌱"
        "BENGALI" -> "🇧🇩"
        else -> "📝"
    }

    val formattedDate = remember(attempt.timestamp) {
        val now = System.currentTimeMillis()
        val diffHours = (now - attempt.timestamp) / (3600 * 1000)
        when {
            diffHours < 1 -> "Just now"
            diffHours < 24 -> "${diffHours}h ago"
            diffHours < 48 -> "Yesterday"
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(attempt.timestamp))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("parent_quiz_score_item_${attempt.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(1.dp, if (isPerfect) Color(0xFFFDE68A) else Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(subjectThemeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = subjectEmoji, fontSize = 22.sp)
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
                        text = "${attempt.subject.replaceFirstChar { it.titlecase() }} Quiz",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )

                    // Score pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isPerfect) Color(0xFFFEF3C7)
                                else if (percentage >= 70) Color(0xFFDCFCE7)
                                else Color(0xFFFEE2E2)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${attempt.score}/${attempt.total} ($percentage%) ${if (isPerfect) "👑" else ""}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = if (isPerfect) Color(0xFFB45309)
                            else if (percentage >= 70) Color(0xFF15803D)
                            else Color(0xFFB91C1C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ +${attempt.starsEarned} Stars • ${attempt.classLevel.replace("_", " ")}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = subjectThemeColor,
                    trackColor = Color(0xFFF1F5F9)
                )
            }
        }
    }
}

/**
 * Detailed Badge Item for Parent Tracking
 */
@Composable
private fun ParentBadgeDetailItem(badge: Badge, profile: StudentProfile) {
    val progressFraction = if (badge.maxProgress > 0) {
        (badge.currentProgress.toFloat() / badge.maxProgress).coerceIn(0f, 1f)
    } else {
        if (badge.isUnlocked) 1f else 0.5f
    }

    val requirementText = if (badge.requirementText.isNotEmpty()) {
        "${badge.requirementText} (${badge.currentProgress}/${badge.maxProgress})"
    } else {
        "Milestone Challenge (${badge.currentProgress}/${badge.maxProgress})"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("parent_badge_item_${badge.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, if (badge.isUnlocked) Color(0xFF86EFAC) else Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (badge.isUnlocked) Color(0xFFDCFCE7) else Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = badge.iconEmoji, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = badge.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (badge.isUnlocked) Color(0xFFDCFCE7) else Color(0xFFE2E8F0))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (badge.isUnlocked) "Unlocked ✨" else "In Progress 🔒",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) Color(0xFF15803D) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = badge.description,
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = requirementText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (badge.isUnlocked) Color(0xFF15803D) else Color(0xFF2563EB)
                    )
                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp)),
                    color = if (badge.isUnlocked) Color(0xFF16A34A) else PrimaryBlue,
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }
    }
}

/**
 * Exports formatted learning summary via Android Share Intent
 */
private fun shareProgressReport(
    context: Context,
    profile: StudentProfile,
    unlockedBadges: List<Badge>,
    attempts: List<QuizAttemptEntity>
) {
    val report = buildString {
        appendLine("🌟 StarLearner Child Progress Report 👨‍👩‍👧")
        appendLine("Student: ${profile.displayName} (${profile.classLevel.label})")
        appendLine("Total Stars: ⭐ ${profile.totalStars} | Daily Streak: 🔥 ${profile.dailyStreak} Days")
        appendLine("Lessons Finished: 📚 ${profile.completedLessons} | Quizzes Completed: 🎯 ${profile.quizzesCompleted}")
        appendLine("\nSubject Mastery Breakdown:")
        appendLine("• Math: ${profile.mathStars} ⭐")
        appendLine("• English: ${profile.englishStars} ⭐")
        appendLine("• Science: ${profile.scienceStars} ⭐")
        appendLine("• Bengali: ${profile.bengaliStars} ⭐")
        appendLine("\nAchievements Unlocked: ${unlockedBadges.size} Badges")
        unlockedBadges.forEach { b -> appendLine("  ${b.iconEmoji} ${b.title}") }
        appendLine("\nRecent Quiz Performance:")
        attempts.take(5).forEach { a ->
            val pct = (a.score * 100 / a.total.coerceAtLeast(1))
            appendLine("  ${a.subject}: ${a.score}/${a.total} ($pct%) • +${a.starsEarned} Stars")
        }
        appendLine("\nGenerated securely on-device with StarLearner.")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "${profile.displayName}'s Learning Report")
        putExtra(Intent.EXTRA_TEXT, report)
    }
    context.startActivity(Intent.createChooser(intent, "Share Progress Report"))
}

/**
 * Simplified summary card requested by parents
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
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
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
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
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
                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
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
                        text = "If your child doesn't open the app for 48 hours, a gentle reminder is automatically scheduled to protect their daily streak.",
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
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
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
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
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
