package com.example.ui.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurriculumMedium
import com.example.ui.theme.PrimaryBlue
import java.util.Locale

/**
 * Text-to-speech manager specifically designed for young learners (Classes 1 - 4).
 * Paced at an optimal gentle speed (0.85x) with clear articulation to assist
 * developing readers in listening to letters, words, sentences, rhymes, and stories.
 */
class LessonSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = mutableStateOf(false)
    val isSpeaking: State<Boolean> = _isSpeaking

    private val _currentUtteranceId = mutableStateOf<String?>(null)
    val currentUtteranceId: State<String?> = _currentUtteranceId

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setSpeechRate(0.85f) // Gentle, clear speed for early readers
            tts?.setPitch(1.05f)      // Warm, cheerful pitch

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _currentUtteranceId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }
            })
        }
    }

    fun speak(text: String, medium: CurriculumMedium = CurriculumMedium.CBSE_ENGLISH, utteranceId: String = "") {
        if (!isInitialized || text.isBlank()) return

        val targetLocale = when (medium) {
            CurriculumMedium.BANGLA -> Locale("bn", "BD")
            CurriculumMedium.HINDI -> Locale("hi", "IN")
            CurriculumMedium.CBSE_ENGLISH -> Locale.US
        }

        speakWithLocale(text, targetLocale, utteranceId)
    }

    fun speakWithLocale(text: String, locale: Locale, utteranceId: String = "") {
        if (!isInitialized || text.isBlank()) return

        val id = if (utteranceId.isNotEmpty()) utteranceId else "utt_${System.currentTimeMillis()}"

        try {
            var result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Try language-only fallback
                result = tts?.setLanguage(Locale(locale.language))
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Safe fallback to default locale
                tts?.setLanguage(Locale.getDefault())
            }

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
        } catch (_: Exception) {
            _isSpeaking.value = false
            _currentUtteranceId.value = null
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        isInitialized = false
    }
}

/**
 * Rememberable provider for LessonSpeechManager in Jetpack Compose
 */
@Composable
fun rememberLessonSpeechManager(): LessonSpeechManager {
    val context = LocalContext.current
    val manager = remember { LessonSpeechManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            manager.shutdown()
        }
    }

    return manager
}

/**
 * Animated child-friendly speaker button that reads lesson content aloud.
 * Displays a gentle pulsing animation when speaking, allowing younger students
 * to tap to listen and tap again to stop.
 */
@Composable
fun LessonSpeechButton(
    textToSpeak: String,
    speechManager: LessonSpeechManager,
    medium: CurriculumMedium = CurriculumMedium.CBSE_ENGLISH,
    itemId: String = textToSpeak.take(15),
    buttonLabel: String? = null,
    themeColor: Color = PrimaryBlue,
    size: Dp = 38.dp,
    modifier: Modifier = Modifier
) {
    val isSpeakingThis = speechManager.isSpeaking.value && speechManager.currentUtteranceId.value == itemId

    // Pulse animation when audio is playing
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(isSpeakingThis) {
        if (isSpeakingThis) {
            pulseScale.animateTo(
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseScale.snapTo(1f)
        }
    }

    Surface(
        modifier = modifier
            .scale(if (isSpeakingThis) pulseScale.value else 1f)
            .clip(if (buttonLabel != null) RoundedCornerShape(12.dp) else CircleShape)
            .clickable {
                if (isSpeakingThis) {
                    speechManager.stop()
                } else {
                    speechManager.speak(textToSpeak, medium, itemId)
                }
            }
            .testTag("tts_button_$itemId"),
        shape = if (buttonLabel != null) RoundedCornerShape(12.dp) else CircleShape,
        color = if (isSpeakingThis) themeColor.copy(alpha = 0.2f) else themeColor.copy(alpha = 0.1f),
        border = BorderStroke(
            1.dp,
            if (isSpeakingThis) themeColor else themeColor.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (buttonLabel != null) 10.dp else 8.dp,
                vertical = if (buttonLabel != null) 6.dp else 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSpeakingThis) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isSpeakingThis) "Stop Reading" else "Listen to Lesson Content",
                tint = themeColor,
                modifier = Modifier.size(if (buttonLabel != null) 16.dp else (size * 0.55f))
            )

            if (buttonLabel != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSpeakingThis) "Listening... 🔊" else buttonLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }
        }
    }
}
