package com.example.mootraki

import android.icu.text.DateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun Calendar(modifier: Modifier = Modifier, viewModel: CalendarEntryViewModel) {
    val today = Calendar.getInstance() // Using Calendar for compatibility
    val currentMonth = remember { mutableStateOf(today) }
    val allEntries by viewModel.allEntries.collectAsState()

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
        CalendarGrid(currentMonth.value)

        Spacer(modifier = Modifier.height(16.dp))

        InsertDummyButton(viewModel)

        // Λίστα καταχωρήσεων
        Text("Καταχωρήσεις:", style = MaterialTheme.typography.titleMedium)
        EntryList(entries = allEntries)
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
    Column(modifier = Modifier.fillMaxWidth()) {
        entries.forEach { entry ->
            Text(
                text = "${entry.date}: ${entry.notes}",
                style = MaterialTheme.typography.bodyLarge, // Αυξημένο μέγεθος γραμματοσειράς
                modifier = Modifier
                    .padding(vertical = 8.dp) // Αυξημένο padding για καλύτερη ευθυγράμμιση
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium) // Προσθήκη φόντου για πλαίσιο
                    .padding(16.dp) // Εσωτερικό padding του πλαισίου
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
fun CalendarGrid(calendar: Calendar) {
    // Get the first day of the month and the total number of days in the month
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
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), // Use theme's onBackground with reduced opacity
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
                        val isToday = isToday(day, calendar)
                        CalendarDay(day = day, isToday = isToday)
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun CalendarDay(day: Int, isToday: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp) // Μεγαλύτερο μέγεθος για τα πλαίσια των ημερών
            .background(
                if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, // Χρώμα για σήμερα
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp // Μεγαλύτερη γραμματοσειρά για καλύτερη ορατότητα
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