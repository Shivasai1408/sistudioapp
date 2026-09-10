package com.example

import com.example.model.LevelSystem
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testLevelSystem_novice() {
    val levelInfo = LevelSystem.calculateLevel(totalMinutes = 30, completedMilestonesCount = 0)
    assertEquals(1, levelInfo.level)
    assertEquals("Novice", levelInfo.title)
    assertEquals(30, levelInfo.currentXp)
  }

  @Test
  fun testLevelSystem_withMilestoneBonus() {
    // 60 minutes + 2 milestones (100 bonus XP) = 160 XP -> Level 2: Beginner
    val levelInfo = LevelSystem.calculateLevel(totalMinutes = 60, completedMilestonesCount = 2)
    assertEquals(2, levelInfo.level)
    assertEquals("Beginner", levelInfo.title)
    assertEquals(160, levelInfo.currentXp)
  }

  @Test
  fun testFormatDuration() {
    assertEquals("45m", LevelSystem.formatDuration(45))
    assertEquals("2h", LevelSystem.formatDuration(120))
    assertEquals("2h 15m", LevelSystem.formatDuration(135))
  }
}

