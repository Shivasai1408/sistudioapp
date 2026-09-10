package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MilestoneEntity
import com.example.data.PracticeSessionEntity
import com.example.data.SkillEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillDetailSheet(
    skillWithDetails: SkillWithDetails,
    onDismiss: () -> Unit,
    onStartTimer: () -> Unit,
    onManualLog: () -> Unit,
    onEdit: (SkillEntity) -> Unit,
    onDelete: (Long) -> Unit,
    onToggleMilestone: (MilestoneEntity) -> Unit,
    onAddMilestone: (skillId: Long, title: String) -> Unit,
    onDeleteMilestone: (Long) -> Unit,
    onDeleteSession: (sessionId: Long, skillId: Long, minutes: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val skill = skillWithDetails.skill
    val skillColor = remember(skill.colorHex) { SkillCategories.parseColor(skill.colorHex) }
    val levelInfo = skillWithDetails.levelInfo

    var selectedTab by remember { mutableIntStateOf(0) }
    var newMilestoneText by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }

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
        containerColor = Color(0xF213141B),
        scrimColor = Color(0x99000000),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("skill_detail_sheet")
        ) {
            // Top Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Pill in Apple Red
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x28FF2D55))
                        .border(1.dp, AppleRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = skill.category,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onEdit(skill) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33282A35))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("edit_skill_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Skill",
                            tint = AppleGray200,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FF2D55))
                            .border(1.dp, AppleRed.copy(alpha = 0.5f), CircleShape)
                            .testTag("delete_skill_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Skill",
                            tint = AppleRed,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33282A35))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("close_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppleGray200,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Skill Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    skillColor.copy(alpha = 0.9f),
                                    skillColor.copy(alpha = 0.6f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x60FFFFFF), Color(0x10FFFFFF))
                            ),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = SkillCategories.getIcon(skill.iconKey),
                        contentDescription = null,
                        tint = AppleGrayWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite,
                            letterSpacing = (-0.3).sp
                        )
                    )
                    if (skill.description.isNotBlank()) {
                        Text(
                            text = skill.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AppleGray400
                            ),
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level & XP Bar Card in Liquid Glass
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassSurface(
                        shape = RoundedCornerShape(20.dp),
                        baseColor = Color(0xCC181A22),
                        borderColor = Color(0x28FFFFFF)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LevelBadge(levelInfo = levelInfo, accentColor = AppleRed)
                        Text(
                            text = "${levelInfo.currentXp} XP",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AppleRed
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    SkillProgressBar(
                        progress = levelInfo.progress,
                        barColor = skillColor,
                        height = 8
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val remainingXp = (levelInfo.xpForNextLevel - levelInfo.currentXp).coerceAtLeast(0)
                        Text(
                            text = "${(levelInfo.progress * 100).toInt()}% to Level ${levelInfo.level + 1}",
                            style = MaterialTheme.typography.bodySmall.copy(color = AppleGray400, fontSize = 11.5.sp)
                        )
                        Text(
                            text = "$remainingXp XP to go",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = AppleRed,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats row (Total Time & Target Goal & Milestones)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Practice Logged
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlassSurface(
                            shape = RoundedCornerShape(16.dp),
                            baseColor = Color(0xAA16171F),
                            borderColor = Color(0x20FFFFFF)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Practice Logged",
                            style = MaterialTheme.typography.labelSmall.copy(color = AppleGray400, fontSize = 10.5.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = LevelSystem.formatDuration(skill.totalMinutes),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite
                            )
                        )
                    }
                }

                // Target Goal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlassSurface(
                            shape = RoundedCornerShape(16.dp),
                            baseColor = Color(0xAA16171F),
                            borderColor = Color(0x20FFFFFF)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Target Goal",
                            style = MaterialTheme.typography.labelSmall.copy(color = AppleGray400, fontSize = 10.5.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${skill.targetHours}h (${(skillWithDetails.targetProgress * 100).toInt()}%)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite
                            )
                        )
                    }
                }

                // Milestones
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlassSurface(
                            shape = RoundedCornerShape(16.dp),
                            baseColor = Color(0xAA16171F),
                            borderColor = Color(0x20FFFFFF)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Milestones",
                            style = MaterialTheme.typography.labelSmall.copy(color = AppleGray400, fontSize = 10.5.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${skillWithDetails.completedMilestonesCount}/${skillWithDetails.milestones.size}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Focus Timer & Log Practice) with Apple Spring Motion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Focus Timer in Apple Red Glass
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appleSpringClick(onClick = onStartTimer)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(AppleRedVibrant, AppleRed)
                            )
                        )
                        .border(1.dp, Color(0x50FFFFFF), RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp)
                        .testTag("start_timer_action"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = AppleGrayWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Focus Timer",
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite,
                            fontSize = 13.5.sp
                        )
                    }
                }

                // Log Practice in Smoky Gray Glass
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appleSpringClick(onClick = onManualLog)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x33282A36))
                        .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp)
                        .testTag("quick_log_action"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = AppleGray200,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Log Practice",
                            fontWeight = FontWeight.SemiBold,
                            color = AppleGray200,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs: Milestones & History
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = AppleRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AppleRed,
                        height = 2.5.dp
                    )
                },
                divider = {
                    HorizontalDivider(color = Color(0x18FFFFFF))
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Roadmap & Milestones (${skillWithDetails.completedMilestonesCount}/${skillWithDetails.milestones.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) AppleGrayWhite else AppleGray400,
                                fontSize = 12.5.sp
                            )
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Practice Logs (${skillWithDetails.sessions.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) AppleGrayWhite else AppleGray400,
                                fontSize = 12.5.sp
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Milestones List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        items(skillWithDetails.milestones, key = { it.id }) { milestone ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = milestone.isCompleted,
                                    onCheckedChange = { onToggleMilestone(milestone) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AppleRed,
                                        uncheckedColor = AppleGray500,
                                        checkmarkColor = AppleGrayWhite
                                    ),
                                    modifier = Modifier.testTag("milestone_checkbox_${milestone.id}")
                                )
                                Text(
                                    text = milestone.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = if (milestone.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (milestone.isCompleted) AppleGray500 else AppleGrayWhite,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { onDeleteMilestone(milestone.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete Milestone",
                                        tint = AppleGray500,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Add new milestone input row
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newMilestoneText,
                                    onValueChange = { newMilestoneText = it },
                                    placeholder = {
                                        Text(
                                            "Add milestone or goal...",
                                            color = AppleGray400,
                                            fontSize = 13.sp
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("detail_add_milestone_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0x441A1B24),
                                        unfocusedContainerColor = Color(0x3316171F),
                                        focusedTextColor = AppleGrayWhite,
                                        unfocusedTextColor = AppleGrayWhite,
                                        focusedBorderColor = AppleRed.copy(alpha = 0.6f),
                                        unfocusedBorderColor = Color(0x22FFFFFF),
                                        cursorColor = AppleRed
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .appleSpringClick(onClick = {
                                            if (newMilestoneText.isNotBlank()) {
                                                onAddMilestone(skill.id, newMilestoneText.trim())
                                                newMilestoneText = ""
                                            }
                                        })
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AppleRed)
                                        .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(14.dp))
                                        .testTag("detail_add_milestone_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = AppleGrayWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Practice Sessions History
                    if (skillWithDetails.sessions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No practice sessions logged yet.\nUse Focus Timer or Log Practice above!",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = AppleGray400,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        ) {
                            items(skillWithDetails.sessions.sortedByDescending { it.dateMillis }, key = { it.id }) { session ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .liquidGlassSurface(
                                            shape = RoundedCornerShape(14.dp),
                                            baseColor = Color(0xAA181921),
                                            borderColor = Color(0x20FFFFFF)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "+${session.minutes} mins (+${session.xpGained} XP)",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = AppleRed,
                                                        fontSize = 13.sp
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = dateFormat.format(Date(session.dateMillis)),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = AppleGray400,
                                                        fontSize = 10.5.sp
                                                    )
                                                )
                                            }
                                            if (session.notes.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(
                                                    text = session.notes,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = AppleGray200,
                                                        fontSize = 12.sp
                                                    )
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteSession(session.id, skill.id, session.minutes) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Session",
                                                tint = AppleRed.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirm Delete Dialog in Frosted Dark Glass
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = Color(0xF5181922),
            title = {
                Text(
                    "Delete Skill?",
                    fontWeight = FontWeight.Bold,
                    color = AppleGrayWhite
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete '${skill.name}' and all its recorded milestones and practice sessions? This cannot be undone.",
                    color = AppleGray300
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .appleSpringClick(onClick = {
                            showDeleteConfirmDialog = false
                            onDelete(skill.id)
                        })
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppleRed)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Delete",
                        fontWeight = FontWeight.Bold,
                        color = AppleGrayWhite
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = AppleGray300)
                }
            }
        )
    }
}
