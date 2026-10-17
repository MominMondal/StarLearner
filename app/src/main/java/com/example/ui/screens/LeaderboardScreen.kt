package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassLevel
import com.example.data.model.LeaderboardEntry
import com.example.data.model.StudentProfile
import com.example.data.repository.CurriculumRepository
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TertiaryAmber

@Composable
fun LeaderboardScreen(
    currentProfile: StudentProfile,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedFilter by remember { mutableStateOf<ClassLevel?>(null) }
    val allEntries = remember(currentProfile) {
        CurriculumRepository.getLeaderboard(currentProfile)
    }

    val filteredEntries = remember(selectedFilter, allEntries) {
        if (selectedFilter == null) allEntries else allEntries.filter { it.classLevel == selectedFilter }
    }

    val topThree = filteredEntries.take(3)
    val remainingEntries = filteredEntries.drop(3)
    val currentUserEntry = allEntries.find { it.isCurrentUser }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("leaderboard_screen")
    ) {
        // App Bar Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("leaderboard_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Dashboard",
                    tint = Color(0xFF1E293B)
                )
            }
            Text(
                text = "Global Leaderboard 🏆",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = Color(0xFF0F172A)
            )
        }

        // Filter chips (All, Class 1, Class 2, Class 3, Class 4)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LeaderboardFilterChip(
                label = "All",
                isSelected = selectedFilter == null,
                onClick = { selectedFilter = null },
                modifier = Modifier.weight(1f)
            )
            ClassLevel.values().forEach { level ->
                LeaderboardFilterChip(
                    label = level.shortLabel,
                    isSelected = selectedFilter == level,
                    onClick = { selectedFilter = level },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Podium for Top 3 (if at least 3 exist)
            if (topThree.size >= 3) {
                item {
                    LeaderboardPodium(
                        first = topThree[0],
                        second = topThree[1],
                        third = topThree[2]
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            item {
                Text(
                    text = "Rankings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(remainingEntries) { entry ->
                LeaderboardItemRow(entry = entry)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Pinned User Ranking Card at Bottom
        currentUserEntry?.let { entry ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("leaderboard_user_pinned_card"),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${entry.rank}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(text = entry.avatarEmoji, fontSize = 28.sp)

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = entry.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFDBEAFE))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "YOU",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryBlue
                                    )
                                }
                            }
                            Text(
                                text = "${entry.classLevel.label} • ${entry.streak} day streak 🔥",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${entry.stars}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "⭐", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryBlue else Color.White)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF64748B)
        )
    }
}

@Composable
private fun LeaderboardPodium(
    first: LeaderboardEntry,
    second: LeaderboardEntry,
    third: LeaderboardEntry
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_podium_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✨ Top Star Champions ✨",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFB45309)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Silver)
                PodiumColumn(
                    entry = second,
                    medal = "🥈",
                    rankNumber = 2,
                    pedestalHeight = 90.dp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.weight(1f)
                )

                // 1st Place (Gold)
                PodiumColumn(
                    entry = first,
                    medal = "🥇",
                    rankNumber = 1,
                    pedestalHeight = 120.dp,
                    color = TertiaryAmber,
                    modifier = Modifier.weight(1.1f)
                )

                // 3rd Place (Bronze)
                PodiumColumn(
                    entry = third,
                    medal = "🥉",
                    rankNumber = 3,
                    pedestalHeight = 75.dp,
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntry,
    medal: String,
    rankNumber: Int,
    pedestalHeight: androidx.compose.ui.unit.Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = medal, fontSize = 24.sp)
        Text(text = entry.avatarEmoji, fontSize = 34.sp)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${entry.stars}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = Color(0xFFB45309)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "⭐", fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.15f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rankNumber",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = color
            )
        }
    }
}

@Composable
private fun LeaderboardItemRow(entry: LeaderboardEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_${entry.rank}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isCurrentUser) Color(0xFFEFF6FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rank Circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${entry.rank}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(text = entry.avatarEmoji, fontSize = 28.sp)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        if (entry.isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "(You)", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "${entry.classLevel.label} • ${entry.streak}d streak 🔥",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${entry.stars}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFFB45309)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "⭐", fontSize = 12.sp)
            }
        }
    }
}
