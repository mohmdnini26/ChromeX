package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

val RainbowGlowColors = listOf(
    Color(0xFFFF007F), // Neon Pink
    Color(0xFFFF5500), // Neon Orange
    Color(0xFFFFDD00), // Neon Yellow
    Color(0xFF00FF66), // Phosphor Neon Green
    Color(0xFF00E5FF), // Electric Cyan
    Color(0xFF7B00FF), // Neon Purple
    Color(0xFFFF007F)  // Loop
)

@Composable
fun Modifier.rgbDancingBorder(
    shape: Shape,
    borderWidth: Dp = 2.5.dp,
    durationMs: Int = 2600
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "dancing_light_rgb")
    val angleDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_angle"
    )

    val rad = Math.toRadians(angleDegrees.toDouble())
    val cosVal = cos(rad).toFloat()
    val sinVal = sin(rad).toFloat()

    // Dynamic rotating linear gradient
    return this.drawWithContent {
        drawContent()

        val width = size.width
        val height = size.height
        val cx = width / 2f
        val cy = height / 2f
        val radius = maxOf(width, height) / 1.4f

        val start = Offset(cx - cosVal * radius, cy - sinVal * radius)
        val end = Offset(cx + cosVal * radius, cy + sinVal * radius)

        val brush = Brush.linearGradient(
            colors = RainbowGlowColors,
            start = start,
            end = end
        )

        val outline = shape.createOutline(size, layoutDirection, this)
        drawOutline(
            outline = outline,
            brush = brush,
            style = Stroke(width = borderWidth.toPx())
        )
    }
}
