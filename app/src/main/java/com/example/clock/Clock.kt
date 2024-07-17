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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.*

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
                        segmentColors = listOf(Color.Red, Color.Yellow, Color.Magenta),
                        waveAmplitude = 20.dp
                    )
                    CircleBoundaryAnimation(
                        angleOffset = 90f,
                        segmentColors = listOf(Color.Magenta, Color.Green, Color.Blue),
                        waveAmplitude = 30.dp
                    )

                    CircleBoundaryAnimation(
                        angleOffset = 45f,
                        segmentColors = listOf(Color.Magenta, Color.Green, Color.Blue),
                        waveAmplitude = 5.dp
                    )
                    CircleBoundaryAnimation(
                        angleOffset = 120f,
                        segmentColors = listOf(Color.Magenta, Color.Green, Color.Blue),
                        waveAmplitude = 15.dp
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
                radius = 163.dp.toPx()
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
    fun CircleBoundaryAnimation( angleOffset: Float,
                                 segmentColors: List<Color>,
                                 waveAmplitude: Dp
    ) {
        var animationProgress by remember { mutableStateOf(0f) }
        val segmentColors = listOf(Color.Red, Color.Green, Color.Blue)

        LaunchedEffect(Unit) {
            animationProgress = 0f
            animate(
                initialValue = 0f,
                targetValue = 100f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 800, easing =LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) { value, _ ->
                animationProgress = value
            }
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val radius = size.width / 2 - 100
            val center = Offset(size.width / 2+5, size.height / 2)

            val waveOffset = (animationProgress + angleOffset) * (PI / 180)
            val waveAmplitude = waveAmplitude.toPx()
            val segmentAngle = 360f / segmentColors.size

            for (segmentIndex in segmentColors.indices) {
                val points = mutableListOf<Offset>()
                val startAngle = segmentIndex * segmentAngle
                val endAngle = (segmentIndex + 1) * segmentAngle

                for (i in startAngle.toInt()..endAngle.toInt()) {
                    val angle = i * (PI / 180)
                    val x = radius * cos(angle) + center.x + sin(waveOffset + angle) * waveAmplitude
                    val y = radius * sin(angle) + center.y + cos(waveOffset + angle) * waveAmplitude

                    if (x in 0f..size.width && y in 0f..size.height) {
                        points.add(Offset(x.toFloat(), y.toFloat()))
                    }
                }

                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = segmentColors[segmentIndex],
                    strokeWidth = 20.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }

}