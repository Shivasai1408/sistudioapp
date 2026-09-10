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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.SkillEntity
import com.example.model.SkillCategories
import com.example.model.SkillTemplates
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditSkillDialog(
    editingSkill: SkillEntity?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: String,
        description: String,
        targetHours: Int,
        colorHex: String,
        iconKey: String,
        milestones: List<String>
    ) -> Unit
) {
    var name by remember { mutableStateOf(editingSkill?.name ?: "") }
    var category by remember { mutableStateOf(editingSkill?.category ?: "Tech & Code") }
    var description by remember { mutableStateOf(editingSkill?.description ?: "") }
    var targetHours by remember { mutableIntStateOf(editingSkill?.targetHours ?: 50) }
    var colorHex by remember { mutableStateOf(editingSkill?.colorHex ?: "#FF2D55") }
    var iconKey by remember { mutableStateOf(editingSkill?.iconKey ?: "code") }
    val milestones = remember { mutableStateListOf<String>() }
    var newMilestoneInput by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val isEditing = editingSkill != null
    val availableIcons = listOf("code", "brush", "translate", "music", "fitness", "mind", "career", "book", "mic", "audio", "psychology")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .padding(vertical = 16.dp)
                .liquidGlassSurface(
                    shape = RoundedCornerShape(26.dp),
                    baseColor = Color(0xF215161E),
                    borderColor = Color(0x35FFFFFF)
                )
                .testTag("add_edit_skill_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isEditing) "Edit Skill" else "Create New Skill",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite,
                                letterSpacing = (-0.3).sp
                            )
                        )
                        Text(
                            text = if (isEditing) "Update parameters and goals" else "Set up roadmap and track your mastery",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AppleGray400,
                                fontSize = 12.sp
                            )
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x33282A35))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("close_skill_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppleGray200,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Quick Template Auto-Fill (only when creating new)
                    if (!isEditing) {
                        Text(
                            text = "Or start from a preset template:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleRed,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SkillTemplates.TEMPLATES.forEach { template ->
                                Box(
                                    modifier = Modifier
                                        .appleSpringClick(onClick = {
                                            name = template.name
                                            category = template.category
                                            description = template.description
                                            targetHours = template.targetHours
                                            colorHex = template.colorHex
                                            iconKey = template.iconKey
                                            milestones.clear()
                                            milestones.addAll(template.defaultMilestones)
                                        })
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x33282A36))
                                        .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Flare,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = SkillCategories.parseColor(template.colorHex)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = template.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = AppleGray200,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Skill Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Skill Name *", color = AppleGray400) },
                        placeholder = { Text("e.g. Kotlin & Compose, Guitar, Spanish", color = AppleGray500) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("skill_name_input"),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category", color = AppleGray400) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("category_dropdown"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0x441A1B24),
                                unfocusedContainerColor = Color(0x3316171F),
                                focusedTextColor = AppleGrayWhite,
                                unfocusedTextColor = AppleGrayWhite,
                                focusedBorderColor = AppleRed.copy(alpha = 0.6f),
                                unfocusedBorderColor = Color(0x22FFFFFF)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color(0xF0181920))
                                .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
                        ) {
                            SkillCategories.DEFINITIONS.forEach { cat ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            cat.name,
                                            color = if (category == cat.name) AppleRed else AppleGray100,
                                            fontWeight = if (category == cat.name) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = SkillCategories.getIcon(cat.iconKey),
                                            contentDescription = null,
                                            tint = SkillCategories.parseColor(cat.defaultColor),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        category = cat.name
                                        if (!isEditing) {
                                            iconKey = cat.iconKey
                                            colorHex = cat.defaultColor
                                        }
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description / Motivation
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / Goal", color = AppleGray400) },
                        placeholder = { Text("What motivates you to master this skill?", color = AppleGray500) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("skill_description_input"),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 3,
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Hours
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Target Hours: $targetHours hrs",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(25, 50, 100, 200).forEach { hours ->
                            val isSelected = targetHours == hours
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .appleSpringClick(onClick = { targetHours = hours })
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) AppleRed else Color(0x33282A36)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0x60FFFFFF) else Color(0x20FFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(vertical = 9.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${hours}h",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AppleGrayWhite else AppleGray300
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Color Palette Picker
                    Text(
                        text = "Accent Color",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SkillCategories.PRESET_COLORS.take(8).forEach { hex ->
                            val color = SkillCategories.parseColor(hex)
                            val isSelected = colorHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color(0x40FFFFFF),
                                        shape = CircleShape
                                    )
                                    .clickable { colorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Icon Picker
                    Text(
                        text = "Skill Icon",
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
                        availableIcons.forEach { key ->
                            val isSelected = iconKey == key
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .appleSpringClick(onClick = { iconKey = key })
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) AppleRed else Color(0x33282A36)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0x60FFFFFF) else Color(0x20FFFFFF),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = SkillCategories.getIcon(key),
                                    contentDescription = key,
                                    tint = if (isSelected) AppleGrayWhite else AppleGray300,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }

                    // Starting Milestones (only shown when creating new skill)
                    if (!isEditing) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Starting Milestones (Optional)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Add milestone row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newMilestoneInput,
                                onValueChange = { newMilestoneInput = it },
                                placeholder = { Text("e.g. Master fingerpicking pattern", color = AppleGray500) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_milestone_input"),
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
                                        if (newMilestoneInput.isNotBlank()) {
                                            milestones.add(newMilestoneInput.trim())
                                            newMilestoneInput = ""
                                        }
                                    })
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AppleRed)
                                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(14.dp))
                                    .testTag("add_milestone_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = AppleGrayWhite)
                            }
                        }

                        if (milestones.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            milestones.forEachIndexed { index, mTitle ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}. $mTitle",
                                        style = MaterialTheme.typography.bodySmall.copy(color = AppleGray200),
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { milestones.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = AppleRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
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
                                enabled = name.isNotBlank(),
                                onClick = {
                                    if (name.isNotBlank()) {
                                        onSave(
                                            name,
                                            category,
                                            description,
                                            targetHours,
                                            colorHex,
                                            iconKey,
                                            milestones.toList()
                                        )
                                    }
                                }
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (name.isNotBlank()) AppleRed else Color(0x33353844)
                            )
                            .border(
                                1.dp,
                                if (name.isNotBlank()) Color(0x50FFFFFF) else Color(0x18FFFFFF),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 11.dp)
                            .testTag("save_skill_button")
                    ) {
                        Text(
                            text = if (isEditing) "Save Changes" else "Create Skill",
                            fontWeight = FontWeight.Bold,
                            color = if (name.isNotBlank()) AppleGrayWhite else AppleGray500,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    }
}
