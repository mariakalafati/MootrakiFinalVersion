package com.example.mootraki

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color

@Composable
fun Charts(viewModel: ChartViewModel) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Your Charts",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
            )
            val selectedMood = dropDownMoods()
            val selectedEmotion = dropDownEmotions()

            val (moodCountEntries, selectedMoodCountEntries, selectedEmotionsCountEntries) = MakeFloats(
                viewModel.countMoodsForLastWeek(),
                viewModel.countMoodForEachMonth(mood = selectedMood),
                viewModel.countEmotionForEachMonth(emotion = selectedEmotion)
            )

            // Pie Chart
            Text(
                text = "Your Mood in the last seven days (PieChart)",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp),
            )
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                PieChart(
                    emotionsCount = moodCountEntries,
                    modifier = Modifier.size(300.dp).padding(20.dp).align(Alignment.CenterHorizontally)
                )
            }

            // Line Chart
            Text(
                text = "Review your year 2024: Line Chart: Shows the distribution of emotion: $selectedEmotion",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LineChart(
                    emotionsData = selectedEmotionsCountEntries
                )
            }

            // Bar Chart
            Text(
                text = "Review your Year 2024: Bar Chart: Represents the distribution of your selected mood for each month.",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)

            ) {
                HorizontalBarChart(
                    moods = selectedMoodCountEntries
                )
            }
        }
    }
}

@Composable
private fun MakeFloats(
    moodCounts: Map<Int, Int>,
    selectedMoodCounts: Map<Int, Int>,
    selectedEmotionsCounts: Map<Int, Int>
) : Triple<List<Float>, List<Float>, List<Float>> {

    val moodCountEntries = moodCounts.values.map { it.toFloat() }
    val selectedMoodCountEntries = selectedMoodCounts.values.map { it.toFloat() }
    val selectedEmotionsCountEntries = selectedEmotionsCounts.values.map { it.toFloat() }

    return Triple(moodCountEntries, selectedMoodCountEntries, selectedEmotionsCountEntries)
}

@Composable
fun dropDownEmotions() : String {
    val dropDownOpen = remember { mutableStateOf(false) }
    val selectedEmotion = remember { mutableStateOf("Select an Emotion") }
    val emotions = listOf("Excited", "Relaxed", "Happy", "Tired", "Annoyed", "Stressed", "Bored", "Hopeful")

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier
            .clickable { dropDownOpen.value = true }
            .padding(16.dp)
        ) {
            Text(text = selectedEmotion.value)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Click to select",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        DropdownMenu(
            expanded = dropDownOpen.value,
            onDismissRequest = { dropDownOpen.value = false }
        ) {
            emotions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedEmotion.value = item
                        dropDownOpen.value = false
                    }
                )

            }
        }
    }
    return selectedEmotion.value
}

@Composable
fun dropDownMoods() : Int {
    val dropDownOpen = remember { mutableStateOf(false) }
    val selectedMood = remember { mutableStateOf("Select a Mood") }
    val moods = listOf("😀", "😊", "😐", "☹️", "😢")

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier
            .clickable { dropDownOpen.value = true }
            .padding(16.dp)
        ) {
            Text(text = selectedMood.value)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Click to select",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        DropdownMenu(
            expanded = dropDownOpen.value,
            onDismissRequest = { dropDownOpen.value = false }
        ) {
            moods.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedMood.value = item
                        dropDownOpen.value = false
                    }
                )
            }
        }
    }

    var moodvalue : Int = -1
    if (selectedMood.value == "😀") {
        moodvalue = 0
    } else if (selectedMood.value == "\uD83D\uDE0A") {
        moodvalue = 1
    } else if (selectedMood.value == "\uD83D\uDE10") {
        moodvalue = 2
    } else if (selectedMood.value == "☹\uFE0F") {
        moodvalue = 3
    } else if (selectedMood.value == "\uD83D\uDE22") {
        moodvalue = 4
    }
    return moodvalue
}
