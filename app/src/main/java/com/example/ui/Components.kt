package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SkillLevelInfo
import com.example.ui.theme.AppleGray100
import com.example.ui.theme.AppleGray200
import com.example.ui.theme.AppleGray300
import com.example.ui.theme.AppleGray400
import com.example.ui.theme.AppleGray700
import com.example.ui.theme.AppleGray800
import com.example.ui.theme.AppleGray900
import com.example.ui.theme.AppleGrayWhite
import com.example.ui.theme.AppleRed
import com.example.ui.theme.AppleRedDark
import com.example.ui.theme.AppleRedGlow
import com.example.ui.theme.AppleRedSubtle
import com.example.ui.theme.AppleRedVibrant
import com.example.ui.theme.GlassBorderBottom
import com.example.ui.theme.GlassBorderTop
import com.example.ui.theme.GlassHighlightSheen
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassSurfaceLight
import com.example.ui.theme.GlassSurfaceMedium
import com.example.ui.theme.JetBlack
import kotlinx.coroutines.launch

/**
 * Apple-style tactile spring scale on touch/press.
 * Scales slightly down (to 0.96) on down event and springs back smoothly.
 */
fun Modifier.appleSpringClick(
    enabled: Boolean = true,
    targetScale: Float = 0.96f,
    onClick: () -> Unit
): Modifier = composed {
    if (!enabled) return@composed this

    val scope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

    this
        .scale(scaleAnim.value)
        .pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                scope.launch {
                    scaleAnim.animateTo(
                        targetValue = targetScale,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }

                val up = waitForUpOrCancellation()
                scope.launch {
                    scaleAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
                if (up != null) {
                    onClick()
                }
            }
        }
}

/**
 * Liquid Glassmorphism styling with specular top edge highlight,
 * subtle vertical translucency gradient, and crisp glass border stroke.
 */
fun Modifier.liquidGlassSurface(
    shape: Shape = RoundedCornerShape(22.dp),
    baseColor: Color = Color(0xCC13141B),
    borderColor: Color = Color(0x35FFFFFF),
    borderWidth: Dp = 1.dp,
    hasSpecularHighlight: Boolean = true
): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            colors = listOf(
                baseColor.copy(alpha = 0.88f),
                baseColor.copy(alpha = 0.72f),
                baseColor.copy(alpha = 0.65f)
            )
        )
    )
    .border(
        BorderStroke(
            borderWidth,
            Brush.verticalGradient(
                colors = listOf(
                    borderColor,
                    borderColor.copy(alpha = 0.15f),
                    Color(0x05FFFFFF)
                )
            )
        ),
        shape = shape
    )
    .then(
        if (hasSpecularHighlight) {
            Modifier.drawBehind {
                // Liquid glass specular sheen across the top portion
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.45f
                    )
                )
            }
        } else Modifier
    )

@Composable
fun LevelBadge(
    levelInfo: SkillLevelInfo,
    modifier: Modifier = Modifier,
    accentColor: Color = AppleRed
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0x28FF2D55),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    accentColor.copy(alpha = 0.6f),
                    accentColor.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Lvl ${levelInfo.level} • ${levelInfo.title}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = AppleGrayWhite,
                    letterSpacing = 0.2.sp
                )
            )
        }
    }
}

@Composable
fun SkillProgressBar(
    progress: Float,
    barColor: Color,
    modifier: Modifier = Modifier,
    height: Int = 7
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    // Liquid Glass Capsule Track
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(height.dp / 2))
            .background(Color(0x33282A35))
            .border(
                BorderStroke(0.5.dp, Color(0x1AFFFFFF)),
                RoundedCornerShape(height.dp / 2)
            )
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(height.dp)
                    .clip(RoundedCornerShape(height.dp / 2))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                barColor.copy(alpha = 0.75f),
                                AppleRedVibrant,
                                AppleRed
                            )
                        )
                    )
            ) {
                // Liquid glass specular streak on top of filled bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((height / 2).dp)
                        .background(Color(0x33FFFFFF))
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeColor: Color? = null
) {
    val chipScale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chip_scale"
    )

    Surface(
        modifier = modifier
            .scale(chipScale)
            .appleSpringClick(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) {
            AppleRed
        } else {
            Color(0x66181920)
        },
        border = BorderStroke(
            1.dp,
            if (isSelected) {
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.45f),
                        AppleRedDark.copy(alpha = 0.3f)
                    )
                )
            } else {
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (badgeColor != null && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                        .border(0.5.dp, Color(0x40FFFFFF), CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                    color = if (isSelected) AppleGrayWhite else AppleGray200
                )
            )
        }
    }
}

/**
 * Animated liquid glowing orb for backgrounds, hero cards and timer sheet
 */
@Composable
fun LiquidGlowBlob(
    color: Color = AppleRed,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    alpha: Float = 0.25f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_blob")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = alpha),
                            color.copy(alpha = alpha * 0.4f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.toPx() / 2f
                    )
                )
            }
    )
}
