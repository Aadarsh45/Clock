package com.example.clock.function.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin


fun Modifier.drawAnimation(
    baseStrokeWidth: Dp = 10.dp,
    maxStrokeWidth: Dp = 20.dp,
    segmentColors: List<Color> = listOf(Color.Red, Color.Green, Color.Blue),
    durationMillis: Int = 2000
) = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val animatedStrokeWidth by infiniteTransition.animateFloat(
        initialValue = .2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(200 , easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Modifier.clip(CircleShape) // Assuming you want a circular border
        .drawWithCache {
            val strokeWidthDelta = maxStrokeWidth - baseStrokeWidth
            val strokeWidthPx = (baseStrokeWidth + (strokeWidthDelta * sin(animatedStrokeWidth * 2 * PI.toFloat()))).toPx().toFloat()
            val segmentAngle = 360f / segmentColors.size

            onDrawWithContent {
                drawContent()
                with(drawContext.canvas.nativeCanvas) {
                    rotate(angle) {
                        for (i in segmentColors.indices) {
                            val startAngle = i * segmentAngle
                            val sweepAngle = segmentAngle
                            drawArc(
                                color = Color.Transparent,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(strokeWidthPx),
                                size = Size(size.width, size.height),
                                topLeft = Offset(0f, 0f)
                            )
                        }
                    }
                }
            }
        }
}