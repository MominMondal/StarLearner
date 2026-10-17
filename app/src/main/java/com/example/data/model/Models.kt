package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BengaliColor
import com.example.ui.theme.BengaliColorLight
import com.example.ui.theme.EnglishColor
import com.example.ui.theme.EnglishColorLight
import com.example.ui.theme.MathColor
import com.example.ui.theme.MathColorLight
import com.example.ui.theme.ScienceColor
import com.example.ui.theme.ScienceColorLight

enum class ClassLevel(val label: String, val shortLabel: String, val description: String) {
    CLASS_1("Class 1", "Gr 1", "Foundational Basics"),
    CLASS_2("Class 2", "Gr 2", "Skill Builders"),
    CLASS_3("Class 3", "Gr 3", "Intermediate Concepts"),
    CLASS_4("Class 4", "Gr 4", "Advanced Mastery")
}

enum class SubjectType(
    val title: String,
    val iconEmoji: String,
    val primaryColor: Color,
    val lightBgColor: Color,
    val subtitle: String
) {
    MATH(
        title = "Math",
        iconEmoji = "🔢",
        primaryColor = MathColor,
        lightBgColor = MathColorLight,
        subtitle = "Numbers, Shapes & Problem Solving"
    ),
    ENGLISH(
        title = "English",
        iconEmoji = "🔤",
        primaryColor = EnglishColor,
        lightBgColor = EnglishColorLight,
        subtitle = "Phonics, Words & Reading Stories"
    ),
    SCIENCE(
        title = "Science",
        iconEmoji = "🌱",
        primaryColor = ScienceColor,
        lightBgColor = ScienceColorLight,
        subtitle = "Nature, Animals & Discovery"
    ),
    BENGALI(
        title = "Bengali",
        iconEmoji = "🇧🇩",
        primaryColor = BengaliColor,
        lightBgColor = BengaliColorLight,
        subtitle = "বাংলা বর্ণমালা, শব্দ ও গল্প (Alphabet & Stories)"
    )
}

enum class CurriculumMedium(
    val label: String,
    val nativeLabel: String,
    val flagEmoji: String,
    val shortCode: String,
    val description: String
) {
    BANGLA(
        label = "Bangla Medium",
        nativeLabel = "বাংলা মাধ্যম",
        flagEmoji = "🇧🇩",
        shortCode = "Bangla",
        description = "WBBSE & NCTB regional board with lessons in Bengali"
    ),
    HINDI(
        label = "Hindi Medium",
        nativeLabel = "हिंदी माध्यम",
        flagEmoji = "🇮🇳",
        shortCode = "Hindi",
        description = "NCERT & State Boards with lessons in Hindi"
    ),
    CBSE_ENGLISH(
        label = "CBSE (English)",
        nativeLabel = "CBSE English",
        flagEmoji = "🌍",
        shortCode = "CBSE",
        description = "Central Board of Secondary Education English syllabus"
    )
}

enum class DifficultyLevel(val label: String, val starsMultiplier: Float, val icon: String, val description: String) {
    EASY("Easy", 1.0f, "🟢", "Gentle pace & clear hints"),
    MEDIUM("Medium", 1.5f, "🟡", "Standard challenge"),
    HARD("Hard", 2.0f, "🔴", "Tricky & higher star bonus!")
}

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val targetCount: Int,
    val currentCount: Int,
    val rewardStars: Int,
    val isClaimed: Boolean = false
) {
    val isCompleted: Boolean get() = currentCount >= targetCount
}

data class QuizQuestion(
    val id: String,
    val subject: SubjectType,
    val classLevel: ClassLevel,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val hint: String = "",
    val difficulty: DifficultyLevel = DifficultyLevel.EASY,
    val medium: CurriculumMedium = CurriculumMedium.CBSE_ENGLISH,
    val imageEmoji: String? = null
)

data class AnimalAvatar(
    val id: String,
    val name: String,
    val emoji: String,
    val colorHex: Long,
    val personality: String
)

data class Badge(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val description: String,
    val requirementText: String,
    val isUnlocked: Boolean,
    val currentProgress: Int,
    val maxProgress: Int
)

data class LessonBookmark(
    val lessonId: String,
    val title: String,
    val subject: SubjectType,
    val classLevel: ClassLevel,
    val medium: CurriculumMedium = CurriculumMedium.CBSE_ENGLISH,
    val durationMinutes: Int = 15,
    val progressPercentage: Int = 60
)

data class StudentProfile(
    val displayName: String = "Star Cadet",
    val avatarId: String = "lion",
    val classLevel: ClassLevel = ClassLevel.CLASS_2,
    val learningMedium: CurriculumMedium = CurriculumMedium.CBSE_ENGLISH,
    val totalStars: Int = 145,
    val dailyStreak: Int = 4,
    val completedLessons: Int = 12,
    val quizzesCompleted: Int = 9,
    val mathStars: Int = 60,
    val englishStars: Int = 45,
    val scienceStars: Int = 40,
    val bengaliStars: Int = 45,
    val hindiStars: Int = 40,
    val unlockedBadgeIds: Set<String> = setOf("first_lesson", "math_whiz", "streak_3", "bengali_star", "hindi_star"),
    val activeBookmark: LessonBookmark? = LessonBookmark(
        lessonId = "l_m2_1",
        title = "Two-Digit Addition: Regrouping Tens & Ones",
        subject = SubjectType.MATH,
        classLevel = ClassLevel.CLASS_2,
        medium = CurriculumMedium.CBSE_ENGLISH,
        durationMinutes = 18,
        progressPercentage = 65
    ),
    val lastQuizDate: String = "2026-10-12",
    val hasCompletedQuizToday: Boolean = false
)

enum class TrophyTier(val label: String, val icon: String, val glowColorHex: Long) {
    BRONZE("Bronze Cup", "🥉", 0xFFCD7F32),
    SILVER("Silver Cup", "🥈", 0xFFC0C0C0),
    GOLD("Gold Trophy", "🥇", 0xFFFFD700),
    DIAMOND("Grand Master Trophy", "👑", 0xFF38BDF8)
}

data class VirtualTrophy(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: String, // "Quiz Mastery", "Daily Habit", "Subject Champion", "Star Legend"
    val tier: TrophyTier,
    val trophyEmoji: String,
    val requirementText: String,
    val isUnlocked: Boolean,
    val currentProgress: Int,
    val targetProgress: Int,
    val earnedDate: String? = null
)

data class QuizPerformanceDay(
    val dayLabel: String, // e.g., "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today"
    val dateLabel: String, // e.g., "Oct 7"
    val isToday: Boolean = false,
    val mathScorePercent: Int,
    val englishScorePercent: Int,
    val scienceScorePercent: Int,
    val overallAveragePercent: Int,
    val totalQuizzesTaken: Int
)

data class SubjectPerformanceMetric(
    val subject: SubjectType,
    val averageAccuracyPercent: Int,
    val totalQuizzes: Int,
    val improvementPercent: Int,
    val trendIsPositive: Boolean
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val avatarEmoji: String,
    val classLevel: ClassLevel,
    val stars: Int,
    val streak: Int,
    val isCurrentUser: Boolean = false
)

data class PhotoLearningCard(
    val id: String,
    val emoji: String,
    val englishWord: String,
    val bengaliWord: String,
    val bengaliPronunciation: String,
    val category: String, // "Animals & Birds", "Fruits & Food", "Everyday Things", "Nature & Colors"
    val funFactEnglish: String,
    val funFactBengali: String,
    val letterHint: String = ""
)

data class PhotoQuizChallenge(
    val id: String,
    val photoEmoji: String,
    val questionBengali: String,
    val questionEnglish: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

