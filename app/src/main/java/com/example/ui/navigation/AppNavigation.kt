package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoLibrary
import com.example.data.db.AppDatabase
import com.example.data.model.ClassLevel
import com.example.data.model.CurriculumMedium
import com.example.data.model.DailyMission
import com.example.data.model.DifficultyLevel
import com.example.data.model.StudentProfile
import com.example.data.model.SubjectType
import com.example.data.repository.CurriculumRepository
import com.example.data.repository.DailyMissionManager
import com.example.data.repository.OfflineRepository
import com.example.ui.components.ReusableQuizEngine
import com.example.ui.screens.BengaliLessonsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MyAwardsScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.PhotoLearningScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    QUIZ,
    PHOTO_LEARNING,
    BENGALI_LESSONS,
    AWARDS,
    LEADERBOARD,
    PROFILE,
    PARENT_DASHBOARD
}

@Composable
fun StarLearnerApp(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize Room Database and Offline Repository
    val database = remember { AppDatabase.getDatabase(context) }
    val offlineRepo = remember { OfflineRepository(database) }

    // Persistent student state
    var studentProfile by remember {
        mutableStateOf(
            StudentProfile(
                displayName = "Alex Cadet",
                avatarId = "lion",
                classLevel = ClassLevel.CLASS_2,
                totalStars = 165,
                dailyStreak = 5,
                completedLessons = 14,
                quizzesCompleted = 9,
                mathStars = 65,
                englishStars = 55,
                scienceStars = 45,
                unlockedBadgeIds = setOf("math_whiz", "early_bird", "streak_3", "science_star", "word_master")
            )
        )
    }

    LaunchedEffect(Unit) {
        offlineRepo.initializeDefaultDataIfEmpty()
        com.example.notification.StreakNotificationManager.recordAppOpened(
            context = context,
            studentName = studentProfile.displayName,
            streakDays = studentProfile.dailyStreak
        )
    }

    val weeklyLogs by offlineRepo.weeklyLogs.collectAsState(initial = emptyList())
    val quizAttempts by offlineRepo.quizAttempts.collectAsState(initial = emptyList())

    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    var selectedClassLevel by remember { mutableStateOf(ClassLevel.CLASS_2) }
    var selectedMedium by remember { mutableStateOf(studentProfile.learningMedium) }
    var activeSubjectForQuiz by remember { mutableStateOf(SubjectType.MATH) }
    var selectedQuizDifficulty by remember { mutableStateOf(DifficultyLevel.EASY) }

    // Daily missions system: 3 randomized achievable learning tasks each day
    var dailyMissions by remember {
        mutableStateOf(DailyMissionManager.loadDailyMissions(context))
    }

    Scaffold(
        modifier = modifier.testTag("app_scaffold"),
        bottomBar = {
            // Hide bottom bar during focused quiz session and parent dashboard
            if (currentScreen != AppScreen.QUIZ && currentScreen != AppScreen.PARENT_DASHBOARD) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { currentScreen = AppScreen.DASHBOARD },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = Color(0xFFEFF6FF)
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.BENGALI_LESSONS,
                        onClick = { currentScreen = AppScreen.BENGALI_LESSONS },
                        icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Lessons") },
                        label = { Text("Lessons 📖", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = Color(0xFFEFF6FF)
                        ),
                        modifier = Modifier.testTag("nav_medium_lessons")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.QUIZ,
                        onClick = {
                            activeSubjectForQuiz = SubjectType.MATH
                            currentScreen = AppScreen.QUIZ
                        },
                        icon = { Icon(Icons.Default.Quiz, contentDescription = "Quick Quiz") },
                        label = { Text("Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = Color(0xFFEFF6FF)
                        ),
                        modifier = Modifier.testTag("nav_quiz")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.AWARDS,
                        onClick = { currentScreen = AppScreen.AWARDS },
                        icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "My Awards") },
                        label = { Text("Awards", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = Color(0xFFEFF6FF)
                        ),
                        modifier = Modifier.testTag("nav_awards")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PHOTO_LEARNING,
                        onClick = { currentScreen = AppScreen.PHOTO_LEARNING },
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Photo Learning") },
                        label = { Text("Photos 📸", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = Color(0xFFEFF6FF)
                        ),
                        modifier = Modifier.testTag("nav_photo_learning")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROFILE,
                        onClick = { currentScreen = AppScreen.PROFILE },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = Color(0xFFEFF6FF)
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.DASHBOARD -> {
                DashboardScreen(
                    profile = studentProfile,
                    selectedClass = selectedClassLevel,
                    selectedMedium = selectedMedium,
                    dailyMissions = dailyMissions,
                    onClaimMission = { claimedMission ->
                        if (claimedMission.isCompleted && !claimedMission.isClaimed) {
                            dailyMissions = DailyMissionManager.claimMission(context, dailyMissions, claimedMission.id)
                            val updatedProfile = studentProfile.copy(
                                totalStars = studentProfile.totalStars + claimedMission.rewardStars
                            )
                            studentProfile = updatedProfile
                            com.example.service.SoundManagerService.playBadgeUnlock(context)
                            coroutineScope.launch {
                                offlineRepo.saveStudentProfile(updatedProfile)
                            }
                        }
                    },
                    onResumeBookmark = { bookmark ->
                        activeSubjectForQuiz = bookmark.subject
                        selectedClassLevel = bookmark.classLevel
                        selectedMedium = bookmark.medium
                        currentScreen = AppScreen.BENGALI_LESSONS
                    },
                    onClassSelected = { selectedClassLevel = it },
                    onMediumSelected = { newMedium ->
                        selectedMedium = newMedium
                        val updated = studentProfile.copy(learningMedium = newMedium)
                        studentProfile = updated
                        coroutineScope.launch {
                            offlineRepo.saveStudentProfile(updated)
                        }
                    },
                    onSubjectClick = { subject ->
                        activeSubjectForQuiz = subject
                        currentScreen = AppScreen.QUIZ
                    },
                    onOpenPhotoLearning = { currentScreen = AppScreen.PHOTO_LEARNING },
                    onOpenBengaliLessons = { currentScreen = AppScreen.BENGALI_LESSONS },
                    onOpenMediumLessons = { currentScreen = AppScreen.BENGALI_LESSONS },
                    onStartQuiz = {
                        activeSubjectForQuiz = SubjectType.MATH
                        currentScreen = AppScreen.QUIZ
                    },
                    onOpenAwards = { currentScreen = AppScreen.AWARDS },
                    onOpenLeaderboard = { currentScreen = AppScreen.LEADERBOARD },
                    onOpenBadges = { currentScreen = AppScreen.AWARDS },
                    onOpenProfile = { currentScreen = AppScreen.PROFILE },
                    onOpenParentDashboard = { currentScreen = AppScreen.PARENT_DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.QUIZ -> {
                val questions = CurriculumRepository.getQuestions(
                    subject = activeSubjectForQuiz,
                    classLevel = selectedClassLevel,
                    difficulty = selectedQuizDifficulty,
                    medium = selectedMedium
                )

                ReusableQuizEngine(
                    questions = questions,
                    selectedDifficulty = selectedQuizDifficulty,
                    onDifficultyChanged = { selectedQuizDifficulty = it },
                    onQuizCompleted = { correctCount, totalCount, starsEarned ->
                        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                        val isFirstQuizToday = (studentProfile.lastQuizDate != todayDate) || !studentProfile.hasCompletedQuizToday
                        val newStreak = if (isFirstQuizToday) studentProfile.dailyStreak + 1 else studentProfile.dailyStreak

                        val newMathStars = if (activeSubjectForQuiz == SubjectType.MATH) studentProfile.mathStars + starsEarned else studentProfile.mathStars
                        val newEngStars = if (activeSubjectForQuiz == SubjectType.ENGLISH) studentProfile.englishStars + starsEarned else studentProfile.englishStars
                        val newSciStars = if (activeSubjectForQuiz == SubjectType.SCIENCE) studentProfile.scienceStars + starsEarned else studentProfile.scienceStars
                        val newBengaliStars = if (activeSubjectForQuiz == SubjectType.BENGALI && selectedMedium == CurriculumMedium.BANGLA) studentProfile.bengaliStars + starsEarned else studentProfile.bengaliStars
                        val newHindiStars = if (selectedMedium == CurriculumMedium.HINDI) studentProfile.hindiStars + starsEarned else studentProfile.hindiStars

                        val updatedBadges = studentProfile.unlockedBadgeIds.toMutableSet()
                        if (newMathStars >= 50) updatedBadges.add("math_whiz")
                        if (newEngStars >= 40) updatedBadges.add("word_master")
                        if (newSciStars >= 40) updatedBadges.add("science_star")
                        if (newBengaliStars >= 40) updatedBadges.add("bengali_star")
                        if (newHindiStars >= 40) updatedBadges.add("hindi_star")
                        if (studentProfile.totalStars + starsEarned >= 100) updatedBadges.add("star_collector")
                        if (newStreak >= 7) updatedBadges.add("streak_7")

                        val updatedProfile = studentProfile.copy(
                            totalStars = studentProfile.totalStars + starsEarned,
                            dailyStreak = newStreak,
                            lastQuizDate = todayDate,
                            hasCompletedQuizToday = true,
                            quizzesCompleted = studentProfile.quizzesCompleted + 1,
                            completedLessons = studentProfile.completedLessons + 1,
                            mathStars = newMathStars,
                            englishStars = newEngStars,
                            scienceStars = newSciStars,
                            bengaliStars = newBengaliStars,
                            hindiStars = newHindiStars,
                            unlockedBadgeIds = updatedBadges
                        )
                        studentProfile = updatedProfile
                        coroutineScope.launch {
                            offlineRepo.saveStudentProfile(updatedProfile)
                        }

                        if (updatedBadges.size > studentProfile.unlockedBadgeIds.size) {
                            com.example.service.SoundManagerService.playBadgeUnlock(context)
                        } else if (isFirstQuizToday) {
                            com.example.service.SoundManagerService.playCorrect(context)
                        }

                        // Update daily missions progress (matching randomized tasks: quiz type, difficulty, perfect score, stars)
                        val isQuizPerfect = (correctCount == totalCount && totalCount > 0)
                        dailyMissions = DailyMissionManager.updateMissionsOnQuizCompleted(
                            context = context,
                            currentMissions = dailyMissions,
                            subject = activeSubjectForQuiz,
                            difficulty = selectedQuizDifficulty,
                            isPerfectScore = isQuizPerfect,
                            starsEarned = starsEarned
                        )

                        // Save attempt and progress to Room Database for offline support
                        coroutineScope.launch {
                            offlineRepo.recordQuizAttempt(
                                subject = activeSubjectForQuiz,
                                classLevel = selectedClassLevel,
                                score = correctCount,
                                total = totalCount,
                                starsEarned = starsEarned
                            )
                            offlineRepo.saveStudentProfile(updatedProfile)
                        }
                    },
                    onClose = { currentScreen = AppScreen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.AWARDS -> {
                MyAwardsScreen(
                    profile = studentProfile,
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.LEADERBOARD -> {
                LeaderboardScreen(
                    currentProfile = studentProfile,
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.PROFILE -> {
                ProfileScreen(
                    profile = studentProfile,
                    onUpdateProfile = { newName, newAvatarId, newClass, newMedium ->
                        val updatedProfile = studentProfile.copy(
                            displayName = newName,
                            avatarId = newAvatarId,
                            classLevel = newClass,
                            learningMedium = newMedium
                        )
                        studentProfile = updatedProfile
                        selectedClassLevel = newClass
                        selectedMedium = newMedium

                        // Save to Room offline database
                        coroutineScope.launch {
                            offlineRepo.saveStudentProfile(updatedProfile)
                        }
                    },
                    onOpenAwards = { currentScreen = AppScreen.AWARDS },
                    onOpenParentDashboard = { currentScreen = AppScreen.PARENT_DASHBOARD },
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.PHOTO_LEARNING -> {
                PhotoLearningScreen(
                    onEarnStars = { stars ->
                        val updatedProfile = studentProfile.copy(
                            totalStars = studentProfile.totalStars + stars,
                            bengaliStars = studentProfile.bengaliStars + stars
                        )
                        studentProfile = updatedProfile
                        dailyMissions = DailyMissionManager.updateMissionsOnPhotoReviewed(
                            context = context,
                            currentMissions = dailyMissions,
                            starsEarned = stars
                        )
                        coroutineScope.launch {
                            offlineRepo.saveStudentProfile(updatedProfile)
                        }
                    },
                    onCardExplored = {
                        dailyMissions = DailyMissionManager.updateMissionsOnPhotoReviewed(
                            context = context,
                            currentMissions = dailyMissions,
                            starsEarned = 0
                        )
                    },
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.BENGALI_LESSONS -> {
                BengaliLessonsScreen(
                    currentClassLevel = selectedClassLevel,
                    initialMedium = selectedMedium,
                    onMediumChanged = { newMedium ->
                        selectedMedium = newMedium
                        val updated = studentProfile.copy(learningMedium = newMedium)
                        studentProfile = updated
                        coroutineScope.launch {
                            offlineRepo.saveStudentProfile(updated)
                        }
                    },
                    onTakeQuiz = {
                        activeSubjectForQuiz = when (selectedMedium) {
                            CurriculumMedium.BANGLA -> SubjectType.BENGALI
                            CurriculumMedium.HINDI -> SubjectType.BENGALI
                            CurriculumMedium.CBSE_ENGLISH -> SubjectType.ENGLISH
                        }
                        currentScreen = AppScreen.QUIZ
                    },
                    onOpenPhotoLearning = {
                        currentScreen = AppScreen.PHOTO_LEARNING
                    },
                    onCompleteLesson = { stars ->
                        val updatedProfile = studentProfile.copy(
                            totalStars = studentProfile.totalStars + stars,
                            bengaliStars = if (selectedMedium == CurriculumMedium.BANGLA) studentProfile.bengaliStars + stars else studentProfile.bengaliStars,
                            hindiStars = if (selectedMedium == CurriculumMedium.HINDI) studentProfile.hindiStars + stars else studentProfile.hindiStars,
                            englishStars = if (selectedMedium == CurriculumMedium.CBSE_ENGLISH) studentProfile.englishStars + stars else studentProfile.englishStars,
                            completedLessons = studentProfile.completedLessons + 1
                        )
                        studentProfile = updatedProfile
                        dailyMissions = DailyMissionManager.updateMissionsOnLessonCompleted(
                            context = context,
                            currentMissions = dailyMissions,
                            starsEarned = stars
                        )
                        coroutineScope.launch {
                            offlineRepo.saveStudentProfile(updatedProfile)
                        }
                    },
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.PARENT_DASHBOARD -> {
                ParentDashboardScreen(
                    profile = studentProfile,
                    weeklyLogs = weeklyLogs,
                    quizAttempts = quizAttempts,
                    onBack = { currentScreen = AppScreen.DASHBOARD },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
