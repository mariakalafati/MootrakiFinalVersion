package com.example.mootraki

import android.icu.text.DateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mootraki.data.Entry
import java.util.Locale
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun Calendar(modifier: Modifier = Modifier, viewModel: CalendarEntryViewModel) {
    val today = Calendar.getInstance() // Using Calendar for compatibility
    val currentMonth = remember { mutableStateOf(today) }
    val allEntries by viewModel.allEntries.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background), // Use the theme's background color
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Section (Month Selector)
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth.value.add(Calendar.MONTH, -1) // Move one month back
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onBackground // Use theme's onBackground color
                )
            }

            val monthDisplay = getMonthDisplay(currentMonth.value)

            Text(
                text = monthDisplay,
                color = MaterialTheme.colorScheme.onBackground, // Use theme's onBackground color
                style = TextStyle(fontSize = 24.sp) // Larger text for better readability
            )

            IconButton(onClick = {
                currentMonth.value.add(Calendar.MONTH, 1) // Move one month forward
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.onBackground // Use theme's onBackground color
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Grid
        CalendarGrid(calendar = currentMonth.value, onDateSelected = { selectedDate ->
            viewModel.fetchEntryForDate(selectedDate) // Fetch the entry for the selected date
        })

        Spacer(modifier = Modifier.height(16.dp))

        // Display selected date's entry or a placeholder message
        if (selectedEntry != null) {

            Text(
                text = "${selectedEntry?.date}: ${selectedEntry?.notes}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            )
        } else {
            Text(
                text = "No entry for this day.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Entry list with scrolling and reduced height
        Text("Recent Entries:", style = MaterialTheme.typography.titleMedium)
        EntryList(entries = allEntries)

        Spacer(modifier = Modifier.height(16.dp))

        InsertDummyButton(viewModel)
    }
}

@Composable
fun InsertDummyButton(viewModel: CalendarEntryViewModel) {
    Button(onClick = { viewModel.insertDummyEntries() }) {
        Text(text = "Εισαγωγή Dummy Δεδομένων")
    }
}

@Composable
fun EntryList(entries: List<Entry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // Reduced height to half
            .verticalScroll(rememberScrollState()) // Make scrollable
    ) {
        entries.forEach { entry ->
            Text(
                text = "${entry.date}: ${entry.notes}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun MonthSelector(currentMonth: MutableState<Calendar>) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { currentMonth.value.add(Calendar.MONTH, -1) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
        }

        val monthDisplay = getMonthDisplay(currentMonth.value)
        Text(monthDisplay, style = TextStyle(fontSize = 24.sp))

        IconButton(onClick = { currentMonth.value.add(Calendar.MONTH, 1) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
        }
    }
}

@Composable
fun CalendarGrid(calendar: Calendar, onDateSelected: (String) -> Unit) {
    val firstDayOfMonth = getFirstDayOfMonth(calendar)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Weekday Headers
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Days
        val rows = (daysInMonth + firstDayOfMonth + 6) / 7
        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..6) {
                    val day = row * 7 + col - firstDayOfMonth + 1
                    if (day in 1..daysInMonth) {
                        val date = "${calendar.get(Calendar.YEAR)}-${String.format("%02d", calendar.get(Calendar.MONTH) + 1)}-${String.format("%02d", day)}"
                        CalendarDay(day = day, isToday = isToday(day, calendar)) {
                            onDateSelected(date) // Pass the date string back to the callback
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun CalendarDay(day: Int, isToday: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            )
            .clickable { onClick() }, // Add clickable action
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}

@Composable
fun EntryItem(entry: Entry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
            .padding(8.dp)
    ) {
        Text(text = "Ημερομηνία: ${entry.date}")
        Text(text = "Περιγραφή: ${entry.notes}")
    }
}

// Helper function to get the formatted month display
fun getMonthDisplay(calendar: Calendar): String {
    val dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
    return dateFormat.format(calendar.time)
}

// Helper function to check if a day is today
fun isToday(day: Int, calendar: Calendar): Boolean {
    val today = Calendar.getInstance()
    return today.get(Calendar.DAY_OF_MONTH) == day && today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
}

// Helper function to get the first day of the month
fun getFirstDayOfMonth(calendar: Calendar): Int {
    val firstDayOfMonth = Calendar.getInstance()
    firstDayOfMonth.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
    return firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // Adjust for 0-based index (Sunday = 0)
}