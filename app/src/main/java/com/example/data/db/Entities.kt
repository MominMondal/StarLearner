package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val classLevel: String,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val starsEarned: Int = 0,
    val medium: String = "CBSE_ENGLISH",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val classLevel: String,
    val score: Int,
    val total: Int,
    val starsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weekly_study_logs")
data class WeeklyStudyLogEntity(
    @PrimaryKey val dayOfWeek: String, // "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    val dayIndex: Int, // 0 for Mon ... 6 for Sun
    val minutesSpent: Int,
    val lessonsCompleted: Int,
    val quizzesAced: Int,
    val starsGained: Int
)

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey val id: String = "current_student",
    val displayName: String,
    val avatarId: String,
    val classLevel: String,
    val totalStars: Int,
    val dailyStreak: Int,
    val completedLessons: Int,
    val quizzesCompleted: Int,
    val mathStars: Int,
    val englishStars: Int,
    val scienceStars: Int,
    val bengaliStars: Int = 45,
    val hindiStars: Int = 40,
    val learningMedium: String = "CBSE_ENGLISH",
    val unlockedBadgesString: String
)
