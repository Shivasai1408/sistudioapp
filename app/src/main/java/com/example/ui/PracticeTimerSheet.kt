package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SkillCategories
import com.example.ui.theme.AmberMastery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeTimerSheet(
    timerState: TimerState,
    onDismiss: () -> Unit,
    onResume: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onNotesChange: (String) -> Unit,
    onFinishAndSave: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val skillColor = remember(timerState.skillColorHex) {
        SkillCategories.parseColor(timerState.skillColorHex)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (timerState.isRunning) 0.8f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val hours = timerState.elapsedSeconds / 3600
    val minutes = (timerState.elapsedSeconds % 3600) / 60
    val seconds = timerState.elapsedSeconds % 60
    val timeFormatted = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    val earnedMinutes = when {
        timerState.elapsedSeconds >= 60 -> timerState.elapsedSeconds / 60
        timerState.elapsedSeconds >= 15 -> 1
        else -> 0
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .testTag("practice_timer_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Focus Practice Session",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = timerState.skillName.ifBlank { "Active Skill" },
                        style = MaterialTheme.typography.bodyMedium.copy(color = skillColor, fontWeight = FontWeight.SemiBold)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_timer_button")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Animated Timer Display
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(230.dp)) {
                    // Outer background ring
                    drawCircle(
                        color = skillColor.copy(alpha = 0.15f),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress arc (cycling every 60s)
                    val sweepAngle = ((timerState.elapsedSeconds % 60) / 60f) * 360f
                    drawArc(
                        color = skillColor.copy(alpha = pulseAlpha),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberMastery.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = AmberMastery,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+$earnedMinutes XP",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AmberMastery
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Play / Pause / Reset Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onReset,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("reset_timer_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = {
                        if (timerState.isRunning) onPause() else onResume()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = skillColor),
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("toggle_timer_button")
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (timerState.isRunning) "Pause" else "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = onFinishAndSave,
                    enabled = timerState.elapsedSeconds >= 15,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("finish_timer_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Finish")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Session Notes Input
            OutlinedTextField(
                value = timerState.notes,
                onValueChange = onNotesChange,
                placeholder = { Text("Session notes or achievements...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timer_notes_input"),
                shape = RoundedCornerShape(14.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Finish & Save button
            Button(
                onClick = onFinishAndSave,
                enabled = timerState.elapsedSeconds >= 15,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_timer_session_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = skillColor)
            ) {
                Text(
                    text = if (timerState.elapsedSeconds < 15) "Practice for at least 15s to save" else "Finish & Save Session (+$earnedMinutes XP)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
