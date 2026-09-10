package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ui.theme.AppleGray100
import com.example.ui.theme.AppleGray200
import com.example.ui.theme.AppleGray300
import com.example.ui.theme.AppleGray400
import com.example.ui.theme.AppleGray500
import com.example.ui.theme.AppleGray600
import com.example.ui.theme.AppleGray700
import com.example.ui.theme.AppleGray800
import com.example.ui.theme.AppleGray900
import com.example.ui.theme.AppleGrayWhite
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleRedDark
import com.example.ui.theme.AppleRedGlow
import com.example.ui.theme.AppleRedSubtle
import com.example.ui.theme.AppleRedVibrant
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.GlassBorderTop
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceMedium
import com.example.ui.theme.JetBlack
import com.example.ui.theme.ObsidianBlack
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
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Apple Liquid Glass icon badge
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            AppleRedVibrant,
                                            AppleRedDark
                                        )
                                    )
                                )
                                .border(1.dp, Color(0x60FFFFFF), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AppleGrayWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Skills",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp,
                                    color = AppleGrayWhite
                                )
                            )
                            Text(
                                text = "Mastery & Growth",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppleGray400,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.2.sp
                                )
                            )
                        }
                    }
                },
                actions = {
                    // Analytics Glass Button
                    IconButton(
                        onClick = { viewModel.toggleStatsSheet(true) },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x33262832))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("open_stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Analytics & Stats",
                            tint = AppleGray200,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Timer Glass Button
                    IconButton(
                        onClick = { viewModel.toggleTimerSheet(true) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (timerState.isRunning) Color(0x33FF2D55) else Color(0x33262832)
                            )
                            .border(
                                1.dp,
                                if (timerState.isRunning) AppleRed.copy(alpha = 0.5f) else Color(0x20FFFFFF),
                                CircleShape
                            )
                            .testTag("open_timer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Practice Timer",
                            tint = if (timerState.isRunning) AppleRed else AppleGray200,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBlack.copy(alpha = 0.85f)
                ),
                modifier = Modifier.border(
                    BorderStroke(0.5.dp, Color(0x14FFFFFF))
                )
            )
        },
        floatingActionButton = {
            // Apple Liquid Glass FAB in Apple Red with spring animation
            Box(
                modifier = Modifier
                    .appleSpringClick(
                        onClick = { viewModel.openAddSkillDialog() }
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                AppleRedVibrant,
                                AppleRed,
                                AppleRedDark
                            )
                        )
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .testTag("add_skill_fab")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Skill",
                        tint = AppleGrayWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Skill",
                        fontWeight = FontWeight.Bold,
                        color = AppleGrayWhite,
                        fontSize = 14.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ambient Liquid Red/Smoke Glow in the background
            LiquidGlowBlob(
                color = AppleRed,
                size = 280.dp,
                alpha = 0.15f,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = (-40).dp)
            )

            LiquidGlowBlob(
                color = AppleGray600,
                size = 320.dp,
                alpha = 0.12f,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = (-80).dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Hero Stats Banner in Liquid Glass
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

                // Search Bar & Filter Controls in Liquid Glass
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        "Search skills or categories...",
                                        color = AppleGray400,
                                        fontSize = 13.5.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = AppleGray400,
                                        modifier = Modifier.size(19.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear search",
                                                tint = AppleGray300,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("search_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0x441A1B22),
                                    unfocusedContainerColor = Color(0x3316171E),
                                    focusedTextColor = AppleGrayWhite,
                                    unfocusedTextColor = AppleGrayWhite,
                                    focusedBorderColor = AppleRed.copy(alpha = 0.6f),
                                    unfocusedBorderColor = Color(0x22FFFFFF),
                                    cursorColor = AppleRed
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box {
                                IconButton(
                                    onClick = { showSortMenu = true },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0x441A1B22))
                                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                                        .appleSpringClick { showSortMenu = true }
                                        .testTag("sort_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = "Sort",
                                        tint = AppleGrayWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false },
                                    modifier = Modifier
                                        .background(Color(0xF0181920))
                                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
                                ) {
                                    SortOption.values().forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    option.label,
                                                    color = if (sortBy == option) AppleRed else AppleGray100,
                                                    fontWeight = if (sortBy == option) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            trailingIcon = {
                                                if (sortBy == option) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = AppleRed,
                                                        modifier = Modifier.size(16.dp)
                                                    )
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
                    items(
                        items = skills,
                        key = { it.skill.id }
                    ) { skillWithDetails ->
                        SkillCard(
                            skillWithDetails = skillWithDetails,
                            onClick = { viewModel.openSkillDetail(skillWithDetails) },
                            onStartTimer = { viewModel.startTimerForSkill(skillWithDetails) },
                            onLogPractice = { viewModel.openManualLog(skillWithDetails) }
                        )
                    }
                }
            }

            // Bottom Mini-Timer Floating Bar (Apple Dynamic Island style)
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

            // Floating Celebration Banner (Apple Dynamic notification style)
            AnimatedVisibility(
                visible = currentCelebration != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                currentCelebration?.let { event ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xEE1E2028),
                                        Color(0xEE121318)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        AppleRed.copy(alpha = 0.8f),
                                        Color(0x20FFFFFF)
                                    )
                                ),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(AppleRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (event.isLevelUp) Icons.Default.Star else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AppleGrayWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = event.message,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleGrayWhite
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
            onSave = { skillId, minutes, dateMillis, notes ->
                viewModel.logPractice(
                    skillId = skillId,
                    minutes = minutes,
                    notes = notes,
                    dateMillis = dateMillis
                )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .liquidGlassSurface(
                shape = RoundedCornerShape(26.dp),
                baseColor = Color(0xDD14151C),
                borderColor = Color(0x35FFFFFF)
            )
            .testTag("hero_stats_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                            color = AppleRed,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = LevelSystem.formatDuration(totalMinutes),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = AppleGrayWhite,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }

                // Active Streak Pill in Apple Red liquid glass
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x28FF2D55))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    AppleRed.copy(alpha = 0.6f),
                                    AppleRed.copy(alpha = 0.2f)
                                )
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AppleRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "$streakDays Day Streak",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppleGrayWhite,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons with Apple spring animations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Start Timer (Apple Red Glass Button)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appleSpringClick(onClick = onQuickTimer)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    AppleRedVibrant,
                                    AppleRed
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.45f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(vertical = 11.dp)
                        .testTag("hero_quick_timer_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = AppleGrayWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Timer",
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite,
                            fontSize = 13.sp
                        )
                    }
                }

                // Analytics (Translucent Gray Glass Button)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appleSpringClick(onClick = onOpenStats)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x33282A36))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.25f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(vertical = 11.dp)
                        .testTag("hero_analytics_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = AppleGray200,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Analytics",
                            fontWeight = FontWeight.SemiBold,
                            color = AppleGray200,
                            fontSize = 13.sp
                        )
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .appleSpringClick(onClick = onClick)
            .liquidGlassSurface(
                shape = RoundedCornerShape(24.dp),
                baseColor = Color(0xCC151720),
                borderColor = Color(0x2EFFFFFF)
            )
            .testTag("skill_card_${skill.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header row with Icon, Name, Category & Level badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glass Icon Container
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
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
                                listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = SkillCategories.getIcon(skill.iconKey),
                        contentDescription = null,
                        tint = AppleGrayWhite,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite,
                            letterSpacing = (-0.2).sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = skill.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AppleRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                LevelBadge(levelInfo = levelInfo, accentColor = AppleRed)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Liquid Glass Capsule Progress Bar
            SkillProgressBar(
                progress = levelInfo.progress,
                barColor = skillColor,
                height = 7
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Progress labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${levelInfo.currentXp} XP • ${(levelInfo.progress * 100).toInt()}% to Lvl ${levelInfo.level + 1}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AppleGray400,
                        fontSize = 11.5.sp
                    )
                )

                Text(
                    text = "${LevelSystem.formatDuration(skill.totalMinutes)} / ${skill.targetHours}h",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AppleGray200,
                        fontSize = 11.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row with milestones and quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Milestone Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33282A35))
                        .border(0.5.dp, Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${skillWithDetails.completedMilestonesCount}/${skillWithDetails.milestones.size} Milestones",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = AppleGray300,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Start Timer in Apple Red Glass Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .appleSpringClick(onClick = onStartTimer)
                            .clip(CircleShape)
                            .background(Color(0x33FF2D55))
                            .border(1.dp, AppleRed.copy(alpha = 0.5f), CircleShape)
                            .testTag("card_timer_${skill.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Focus Timer",
                            tint = AppleRed,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Manual Log Glass Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .appleSpringClick(onClick = onLogPractice)
                            .clip(CircleShape)
                            .background(Color(0x33282A35))
                            .border(1.dp, Color(0x20FFFFFF), CircleShape)
                            .testTag("card_log_${skill.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Quick Log",
                            tint = AppleGray200,
                            modifier = Modifier.size(17.dp)
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
    val minutes = (timerState.elapsedSeconds % 3600) / 60
    val seconds = timerState.elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .appleSpringClick(onClick = onOpenTimer)
            .liquidGlassSurface(
                shape = RoundedCornerShape(22.dp),
                baseColor = Color(0xEE161820),
                borderColor = AppleRed.copy(alpha = 0.5f)
            )
            .testTag("mini_timer_bar")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pulsing Apple Red dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (timerState.isRunning) AppleRed else AppleGray500)
                        .border(1.dp, Color(0x60FFFFFF), CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = timerState.skillName.ifBlank { "Practice Timer" },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = if (timerState.isRunning) "In Progress • Tap to view" else "Paused",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AppleGray400,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = AppleRed,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .appleSpringClick(onClick = onToggle)
                        .clip(CircleShape)
                        .background(AppleRed)
                        .border(1.dp, Color(0x40FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle",
                        tint = AppleGrayWhite,
                        modifier = Modifier.size(17.dp)
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
            .padding(24.dp)
            .liquidGlassSurface(
                shape = RoundedCornerShape(26.dp),
                baseColor = Color(0xCC14151C),
                borderColor = Color(0x22FFFFFF)
            )
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x22FF2D55))
                    .border(1.dp, AppleRed.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AppleRed,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (searchQuery.isNotBlank()) "No matching skills found" else "No skills in this category yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppleGrayWhite
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (searchQuery.isNotBlank()) "Try another search keyword or clear filters." else "Create a skill or start with a pre-configured template!",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AppleGray400,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .appleSpringClick(onClick = onAddSkill)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppleRed)
                    .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 11.dp)
            ) {
                Text(
                    text = "+ Add New Skill",
                    fontWeight = FontWeight.Bold,
                    color = AppleGrayWhite,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}
