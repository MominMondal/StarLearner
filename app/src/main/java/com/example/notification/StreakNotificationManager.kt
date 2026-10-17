package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

/**
 * Manages local notifications for StarLearner, including gentle 48-hour
 * inactivity nudges to protect daily learning streaks.
 */
object StreakNotificationManager {

    const val CHANNEL_ID = "starlearner_streak_nudges"
    const val CHANNEL_NAME = "Streak & Learning Nudges"
    const val CHANNEL_DESC = "Gentle, cheerful reminders to keep your learning streak glowing!"
    const val NOTIFICATION_ID = 2026

    private const val PREFS_NAME = "starlearner_streak_prefs"
    private const val KEY_LAST_OPENED = "last_opened_timestamp"
    private const val KEY_STUDENT_NAME = "student_name"
    private const val KEY_STREAK_COUNT = "streak_count"

    const val ACTION_STREAK_NUDGE = "com.example.ACTION_STREAK_NUDGE"
    const val FORTY_EIGHT_HOURS_MILLIS = 48L * 60L * 60L * 1000L

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Initializes the notification channel on Android 8.0+ (Oreo).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Records that the student opened the app now, saving the timestamp
     * and resetting the 48-hour reminder timer.
     */
    fun recordAppOpened(context: Context, studentName: String = "Star Learner", streakDays: Int = 1) {
        val now = System.currentTimeMillis()
        getPrefs(context).edit()
            .putLong(KEY_LAST_OPENED, now)
            .putString(KEY_STUDENT_NAME, studentName)
            .putInt(KEY_STREAK_COUNT, streakDays)
            .apply()

        createNotificationChannel(context)
        schedule48HourReminder(context, studentName, streakDays)
    }

    /**
     * Schedules a gentle nudge 48 hours from now using AlarmManager.
     */
    fun schedule48HourReminder(context: Context, studentName: String, streakDays: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAtMillis = System.currentTimeMillis() + FORTY_EIGHT_HOURS_MILLIS

        val intent = Intent(context, StreakReminderReceiver::class.java).apply {
            action = ACTION_STREAK_NUDGE
            putExtra("student_name", studentName)
            putExtra("streak_days", streakDays)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getBroadcast(context, 1001, intent, flags)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            // Gracefully handle devices where exact alarm scheduling requires user toggle
        }
    }

    /**
     * Sends an immediate gentle nudge notification. Perfect for user previewing
     * or verifying notification permissions in the Parent Portal or Dashboard.
     */
    fun sendNudgeNotification(
        context: Context,
        studentName: String = "Star Learner",
        streakDays: Int = 5,
        isTest: Boolean = false
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(context, 2002, intent, flags)

        val title = if (isTest) {
            "🌟 [Preview] Your $streakDays-Day Streak Misses You!"
        } else {
            "🌟 Keep your $streakDays-day Star Streak alive, $studentName!"
        }

        val message = "You're doing fantastic! Pop in for a quick 2-minute quiz today to keep your streak glowing bright! 🚀"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }
}

/**
 * BroadcastReceiver triggered after 48 hours of inactivity or on device reboot.
 */
class StreakReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = StreakNotificationManager.getPrefs(context)
        val lastOpened = prefs.getLong("last_opened_timestamp", 0L)
        val studentName = intent.getStringExtra("student_name") ?: prefs.getString("student_name", "Star Learner") ?: "Star Learner"
        val streakDays = intent.getIntExtra("streak_days", prefs.getInt("streak_count", 1))

        val now = System.currentTimeMillis()
        val hoursSinceOpened = (now - lastOpened) / (1000 * 60 * 60)

        // Only fire if 48+ hours have elapsed since the app was last opened
        if (hoursSinceOpened >= 47 || lastOpened == 0L) {
            StreakNotificationManager.sendNudgeNotification(
                context = context,
                studentName = studentName,
                streakDays = streakDays,
                isTest = false
            )
        }
    }
}
