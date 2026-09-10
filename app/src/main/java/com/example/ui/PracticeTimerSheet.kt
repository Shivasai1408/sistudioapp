package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SkillCategories
import com.example.ui.theme.AppleGray100
import com.example.ui.theme.AppleGray200
import com.example.ui.theme.AppleGray300
import com.example.ui.theme.AppleGray400
import com.example.ui.theme.AppleGray500
import com.example.ui.theme.AppleGrayWhite
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleRedDark
import com.example.ui.theme.AppleRedGlow
import com.example.ui.theme.AppleRedVibrant

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
        initialValue = 0.4f,
        targetValue = if (timerState.isRunning) 0.95f else 0.4f,
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
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 36.dp, height = 4.5.dp)
                    .clip(CircleShape)
                    .background(Color(0x40FFFFFF))
            )
        },
        containerColor = Color(0xF2121319),
        scrimColor = Color(0x99000000),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
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
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppleGrayWhite,
                            letterSpacing = (-0.2).sp
                        )
                    )
                    Text(
                        text = timerState.skillName.ifBlank { "Active Skill" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppleRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33282A35))
                        .border(1.dp, Color(0x20FFFFFF), CircleShape)
                        .testTag("close_timer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = AppleGray200,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Animated Liquid Glass Timer Display
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle red glow blob
                LiquidGlowBlob(
                    color = AppleRed,
                    size = 180.dp,
                    alpha = if (timerState.isRunning) 0.25f else 0.1f,
                    modifier = Modifier.align(Alignment.Center)
                )

                Canvas(modifier = Modifier.size(228.dp)) {
                    // Outer glass track
                    drawCircle(
                        color = Color(0x22FFFFFF),
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress arc (cycling every 60s)
                    val sweepAngle = ((timerState.elapsedSeconds % 60) / 60f) * 360f
                    if (sweepAngle > 0f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    AppleRedDark,
                                    AppleRed,
                                    AppleRedVibrant
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppleGrayWhite,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x28FF2D55))
                            .border(1.dp, AppleRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = AppleRed,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+$earnedMinutes XP",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppleGrayWhite,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Play / Pause / Reset Controls with Apple spring animations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button (Translucent Glass)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .appleSpringClick(onClick = onReset)
                        .clip(CircleShape)
                        .background(Color(0x33282A35))
                        .border(1.dp, Color(0x25FFFFFF), CircleShape)
                        .testTag("reset_timer_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = AppleGray200,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(22.dp))

                // Toggle Play/Pause Button (Apple Red Liquid Glass)
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .appleSpringClick(onClick = {
                            if (timerState.isRunning) onPause() else onResume()
                        })
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    AppleRedVibrant,
                                    AppleRed,
                                    AppleRedDark
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.55f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            ),
                            CircleShape
                        )
                        .testTag("toggle_timer_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (timerState.isRunning) "Pause" else "Play",
                        tint = AppleGrayWhite,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(22.dp))

                // Finish Checkmark (Frosted Glass Button)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .appleSpringClick(
                            enabled = timerState.elapsedSeconds >= 15,
                            onClick = onFinishAndSave
                        )
                        .clip(CircleShape)
                        .background(
                            if (timerState.elapsedSeconds >= 15) Color(0x33FF2D55) else Color(0x18FFFFFF)
                        )
                        .border(
                            1.dp,
                            if (timerState.elapsedSeconds >= 15) AppleRed.copy(alpha = 0.6f) else Color(0x15FFFFFF),
                            CircleShape
                        )
                        .testTag("finish_timer_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Finish",
                        tint = if (timerState.elapsedSeconds >= 15) AppleRed else AppleGray500,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Session Notes Input in Frosted Glass
            OutlinedTextField(
                value = timerState.notes,
                onValueChange = onNotesChange,
                placeholder = {
                    Text(
                        "Session notes or achievements...",
                        color = AppleGray400,
                        fontSize = 13.5.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timer_notes_input"),
                shape = RoundedCornerShape(16.dp),
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x441A1B24),
                    unfocusedContainerColor = Color(0x3316171F),
                    focusedTextColor = AppleGrayWhite,
                    unfocusedTextColor = AppleGrayWhite,
                    focusedBorderColor = AppleRed.copy(alpha = 0.6f),
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    cursorColor = AppleRed
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Finish & Save button in Apple Red Liquid Glass
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .appleSpringClick(
                        enabled = timerState.elapsedSeconds >= 15,
                        onClick = onFinishAndSave
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (timerState.elapsedSeconds >= 15) {
                            Brush.verticalGradient(listOf(AppleRedVibrant, AppleRed))
                        } else {
                            Brush.verticalGradient(listOf(Color(0x33383B46), Color(0x33282A33)))
                        }
                    )
                    .border(
                        1.dp,
                        if (timerState.elapsedSeconds >= 15) Color(0x50FFFFFF) else Color(0x18FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("save_timer_session_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (timerState.elapsedSeconds < 15) "Practice for at least 15s to save" else "Finish & Save Session (+$earnedMinutes XP)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (timerState.elapsedSeconds >= 15) AppleGrayWhite else AppleGray400,
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
