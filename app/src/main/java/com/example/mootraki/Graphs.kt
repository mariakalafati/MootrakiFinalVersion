package com.example.mootraki

// BarChart.kt
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
// LineChart.kt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
// PieChart.kt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp // Import for dp
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb

// Define custom colors
val CustomBarColor1 = Color(153, 204, 255) // Custom RGB color
// Define custom colors
val CustomLineColor = Color(102, 255, 158) // Custom RGB color
val CustomPointColor = Color(255, 153, 153) // Custom RGB color
// Define custom colors directly in this file
val PastelRed = Color(208, 135, 135, 255) // RGB
val PastelGreen = Color(102, 255, 158) // RGB
val PastelBlue = Color(153, 204, 255) // RGB
val PastelPurple = Color(162, 146, 162, 255) // RGB
val PastelOrange = Color(224, 182, 149, 255) // RGB

@Composable
fun HorizontalBarChart(moods: List<Float>) {
    val maxValue = moods.maxOrNull() ?: 1f
    val barHeight = 30.dp // height of the bars
    val space = 1.dp
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec")
    var a = 0

    // Use a Column to stack the bars
    Column(
        modifier = Modifier
            .fillMaxHeight() // Fill the height of the parent
            .padding(30.dp), // adds 16 dp of padding around it
        verticalArrangement = Arrangement.spacedBy(space), // Space between bars
        horizontalAlignment = Alignment.Start // Align bars to the start
    ) {
        moods.forEach { value ->
            // Use a Row to place the bar and labels in the same line
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the parent
                    .height(barHeight), // Set the height of the bar
                verticalAlignment = Alignment.CenterVertically // Center the content vertically
            ) {
                // Use a Box to control the width of the bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = value / maxValue) // Fill width based on value
                        .height(barHeight) // Set the height of the bar
                        .background(color = CustomBarColor1) // Set the color of the bar
                ) {
                    // Display the value and month inside the bar
                    Text(
                        text = "${value.toInt()} (${months[a]})", // Combine value and month
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Align text to the bottom end of the bar
                            .padding(end = 8.dp) // Add some padding to the end
                            .background(Color.White.copy(alpha = 0.7f)) // Optional: background for better visibility
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

    // Create a Column to stack the chart and labels
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

        // Create a Row to display labels under each point
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
                        fontSize = 12.sp, // Decrease font size
                        modifier = Modifier.width((chartWidth.value.toInt() / (emotionsData.size - 1)).dp), // Set width to align with points
                        textAlign = TextAlign.Left // Center the text under each point
                    )
                    Text(
                        text = months[i],
                        fontSize = 12.sp,
                        modifier = Modifier.width((chartWidth.value.toInt() / (emotionsData.size - 1)).dp), // Set width to align with points
                        textAlign = TextAlign.Left // Center the text under each point
                    )
                }
            }
            //}
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    emotionsCount: List<Float>,
) {
    // Define colors and labels inside the function
    val colors = listOf(PastelRed,PastelGreen,PastelOrange,PastelPurple,PastelBlue) // Fixed colors
    val labels = listOf("😀", "😊", "😐", "☹️", "😢") // Fixed labels

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

            // Calculate the position for the label
            val labelAngle = startAngle + angle / 2
            val x = (size.width / 2) + (size.height / 2) * kotlin.math.cos(Math.toRadians(labelAngle.toDouble())).toFloat()
            val y = (size.height / 2) + (size.height / 3) * kotlin.math.sin(Math.toRadians(labelAngle.toDouble())).toFloat()

            // Draw the label
            drawContext.canvas.nativeCanvas.drawText(
                labels[index],
                x,
                y,
                Paint().apply {
                    color = Color.Black.toArgb() // Set text color
                    textSize = 40f // Set text size
                }
            )
            startAngle += angle
        }
    }
}

// Preview for BarChart
@Preview(showBackground = true)
@Composable
fun PreviewVerticalBarChart() {
    Text(
        text = "Bar Chart: Represents the distribution of x mood For each month.",
        style = MaterialTheme.typography.bodyLarge
    )
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalBarChart(moods = listOf(1f, 2f, 3f, 2f, 2f, 1f,3f,1f,1f,2f,3f,1f)) // Sample data for preview
    }
}

// Preview for LineChart
@Preview(showBackground = true)
@Composable
fun PreviewLineChart() {
    LineChart(emotionsData = listOf(1f, 2f, 3f, 2f, 2f, 1f,3f,1f,1f,2f,3f, 1f)) // Sample data for preview
    Text(text = "Line Chart: Shows the distribution for the selected emotion", style = MaterialTheme.typography.bodyLarge)
}

// Preview for PieChart
@Preview(showBackground = true)
@Composable
fun PreviewPieChart() {
    Text(text = "Pie Chart: Displays the Mood in the last seven days.", style = MaterialTheme.typography.bodyLarge)
    PieChart(
        //colors = listOf(PastelRed,PastelGreen, PastelBlue,PastelPurple,PastelOrange), // Use custom colors
        emotionsCount = listOf(1f, 2f, 1f, 1f, 2f),
        modifier = Modifier.size(200.dp), // Ensure the size is set for the preview
        //labels =  listOf("😀", "😊", "😐", "☹️", "😢") //listOf("PastelRed","PastelGreen", "PastelBlue", "PastelPurple", "PastelOrange")
    )
}