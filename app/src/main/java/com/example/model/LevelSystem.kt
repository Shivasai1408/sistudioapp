package com.example.model

/**
 * Level system for skills:
 * XP is derived from total minutes practiced + bonus XP from completed milestones (50 XP each).
 */
data class SkillLevelInfo(
    val level: Int,
    val title: String,
    val currentXp: Int,
    val xpForCurrentLevel: Int,
    val xpForNextLevel: Int,
    val progress: Float // 0f to 1f
)

object LevelSystem {
    // Level XP thresholds (cumulative required XP to achieve each level)
    private val thresholds = listOf(
        0 to "Novice",            // Level 1: 0 - 119 XP (~0-2 hours)
        120 to "Beginner",        // Level 2: 120 - 299 XP (~2-5 hours)
        300 to "Apprentice",      // Level 3: 300 - 599 XP (~5-10 hours)
        600 to "Practitioner",    // Level 4: 600 - 1199 XP (~10-20 hours)
        1200 to "Adept",          // Level 5: 1200 - 2399 XP (~20-40 hours)
        2400 to "Skilled",        // Level 6: 2400 - 4199 XP (~40-70 hours)
        4200 to "Specialist",     // Level 7: 4200 - 6599 XP (~70-110 hours)
        6600 to "Expert",         // Level 8: 6600 - 9999 XP (~110-166 hours)
        10000 to "Master"         // Level 9: 10000+ XP
    )

    fun calculateLevel(totalMinutes: Int, completedMilestonesCount: Int): SkillLevelInfo {
        val xpFromMinutes = totalMinutes // 1 XP per minute
        val xpFromMilestones = completedMilestonesCount * 50 // 50 bonus XP per milestone
        val totalXp = xpFromMinutes + xpFromMilestones

        var currentLvl = 1
        var currentTitle = thresholds[0].second
        var currentFloor = 0
        var nextCeiling = thresholds[1].first

        for (i in thresholds.indices) {
            val (reqXp, title) = thresholds[i]
            if (totalXp >= reqXp) {
                currentLvl = i + 1
                currentTitle = title
                currentFloor = reqXp
                nextCeiling = if (i + 1 < thresholds.size) thresholds[i + 1].first else reqXp * 2
            } else {
                break
            }
        }

        val range = (nextCeiling - currentFloor).coerceAtLeast(1)
        val progress = if (currentLvl >= thresholds.size) {
            1f
        } else {
            ((totalXp - currentFloor).toFloat() / range.toFloat()).coerceIn(0f, 1f)
        }

        return SkillLevelInfo(
            level = currentLvl,
            title = currentTitle,
            currentXp = totalXp,
            xpForCurrentLevel = currentFloor,
            xpForNextLevel = nextCeiling,
            progress = progress
        )
    }

    fun formatDuration(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }
    }
}
