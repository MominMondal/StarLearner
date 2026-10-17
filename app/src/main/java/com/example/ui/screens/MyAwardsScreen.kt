package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import com.example.data.model.TrophyTier
import com.example.data.model.VirtualTrophy
import com.example.data.repository.CurriculumRepository
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.MintGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TertiaryAmber

enum class AwardCategoryFilter(val label: String, val icon: String) {
    ALL("All Awards", "🏆"),
    QUIZ("Quiz High-Score", "🎯"),
    SUBJECTS("Subject Champions", "🥇"),
    STREAKS("Daily Habits", "🔥"),
    UNLOCKED("Unlocked Only", "✨")
}

@Composable
fun MyAwardsScreen(
    profile: StudentProfile,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = androidx.compose.ui.platform.LocalContext.current

    val trophies = remember(profile) {
        CurriculumRepository.getVirtualTrophies(profile)
    }

    var selectedFilter by remember { mutableStateOf(AwardCategoryFilter.ALL) }
    var inspectTrophy by remember { mutableStateOf<VirtualTrophy?>(null) }

    val unlockedCount = trophies.count { it.isUnlocked }

    val filteredTrophies = remember(selectedFilter, trophies) {
        when (selectedFilter) {
            AwardCategoryFilter.ALL -> trophies
            AwardCategoryFilter.QUIZ -> trophies.filter { it.category == "Quiz High-Score" }
            AwardCategoryFilter.SUBJECTS -> trophies.filter { it.category == "Subject Mastery" }
            AwardCategoryFilter.STREAKS -> trophies.filter { it.category == "Daily Habit" }
            AwardCategoryFilter.UNLOCKED -> trophies.filter { it.isUnlocked }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
            .testTag("my_awards_screen")
    ) {
        item {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("awards_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                    Column {
                        Text(
                            text = "My Awards",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Trophy Showcase & Milestones",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Unlocked count pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$unlockedCount / ${trophies.size}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hall of Fame Showcase Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("awards_hall_of_fame_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Virtual Trophy Room 🌟",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFDE047)
                                )
                                Text(
                                    text = "Earned for milestones & quiz high-scores",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Text(text = "👑", fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Trophy Pedestals Showcase
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TrophyPedestal(tier = TrophyTier.DIAMOND, count = trophies.count { it.tier == TrophyTier.DIAMOND && it.isUnlocked })
                            TrophyPedestal(tier = TrophyTier.GOLD, count = trophies.count { it.tier == TrophyTier.GOLD && it.isUnlocked })
                            TrophyPedestal(tier = TrophyTier.SILVER, count = trophies.count { it.tier == TrophyTier.SILVER && it.isUnlocked })
                            TrophyPedestal(tier = TrophyTier.BRONZE, count = trophies.count { it.tier == TrophyTier.BRONZE && it.isUnlocked })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AwardCategoryFilter.values()) { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("award_filter_${filter.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = filter.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Trophies List
        items(filteredTrophies) { trophy ->
            TrophyCardItem(
                trophy = trophy,
                onClick = {
                    inspectTrophy = trophy
                    if (trophy.isUnlocked) {
                        com.example.service.SoundManagerService.playBadgeUnlock(context)
                    } else {
                        com.example.service.SoundManagerService.playStarPop(context)
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Trophy Detail Dialog
    inspectTrophy?.let { trophy ->
        TrophyDetailDialog(
            trophy = trophy,
            onDismiss = { inspectTrophy = null }
        )
    }
}

@Composable
private fun TrophyPedestal(
    tier: TrophyTier,
    count: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = tier.icon, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tier.label.split(" ").first(),
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun TrophyCardItem(
    trophy: VirtualTrophy,
    onClick: () -> Unit
) {
    val tierColor = Color(trophy.tier.glowColorHex)
    val isUnlocked = trophy.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("trophy_card_${trophy.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color.White else Color(0xFFF9FAFB)
        ),
        border = BorderStroke(
            width = if (isUnlocked) 1.5.dp else 1.dp,
            color = if (isUnlocked) tierColor.copy(alpha = 0.6f) else Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trophy Emblem Box
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isUnlocked) {
                            tierColor.copy(alpha = 0.15f)
                        } else {
                            Color(0xFFF1F5F9)
                        }
                    )
                    .border(
                        width = if (isUnlocked) 1.5.dp else 1.dp,
                        color = if (isUnlocked) tierColor else Color(0xFFCBD5E1),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = trophy.trophyEmoji, fontSize = 32.sp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Trophy",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = trophy.trophyEmoji,
                            fontSize = 16.sp,
                            color = Color(0xFF94A3B8).copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = trophy.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isUnlocked) Color(0xFF0F172A) else Color(0xFF64748B)
                    )

                    // Tier Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(tierColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = trophy.tier.label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = tierColor
                        )
                    }
                }

                Text(
                    text = trophy.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) PrimaryBlue else Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = trophy.requirementText,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = {
                        if (trophy.targetProgress > 0) {
                            (trophy.currentProgress.toFloat() / trophy.targetProgress).coerceIn(0f, 1f)
                        } else 1f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (isUnlocked) tierColor else PrimaryBlue,
                    trackColor = Color(0xFFE2E8F0),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isUnlocked) {
                            trophy.earnedDate ?: "✓ Milestone Achieved"
                        } else {
                            "${trophy.currentProgress} / ${trophy.targetProgress}"
                        },
                        fontSize = 10.sp,
                        fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Medium,
                        color = if (isUnlocked) MintGreen else Color(0xFF94A3B8)
                    )

                    if (isUnlocked) {
                        Text(
                            text = "Tap to Inspect ➜",
                            fontSize = 10.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrophyDetailDialog(
    trophy: VirtualTrophy,
    onDismiss: () -> Unit
) {
    val tierColor = Color(trophy.tier.glowColorHex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(tierColor.copy(alpha = 0.15f))
                        .border(2.dp, tierColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = trophy.trophyEmoji, fontSize = 42.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = trophy.title,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "${trophy.tier.label} • ${trophy.category}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = tierColor
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = trophy.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Goal Milestone:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = trophy.requirementText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Status: ${if (trophy.isUnlocked) "🏆 Unlocked & in your Hall of Fame" else "⏳ In Progress (${trophy.currentProgress}/${trophy.targetProgress})"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trophy.isUnlocked) MintGreen else Color(0xFFD97706)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("trophy_dialog_ok")
            ) {
                Text("Awesome!", fontWeight = FontWeight.Bold)
            }
        }
    )
}
