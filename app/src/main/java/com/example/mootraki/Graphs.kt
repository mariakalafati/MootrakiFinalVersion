package com.example.mootraki

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb

val CustomBarColor1 = Color(153, 204, 255)
val CustomLineColor = Color(102, 255, 158)
val CustomPointColor = Color(255, 153, 153)
val PastelRed = Color(208, 135, 135, 255)
val PastelGreen = Color(102, 255, 158)
val PastelBlue = Color(153, 204, 255)
val PastelPurple = Color(162, 146, 162, 255)
val PastelOrange = Color(224, 182, 149, 255)

@Composable
fun HorizontalBarChart(moods: List<Float>) {
    val maxValue = moods.maxOrNull() ?: 1f
    val barHeight = 30.dp
    val space = 1.dp
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec")
    var a = 0

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(space),
        horizontalAlignment = Alignment.Start
    ) {
        moods.forEach { value ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = value / maxValue)
                        .height(barHeight)
                        .background(color = CustomBarColor1)
                ) {
                    Text(
                        text = "${value.toInt()} (${months[a]})",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp)
                            .background(Color.White.copy(alpha = 0.7f))
                    )
                }
            }
            a += 1
        }
    }
}

@Composable
fun LineChart(emotionsData: List<Float>) {
    val maxValue = emotionsData.maxOrNull() ?: 1f
    val chartHeight = 200.dp
    val chartWidth = 400.dp
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var a = 0

    Column {
        Canvas(
            modifier = Modifier
                .size(chartWidth, chartHeight)
                .padding(16.dp)
        ) {
            val pointWidth = size.width / (emotionsData.size - 1)
            val lineHeight = size.height / maxValue

            for (i in 0 until emotionsData.size - 1) {
                drawLine(
                    color = CustomLineColor,
                    start = androidx.compose.ui.geometry.Offset(
                        i * pointWidth,
                        size.height - (emotionsData[i] * lineHeight)
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        (i + 1) * pointWidth,
                        size.height - (emotionsData[i + 1] * lineHeight)
                    ),
                    strokeWidth = 4f
                )
            }

            for (i in emotionsData.indices) {
                drawCircle(
                    color = CustomPointColor,
                    radius = 6f,
                    center = androidx.compose.ui.geometry.Offset(
                        i * pointWidth,
                        size.height - (emotionsData[i] * lineHeight)
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(-3.dp)
        ) {
            for (i in emotionsData.indices) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = emotionsData[i].toInt().toString(),
                        fontSize = 12.sp,
                        modifier = Modifier.width((chartWidth.value.toInt() / (emotionsData.size - 1)).dp),
                        textAlign = TextAlign.Left
                    )
                    Text(
                        text = months[i],
                        fontSize = 12.sp,
                        modifier = Modifier.width((chartWidth.value.toInt() / (emotionsData.size - 1)).dp),
                        textAlign = TextAlign.Left
                    )
                }
            }
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    emotionsCount: List<Float>,
) {
    val colors = listOf(PastelRed,PastelGreen,PastelOrange,PastelPurple,PastelBlue)
    val labels = listOf("😀", "😊", "😐", "☹️", "😢")

    val total = emotionsCount.sum()
    val slices = emotionsCount.map { it / total }
    val angles = slices.map { it * 360f }

    Canvas(modifier = modifier) {
        var startAngle = 0f
        angles.forEachIndexed { index, angle ->
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = angle,
                useCenter = true
            )

            val labelAngle = startAngle + angle / 2
            val x = (size.width / 2) + (size.height / 2) * kotlin.math.cos(Math.toRadians(labelAngle.toDouble())).toFloat()
            val y = (size.height / 2) + (size.height / 3) * kotlin.math.sin(Math.toRadians(labelAngle.toDouble())).toFloat()

            drawContext.canvas.nativeCanvas.drawText(
                labels[index],
                x,
                y,
                Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 40f
                }
            )
            startAngle += angle
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewVerticalBarChart() {
    Text(
        text = "Bar Chart: Represents the distribution of x mood For each month.",
        style = MaterialTheme.typography.bodyLarge
    )
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalBarChart(moods = listOf(1f, 2f, 3f, 2f, 2f, 1f,3f,1f,1f,2f,3f,1f))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLineChart() {
    LineChart(emotionsData = listOf(1f, 2f, 3f, 2f, 2f, 1f,3f,1f,1f,2f,3f, 1f))
    Text(
        text = "Line Chart: Shows the distribution for the selected emotion",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewPieChart() {
    Text(
        text = "Pie Chart: Displays the Mood in the last seven days.",
        style = MaterialTheme.typography.bodyLarge
    )
    PieChart(
        emotionsCount = listOf(1f, 2f, 1f, 1f, 2f),
        modifier = Modifier.size(200.dp)
    )
}
