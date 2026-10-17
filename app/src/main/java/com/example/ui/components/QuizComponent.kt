package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import com.example.data.model.DifficultyLevel
import com.example.data.model.QuizQuestion
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.CoralRed
import com.example.ui.theme.CoralRedLight
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TertiaryAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ReusableQuizEngine(
    questions: List<QuizQuestion>,
    selectedDifficulty: DifficultyLevel = DifficultyLevel.EASY,
    onDifficultyChanged: (DifficultyLevel) -> Unit = {},
    onQuizCompleted: (correctCount: Int, totalCount: Int, starsEarned: Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (questions.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No questions available for this topic and difficulty.")
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var starsWonThisQuiz by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var isHintLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Reset current question when questions list changes (e.g., when difficulty changes)
    LaunchedEffect(questions) {
        if (currentIndex >= questions.size) {
            currentIndex = 0
            selectedOptionIndex = null
            hasAnswered = false
            showHint = false
            isHintLoading = false
        }
    }

    val currentQuestion = questions[currentIndex.coerceIn(0, questions.lastIndex)]
    val totalQuestions = questions.size

    if (isQuizFinished) {
        QuizSummaryScreen(
            score = score,
            total = totalQuestions,
            starsEarned = starsWonThisQuiz,
            difficulty = selectedDifficulty,
            onPlayAgain = {
                currentIndex = 0
                selectedOptionIndex = null
                hasAnswered = false
                score = 0
                starsWonThisQuiz = 0
                isQuizFinished = false
                showHint = false
                isHintLoading = false
            },
            onDone = onClose
        )
        return
    }

    ScaffoldQuizLayout(
        currentQuestion = currentQuestion,
        currentIndex = currentIndex,
        totalQuestions = totalQuestions,
        selectedOptionIndex = selectedOptionIndex,
        hasAnswered = hasAnswered,
        showHint = showHint,
        isHintLoading = isHintLoading,
        selectedDifficulty = selectedDifficulty,
        onDifficultyChanged = onDifficultyChanged,
        onTriggerHint = {
            if (showHint) {
                showHint = false
            } else if (!isHintLoading) {
                isHintLoading = true
                coroutineScope.launch {
                    delay(700) // subtle delay to keep challenge balanced
                    isHintLoading = false
                    showHint = true
                }
            }
        },
        onOptionSelected = { optionIndex ->
            if (!hasAnswered) {
                selectedOptionIndex = optionIndex
                hasAnswered = true
                val isCorrect = optionIndex == currentQuestion.correctAnswerIndex
                val baseStars = if (isCorrect) 10 else 2
                val earned = (baseStars * selectedDifficulty.starsMultiplier).roundToInt()
                if (isCorrect) {
                    score += 1
                    starsWonThisQuiz += earned
                    com.example.service.SoundManagerService.playCorrect(context)
                } else {
                    starsWonThisQuiz += earned // Encouragement participation stars
                    com.example.service.SoundManagerService.playWrong(context)
                }
            }
        },
        onNextQuestion = {
            if (currentIndex + 1 < totalQuestions) {
                currentIndex += 1
                selectedOptionIndex = null
                hasAnswered = false
                showHint = false
                isHintLoading = false
            } else {
                isQuizFinished = true
                com.example.service.SoundManagerService.playQuizComplete(context)
                onQuizCompleted(score, totalQuestions, starsWonThisQuiz)
            }
        },
        onClose = onClose,
        modifier = modifier
    )
}

@Composable
private fun ScaffoldQuizLayout(
    currentQuestion: QuizQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedOptionIndex: Int?,
    hasAnswered: Boolean,
    showHint: Boolean,
    isHintLoading: Boolean,
    selectedDifficulty: DifficultyLevel,
    onDifficultyChanged: (DifficultyLevel) -> Unit,
    onTriggerHint: () -> Unit,
    onOptionSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentIndex.toFloat() + if (hasAnswered) 1f else 0.5f) / totalQuestions

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar: Exit button, Subject/Class badge, Question counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("quiz_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Quiz",
                        tint = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${currentQuestion.subject.title} • ${currentQuestion.classLevel.label}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = currentQuestion.subject.primaryColor
                    )
                }

                Text(
                    text = "${currentIndex + 1} / $totalQuestions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Difficulty Selector Row (Easy, Medium, Hard)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_difficulty_selector_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Level:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    DifficultyLevel.values().forEach { level ->
                        val isSelected = level == selectedDifficulty
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onDifficultyChanged(level) }
                                .testTag("difficulty_chip_${level.name.lowercase()}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PrimaryBlue else Color(0xFFF1F5F9),
                            border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = level.icon, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = level.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("quiz_progress_bar"),
                color = currentQuestion.subject.primaryColor,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_question_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentIndex + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF94A3B8)
                        )

                        // Difficulty tag on question
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${selectedDifficulty.icon} ${selectedDifficulty.label} • ×${selectedDifficulty.starsMultiplier} ⭐",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    // Revealed Hint Card
                    AnimatedVisibility(visible = showHint && currentQuestion.hint.isNotEmpty()) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(12.dp)
                                    .testTag("quiz_hint_content")
                            ) {
                                Text(
                                    text = "💡 Helpful Tip: ${currentQuestion.hint}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentQuestion.questionText,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp
                        ),
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Multiple-Choice Options with Immediate Feedback
            Text(
                text = "Select your answer:",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            currentQuestion.options.forEachIndexed { optIndex, optionText ->
                AnimatedOptionCard(
                    optionLetter = ('A' + optIndex).toString(),
                    optionText = optionText,
                    optionIndex = optIndex,
                    isSelected = selectedOptionIndex == optIndex,
                    isCorrect = optIndex == currentQuestion.correctAnswerIndex,
                    hasAnswered = hasAnswered,
                    onClick = { onOptionSelected(optIndex) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Immediate Feedback Animated Explanation Banner
            AnimatedVisibility(
                visible = hasAnswered,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(300)
                )
            ) {
                val isUserCorrect = selectedOptionIndex == currentQuestion.correctAnswerIndex
                val baseStars = if (isUserCorrect) 10 else 2
                val earned = (baseStars * selectedDifficulty.starsMultiplier).roundToInt()
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_feedback_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUserCorrect) MintGreenLight else CoralRedLight
                        ),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = if (isUserCorrect) MintGreen else CoralRed
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isUserCorrect) "🎉 Outstanding! +$earned ⭐" else "🤔 Almost! Keep learning! +$earned ⭐",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = if (isUserCorrect) EmeraldDark else CoralRed
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQuestion.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isUserCorrect) Color(0xFF065F46) else Color(0xFF991B1B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isUserCorrect) MintGreen else PrimaryBlue
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 < totalQuestions) "Next Question" else "See Results 🌟",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continue",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom floating button
        }

        // Floating 'Hint' Button with Subtle Delay Animation
        if (currentQuestion.hint.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 24.dp)
                    .clip(CircleShape)
                    .clickable(enabled = !isHintLoading) { onTriggerHint() }
                    .testTag("floating_hint_button"),
                shape = CircleShape,
                color = if (showHint) TertiaryAmber else Color(0xFF1E293B),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isHintLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thinking...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Floating Hint Button",
                            tint = if (showHint) Color(0xFF451A03) else TertiaryAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showHint) "Hide Hint" else "Hint 💡",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showHint) Color(0xFF451A03) else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedOptionCard(
    optionLetter: String,
    optionText: String,
    optionIndex: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    hasAnswered: Boolean,
    onClick: () -> Unit
) {
    // Shake animation for incorrect answer
    val coroutineScope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(hasAnswered, isSelected) {
        if (hasAnswered && isSelected && !isCorrect) {
            coroutineScope.launch {
                shakeOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) {
                    // Shake left and right
                }
            }
        }
    }

    // Determine target colors based on feedback state
    val backgroundColor by animateColorAsState(
        targetValue = when {
            !hasAnswered -> Color.White
            isSelected && isCorrect -> MintGreenLight
            isSelected && !isCorrect -> CoralRedLight
            !isSelected && isCorrect -> Color(0xFFE6FFFA) // subtle highlight to show right answer
            else -> Color(0xFFF8FAFC)
        },
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "option_bg_color"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !hasAnswered -> Color(0xFFE2E8F0)
            isSelected && isCorrect -> MintGreen
            isSelected && !isCorrect -> CoralRed
            !isSelected && isCorrect -> MintGreen
            else -> Color(0xFFCBD5E1)
        },
        animationSpec = tween(durationMillis = 350),
        label = "option_border_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "option_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = !hasAnswered) { onClick() }
            .testTag("quiz_option_$optionIndex"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(if (isSelected || (hasAnswered && isCorrect)) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option Letter Badge (A, B, C, D)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            !hasAnswered -> Color(0xFFF1F5F9)
                            isSelected && isCorrect -> MintGreen
                            isSelected && !isCorrect -> CoralRed
                            !isSelected && isCorrect -> MintGreen
                            else -> Color(0xFFE2E8F0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionLetter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = when {
                        !hasAnswered -> Color(0xFF475569)
                        isSelected || isCorrect -> Color.White
                        else -> Color(0xFF64748B)
                    }
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Option Content
            Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = when {
                    !hasAnswered -> Color(0xFF1E293B)
                    isSelected && isCorrect -> Color(0xFF065F46)
                    isSelected && !isCorrect -> Color(0xFF991B1B)
                    !isSelected && isCorrect -> Color(0xFF065F46)
                    else -> Color(0xFF64748B)
                },
                modifier = Modifier.weight(1f)
            )

            // Feedback Icon
            if (hasAnswered) {
                if (isSelected && isCorrect) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct Answer",
                        tint = MintGreen,
                        modifier = Modifier.size(26.dp)
                    )
                } else if (isSelected && !isCorrect) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Wrong Answer",
                        tint = CoralRed,
                        modifier = Modifier.size(26.dp)
                    )
                } else if (isCorrect) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct Answer Indicator",
                        tint = MintGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizSummaryScreen(
    score: Int,
    total: Int,
    starsEarned: Int,
    difficulty: DifficultyLevel = DifficultyLevel.EASY,
    onPlayAgain: () -> Unit,
    onDone: () -> Unit
) {
    val percentage = ((score.toFloat() / total) * 100).toInt()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_summary_screen"),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Trophy / Star burst badge
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(AmberGlow, TertiaryAmber)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (percentage >= 80) "🏆" else if (percentage >= 50) "🌟" else "💪",
                    fontSize = 54.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (percentage == 100) "Perfect Score!" else if (percentage >= 70) "Fantastic Work!" else "Good Effort!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You scored $score out of $total questions correctly ($percentage%)",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stars reward pill
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars Won",
                        tint = TertiaryAmber,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "+$starsEarned Stars Earned!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Difficulty: ${difficulty.label} (${difficulty.icon}) • ×${difficulty.starsMultiplier}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Action buttons
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("quiz_finish_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Return to Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("quiz_retry_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
