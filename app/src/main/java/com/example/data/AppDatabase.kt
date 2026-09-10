package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SkillEntity::class,
        MilestoneEntity::class,
        PracticeSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun skillDao(): SkillDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "skills_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.skillDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: SkillDao) {
            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L

            // 1. Android Development Skill
            val skill1Id = dao.insertSkill(
                SkillEntity(
                    name = "Jetpack Compose & Kotlin",
                    category = "Tech & Code",
                    description = "Modern declarative Android UI framework, coroutines, and app architecture.",
                    targetHours = 50,
                    totalMinutes = 480, // 8 hours
                    colorHex = "#4F46E5",
                    iconKey = "code",
                    createdAt = now - (14 * oneDayMs),
                    lastPracticedAt = now - (2 * 60 * 60 * 1000L) // today
                )
            )
            dao.insertMilestones(
                listOf(
                    MilestoneEntity(skillId = skill1Id, title = "Master State & Recomposition lifecycle", isCompleted = true, completedAt = now - 10 * oneDayMs, orderIndex = 0),
                    MilestoneEntity(skillId = skill1Id, title = "Build responsive layouts with Scaffold and WindowInsets", isCompleted = true, completedAt = now - 5 * oneDayMs, orderIndex = 1),
                    MilestoneEntity(skillId = skill1Id, title = "Integrate Room Database and Kotlin Flows", isCompleted = false, orderIndex = 2),
                    MilestoneEntity(skillId = skill1Id, title = "Implement custom graphics with DrawScope Canvas", isCompleted = false, orderIndex = 3),
                    MilestoneEntity(skillId = skill1Id, title = "Deploy first production release to Google Play", isCompleted = false, orderIndex = 4)
                )
            )
            dao.insertPracticeSession(
                PracticeSessionEntity(skillId = skill1Id, minutes = 60, dateMillis = now - 2 * 60 * 60 * 1000L, notes = "Implemented reactive StateFlow and custom animations.", xpGained = 60)
            )
            dao.insertPracticeSession(
                PracticeSessionEntity(skillId = skill1Id, minutes = 90, dateMillis = now - oneDayMs, notes = "Built custom Canvas ring indicators and bottom sheets.", xpGained = 90)
            )
            dao.insertPracticeSession(
                PracticeSessionEntity(skillId = skill1Id, minutes = 120, dateMillis = now - 3 * oneDayMs, notes = "Studied M3 dynamic theming and color palettes.", xpGained = 120)
            )

            // 2. Acoustic Guitar Skill
            val skill2Id = dao.insertSkill(
                SkillEntity(
                    name = "Acoustic Guitar",
                    category = "Music & Audio",
                    description = "Fingerstyle techniques, rhythm strumming, and chord progressions.",
                    targetHours = 40,
                    totalMinutes = 320, // ~5.3 hours
                    colorHex = "#8B5CF6",
                    iconKey = "music",
                    createdAt = now - (20 * oneDayMs),
                    lastPracticedAt = now - oneDayMs
                )
            )
            dao.insertMilestones(
                listOf(
                    MilestoneEntity(skillId = skill2Id, title = "Learn 8 open chords (C, G, D, Em, Am, E, A, Dm)", isCompleted = true, completedAt = now - 12 * oneDayMs, orderIndex = 0),
                    MilestoneEntity(skillId = skill2Id, title = "Master 60 BPM smooth chord switching", isCompleted = true, completedAt = now - 4 * oneDayMs, orderIndex = 1),
                    MilestoneEntity(skillId = skill2Id, title = "Play clean F barre chord with no buzzing", isCompleted = false, orderIndex = 2),
                    MilestoneEntity(skillId = skill2Id, title = "Learn Travis fingerpicking pattern", isCompleted = false, orderIndex = 3)
                )
            )
            dao.insertPracticeSession(
                PracticeSessionEntity(skillId = skill2Id, minutes = 45, dateMillis = now - oneDayMs, notes = "Practiced transition to Barre F chord with metronome.", xpGained = 45)
            )
            dao.insertPracticeSession(
                PracticeSessionEntity(skillId = skill2Id, minutes = 60, dateMillis = now - 2 * oneDayMs, notes = "Played along to chord chart for 3 songs.", xpGained = 60)
            )

            // 3. Conversational Spanish
            val skill3Id = dao.insertSkill(
                SkillEntity(
                    name = "Conversational Spanish",
                    category = "Languages",
                    description = "Daily conversational fluency, core vocabulary, and natural speaking.",
                    targetHours = 60,
                    totalMinutes = 180, // 3 hours
                    colorHex = "#0EA5E9",
                    iconKey = "translate",
                    createdAt = now - (7 * oneDayMs),
                    lastPracticedAt = now - 2 * oneDayMs
                )
            )
            dao.insertMilestones(
                listOf(
                    MilestoneEntity(skillId = skill3Id, title = "Learn phonetic vowel sounds and rolled R", isCompleted = true, completedAt = now - 6 * oneDayMs, orderIndex = 0),
                    MilestoneEntity(skillId = skill3Id, title = "Memorize top 200 essential survival words", isCompleted = false, orderIndex = 1),
                    MilestoneEntity(skillId = skill3Id, title = "Master present tense verb conjugations", isCompleted = false, orderIndex = 2),
                    MilestoneEntity(skillId = skill3Id, title = "Hold 5-minute conversation with native speaker", isCompleted = false, orderIndex = 3)
                )
            )
            dao.insertPracticeSession(
                PracticeSessionEntity(skillId = skill3Id, minutes = 30, dateMillis = now - 2 * oneDayMs, notes = "Vocabulary flashcards & sentence builder exercises.", xpGained = 30)
            )
        }
    }
}
