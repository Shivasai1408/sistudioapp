package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PracticeSessionEntity
import com.example.model.LevelSystem
import com.example.model.SkillCategories
import com.example.model.SkillWithDetails
import com.example.ui.theme.AppleGray100
import com.example.ui.theme.AppleGray200
import com.example.ui.theme.AppleGray300
import com.example.ui.theme.AppleGray400
import com.example.ui.theme.AppleGray500
import com.example.ui.theme.AppleGray600
import com.example.ui.theme.AppleGray700
import com.example.ui.theme.AppleGrayWhite
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleRedDark
import com.example.ui.theme.AppleRedVibrant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsSheet(
    skills: List<SkillWithDetails>,
    sessions: List<PracticeSessionEntity>,
    streakDays: Int,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val totalPracticeMinutes = remember(skills) {
        skills.sumOf { it.skill.totalMinutes }
    }
    val totalMilestonesCompleted = remember(skills) {
        skills.sumOf { it.completedMilestonesCount }
    }
    val totalMilestones = remember(skills) {
        skills.sumOf { it.milestones.size }
    }
    val totalXp = remember(skills) {
        skills.sumOf { it.levelInfo.currentXp }
    }

    // Category breakdown
    val categoryMinutes = remember(skills) {
        skills.groupBy { it.skill.category }
            .mapValues { entry -> entry.value.sumOf { it.skill.totalMinutes } }
            .filter { it.value > 0 }
            .toList()
            .sortedByDescending { it.second }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.5.dp)
                    .clip(CircleShape)
                    .background(Color(0x40FFFFFF))
            )
        },
        containerColor = Color(0xF2121319),
        scrimColor = Color(0x99000000),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("stats_sheet")
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Learning Analytics & Stats",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite,
                                letterSpacing = (-0.3).sp
                            )
                        )
                        Text(
                            text = "Track your long-term growth and consistency",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AppleGray400,
                                fontSize = 12.sp
                            )
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33282A35))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("close_stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppleGray200,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Hero 4-stat grid with Liquid Glass surfaces
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Time
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlassSurface(
                                shape = RoundedCornerShape(18.dp),
                                baseColor = Color(0xCC181922),
                                borderColor = Color(0x28FFFFFF)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x28FF2D55))
                                    .border(1.dp, AppleRed.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = AppleRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = LevelSystem.formatDuration(totalPracticeMinutes),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleGrayWhite
                                )
                            )
                            Text(
                                text = "Total Practice",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppleGray400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Streak
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlassSurface(
                                shape = RoundedCornerShape(18.dp),
                                baseColor = Color(0xCC181922),
                                borderColor = AppleRed.copy(alpha = 0.35f)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FF2D55))
                                    .border(1.dp, AppleRed.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = AppleRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$streakDays Days",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleRed
                                )
                            )
                            Text(
                                text = "Current Streak",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppleGray400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Milestones completed
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlassSurface(
                                shape = RoundedCornerShape(18.dp),
                                baseColor = Color(0xCC181922),
                                borderColor = Color(0x28FFFFFF)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33383B46))
                                    .border(1.dp, Color(0x30FFFFFF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TaskAlt,
                                    contentDescription = null,
                                    tint = AppleGrayWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$totalMilestonesCompleted / $totalMilestones",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleGrayWhite
                                )
                            )
                            Text(
                                text = "Milestones Conquered",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppleGray400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Total XP
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .liquidGlassSurface(
                                shape = RoundedCornerShape(18.dp),
                                baseColor = Color(0xCC181922),
                                borderColor = Color(0x28FFFFFF)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x28FF2D55))
                                    .border(1.dp, AppleRed.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AppleRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$totalXp XP",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleRed
                                )
                            )
                            Text(
                                text = "Total Experience",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppleGray400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
            }

            // Category Distribution
            item {
                Text(
                    text = "Practice by Category",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleGrayWhite
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (categoryMinutes.isEmpty()) {
                    Text(
                        text = "No practice time logged yet.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = AppleGray400)
                    )
                } else {
                    categoryMinutes.forEach { (catName, mins) ->
                        val catColor = SkillCategories.DEFINITIONS.find { it.name == catName }?.defaultColor?.let {
                            SkillCategories.parseColor(it)
                        } ?: AppleRed

                        val percentage = if (totalPracticeMinutes > 0) mins.toFloat() / totalPracticeMinutes else 0f

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = catName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppleGrayWhite
                                    )
                                )
                                Text(
                                    text = "${LevelSystem.formatDuration(mins)} (${(percentage * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AppleGray400,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            SkillProgressBar(
                                progress = percentage,
                                barColor = catColor,
                                height = 7
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
            }

            // Unlocked Badges / Achievements in Liquid Glass
            item {
                Text(
                    text = "Mastery Badges",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleGrayWhite
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                val achievements = listOf(
                    Triple("First Step", "Log your first practice session", sessions.isNotEmpty()),
                    Triple("Ten Hours In", "Accumulate at least 10 hours of practice", totalPracticeMinutes >= 600),
                    Triple("Milestone Crusher", "Complete 5 skill milestones", totalMilestonesCompleted >= 5),
                    Triple("Dedicated Habit", "Maintain a 3+ day practice streak", streakDays >= 3),
                    Triple("Renaissance Learner", "Active skills across 3+ categories", skills.map { it.skill.category }.distinct().size >= 3)
                )

                achievements.forEach { (title, desc, isUnlocked) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .liquidGlassSurface(
                                shape = RoundedCornerShape(16.dp),
                                baseColor = if (isUnlocked) Color(0xCC181A24) else Color(0x8814151C),
                                borderColor = if (isUnlocked) AppleRed.copy(alpha = 0.35f) else Color(0x18FFFFFF)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) Color(0x28FF2D55)
                                        else Color(0x18FFFFFF)
                                    )
                                    .border(
                                        1.dp,
                                        if (isUnlocked) AppleRed.copy(alpha = 0.5f) else Color(0x15FFFFFF),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = if (isUnlocked) AppleRed else AppleGray500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) AppleGrayWhite else AppleGray400
                                    )
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isUnlocked) AppleGray300 else AppleGray500,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                            if (isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x28FF2D55))
                                        .border(0.5.dp, AppleRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "UNLOCKED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.5.sp,
                                            color = AppleRed,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
