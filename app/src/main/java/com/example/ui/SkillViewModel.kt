package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MilestoneEntity
import com.example.data.PracticeSessionEntity
import com.example.data.SkillEntity
import com.example.data.SkillRepository
import com.example.model.SkillWithDetails
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class SortOption(val label: String) {
    RECENT("Recently Practiced"),
    LEVEL("Highest Level"),
    HOURS("Most Practice Time"),
    NAME("Alphabetical")
}

data class TimerState(
    val skillId: Long? = null,
    val skillName: String = "",
    val skillColorHex: String = "#4F46E5",
    val isRunning: Boolean = false,
    val elapsedSeconds: Int = 0,
    val notes: String = ""
)

data class CelebrationEvent(
    val message: String,
    val isLevelUp: Boolean = false
)

class SkillViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SkillRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = SkillRepository(db.skillDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val allSkills: StateFlow<List<SkillWithDetails>> = repository.allSkills
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSessions: StateFlow<List<PracticeSessionEntity>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortBy = MutableStateFlow(SortOption.RECENT)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()

    val filteredSkills: StateFlow<List<SkillWithDetails>> = combine(
        allSkills,
        _selectedCategory,
        _searchQuery,
        _sortBy
    ) { skills, category, query, sort ->
        var result = skills

        if (category != "All") {
            result = result.filter { it.skill.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.skill.name.lowercase().contains(q) ||
                it.skill.category.lowercase().contains(q) ||
                it.skill.description.lowercase().contains(q)
            }
        }

        when (sort) {
            SortOption.RECENT -> result.sortedByDescending { it.skill.lastPracticedAt ?: 0L }
            SortOption.LEVEL -> result.sortedByDescending { it.levelInfo.currentXp }
            SortOption.HOURS -> result.sortedByDescending { it.skill.totalMinutes }
            SortOption.NAME -> result.sortedBy { it.skill.name.lowercase() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI Dialog & BottomSheet state
    private val _selectedSkillId = MutableStateFlow<Long?>(null)
    val selectedSkillId: StateFlow<Long?> = _selectedSkillId.asStateFlow()

    val selectedSkill: StateFlow<SkillWithDetails?> = combine(
        allSkills,
        _selectedSkillId
    ) { list, id ->
        list.find { it.skill.id == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _showAddSkillDialog = MutableStateFlow(false)
    val showAddSkillDialog: StateFlow<Boolean> = _showAddSkillDialog.asStateFlow()

    private val _editingSkill = MutableStateFlow<SkillEntity?>(null)
    val editingSkill: StateFlow<SkillEntity?> = _editingSkill.asStateFlow()

    private val _manualLogSkill = MutableStateFlow<SkillWithDetails?>(null)
    val manualLogSkill: StateFlow<SkillWithDetails?> = _manualLogSkill.asStateFlow()

    private val _showStatsSheet = MutableStateFlow(false)
    val showStatsSheet: StateFlow<Boolean> = _showStatsSheet.asStateFlow()

    private val _showTimerSheet = MutableStateFlow(false)
    val showTimerSheet: StateFlow<Boolean> = _showTimerSheet.asStateFlow()

    // Timer State
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null

    // Event Flow
    private val _events = MutableSharedFlow<CelebrationEvent>()
    val events = _events.asSharedFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortBy(option: SortOption) {
        _sortBy.value = option
    }

    fun openSkillDetail(skill: SkillWithDetails) {
        _selectedSkillId.value = skill.skill.id
    }

    fun closeSkillDetail() {
        _selectedSkillId.value = null
    }

    fun openAddSkillDialog() {
        _editingSkill.value = null
        _showAddSkillDialog.value = true
    }

    fun openEditSkillDialog(skill: SkillEntity) {
        _editingSkill.value = skill
        _showAddSkillDialog.value = true
    }

    fun closeAddEditSkillDialog() {
        _showAddSkillDialog.value = false
        _editingSkill.value = null
    }

    fun openManualLog(skill: SkillWithDetails) {
        _manualLogSkill.value = skill
    }

    fun closeManualLog() {
        _manualLogSkill.value = null
    }

    fun toggleStatsSheet(show: Boolean) {
        _showStatsSheet.value = show
    }

    fun toggleTimerSheet(show: Boolean) {
        _showTimerSheet.value = show
    }

    fun saveSkill(
        name: String,
        category: String,
        description: String,
        targetHours: Int,
        colorHex: String,
        iconKey: String,
        initialMilestones: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val existing = _editingSkill.value
            if (existing != null) {
                repository.updateSkill(
                    existing.copy(
                        name = name,
                        category = category,
                        description = description,
                        targetHours = targetHours,
                        colorHex = colorHex,
                        iconKey = iconKey
                    )
                )
                _events.emit(CelebrationEvent("Updated $name!"))
            } else {
                repository.createSkill(
                    name = name,
                    category = category,
                    description = description,
                    targetHours = targetHours,
                    colorHex = colorHex,
                    iconKey = iconKey,
                    initialMilestones = initialMilestones
                )
                _events.emit(CelebrationEvent("Created new skill: $name!"))
            }
            closeAddEditSkillDialog()
        }
    }

    fun deleteSkill(skillId: Long) {
        viewModelScope.launch {
            if (_selectedSkillId.value == skillId) {
                _selectedSkillId.value = null
            }
            repository.deleteSkill(skillId)
            _events.emit(CelebrationEvent("Skill deleted"))
        }
    }

    fun logPractice(skillId: Long, minutes: Int, notes: String, dateMillis: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val skillBefore = allSkills.value.find { it.skill.id == skillId }
            val lvlBefore = skillBefore?.levelInfo?.level ?: 1

            repository.logPracticeSession(skillId, minutes, notes, dateMillis)

            // Check if level increased
            val skillAfter = allSkills.value.find { it.skill.id == skillId }
            val lvlAfter = skillAfter?.levelInfo?.level ?: lvlBefore

            if (lvlAfter > lvlBefore) {
                _events.emit(
                    CelebrationEvent(
                        message = "LEVEL UP! Reached Level $lvlAfter • ${skillAfter?.levelInfo?.title}!",
                        isLevelUp = true
                    )
                )
            } else {
                _events.emit(CelebrationEvent("Logged +${minutes}m practice (+${minutes} XP)!"))
            }
            closeManualLog()
        }
    }

    fun toggleMilestone(milestone: MilestoneEntity) {
        viewModelScope.launch {
            val wasCompleted = milestone.isCompleted
            repository.toggleMilestone(milestone)
            if (!wasCompleted) {
                _events.emit(CelebrationEvent("Milestone completed! +50 Bonus XP 🎉", isLevelUp = false))
            }
        }
    }

    fun addMilestone(skillId: Long, title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addMilestone(skillId, title)
            _events.emit(CelebrationEvent("Added new milestone!"))
        }
    }

    fun deleteMilestone(milestoneId: Long) {
        viewModelScope.launch {
            repository.deleteMilestone(milestoneId)
        }
    }

    fun deleteSession(sessionId: Long, skillId: Long, minutes: Int) {
        viewModelScope.launch {
            repository.deleteSession(sessionId, skillId, minutes)
        }
    }

    // --- Practice Timer Controls ---
    fun startTimerForSkill(skill: SkillWithDetails) {
        if (_timerState.value.skillId != skill.skill.id) {
            _timerState.value = TimerState(
                skillId = skill.skill.id,
                skillName = skill.skill.name,
                skillColorHex = skill.skill.colorHex,
                isRunning = true,
                elapsedSeconds = 0,
                notes = ""
            )
        } else {
            _timerState.value = _timerState.value.copy(isRunning = true)
        }
        _showTimerSheet.value = true
        launchTimerLoop()
    }

    fun resumeTimer() {
        if (_timerState.value.skillId == null) return
        _timerState.value = _timerState.value.copy(isRunning = true)
        launchTimerLoop()
    }

    fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isRunning = false)
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        _timerState.value = _timerState.value.copy(elapsedSeconds = 0, notes = "")
    }

    fun updateTimerNotes(notes: String) {
        _timerState.value = _timerState.value.copy(notes = notes)
    }

    fun finishAndSaveTimerSession() {
        val state = _timerState.value
        val skillId = state.skillId ?: return
        val totalSeconds = state.elapsedSeconds
        // Even if less than 1 minute, give at least 1 minute credit if practiced for > 15 seconds
        val minutes = when {
            totalSeconds >= 60 -> (totalSeconds / 60)
            totalSeconds >= 15 -> 1
            else -> 0
        }

        if (minutes > 0) {
            logPractice(skillId, minutes, state.notes.ifBlank { "Live Practice Session (${totalSeconds / 60}m ${totalSeconds % 60}s)" })
        }
        resetTimer()
        _showTimerSheet.value = false
    }

    private fun launchTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning) {
                delay(1000L)
                _timerState.value = _timerState.value.copy(
                    elapsedSeconds = _timerState.value.elapsedSeconds + 1
                )
            }
        }
    }

    // --- Streak & Stats Calculations ---
    fun calculateStreak(sessions: List<PracticeSessionEntity>): Int {
        if (sessions.isEmpty()) return 0
        val sortedDays = sessions.map { session ->
            val cal = Calendar.getInstance().apply { timeInMillis = session.dateMillis }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.distinct().sortedDescending()

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayMs = todayCal.timeInMillis
        val yesterdayMs = todayMs - (24 * 60 * 60 * 1000L)

        // Check if user practiced today or yesterday
        if (sortedDays.isEmpty() || (sortedDays[0] != todayMs && sortedDays[0] != yesterdayMs)) {
            return 0
        }

        var streak = 1
        var previousDay = sortedDays[0]

        for (i in 1 until sortedDays.size) {
            val currentDay = sortedDays[i]
            val diffDays = (previousDay - currentDay) / (24 * 60 * 60 * 1000L)
            if (diffDays == 1L) {
                streak++
                previousDay = currentDay
            } else if (diffDays > 1L) {
                break
            }
        }
        return streak
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
