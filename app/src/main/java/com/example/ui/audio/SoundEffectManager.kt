package com.example.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * High-performance, zero-asset audio synthesizer that generates warm, joyful,
 * age-appropriate acoustic feedback for young students.
 */
object SoundEffectManager {

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var isMuted: Boolean = false

    private const val SAMPLE_RATE = 44100

    /**
     * Joyful double-bell/xylophone chime when a quiz question is answered correctly.
     * Ascending bright arpeggio with rich harmonic overtones: C5 -> E5 -> G5 -> C6.
     */
    fun playCorrectSound() {
        if (isMuted) return
        audioScope.launch {
            val notes = listOf(
                Pair(523.25, 75),  // C5
                Pair(659.25, 75),  // E5
                Pair(783.99, 90),  // G5
                Pair(1046.50, 180) // C6 high bell chime
            )
            playToneSequence(notes, decayFactor = 0.88, addHarmonics = true)
        }
    }

    /**
     * Gentle, encouraging tone for incorrect answers. Soft and supportive without being harsh.
     */
    fun playWrongSound() {
        if (isMuted) return
        audioScope.launch {
            val notes = listOf(
                Pair(392.00, 100), // G4
                Pair(311.13, 160)  // Eb4
            )
            playToneSequence(notes, decayFactor = 0.7, addHarmonics = false)
        }
    }

    /**
     * Celebratory victory fanfare upon completing a full quiz session.
     */
    fun playQuizCompletedFanfare() {
        if (isMuted) return
        audioScope.launch {
            val notes = listOf(
                Pair(523.25, 80),   // C5
                Pair(659.25, 80),   // E5
                Pair(783.99, 100),  // G5
                Pair(880.00, 100),  // A5
                Pair(1046.50, 320)  // C6 grand celebration
            )
            playToneSequence(notes, decayFactor = 0.92, addHarmonics = true)
        }
    }

    /**
     * Sparkling, triumphant fanfare played whenever a student unlocks a badge or virtual trophy.
     * E5 -> G#5 -> B5 -> E6 -> G#6 celebratory flourish.
     */
    fun playBadgeUnlockedSound() {
        if (isMuted) return
        audioScope.launch {
            val notes = listOf(
                Pair(659.25, 70),   // E5
                Pair(830.61, 70),   // G#5
                Pair(987.77, 85),   // B5
                Pair(1318.51, 100), // E6
                Pair(1661.22, 320)  // G#6 shimmering sparkle
            )
            playToneSequence(notes, decayFactor = 0.94, addHarmonics = true)
        }
    }

    /**
     * Cheerful pop chime when stars are earned or collected.
     */
    fun playStarPopSound() {
        if (isMuted) return
        audioScope.launch {
            val notes = listOf(
                Pair(880.00, 60),  // A5
                Pair(1174.66, 90)  // D6
            )
            playToneSequence(notes, decayFactor = 0.65, addHarmonics = true)
        }
    }

    private fun playToneSequence(
        notes: List<Pair<Double, Int>>,
        decayFactor: Double,
        addHarmonics: Boolean = true
    ) {
        try {
            var totalSamples = 0
            for ((_, durationMs) in notes) {
                totalSamples += (SAMPLE_RATE * durationMs) / 1000
            }

            val buffer = ShortArray(totalSamples)
            var bufferOffset = 0

            for ((frequency, durationMs) in notes) {
                val numSamples = (SAMPLE_RATE * durationMs) / 1000
                for (i in 0 until numSamples) {
                    val time = i.toDouble() / SAMPLE_RATE
                    val angle = 2.0 * Math.PI * frequency * time

                    // Fundamental frequency
                    var rawSignal = sin(angle)

                    // Warm second and third harmonic overtones for bell-like quality
                    if (addHarmonics) {
                        rawSignal += 0.35 * sin(2.0 * angle) + 0.15 * sin(3.0 * angle)
                    }

                    // Smooth envelope to prevent audio popping
                    val envelope = when {
                        i < numSamples * 0.08 -> i / (numSamples * 0.08) // fast attack
                        else -> Math.pow((numSamples - i).toDouble() / (numSamples * 0.92), decayFactor)
                    }

                    val sample = (rawSignal * envelope * Short.MAX_VALUE * 0.35).toInt().coerceIn(
                        Short.MIN_VALUE.toInt(),
                        Short.MAX_VALUE.toInt()
                    ).toShort()

                    buffer[bufferOffset + i] = sample
                }
                bufferOffset += numSamples
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            val durationTotalMs = notes.sumOf { it.second }
            Thread.sleep(durationTotalMs.toLong() + 40)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Audio failure should never crash the app
        }
    }
}
