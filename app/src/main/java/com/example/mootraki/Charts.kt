package com.example.mootraki

// Charts.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun Charts(viewModel: ChartViewModel) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // Make the column scrollable
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Enable scrolling
        ) {
            // Get the selected mood and emotion from dropdowns
            val selectedMood = dropDownMoods() // Assuming this returns an Int
            val selectedEmotion = dropDownEmotions() // Assuming this returns a String

            // Collect the entries from the ViewModel
            val entries = viewModel.allEntries.collectAsState(initial = emptyList()).value

            // Perform counting functions
            val moodCounts = remember { viewModel.countMoodsForLastWeek() }
            val selectedMoodCounts = remember { viewModel.countMoodForEachMonth(mood = selectedMood) }
            val selectedEmotionsCounts = remember { viewModel.countEmotionForEachMonth(emotion = selectedEmotion) }

            // Prepare data for the charts
            val (moodCountEntries, selectedMoodCountEntries, selectedEmotionsCountEntries) = MakeFloats(
                viewModel.countMoodsForLastWeek(),
                viewModel.countMoodForEachMonth(mood = selectedMood),
                viewModel.countEmotionForEachMonth(emotion = selectedEmotion)
            )

            // Pie Chart
            Text(
                text = "Your Mood in the last seven days (PieChart)",
                style = MaterialTheme.typography.bodyLarge
            )
            PieChart(
                emotionsCount = moodCountEntries,
                modifier = Modifier.size(200.dp)
            )

            // Line Chart
            Text(
                text = "Review your year 2024: Line Chart: Shows the distribution of emotion: $selectedEmotion",
                style = MaterialTheme.typography.bodyLarge
            )
            LineChart(
                emotionsData = selectedEmotionsCountEntries
            )

            // Bar Chart
            Text(
                text = "Review your Year 2024: Bar Chart: Represents the distribution of $selectedMood mood for each month.",
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalBarChart(
                moods = selectedMoodCountEntries
            )
        }
    }
}// ειναι πολύ πιθανό να λέιπουν ή να είναι παραπάνω {}
//
//    // Display the results
//    Text("Mood Counts for Last Week: $moodCounts")
//    Text("Selected Mood Counts for Each Month: $selectedMoodCounts")
//    Text("Selected Emotions Counts for Each Month: $selectedEmotionsCounts")
//}

@Composable
private fun MakeFloats(
    moodCounts: Map<Int, Int>,
    selectedMoodCounts: Map<Int, Int>,
    selectedEmotionsCounts: Map<Int, Int>
) : Triple<List<Float>, List<Float>, List<Float>> {
    // Convert mood counts to List<Float>
    val moodCountEntries = moodCounts.values.map { it.toFloat() }

    // Convert selected mood counts to List<Float>
    val selectedMoodCountEntries = selectedMoodCounts.values.map { it.toFloat() }

    // Convert selected emotions counts to List<Float>
    val selectedEmotionsCountEntries = selectedEmotionsCounts.values.map { it.toFloat() }

    // Return the three lists as a Triple
    return Triple(moodCountEntries, selectedMoodCountEntries, selectedEmotionsCountEntries)

//        // Now calls Charts with the prepared data
//        PieChart(emotionsCount = moodCountEntries) // For mood counts
//        HorizontalBarChart(selectedMoodCountEntries) // For selected mood counts
//        LineChart(selectedEmotionsCountEntries) // For selected emotions counts
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable //DropDownMenu
fun dropDownEmotions() : String {
    val dropDownOpen = remember { mutableStateOf(false) }
    val selectedEmotion = remember { mutableStateOf("Select an Emotion") }
    val emotions = listOf("Excited", "Relaxed", "Happy", "Tired", "Annoyed", "Stressed", "Bored", "Hopeful")

    //Column(modifier = Modifier.fillMaxSize())
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = selectedEmotion.value,
            modifier = Modifier
                .clickable { dropDownOpen.value = true }
                .padding(16.dp)
        )
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

    // Display selected option
    //Text(text = "Selected Option: ${selectedEmotion.value}", modifier = Modifier.padding(16.dp))
    return selectedEmotion.value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable //DropDownMenu
fun dropDownMoods() : Int {
    val dropDownOpen = remember { mutableStateOf(false) }
    val selectedMood = remember { mutableStateOf("Select a Mood") }
    val moods = listOf("😀", "😊", "😐", "☹️", "😢")

    //Column(modifier = Modifier.fillMaxSize())
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = selectedMood.value,
            modifier = Modifier
                .clickable { dropDownOpen.value = true }
                .padding(16.dp)
        )
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

    // Display selected option
    var moodvalue : Int = -1
    //Text(text = "Selected Option: ${selectedMood.value}", modifier = Modifier.padding(16.dp))
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