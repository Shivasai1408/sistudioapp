package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.LevelSystem
import com.example.model.SkillCategories
import com.example.model.SkillWithDetails
import com.example.ui.theme.AmberMastery
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseCoral
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillHomeScreen(viewModel: SkillViewModel) {
    val skills by viewModel.filteredSkills.collectAsStateWithLifecycle()
    val allSkillsList by viewModel.allSkills.collectAsStateWithLifecycle()
    val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()

    val selectedSkill by viewModel.selectedSkill.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddSkillDialog.collectAsStateWithLifecycle()
    val editingSkill by viewModel.editingSkill.collectAsStateWithLifecycle()
    val manualLogSkill by viewModel.manualLogSkill.collectAsStateWithLifecycle()
    val showTimerSheet by viewModel.showTimerSheet.collectAsStateWithLifecycle()
    val showStatsSheet by viewModel.showStatsSheet.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()

    var showSortMenu by remember { mutableStateOf(false) }
    var currentCelebration by remember { mutableStateOf<CelebrationEvent?>(null) }

    // Listen to celebration events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            currentCelebration = event
            delay(3500L)
            currentCelebration = null
        }
    }

    val streakDays = remember(allSessions) { viewModel.calculateStreak(allSessions) }
    val totalPracticeMinutes = remember(allSkillsList) { allSkillsList.sumOf { it.skill.totalMinutes } }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberMastery,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Skills",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "Mastery & Growth",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleStatsSheet(true) },
                        modifier = Modifier.testTag("open_stats_button")
                    ) {
                        Icon(imageVector = Icons.Default.BarChart, contentDescription = "Analytics & Stats")
                    }

                    IconButton(
                        onClick = { viewModel.toggleTimerSheet(true) },
                        modifier = Modifier.testTag("open_timer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Practice Timer",
                            tint = if (timerState.isRunning) AmberMastery else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddSkillDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_skill_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Skill")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Skill", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Hero Stats Banner
                item {
                    HeroStatsCard(
                        totalMinutes = totalPracticeMinutes,
                        activeSkillsCount = allSkillsList.size,
                        streakDays = streakDays,
                        onOpenStats = { viewModel.toggleStatsSheet(true) },
                        onQuickTimer = {
                            if (allSkillsList.isNotEmpty()) {
                                viewModel.startTimerForSkill(allSkillsList.first())
                            } else {
                                viewModel.openAddSkillDialog()
                            }
                        }
                    )
                }

                // Search Bar & Filter Controls
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                placeholder = { Text("Search skills or categories...") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("search_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box {
                                IconButton(
                                    onClick = { showSortMenu = true },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                        .testTag("sort_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort")
                                }

                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false }
                                ) {
                                    SortOption.values().forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.label) },
                                            trailingIcon = {
                                                if (sortBy == option) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                                }
                                            },
                                            onClick = {
                                                viewModel.updateSortBy(option)
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Chips Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SkillCategories.CATEGORIES.forEach { cat ->
                                val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                                val badgeColor = SkillCategories.DEFINITIONS.find { it.name == cat }?.defaultColor?.let {
                                    SkillCategories.parseColor(it)
                                }
                                CategoryChip(
                                    text = cat,
                                    isSelected = isSelected,
                                    badgeColor = badgeColor,
                                    onClick = { viewModel.selectCategory(cat) },
                                    modifier = Modifier.testTag("category_chip_$cat")
                                )
                            }
                        }
                    }
                }

                // Skills List
                if (skills.isEmpty()) {
                    item {
                        EmptyStateView(
                            searchQuery = searchQuery,
                            onAddSkill = { viewModel.openAddSkillDialog() }
                        )
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Skills (${skills.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = sortBy.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    items(skills, key = { it.skill.id }) { skillWithDetails ->
                        SkillCard(
                            skillWithDetails = skillWithDetails,
                            onClick = { viewModel.openSkillDetail(skillWithDetails) },
                            onStartTimer = { viewModel.startTimerForSkill(skillWithDetails) },
                            onLogPractice = { viewModel.openManualLog(skillWithDetails) }
                        )
                    }
                }
            }

            // Bottom Mini-Timer Floating Bar (if active and sheet is closed)
            if (timerState.elapsedSeconds > 0 && !showTimerSheet) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 76.dp, start = 16.dp, end = 16.dp)
                ) {
                    MiniTimerBar(
                        timerState = timerState,
                        onOpenTimer = { viewModel.toggleTimerSheet(true) },
                        onToggle = {
                            if (timerState.isRunning) viewModel.pauseTimer() else viewModel.resumeTimer()
                        }
                    )
                }
            }

            // Floating Celebration Banner
            AnimatedVisibility(
                visible = currentCelebration != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                currentCelebration?.let { event ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (event.isLevelUp) AmberMastery else IndigoPrimary,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (event.isLevelUp) Icons.Default.Star else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = event.message,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheets & Dialogs
    if (selectedSkill != null) {
        SkillDetailSheet(
            skillWithDetails = selectedSkill!!,
            onDismiss = { viewModel.closeSkillDetail() },
            onStartTimer = {
                val s = selectedSkill!!
                viewModel.closeSkillDetail()
                viewModel.startTimerForSkill(s)
            },
            onManualLog = {
                val s = selectedSkill!!
                viewModel.openManualLog(s)
            },
            onEdit = { skillEntity ->
                viewModel.openEditSkillDialog(skillEntity)
            },
            onDelete = { skillId ->
                viewModel.deleteSkill(skillId)
            },
            onToggleMilestone = { milestone ->
                viewModel.toggleMilestone(milestone)
            },
            onAddMilestone = { skillId, title ->
                viewModel.addMilestone(skillId, title)
            },
            onDeleteMilestone = { milestoneId ->
                viewModel.deleteMilestone(milestoneId)
            },
            onDeleteSession = { sessionId, skillId, mins ->
                viewModel.deleteSession(sessionId, skillId, mins)
            }
        )
    }

    if (showAddDialog) {
        AddEditSkillDialog(
            editingSkill = editingSkill,
            onDismiss = { viewModel.closeAddEditSkillDialog() },
            onSave = { name, category, description, targetHours, colorHex, iconKey, milestones ->
                viewModel.saveSkill(
                    name = name,
                    category = category,
                    description = description,
                    targetHours = targetHours,
                    colorHex = colorHex,
                    iconKey = iconKey,
                    initialMilestones = milestones
                )
            }
        )
    }

    if (manualLogSkill != null) {
        ManualLogDialog(
            skillWithDetails = manualLogSkill!!,
            onDismiss = { viewModel.closeManualLog() },
            onConfirm = { minutes, notes ->
                viewModel.logPractice(manualLogSkill!!.skill.id, minutes, notes)
            }
        )
    }

    if (showTimerSheet) {
        PracticeTimerSheet(
            timerState = timerState,
            onDismiss = { viewModel.toggleTimerSheet(false) },
            onResume = { viewModel.resumeTimer() },
            onPause = { viewModel.pauseTimer() },
            onReset = { viewModel.resetTimer() },
            onNotesChange = { viewModel.updateTimerNotes(it) },
            onFinishAndSave = { viewModel.finishAndSaveTimerSession() }
        )
    }

    if (showStatsSheet) {
        StatsSheet(
            skills = allSkillsList,
            sessions = allSessions,
            streakDays = streakDays,
            onDismiss = { viewModel.toggleStatsSheet(false) }
        )
    }
}

@Composable
fun HeroStatsCard(
    totalMinutes: Int,
    activeSkillsCount: Int,
    streakDays: Int,
    onOpenStats: () -> Unit,
    onQuickTimer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("hero_stats_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            IndigoPrimary.copy(alpha = 0.08f),
                            AmberMastery.copy(alpha = 0.05f)
                        )
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
                            text = "TOTAL MASTERY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = LevelSystem.formatDuration(totalMinutes),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }

                    // Active Streak Pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = RoseCoral.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = RoseCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streakDays Day Streak",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RoseCoral
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onQuickTimer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hero_quick_timer_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Timer")
                    }

                    Button(
                        onClick = onOpenStats,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hero_analytics_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analytics")
                    }
                }
            }
        }
    }
}

@Composable
fun SkillCard(
    skillWithDetails: SkillWithDetails,
    onClick: () -> Unit,
    onStartTimer: () -> Unit,
    onLogPractice: () -> Unit
) {
    val skill = skillWithDetails.skill
    val skillColor = remember(skill.colorHex) { SkillCategories.parseColor(skill.colorHex) }
    val levelInfo = skillWithDetails.levelInfo

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("skill_card_${skill.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with Icon, Name, Category & Level badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(skillColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = SkillCategories.getIcon(skill.iconKey),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = skill.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = skillColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                LevelBadge(levelInfo = levelInfo, accentColor = AmberMastery)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Level Progress Bar
            SkillProgressBar(
                progress = levelInfo.progress,
                barColor = skillColor,
                height = 8
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Progress labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${levelInfo.currentXp} XP • ${(levelInfo.progress * 100).toInt()}% to Lvl ${levelInfo.level + 1}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Text(
                    text = "${LevelSystem.formatDuration(skill.totalMinutes)} / ${skill.targetHours}h",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row with milestones and quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${skillWithDetails.completedMilestonesCount}/${skillWithDetails.milestones.size} Milestones",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Start Timer
                    IconButton(
                        onClick = onStartTimer,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(skillColor.copy(alpha = 0.12f))
                            .testTag("card_timer_${skill.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Focus Timer",
                            tint = skillColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Manual Log
                    IconButton(
                        onClick = onLogPractice,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("card_log_${skill.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Quick Log",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniTimerBar(
    timerState: TimerState,
    onOpenTimer: () -> Unit,
    onToggle: () -> Unit
) {
    val skillColor = remember(timerState.skillColorHex) { SkillCategories.parseColor(timerState.skillColorHex) }
    val minutes = (timerState.elapsedSeconds % 3600) / 60
    val seconds = timerState.elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onOpenTimer)
            .testTag("mini_timer_bar"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, skillColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (timerState.isRunning) EmeraldSuccess else RoseCoral)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = timerState.skillName.ifBlank { "Practice Timer" },
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Text(
                        text = if (timerState.isRunning) "In Progress • Tap to view" else "Paused",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = skillColor
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(skillColor)
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    searchQuery: String,
    onAddSkill: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (searchQuery.isNotBlank()) "No matching skills found" else "No skills in this category yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (searchQuery.isNotBlank()) "Try another search keyword or clear filters." else "Create a skill or start with a pre-configured template!",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddSkill,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Add New Skill")
            }
        }
    }
}
