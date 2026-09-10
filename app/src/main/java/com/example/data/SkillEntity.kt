package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: String, // e.g. "Tech", "Music", "Languages", "Fitness", "Creative", "Mind"
    val description: String = "",
    val targetHours: Int = 50,
    val totalMinutes: Int = 0,
    val colorHex: String = "#4F46E5",
    val iconKey: String = "code",
    val createdAt: Long = System.currentTimeMillis(),
    val lastPracticedAt: Long? = null
)
