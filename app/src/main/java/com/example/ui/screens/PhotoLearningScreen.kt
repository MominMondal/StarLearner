package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PhotoLearningCard
import com.example.data.model.PhotoQuizChallenge
import com.example.data.repository.CurriculumRepository
import com.example.service.SoundManagerService
import com.example.ui.components.ConfettiCelebrationOverlay
import com.example.ui.theme.BengaliColor
import com.example.ui.theme.BengaliColorLight
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PrimaryBlue

enum class PhotoLearningTab {
    FLASHCARDS,
    PHOTO_QUIZ
}

@Composable
fun PhotoLearningScreen(
    onEarnStars: (Int) -> Unit = {},
    onCardExplored: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val allCards = remember { CurriculumRepository.getPhotoLearningCards() }
    val allQuizzes = remember { CurriculumRepository.getPhotoQuizChallenges() }

    var selectedTab by remember { mutableStateOf(PhotoLearningTab.FLASHCARDS) }
    var selectedCategory by remember { mutableStateOf("All") }
    var inspectCard by remember { mutableStateOf<PhotoLearningCard?>(null) }

    val categories = listOf("All", "Fruits & Food", "Animals & Birds", "Nature & Colors", "Everyday Things")

    val filteredCards = remember(selectedCategory, allCards) {
        if (selectedCategory == "All") allCards else allCards.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("photo_learning_screen")
    ) {
        // Top Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("photo_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E293B)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ছবি দেখে শেখো 📸",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BengaliColorLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "বাংলা ও English",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BengaliColor
                                    )
                                }
                            }
                            Text(
                                text = "Visual Photo Discovery & Picture Quiz",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Sound Hint Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable {
                                SoundManagerService.playCorrect(context)
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Audio feedback active", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Sub-tabs: Flashcards vs Photo Quiz
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color.White,
                    contentColor = BengaliColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = BengaliColor,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == PhotoLearningTab.FLASHCARDS,
                        onClick = {
                            selectedTab = PhotoLearningTab.FLASHCARDS
                            SoundManagerService.playStarPop(context)
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ফ্ল্যাশ কার্ড (Explore)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_photo_flashcards")
                    )

                    Tab(
                        selected = selectedTab == PhotoLearningTab.PHOTO_QUIZ,
                        onClick = {
                            selectedTab = PhotoLearningTab.PHOTO_QUIZ
                            SoundManagerService.playStarPop(context)
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ছবি দেখে কুইজ (Quiz)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_photo_quiz")
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            PhotoLearningTab.FLASHCARDS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories) { cat ->
                                val isSelected = cat == selectedCategory
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) BengaliColor else Color.White)
                                        .border(
                                            1.dp,
                                            if (isSelected) BengaliColor else Color(0xFFE2E8F0),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable {
                                            selectedCategory = cat
                                            SoundManagerService.playStarPop(context)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                        .testTag("category_chip_$cat")
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Instruction Helper Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                            border = BorderStroke(1.dp, Color(0xFFFFCCD5))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💡", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ছবিতে ট্যাপ করো ও উচ্চারণ শোনো!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF9F1239)
                                    )
                                    Text(
                                        text = "Tap any photo card to hear cheerful sounds & see Bengali pronunciation details.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF881337)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Cards Grid (2 columns representation in vertical LazyColumn)
                    val chunkedCards = filteredCards.chunked(2)
                    items(chunkedCards) { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { card ->
                                PhotoCardItem(
                                    card = card,
                                    onClick = {
                                        inspectCard = card
                                        SoundManagerService.playCorrect(context)
                                        onCardExplored()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }

            PhotoLearningTab.PHOTO_QUIZ -> {
                PhotoQuizSection(
                    challenges = allQuizzes,
                    onEarnStars = onEarnStars,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Photo Card Detail Dialog
    inspectCard?.let { card ->
        Dialog(onDismissRequest = { inspectCard = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("photo_detail_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { inspectCard = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(BengaliColorLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = card.emoji, fontSize = 56.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = card.bengaliWord,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = "${card.englishWord} • Pronounced: '${card.bengaliPronunciation}'",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BengaliColor
                    )

                    if (card.letterHint.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "বর্ণ নির্দেশিকা: '${card.letterHint}' দিয়ে শুরু",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "মজার তথ্য (Fun Fact) 🌟",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = card.funFactBengali,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = card.funFactEnglish,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            SoundManagerService.playBadgeUnlock(context)
                            inspectCard = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BengaliColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("দারুণ! (Got it!)", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoCardItem(
    card: PhotoLearningCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("photo_card_${card.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(BengaliColorLight.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = card.emoji, fontSize = 38.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = card.bengaliWord,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )

            Text(
                text = card.englishWord,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "🔊 ${card.bengaliPronunciation}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
private fun PhotoQuizSection(
    challenges: List<PhotoQuizChallenge>,
    onEarnStars: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    val currentChallenge = challenges[currentIndex.coerceIn(0, challenges.lastIndex)]
    val isCorrect = selectedOption == currentChallenge.correctIndex
    val maxScore = challenges.size * 10
    val isPerfect = score == maxScore && maxScore > 0

    Box(modifier = modifier.fillMaxSize()) {
        if (isQuizFinished) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("photo_quiz_summary"),
                color = Color(0xFFF8FAFC)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(if (isPerfect) Color(0xFFFEF3C7) else Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (isPerfect) "👑" else "🏆", fontSize = 52.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = if (isPerfect) "Perfect Photo Quiz! 🌟" else "Great Job! 📸",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You scored $score out of $maxScore points!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Text(
                            text = "+$score Stars Won! ⭐",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            currentIndex = 0
                            selectedOption = null
                            hasAnswered = false
                            score = 0
                            isQuizFinished = false
                            showConfetti = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BengaliColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("photo_quiz_retry_button")
                    ) {
                        Text("Play Again 🔄", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Quiz Progress Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "প্রশ্ন ${currentIndex + 1}/${challenges.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐ $score Points", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFFB45309))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main Photo Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_photo_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFFFCCD5))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(BengaliColorLight, Color.White)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentChallenge.photoEmoji, fontSize = 64.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = currentChallenge.questionBengali,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = currentChallenge.questionEnglish,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Options List
                    currentChallenge.options.forEachIndexed { index, option ->
                        val isThisOptionSelected = selectedOption == index
                        val isThisCorrect = index == currentChallenge.correctIndex

                        val borderColor = when {
                            !hasAnswered -> if (isThisOptionSelected) BengaliColor else Color(0xFFE2E8F0)
                            isThisCorrect -> MintGreen
                            isThisOptionSelected && !isThisCorrect -> Color(0xFFEF4444)
                            else -> Color(0xFFE2E8F0)
                        }

                        val bgColor = when {
                            !hasAnswered -> Color.White
                            isThisCorrect -> Color(0xFFD1FAE5)
                            isThisOptionSelected && !isThisCorrect -> Color(0xFFFEE2E2)
                            else -> Color.White
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable(enabled = !hasAnswered) {
                                    selectedOption = index
                                    hasAnswered = true
                                    if (index == currentChallenge.correctIndex) {
                                        score += 10
                                        SoundManagerService.playCorrect(context)
                                        onEarnStars(10)
                                    } else {
                                        SoundManagerService.playWrong(context)
                                    }
                                }
                                .testTag("quiz_option_$index"),
                            shape = RoundedCornerShape(14.dp),
                            color = bgColor,
                            border = BorderStroke(2.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E293B)
                                )

                                if (hasAnswered && isThisCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = MintGreen)
                                } else if (hasAnswered && isThisOptionSelected && !isThisCorrect) {
                                    Icon(Icons.Default.Close, contentDescription = "Wrong", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Explanation & Next Button
                    AnimatedVisibility(visible = hasAnswered) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                color = if (isCorrect) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentChallenge.explanation,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isCorrect) Color(0xFF047857) else Color(0xFFB45309),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (currentIndex + 1 < challenges.size) {
                                        currentIndex += 1
                                        selectedOption = null
                                        hasAnswered = false
                                    } else {
                                        // Finished quiz!
                                        isQuizFinished = true
                                        if (isCorrect && score + 0 >= maxScore) {
                                            showConfetti = true
                                        }
                                        SoundManagerService.playQuizComplete(context)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BengaliColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("photo_quiz_next_button")
                            ) {
                                Text(
                                    text = if (currentIndex + 1 < challenges.size) "পরবর্তী প্রশ্ন (Next Question) →" else "ফলাফল দেখো (View Results) 🏆",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        ConfettiCelebrationOverlay(
            isVisible = showConfetti || (isQuizFinished && isPerfect),
            title = "PERFECT PHOTO SCORE! 👑",
            subtitle = "Sensational! You identified all photo flashcard challenges with 100% accuracy!",
            bonusText = "+$score Stars Won! 🏆",
            badgeEmoji = "👑",
            onDismiss = { showConfetti = false }
        )
    }
}
