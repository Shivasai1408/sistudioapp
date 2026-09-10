package com.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.MilestoneEntity
import com.example.data.SkillEntity
import com.example.model.SkillWithDetails
import com.example.ui.HeroStatsCard
import com.example.ui.SkillCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent { MyApplicationTheme { Greeting("Skills") } }
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun skills_overview_screenshot() {
    val sampleSkill = SkillWithDetails(
      skill = SkillEntity(
        id = 1L,
        name = "Kotlin & Compose",
        category = "Tech & Code",
        description = "Modern declarative UI on Android",
        targetHours = 50,
        totalMinutes = 145,
        colorHex = "#4F46E5",
        iconKey = "code"
      ),
      milestones = listOf(
        MilestoneEntity(id = 1, skillId = 1, title = "Master State & Recomposition", isCompleted = true),
        MilestoneEntity(id = 2, skillId = 1, title = "Build Room & Flow integration", isCompleted = true),
        MilestoneEntity(id = 3, skillId = 1, title = "Custom Graphics & Canvas animations", isCompleted = false)
      )
    )

    val guitarSkill = SkillWithDetails(
      skill = SkillEntity(
        id = 2L,
        name = "Acoustic Guitar",
        category = "Music & Audio",
        description = "Chords, fingerpicking, and rhythm",
        targetHours = 60,
        totalMinutes = 80,
        colorHex = "#D97706",
        iconKey = "music"
      ),
      milestones = listOf(
        MilestoneEntity(id = 4, skillId = 2, title = "Open chords fluency (C, G, D, Em, Am)", isCompleted = true),
        MilestoneEntity(id = 5, skillId = 2, title = "Barre chords mastery (F, Bm)", isCompleted = false)
      )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
          HeroStatsCard(
            totalMinutes = 225,
            activeSkillsCount = 2,
            streakDays = 5,
            onOpenStats = {},
            onQuickTimer = {}
          )
          SkillCard(
            skillWithDetails = sampleSkill,
            onClick = {},
            onStartTimer = {},
            onLogPractice = {}
          )
          SkillCard(
            skillWithDetails = guitarSkill,
            onClick = {},
            onStartTimer = {},
            onLogPractice = {}
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/skills_overview.png")
  }
}

