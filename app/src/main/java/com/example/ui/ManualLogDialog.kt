package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManualLogDialog(
    skillWithDetails: SkillWithDetails,
    onDismiss: () -> Unit,
    onSave: (skillId: Long, minutes: Int, dateMillis: Long, notes: String) -> Unit
) {
    val skill = skillWithDetails.skill
    val skillColor = remember(skill.colorHex) { SkillCategories.parseColor(skill.colorHex) }

    var selectedMinutes by remember { mutableIntStateOf(30) }
    var customMinutesText by remember { mutableStateOf("") }
    var isCustomSelected by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }

    val presetDurations = listOf(15, 30, 45, 60, 90, 120)

    val effectiveMinutes = if (isCustomSelected) {
        customMinutesText.toIntOrNull() ?: 0
    } else {
        selectedMinutes
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp)
                .liquidGlassSurface(
                    shape = RoundedCornerShape(26.dp),
                    baseColor = Color(0xF215161E),
                    borderColor = Color(0x35FFFFFF)
                )
                .testTag("manual_log_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x28FF2D55))
                                .border(1.dp, AppleRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = SkillCategories.getIcon(skill.iconKey),
                                contentDescription = null,
                                tint = AppleRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Log Practice",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleGrayWhite
                                )
                            )
                            Text(
                                text = skill.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AppleGray400
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x33282A35))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppleGray200,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // XP Earned Banner in Apple Red Glass
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x28FF2D55))
                        .border(1.dp, AppleRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AppleRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+$effectiveMinutes XP",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = AppleGrayWhite
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "toward next level",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AppleGray400,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Duration Selector
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleGrayWhite
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetDurations.forEach { mins ->
                        val isSelected = !isCustomSelected && selectedMinutes == mins
                        Box(
                            modifier = Modifier
                                .appleSpringClick(onClick = {
                                    isCustomSelected = false
                                    selectedMinutes = mins
                                })
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) AppleRed else Color(0x33282A36)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0x60FFFFFF) else Color(0x20FFFFFF),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${mins}m",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) AppleGrayWhite else AppleGray300,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }

                    // Custom toggle
                    Box(
                        modifier = Modifier
                            .appleSpringClick(onClick = { isCustomSelected = true })
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isCustomSelected) AppleRed else Color(0x33282A36)
                            )
                            .border(
                                1.dp,
                                if (isCustomSelected) Color(0x60FFFFFF) else Color(0x20FFFFFF),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Custom",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCustomSelected) AppleGrayWhite else AppleGray300,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                if (isCustomSelected) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customMinutesText,
                        onValueChange = { customMinutesText = it.filter { ch -> ch.isDigit() }.take(4) },
                        label = { Text("Enter minutes", color = AppleGray400) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_minutes_input"),
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes input
                Text(
                    text = "Session Reflection / Notes",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppleGrayWhite
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    placeholder = {
                        Text(
                            "What did you focus on? e.g. practiced scale transitions...",
                            color = AppleGray500,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("session_notes_input"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4,
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

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = AppleGray300)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .appleSpringClick(
                                enabled = effectiveMinutes > 0,
                                onClick = {
                                    if (effectiveMinutes > 0) {
                                        onSave(skill.id, effectiveMinutes, System.currentTimeMillis(), notesText)
                                    }
                                }
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (effectiveMinutes > 0) AppleRed else Color(0x33353844)
                            )
                            .border(
                                1.dp,
                                if (effectiveMinutes > 0) Color(0x50FFFFFF) else Color(0x18FFFFFF),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 11.dp)
                            .testTag("confirm_log_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = if (effectiveMinutes > 0) AppleGrayWhite else AppleGray500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save Session",
                                fontWeight = FontWeight.Bold,
                                color = if (effectiveMinutes > 0) AppleGrayWhite else AppleGray500,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
