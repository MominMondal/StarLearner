package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.SoundManagerService
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ConfettiShape {
    RECTANGLE,
    CIRCLE,
    STAR,
    RIBBON
}

private data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float,
    var vRot: Float,
    var size: Float,
    val color: Color,
    val shape: ConfettiShape,
    val swaySpeed: Float,
    val swayAmplitude: Float,
    var alpha: Float = 1.0f
)

private val ConfettiColors = listOf(
    Color(0xFFF59E0B), // Vibrant Amber Gold
    Color(0xFF10B981), // Emerald Green
    Color(0xFF3B82F6), // Royal Blue
    Color(0xFFEF4444), // Coral Red
    Color(0xFF8B5CF6), // Royal Purple
    Color(0xFFEC4899), // Pink
    Color(0xFF06B6D4), // Cyan
    Color(0xFFFFD700)  // Gold
)

@Composable
fun ConfettiCelebrationOverlay(
    isVisible: Boolean,
    title: String = "PERFECT SCORE! 100%",
    subtitle: String = "Flawless work! You answered every question correctly on the first attempt!",
    bonusText: String = "+30 Bonus Stars Earned! 🌟",
    badgeEmoji: String = "🏆",
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(isVisible) {
        if (isVisible) {
            try {
                SoundManagerService.playBadgeUnlock(context)
            } catch (_: Exception) {}
        }
    }

    // 85 Confetti particles
    val particles = remember {
        val rand = Random(System.currentTimeMillis())
        List(85) {
            val initialAngle = rand.nextDouble(0.0, 2 * PI).toFloat()
            val initialSpeed = rand.nextDouble(80.0, 380.0).toFloat()
            ConfettiParticle(
                x = rand.nextFloat(), // fractional [0..1]
                y = rand.nextFloat() * -0.5f, // start above top of screen
                vx = cos(initialAngle) * (initialSpeed / 600f) + (rand.nextFloat() - 0.5f) * 0.05f,
                vy = rand.nextDouble(0.25, 0.75).toFloat(), // downward speed
                rotation = rand.nextFloat() * 360f,
                vRot = (rand.nextFloat() - 0.5f) * 12f,
                size = rand.nextDouble(8.0, 18.0).toFloat(),
                color = ConfettiColors[rand.nextInt(ConfettiColors.size)],
                shape = ConfettiShape.values()[rand.nextInt(ConfettiShape.values().size)],
                swaySpeed = rand.nextDouble(1.5, 4.0).toFloat(),
                swayAmplitude = rand.nextDouble(0.005, 0.02).toFloat()
            )
        }
    }

    var animationTime by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isVisible) {
        var lastTimeNanos = 0L
        while (isVisible) {
            withFrameNanos { frameTimeNanos ->
                if (lastTimeNanos != 0L) {
                    val dt = ((frameTimeNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    animationTime += dt

                    particles.forEach { p ->
                        p.y += p.vy * dt
                        p.x += (p.vx + sin(animationTime * p.swaySpeed) * p.swayAmplitude) * dt
                        p.rotation += p.vRot

                        // When particle falls below screen, loop from top with new random x
                        if (p.y > 1.15f) {
                            p.y = -0.1f
                            p.x = Random.nextFloat()
                        }
                    }
                }
                lastTimeNanos = frameTimeNanos
            }
        }
    }

    // Pulsing badge animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_confetti")
    val badgeScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(250)) + scaleIn(tween(350)),
        exit = fadeOut(tween(200)) + scaleOut(tween(250))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
                .testTag("confetti_celebration_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Live falling confetti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                particles.forEach { p ->
                    val px = p.x * canvasW
                    val py = p.y * canvasH

                    rotate(degrees = p.rotation, pivot = Offset(px, py)) {
                        drawConfettiShape(p, px, py)
                    }
                }
            }

            // Central Celebration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* absorb clicks */ }
                    .testTag("celebration_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing golden trophy / star circle
                    Box(
                        modifier = Modifier
                            .scale(badgeScale)
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A), Color(0xFFF59E0B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeEmoji,
                            fontSize = 48.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtitle
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bonus Reward Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("celebration_bonus_tag")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = bonusText,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF065F46)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Button
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("celebration_dismiss_button")
                    ) {
                        Text(
                            text = "Celebrate & Continue 🎊",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawConfettiShape(p: ConfettiParticle, px: Float, py: Float) {
    val s = p.size
    when (p.shape) {
        ConfettiShape.RECTANGLE -> {
            drawRect(
                color = p.color,
                topLeft = Offset(px - s / 2, py - s / 3),
                size = Size(s, s * 0.65f)
            )
        }
        ConfettiShape.CIRCLE -> {
            drawCircle(
                color = p.color,
                radius = s / 2,
                center = Offset(px, py)
            )
        }
        ConfettiShape.RIBBON -> {
            drawRect(
                color = p.color,
                topLeft = Offset(px - s / 4, py - s),
                size = Size(s * 0.5f, s * 1.8f)
            )
        }
        ConfettiShape.STAR -> {
            val path = Path()
            val points = 5
            val outerRadius = s * 0.7f
            val innerRadius = outerRadius * 0.45f
            var angle = -PI.toFloat() / 2f
            val step = PI.toFloat() / points

            path.moveTo(
                px + outerRadius * cos(angle),
                py + outerRadius * sin(angle)
            )
            for (i in 0 until points * 2) {
                val r = if (i % 2 == 0) innerRadius else outerRadius
                angle += step
                path.lineTo(
                    px + r * cos(angle),
                    py + r * sin(angle)
                )
            }
            path.close()
            drawPath(path, color = p.color)
        }
    }
}
