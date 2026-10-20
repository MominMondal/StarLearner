package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.DailyMission
import com.example.data.model.DifficultyLevel
import com.example.data.model.MissionType
import com.example.data.model.SubjectType
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages daily learning missions:
 * 1. Generates 3 randomized learning tasks each day based on the calendar date.
 * 2. Persists daily progress and claimed status across app restarts.
 * 3. Tracks progress when students complete quizzes, lessons, or flashcards.
 * 4. Awards bonus star points upon claiming rewards.
 */
object DailyMissionManager {
    private const val PREFS_NAME = "starlearner_daily_missions_prefs"
    private const val KEY_MISSION_DATE = "missions_date"
    private const val KEY_MISSIONS_JSON = "missions_json"

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Loads 3 randomized missions for today. If the date changed, generates a fresh randomized set of 3 tasks.
     * Preserves currentCount and isClaimed for the current calendar day.
     */
    fun loadDailyMissions(context: Context): List<DailyMission> {
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_MISSION_DATE, null)
        val savedJson = prefs.getString(KEY_MISSIONS_JSON, null)

        if (savedDate == today && !savedJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(savedJson)
                val list = mutableListOf<DailyMission>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.getString("id")
                    val template = CurriculumRepository.allMissionTemplates.find { it.id == id }
                    if (template != null) {
                        list.add(
                            template.copy(
                                currentCount = obj.optInt("currentCount", 0),
                                isClaimed = obj.optBoolean("isClaimed", false)
                            )
                        )
                    }
                }
                if (list.size == 3) {
                    return list
                }
            } catch (_: Exception) {
                // fallback to generating fresh missions
            }
        }

        // Generate 3 randomized missions for today
        val newMissions = CurriculumRepository.generateDailyMissions(today)
        saveDailyMissions(context, newMissions)
        return newMissions
    }

    fun saveDailyMissions(context: Context, missions: List<DailyMission>) {
        val today = getTodayDateString()
        val array = JSONArray()
        missions.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("currentCount", m.currentCount)
                put("isClaimed", m.isClaimed)
            }
            array.put(obj)
        }
        getPrefs(context).edit()
            .putString(KEY_MISSION_DATE, today)
            .putString(KEY_MISSIONS_JSON, array.toString())
            .apply()
    }

    fun updateMissionsOnQuizCompleted(
        context: Context,
        currentMissions: List<DailyMission>,
        subject: SubjectType,
        difficulty: DifficultyLevel,
        isPerfectScore: Boolean,
        starsEarned: Int
    ): List<DailyMission> {
        val updated = currentMissions.map { mission ->
            val increment = when (mission.missionType) {
                MissionType.QUIZ_ANY -> 1
                MissionType.QUIZ_MATH -> if (subject == SubjectType.MATH) 1 else 0
                MissionType.QUIZ_ENGLISH -> if (subject == SubjectType.ENGLISH) 1 else 0
                MissionType.QUIZ_SCIENCE -> if (subject == SubjectType.SCIENCE) 1 else 0
                MissionType.QUIZ_HARD -> if (difficulty == DifficultyLevel.HARD) 1 else 0
                MissionType.QUIZ_PERFECT -> if (isPerfectScore) 1 else 0
                MissionType.EARN_STARS -> starsEarned
                else -> 0
            }
            if (increment > 0) {
                mission.copy(currentCount = (mission.currentCount + increment).coerceAtMost(mission.targetCount))
            } else {
                mission
            }
        }
        saveDailyMissions(context, updated)
        return updated
    }

    fun updateMissionsOnLessonCompleted(
        context: Context,
        currentMissions: List<DailyMission>,
        starsEarned: Int
    ): List<DailyMission> {
        val updated = currentMissions.map { mission ->
            val increment = when (mission.missionType) {
                MissionType.LESSON_COMPLETE -> 1
                MissionType.EARN_STARS -> starsEarned
                else -> 0
            }
            if (increment > 0) {
                mission.copy(currentCount = (mission.currentCount + increment).coerceAtMost(mission.targetCount))
            } else {
                mission
            }
        }
        saveDailyMissions(context, updated)
        return updated
    }

    fun updateMissionsOnPhotoReviewed(
        context: Context,
        currentMissions: List<DailyMission>,
        starsEarned: Int = 0
    ): List<DailyMission> {
        val updated = currentMissions.map { mission ->
            val increment = when (mission.missionType) {
                MissionType.PHOTO_FLASHCARDS -> 1
                MissionType.EARN_STARS -> starsEarned
                else -> 0
            }
            if (increment > 0) {
                mission.copy(currentCount = (mission.currentCount + increment).coerceAtMost(mission.targetCount))
            } else {
                mission
            }
        }
        saveDailyMissions(context, updated)
        return updated
    }

    fun claimMission(
        context: Context,
        currentMissions: List<DailyMission>,
        missionId: String
    ): List<DailyMission> {
        val updated = currentMissions.map {
            if (it.id == missionId) it.copy(isClaimed = true) else it
        }
        saveDailyMissions(context, updated)
        return updated
    }
}
