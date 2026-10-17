package com.example

import com.example.data.model.ClassLevel
import com.example.data.model.DifficultyLevel
import com.example.data.model.LessonBookmark
import com.example.data.model.StudentProfile
import com.example.data.model.SubjectType
import com.example.data.repository.CurriculumRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun defaultDailyMissions_providesThreeAchievableTasks() {
        val missions = CurriculumRepository.getDefaultDailyMissions()
        assertEquals(3, missions.size)
        assertTrue(missions.any { it.title.contains("Quiz", ignoreCase = true) })
        assertTrue(missions.any { it.title.contains("Star", ignoreCase = true) })
        missions.forEach { mission ->
            assertTrue(mission.rewardStars > 0)
            assertFalse(mission.isClaimed)
        }
    }

    @Test
    fun getQuestions_filtersByDifficultyAndSubject() {
        val easyMath = CurriculumRepository.getQuestions(
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_2,
            difficulty = DifficultyLevel.EASY
        )
        assertTrue(easyMath.isNotEmpty())
        assertEquals(SubjectType.MATH, easyMath.first().subject)

        val hardMath = CurriculumRepository.getQuestions(
            subject = SubjectType.MATH,
            classLevel = ClassLevel.CLASS_2,
            difficulty = DifficultyLevel.HARD
        )
        assertTrue(hardMath.isNotEmpty())
    }

    @Test
    fun studentProfile_continueReadingBookmark() {
        val profile = StudentProfile()
        assertNotNull(profile.activeBookmark)
        val bookmark = profile.activeBookmark!!
        assertEquals(SubjectType.MATH, bookmark.subject)
        assertEquals(ClassLevel.CLASS_2, bookmark.classLevel)
        assertTrue(bookmark.progressPercentage > 0)
    }

    @Test
    fun difficultyLevel_multipliersAreCorrect() {
        assertEquals(1.0f, DifficultyLevel.EASY.starsMultiplier, 0.01f)
        assertEquals(1.5f, DifficultyLevel.MEDIUM.starsMultiplier, 0.01f)
        assertEquals(2.0f, DifficultyLevel.HARD.starsMultiplier, 0.01f)
    }

    @Test
    fun virtualTrophies_containsMilestonesAndQuizHighScores() {
        val profile = StudentProfile(
            totalStars = 150,
            quizzesCompleted = 6,
            mathStars = 55,
            englishStars = 45,
            scienceStars = 40,
            dailyStreak = 7
        )
        val trophies = CurriculumRepository.getVirtualTrophies(profile)
        assertTrue(trophies.isNotEmpty())
        assertTrue(trophies.any { it.category == "Quiz High-Score" })
        assertTrue(trophies.any { it.id == "trophy_quiz_prodigy" && it.isUnlocked })
        assertTrue(trophies.any { it.id == "trophy_math_olympus" && it.isUnlocked })
        assertTrue(trophies.any { it.id == "trophy_streak_fire" && it.isUnlocked })
    }

    @Test
    fun sevenDayQuizPerformance_coversSevenDaysWithSubjectBreakdown() {
        val days = CurriculumRepository.getSevenDayQuizPerformance()
        assertEquals(7, days.size)
        val lastDay = days.last()
        assertTrue(lastDay.isToday)
        assertTrue(lastDay.mathScorePercent in 0..100)
        assertTrue(lastDay.englishScorePercent in 0..100)
        assertTrue(lastDay.scienceScorePercent in 0..100)
        assertTrue(lastDay.overallAveragePercent in 0..100)
    }

    @Test
    fun firstQuizOfDay_incrementsDailyStreak() {
        val initialProfile = StudentProfile(
            dailyStreak = 4,
            lastQuizDate = "2026-10-12",
            hasCompletedQuizToday = false
        )
        val today = "2026-10-13"
        val isFirstQuizToday = (initialProfile.lastQuizDate != today) || !initialProfile.hasCompletedQuizToday
        assertTrue(isFirstQuizToday)

        val updatedProfile = initialProfile.copy(
            dailyStreak = if (isFirstQuizToday) initialProfile.dailyStreak + 1 else initialProfile.dailyStreak,
            lastQuizDate = today,
            hasCompletedQuizToday = true
        )
        assertEquals(5, updatedProfile.dailyStreak)
        assertTrue(updatedProfile.hasCompletedQuizToday)
    }

    @Test
    fun streakNotificationManager_fortyEightHourIntervalIsAccurate() {
        val fortyEightHours = com.example.notification.StreakNotificationManager.FORTY_EIGHT_HOURS_MILLIS
        assertEquals(48L * 60L * 60L * 1000L, fortyEightHours)
        assertEquals("starlearner_streak_nudges", com.example.notification.StreakNotificationManager.CHANNEL_ID)
        assertEquals("com.example.ACTION_STREAK_NUDGE", com.example.notification.StreakNotificationManager.ACTION_STREAK_NUDGE)
    }

    @Test
    fun soundManagerService_actionsAreProperlyDefined() {
        assertEquals("com.example.service.ACTION_PLAY_CORRECT", com.example.service.SoundManagerService.ACTION_PLAY_CORRECT)
        assertEquals("com.example.service.ACTION_PLAY_BADGE_UNLOCK", com.example.service.SoundManagerService.ACTION_PLAY_BADGE_UNLOCK)
        assertEquals("com.example.service.ACTION_PLAY_QUIZ_COMPLETE", com.example.service.SoundManagerService.ACTION_PLAY_QUIZ_COMPLETE)
        assertEquals("com.example.service.ACTION_PLAY_WRONG", com.example.service.SoundManagerService.ACTION_PLAY_WRONG)
    }

    @Test
    fun parentPortal_badgeSummaryReflectsStudentProgress() {
        val student = StudentProfile(
            displayName = "Maya",
            totalStars = 200,
            dailyStreak = 5,
            mathStars = 60,
            englishStars = 45,
            scienceStars = 40,
            bengaliStars = 45
        )
        val badges = CurriculumRepository.getAllBadges(student)
        assertTrue(badges.isNotEmpty())
        val unlocked = badges.filter { it.isUnlocked }
        assertTrue(unlocked.any { it.id == "math_whiz" })
        assertTrue(unlocked.any { it.id == "word_master" })
        assertTrue(unlocked.any { it.id == "science_star" })
        assertTrue(unlocked.any { it.id == "bengali_star" })
    }

    @Test
    fun bengaliSubject_questionsExistWithCorrectAnswers() {
        val bengaliQ = CurriculumRepository.getQuestions(
            subject = SubjectType.BENGALI,
            classLevel = ClassLevel.CLASS_1,
            difficulty = DifficultyLevel.EASY
        )
        assertTrue("Bengali questions should be available", bengaliQ.isNotEmpty())
        bengaliQ.forEach { question ->
            assertEquals(SubjectType.BENGALI, question.subject)
            assertTrue(question.options.isNotEmpty())
            assertTrue(question.correctAnswerIndex in question.options.indices)
            assertTrue(question.explanation.isNotEmpty())
        }
    }

    @Test
    fun photoLearning_cardsHaveBilingualDataAndCategories() {
        val cards = CurriculumRepository.getPhotoLearningCards()
        assertTrue("Photo learning cards should not be empty", cards.isNotEmpty())
        cards.forEach { card ->
            assertTrue(card.emoji.isNotEmpty())
            assertTrue(card.bengaliWord.isNotEmpty())
            assertTrue(card.englishWord.isNotEmpty())
            assertTrue(card.bengaliPronunciation.isNotEmpty())
            assertTrue(card.category.isNotEmpty())
            assertTrue(card.funFactBengali.isNotEmpty())
        }

        // Verify categories exist
        val categories = cards.map { it.category }.distinct()
        assertTrue(categories.contains("Animals & Birds"))
        assertTrue(categories.contains("Fruits & Food"))
    }

    @Test
    fun photoQuizChallenges_haveValidQuestionsAndExplanations() {
        val quizzes = CurriculumRepository.getPhotoQuizChallenges()
        assertTrue("Photo quiz challenges should exist", quizzes.isNotEmpty())
        quizzes.forEach { q ->
            assertTrue(q.photoEmoji.isNotEmpty())
            assertTrue(q.questionBengali.isNotEmpty())
            assertTrue(q.options.size >= 3)
            assertTrue(q.correctIndex in q.options.indices)
            assertTrue(q.explanation.isNotEmpty())
        }
    }
}



