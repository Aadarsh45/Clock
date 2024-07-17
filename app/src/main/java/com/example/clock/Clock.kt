package com.example.clock

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope


import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.*
import androidx.compose.ui.graphics.Color

class Clock : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Spacer(modifier = Modifier.height(35.dp))
                val calendar = remember { Calendar.getInstance() }
                val date by remember {
                    derivedStateOf {
                        SimpleDateFormat("dd", Locale.getDefault()).format(calendar.time)
                    }
                }
                val dayOfWeek by remember {
                    derivedStateOf {
                        SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
                    }
                }
                Text(
                    text = dayOfWeek + " " + date,
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircleBoundaryAnimation(
                        angleOffset = 0f,
                        segmentColors = listOf(Color.Magenta, Color.Yellow, Color.Blue),
                        waveAmplitude = 25.dp,
                        durationMillis = 3000
                    )
                    CircleBoundaryAnimation(
                        angleOffset = 90f,
                        segmentColors = listOf(Color.Magenta, Color.Yellow, Color.Blue),
                        waveAmplitude = 17.dp,
                        durationMillis = 900
                    )

                    CircleBoundaryAnimation(
                        angleOffset = 45f,
                        segmentColors = listOf(Color.Magenta, Color.Yellow, Color.Blue),
                        waveAmplitude = 5.dp,
                        durationMillis = 3500
                    )
                    CircleBoundaryAnimation(
                        angleOffset = 120f,
                        segmentColors = listOf(Color.Magenta, Color.Yellow, Color.Blue),
                        waveAmplitude = 15.dp,
                        durationMillis = 1500
                    )

                    Circle()
                    MyClock()
                }
            }
        }
    }

    @Composable
    fun Circle() {
        Canvas(modifier = Modifier.size(100.dp), onDraw = {
            drawCircle(
                color = Color.Black,
                radius = 181.dp.toPx()
            )
        })
    }

    @Composable
    fun MyClock() {
        val calendar = remember { Calendar.getInstance() }
        val second by remember { derivedStateOf { calendar.get(Calendar.SECOND) } }
        val minute by remember { derivedStateOf { calendar.get(Calendar.MINUTE) } }
        val hour by remember { derivedStateOf { calendar.get(Calendar.HOUR) } }

        LaunchedEffect(key1 = true) {while (true) {
            calendar.timeInMillis = System.currentTimeMillis()
            delay(1L)
        }
        }

        Canvas(
            modifier = Modifier
                .size(350.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f
            val numSegments = 10 // Number of segments for each hand

            // Draw hour hand
            val hourAngle = (hour % 12 + minute / 60f) * 30f
            drawSegmentedHand(
                angle = hourAngle,
                length = 0.4f * radius,
                thickness = 8.dp,
                numSegments = numSegments,
                colors = listOf(Color.Green, Color.Gray)
            )

            // Draw minute hand
            val minuteAngle = (minute + second / 60f) * 6f
            drawSegmentedHand(
                angle = minuteAngle,
                length = 0.6f * radius,thickness = 6.dp,
                numSegments = numSegments,
                colors = listOf(Color.Blue, Color.Gray)
            )

            // Draw second hand
            val secondAngle = second * 6f
            drawSegmentedHand(
                angle = secondAngle,
                length = 0.8f * radius,
                thickness = 4.dp,
                numSegments = numSegments,
                colors = listOf(Color.Red, Color.Gray)
            )
        }
    }
    private fun DrawScope.drawSegmentedHand(
        angle: Float,
        length: Float,
        thickness: Dp,
        numSegments: Int,
        colors: List<Color>
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val segmentLength = length / numSegments

        for (i in 0 until numSegments) {
            val startDistance = i * segmentLength
            val endDistance = (i + 1) * segmentLength
            val color = colors[i % colors.size]

            val start = Offset(
                x = center.x + startDistance * cos(Math.toRadians((angle - 90).toDouble()).toFloat()),
                y = center.y + startDistance * sin(Math.toRadians((angle - 90).toDouble()).toFloat())
            )
            val end = Offset(
                x = center.x + endDistance * cos(Math.toRadians((angle - 90).toDouble()).toFloat()),
                y = center.y + endDistance * sin(Math.toRadians((angle - 90).toDouble()).toFloat())
            )
            drawLine(
                color = color,
                start = start,
                end = end,
                strokeWidth = thickness.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

    @Composable
        fun CircleBoundaryAnimation(
            angleOffset: Float,
            segmentColors: List<Color>,
            waveAmplitude: Dp,
            durationMillis: Int
        ) {
            var animationProgress by remember { mutableStateOf(0f) }

            LaunchedEffect(Unit) {
                animationProgress = 0f
                animate(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                ) { value, _ ->
                    animationProgress = value
                }
            }

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val radius = size.width / 2 -100
                val center = Offset(size.width / 2 + 5, size.height / 2)

                val waveOffset = (animationProgress + angleOffset) * (PI / 180)
                val waveAmplitudePx = waveAmplitude.toPx()
                val segmentAngle = 360f / segmentColors.size

                for (segmentIndex in segmentColors.indices) {
                    val path = Path()
                    val startAngle = segmentIndex * segmentAngle
                    val endAngle = (segmentIndex + 1) * segmentAngle
                    val baseColor = segmentColors[segmentIndex]

                    path.moveTo(
                        x = (center.x + radius * cos(startAngle * (PI / 180)) + sin(waveOffset + startAngle * (PI / 180)) * waveAmplitudePx).toFloat(),
                        y = (center.y + radius * sin(startAngle * (PI / 180)) + cos(waveOffset + startAngle * (PI / 180)) * waveAmplitudePx).toFloat()
                    )
                    var segmentColor = Color.Transparent
                    for (i in startAngle.toInt()..endAngle.toInt()) {
                        val angle = i * (PI / 180)
                        val distanceFromCenter = radius + sin(waveOffset + angle) * waveAmplitudePx
                        val colorAlpha = (1 - distanceFromCenter / (size.width / 2))
                            .coerceIn(0.0, 1.0) // Calculate alpha based on distance
                            segmentColor = baseColor.copy(alpha = colorAlpha.toFloat())

                        val x = distanceFromCenter * cos(angle) + center.x
                        val y = distanceFromCenter * sin(angle) + center.y

                        if (x in 0f..size.width && y in 0f..size.height) {
                            path.lineTo(x.toFloat(), y.toFloat())
                        }
                    }

                    // Close the path for a continuous line
                    path.lineTo(
                        x = (center.x + radius * cos(endAngle * (PI / 180)) + sin(waveOffset + endAngle * (PI / 180)) * waveAmplitudePx).toFloat(),
                        y = (center.y + radius * sin(endAngle * (PI / 180)) + cos(waveOffset + endAngle * (PI / 180)) * waveAmplitudePx).toFloat()
                    )

                    drawPath(
                        path = path,
                        color = segmentColor, // Use the calculated color with alpha
                        style = Stroke(width = 40.dp.toPx(),
                            cap = StrokeCap.Round)
                    )
                }
            }
        }


}