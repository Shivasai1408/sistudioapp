package com.example.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.MilestoneEntity
import com.example.data.PracticeSessionEntity
import com.example.data.SkillEntity

data class SkillWithDetails(
    @Embedded val skill: SkillEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "skillId"
    )
    val milestones: List<MilestoneEntity> = emptyList(),
    @Relation(
        parentColumn = "id",
        entityColumn = "skillId"
    )
    val sessions: List<PracticeSessionEntity> = emptyList()
) {
    val completedMilestonesCount: Int
        get() = milestones.count { it.isCompleted }

    val levelInfo: SkillLevelInfo
        get() = LevelSystem.calculateLevel(skill.totalMinutes, completedMilestonesCount)

    val targetMinutes: Int
        get() = (skill.targetHours * 60).coerceAtLeast(60)

    val targetProgress: Float
        get() = (skill.totalMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)
}
