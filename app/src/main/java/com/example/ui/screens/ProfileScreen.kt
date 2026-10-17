package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimalAvatar
import com.example.data.model.Badge
import com.example.data.model.ClassLevel
import com.example.data.model.CurriculumMedium
import com.example.data.model.StudentProfile
import com.example.data.repository.CurriculumRepository
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TertiaryAmber

@Composable
fun ProfileScreen(
    profile: StudentProfile,
    onUpdateProfile: (name: String, avatarId: String, classLevel: ClassLevel, medium: CurriculumMedium) -> Unit,
    onOpenAwards: () -> Unit = {},
    onOpenParentDashboard: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = androidx.compose.ui.platform.LocalContext.current

    var displayNameInput by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    var selectedAvatarId by remember(profile.avatarId) { mutableStateOf(profile.avatarId) }
    var selectedClassLevel by remember(profile.classLevel) { mutableStateOf(profile.classLevel) }
    var selectedMedium by remember(profile.learningMedium) { mutableStateOf(profile.learningMedium) }
    var selectedBadgeForDialog by remember { mutableStateOf<Badge?>(null) }
    var showSaveConfirmation by remember { mutableStateOf(false) }

    val allBadges = remember(profile) {
        CurriculumRepository.getAllBadges(profile)
    }
    val currentAvatar = CurriculumRepository.getAvatarById(selectedAvatarId)

    var isSoundMuted by remember { mutableStateOf(com.example.ui.audio.SoundEffectManager.isMuted) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
            .testTag("profile_screen")
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
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                    Text(
                        text = "My Student Profile 👤",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFF0F172A)
                    )
                }

                // Sound Effect Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSoundMuted) Color(0xFFFEE2E2) else Color(0xFFEFF6FF))
                        .clickable {
                            isSoundMuted = !isSoundMuted
                            com.example.service.SoundManagerService.setMuted(context, isSoundMuted)
                            if (!isSoundMuted) com.example.service.SoundManagerService.playCorrect(context)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("sound_toggle_button")
                ) {
                    Text(
                        text = if (isSoundMuted) "🔇 Muted" else "🔊 Sound On",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSoundMuted) Color(0xFFB91C1C) else PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Avatar Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(currentAvatar.colorHex).copy(alpha = 0.15f))
                            .border(3.dp, Color(currentAvatar.colorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = currentAvatar.emoji, fontSize = 54.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentAvatar.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = currentAvatar.personality,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animal Avatar Selector row
                    Text(
                        text = "Choose your Animal Avatar:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF334155),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(CurriculumRepository.animalAvatars) { avatar ->
                            val isChosen = avatar.id == selectedAvatarId
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isChosen) Color(0xFFEFF6FF) else Color(0xFFF1F5F9))
                                    .border(
                                        width = if (isChosen) 2.5.dp else 1.dp,
                                        color = if (isChosen) PrimaryBlue else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        selectedAvatarId = avatar.id
                                        onUpdateProfile(displayNameInput, avatar.id, selectedClassLevel, selectedMedium)
                                    }
                                    .testTag("avatar_choice_${avatar.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = avatar.emoji, fontSize = 32.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Display Name & Class Editor Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_edit_name_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Student Name & Grade",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = displayNameInput,
                        onValueChange = { displayNameInput = it },
                        label = { Text("Display Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Current Class / Grade:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClassLevel.values().forEach { level ->
                            val isSelected = level == selectedClassLevel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                                    .clickable { selectedClassLevel = level }
                                    .padding(vertical = 10.dp)
                                    .testTag("profile_class_${level.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = level.shortLabel,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else Color(0xFF475569)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Learning Medium (পড়াশোনার মাধ্যম / माध्यम):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CurriculumMedium.values().forEach { medium ->
                            val isSelected = medium == selectedMedium
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9))
                                    .clickable { selectedMedium = medium }
                                    .padding(vertical = 10.dp, horizontal = 4.dp)
                                    .testTag("profile_medium_${medium.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${medium.flagEmoji} ${medium.shortCode}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else Color(0xFF334155)
                                    )
                                    Text(
                                        text = medium.nativeLabel,
                                        fontSize = 9.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (displayNameInput.isNotBlank()) {
                                onUpdateProfile(displayNameInput.trim(), selectedAvatarId, selectedClassLevel, selectedMedium)
                                showSaveConfirmation = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_save_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Save", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                    }

                    if (showSaveConfirmation) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Profile updated successfully!",
                            color = MintGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stats Matrix Card
            Text(
                text = "Learning Statistics 📊",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Total Stars",
                    value = "${profile.totalStars}",
                    emoji = "⭐",
                    bgColor = Color(0xFFFEF3C7),
                    textColor = Color(0xFFB45309),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Daily Streak",
                    value = "${profile.dailyStreak} Days",
                    emoji = "🔥",
                    bgColor = Color(0xFFFFF7ED),
                    textColor = Color(0xFFC2410C),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Lessons",
                    value = "${profile.completedLessons}",
                    emoji = "📚",
                    bgColor = Color(0xFFDBEAFE),
                    textColor = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Quizzes",
                    value = "${profile.quizzesCompleted}",
                    emoji = "🎯",
                    bgColor = MintGreenLight,
                    textColor = Color(0xFF047857),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rewards & Virtual Badges Section (Requirement 6)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Virtual Badges & Milestones 🎖️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color(0xFF0F172A)
                )

                val unlockedCount = allBadges.count { it.isUnlocked }
                Text(
                    text = "$unlockedCount / ${allBadges.size} Unlocked",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TertiaryAmber
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // My Awards shortcut card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAwards() }
                    .testTag("profile_my_awards_shortcut"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏆", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "My Awards & Trophy Room",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "View virtual trophies for high scores & milestones",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }

                    Text(
                        text = "Open ➔",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Badges Grid / List
        items(allBadges) { badge ->
            BadgeRowItem(
                badge = badge,
                onClick = {
                    selectedBadgeForDialog = badge
                    if (badge.isUnlocked) {
                        com.example.service.SoundManagerService.playBadgeUnlock(context)
                    } else {
                        com.example.service.SoundManagerService.playStarPop(context)
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Dedicated Parent Portal Card
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenParentDashboard() }
                    .testTag("profile_parent_portal_button"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.5.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👨‍👩‍👧", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Parent Portal & Weekly Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "View simplified daily activity, subject progress & 48h nudges",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                    Text(text = "View →", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF166534))
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Badge Detail Modal Dialog
    selectedBadgeForDialog?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadgeForDialog = null }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    emoji: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = textColor
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun BadgeRowItem(
    badge: Badge,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("badge_item_${badge.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF1F5F9)
        ),
        border = BorderStroke(
            width = if (badge.isUnlocked) 1.5.dp else 1.dp,
            color = if (badge.isUnlocked) AmberGlow else Color(0xFFE2E8F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge Icon Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) Color(0xFFFEF3C7) else Color(0xFFE2E8F0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.iconEmoji,
                    fontSize = 28.sp,
                    color = if (badge.isUnlocked) Color.Unspecified else Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (badge.isUnlocked) Color(0xFF0F172A) else Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    if (badge.isUnlocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MintGreenLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "UNLOCKED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF047857)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )

                if (!badge.isUnlocked) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val progressFloat = (badge.currentProgress.toFloat() / badge.maxProgress).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressFloat },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TertiaryAmber,
                        trackColor = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${badge.currentProgress} / ${badge.maxProgress} (${badge.requirementText})",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeDetailDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = badge.iconEmoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = badge.title,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (badge.isUnlocked) MintGreenLight else Color(0xFFF1F5F9))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = if (badge.isUnlocked) "🎉 Milestone Reached!" else "🎯 Requirement:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (badge.isUnlocked) Color(0xFF047857) else Color(0xFF475569)
                        )
                        Text(
                            text = badge.requirementText,
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B)
                        )
                        if (!badge.isUnlocked) {
                            Text(
                                text = "Current Progress: ${badge.currentProgress} / ${badge.maxProgress}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TertiaryAmber
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Awesome!", fontWeight = FontWeight.Bold)
            }
        }
    )
}
