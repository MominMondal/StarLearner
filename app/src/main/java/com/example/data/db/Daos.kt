package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY classLevel ASC, subject ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE subject = :subject AND classLevel = :classLevel")
    fun getLessonsBySubjectAndClass(subject: String, classLevel: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE medium = :medium AND classLevel = :classLevel")
    fun getLessonsByMediumAndClass(medium: String, classLevel: String): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = 1, starsEarned = :stars, updatedAt = :timestamp WHERE id = :lessonId")
    suspend fun markLessonComplete(lessonId: String, stars: Int, timestamp: Long)

    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1")
    fun getCompletedLessonsCount(): Flow<Int>
}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE subject = :subject ORDER BY timestamp DESC")
    fun getAttemptsBySubject(subject: String): Flow<List<QuizAttemptEntity>>

    @Insert
    suspend fun insertAttempt(attempt: QuizAttemptEntity)
}

@Dao
interface StudyLogDao {
    @Query("SELECT * FROM weekly_study_logs ORDER BY dayIndex ASC")
    fun getWeeklyLogs(): Flow<List<WeeklyStudyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLogs(logs: List<WeeklyStudyLogEntity>)

    @Query("UPDATE weekly_study_logs SET minutesSpent = minutesSpent + :minutes, starsGained = starsGained + :stars WHERE dayOfWeek = :day")
    suspend fun addStudyTime(day: String, minutes: Int, stars: Int)
}

@Dao
interface StudentProfileDao {
    @Query("SELECT * FROM student_profiles WHERE id = 'current_student'")
    fun getProfile(): Flow<StudentProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: StudentProfileEntity)
}
