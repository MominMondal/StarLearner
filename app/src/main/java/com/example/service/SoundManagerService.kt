package com.example.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.example.ui.audio.SoundEffectManager

/**
 * Android Service for managing sound effects and audio playback across the app.
 * Can be triggered via Intents or bound directly.
 */
class SoundManagerService : Service() {

    companion object {
        const val ACTION_PLAY_CORRECT = "com.example.service.ACTION_PLAY_CORRECT"
        const val ACTION_PLAY_WRONG = "com.example.service.ACTION_PLAY_WRONG"
        const val ACTION_PLAY_BADGE_UNLOCK = "com.example.service.ACTION_PLAY_BADGE_UNLOCK"
        const val ACTION_PLAY_QUIZ_COMPLETE = "com.example.service.ACTION_PLAY_QUIZ_COMPLETE"
        const val ACTION_PLAY_STAR_POP = "com.example.service.ACTION_PLAY_STAR_POP"
        const val ACTION_SET_MUTE = "com.example.service.ACTION_SET_MUTE"
        const val EXTRA_MUTE_STATE = "extra_mute_state"

        fun playCorrect(context: Context) {
            val intent = Intent(context, SoundManagerService::class.java).apply {
                action = ACTION_PLAY_CORRECT
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
                // Direct fallback
                SoundEffectManager.playCorrectSound()
            }
        }

        fun playBadgeUnlock(context: Context) {
            val intent = Intent(context, SoundManagerService::class.java).apply {
                action = ACTION_PLAY_BADGE_UNLOCK
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
                SoundEffectManager.playBadgeUnlockedSound()
            }
        }

        fun playQuizComplete(context: Context) {
            val intent = Intent(context, SoundManagerService::class.java).apply {
                action = ACTION_PLAY_QUIZ_COMPLETE
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
                SoundEffectManager.playQuizCompletedFanfare()
            }
        }

        fun playWrong(context: Context) {
            val intent = Intent(context, SoundManagerService::class.java).apply {
                action = ACTION_PLAY_WRONG
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
                SoundEffectManager.playWrongSound()
            }
        }

        fun playStarPop(context: Context) {
            val intent = Intent(context, SoundManagerService::class.java).apply {
                action = ACTION_PLAY_STAR_POP
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
                SoundEffectManager.playStarPopSound()
            }
        }

        fun setMuted(context: Context, muted: Boolean) {
            SoundEffectManager.isMuted = muted
            val intent = Intent(context, SoundManagerService::class.java).apply {
                action = ACTION_SET_MUTE
                putExtra(EXTRA_MUTE_STATE, muted)
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {}
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_CORRECT -> SoundEffectManager.playCorrectSound()
            ACTION_PLAY_WRONG -> SoundEffectManager.playWrongSound()
            ACTION_PLAY_BADGE_UNLOCK -> SoundEffectManager.playBadgeUnlockedSound()
            ACTION_PLAY_QUIZ_COMPLETE -> SoundEffectManager.playQuizCompletedFanfare()
            ACTION_PLAY_STAR_POP -> SoundEffectManager.playStarPopSound()
            ACTION_SET_MUTE -> {
                val muted = intent.getBooleanExtra(EXTRA_MUTE_STATE, false)
                SoundEffectManager.isMuted = muted
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
