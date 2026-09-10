package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = SkillEntity::class,
            parentColumns = ["id"],
            childColumns = ["skillId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["skillId"])]
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val skillId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val orderIndex: Int = 0
)
