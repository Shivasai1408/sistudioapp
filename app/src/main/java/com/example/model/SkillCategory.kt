package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryDef(
    val name: String,
    val iconKey: String,
    val defaultColor: String
)

object SkillCategories {
    val CATEGORIES = listOf(
        "All",
        "Tech & Code",
        "Creative & Arts",
        "Languages",
        "Music & Audio",
        "Fitness & Body",
        "Mind & Lifestyle",
        "Career & Business"
    )

    val DEFINITIONS = listOf(
        CategoryDef("Tech & Code", "code", "#4F46E5"),
        CategoryDef("Creative & Arts", "brush", "#EC4899"),
        CategoryDef("Languages", "translate", "#0EA5E9"),
        CategoryDef("Music & Audio", "music", "#8B5CF6"),
        CategoryDef("Fitness & Body", "fitness", "#10B981"),
        CategoryDef("Mind & Lifestyle", "mind", "#F59E0B"),
        CategoryDef("Career & Business", "career", "#6366F1")
    )

    val PRESET_COLORS = listOf(
        "#4F46E5", // Indigo
        "#8B5CF6", // Violet
        "#EC4899", // Pink
        "#F43F5E", // Rose
        "#F59E0B", // Amber
        "#10B981", // Emerald
        "#0EA5E9", // Sky
        "#06B6D4", // Cyan
        "#3B82F6", // Blue
        "#84CC16"  // Lime
    )

    fun getIcon(iconKey: String): ImageVector {
        return when (iconKey.lowercase()) {
            "code" -> Icons.Default.Code
            "brush", "palette" -> Icons.Default.Brush
            "translate", "language" -> Icons.Default.Translate
            "music", "guitar" -> Icons.Default.MusicNote
            "fitness", "gym" -> Icons.Default.FitnessCenter
            "mind", "zen" -> Icons.Default.SelfImprovement
            "career", "work" -> Icons.Default.Work
            "book" -> Icons.AutoMirrored.Filled.MenuBook
            "mic", "voice" -> Icons.Default.RecordVoiceOver
            "psychology" -> Icons.Default.Psychology
            "audio" -> Icons.Default.Headphones
            else -> Icons.Default.Lightbulb
        }
    }

    fun parseColor(hex: String, fallback: Color = Color(0xFF4F46E5)): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            val colorLong = if (cleanHex.length == 6) {
                "FF$cleanHex".toLong(16)
            } else {
                cleanHex.toLong(16)
            }
            Color(colorLong)
        } catch (_: Exception) {
            fallback
        }
    }
}

data class SkillTemplate(
    val name: String,
    val category: String,
    val description: String,
    val targetHours: Int,
    val colorHex: String,
    val iconKey: String,
    val defaultMilestones: List<String>
)

object SkillTemplates {
    val TEMPLATES = listOf(
        SkillTemplate(
            name = "Jetpack Compose & Kotlin",
            category = "Tech & Code",
            description = "Modern declarative Android UI framework and reactive Kotlin flows.",
            targetHours = 60,
            colorHex = "#4F46E5",
            iconKey = "code",
            defaultMilestones = listOf(
                "Master State & Recomposition primitives",
                "Build reusable custom layouts & modifiers",
                "Integrate Navigation Compose & Room Database",
                "Implement custom Canvas graphics & animations",
                "Architect a production-ready clean MVVM app"
            )
        ),
        SkillTemplate(
            name = "Acoustic Guitar",
            category = "Music & Audio",
            description = "Fingerstyle techniques, rhythm strumming, and chord progressions.",
            targetHours = 50,
            colorHex = "#8B5CF6",
            iconKey = "music",
            defaultMilestones = listOf(
                "Learn 8 fundamental open chords (C, G, D, Em, Am, E, A, Dm)",
                "Master smooth chord transitions with a metronome",
                "Conquer the F major barre chord cleanly",
                "Learn fingerpicking patterns & Travis picking",
                "Play 3 complete songs without stopping"
            )
        ),
        SkillTemplate(
            name = "Conversational Spanish",
            category = "Languages",
            description = "Daily conversational fluency, core vocabulary, and natural dialogue.",
            targetHours = 80,
            colorHex = "#0EA5E9",
            iconKey = "translate",
            defaultMilestones = listOf(
                "Master alphabet, vowels & phonetic pronunciation",
                "Build active vocabulary of first 500 common words",
                "Conjugate regular present & preterite past tenses",
                "Hold a 5-minute continuous conversation with a speaker",
                "Watch and understand an episode of a Spanish series"
            )
        ),
        SkillTemplate(
            name = "Digital Illustration",
            category = "Creative & Arts",
            description = "Character design, dynamic lighting, color harmony, and brushwork.",
            targetHours = 50,
            colorHex = "#EC4899",
            iconKey = "brush",
            defaultMilestones = listOf(
                "Practice daily 30-second gesture drawings",
                "Learn skull, torso, and limb anatomy landmarks",
                "Master value scales & dynamic light sources",
                "Study color palettes & atmospheric rendering",
                "Complete a polished concept illustration"
            )
        ),
        SkillTemplate(
            name = "Chess Strategy & Tactics",
            category = "Mind & Lifestyle",
            description = "Tactical vision, opening repertoires, and endgame calculation.",
            targetHours = 40,
            colorHex = "#F59E0B",
            iconKey = "mind",
            defaultMilestones = listOf(
                "Solve 200 tactical puzzles (forks, pins, skewers)",
                "Memorize solid opening system for White and Black",
                "Master King + Pawn and Rook endgame basics",
                "Reach 1200+ rating on online blitz/rapid",
                "Analyze and review 20 recorded Grandmaster games"
            )
        )
    )
}
