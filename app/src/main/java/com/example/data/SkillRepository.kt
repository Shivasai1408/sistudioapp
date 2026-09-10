package com.example.data

import com.example.model.SkillWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SkillRepository(private val skillDao: SkillDao) {

    val allSkills: Flow<List<SkillWithDetails>> = skillDao.getAllSkillsWithDetails()

    val allSessions: Flow<List<PracticeSessionEntity>> = skillDao.getAllPracticeSessions()

    fun getSkillById(id: Long): Flow<SkillWithDetails?> = skillDao.getSkillWithDetailsById(id)

    suspend fun createSkill(
        name: String,
        category: String,
        description: String,
        targetHours: Int,
        colorHex: String,
        iconKey: String,
        initialMilestones: List<String> = emptyList()
    ): Long = withContext(Dispatchers.IO) {
        val skill = SkillEntity(
            name = name.trim(),
            category = category,
            description = description.trim(),
            targetHours = targetHours.coerceAtLeast(1),
            totalMinutes = 0,
            colorHex = colorHex,
            iconKey = iconKey,
            createdAt = System.currentTimeMillis()
        )
        val skillId = skillDao.insertSkill(skill)
        if (initialMilestones.isNotEmpty()) {
            val milestoneEntities = initialMilestones
                .filter { it.isNotBlank() }
                .mapIndexed { index, title ->
                    MilestoneEntity(
                        skillId = skillId,
                        title = title.trim(),
                        isCompleted = false,
                        orderIndex = index
                    )
                }
            if (milestoneEntities.isNotEmpty()) {
                skillDao.insertMilestones(milestoneEntities)
            }
        }
        skillId
    }

    suspend fun updateSkill(skill: SkillEntity) = withContext(Dispatchers.IO) {
        skillDao.updateSkill(skill)
    }

    suspend fun deleteSkill(skillId: Long) = withContext(Dispatchers.IO) {
        skillDao.deleteSkillById(skillId)
    }

    suspend fun logPracticeSession(
        skillId: Long,
        minutes: Int,
        notes: String = "",
        dateMillis: Long = System.currentTimeMillis()
    ) = withContext(Dispatchers.IO) {
        val session = PracticeSessionEntity(
            skillId = skillId,
            minutes = minutes,
            dateMillis = dateMillis,
            notes = notes.trim(),
            xpGained = minutes
        )
        skillDao.insertPracticeSession(session)
        skillDao.addPracticeMinutes(skillId, minutes, dateMillis)
    }

    suspend fun deleteSession(sessionId: Long, skillId: Long, minutes: Int) = withContext(Dispatchers.IO) {
        skillDao.deletePracticeSessionById(sessionId)
        // Adjust total minutes on skill
        skillDao.addPracticeMinutes(skillId, -minutes, System.currentTimeMillis())
    }

    suspend fun addMilestone(skillId: Long, title: String) = withContext(Dispatchers.IO) {
        val milestone = MilestoneEntity(
            skillId = skillId,
            title = title.trim(),
            isCompleted = false,
            orderIndex = System.currentTimeMillis().toInt()
        )
        skillDao.insertMilestone(milestone)
    }

    suspend fun toggleMilestone(milestone: MilestoneEntity) = withContext(Dispatchers.IO) {
        val updated = milestone.copy(
            isCompleted = !milestone.isCompleted,
            completedAt = if (!milestone.isCompleted) System.currentTimeMillis() else null
        )
        skillDao.updateMilestone(updated)
    }

    suspend fun deleteMilestone(milestoneId: Long) = withContext(Dispatchers.IO) {
        skillDao.deleteMilestoneById(milestoneId)
    }

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val existing = allSkills.first()
        if (existing.isEmpty()) {
            AppDatabase.populateInitialData(skillDao)
        }
    }
}
