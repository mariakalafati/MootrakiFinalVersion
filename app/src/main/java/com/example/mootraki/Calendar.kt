package com.example.mootraki

import android.icu.text.DateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mootraki.data.Entry
import java.util.Locale

/**
 * Main composable function for displaying a calendar with entries.
 * @param modifier Modifier for styling.
 * @param viewModel ViewModel instance to manage calendar data.
 */
@Composable
fun Calendar(modifier: Modifier = Modifier, viewModel: CalendarEntryViewModel) {
    val today = Calendar.getInstance()
    val currentMonth = remember { mutableStateOf(today) }
    val allEntries by viewModel.allEntries.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    val selectedDate = remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display current month and year
        Text(
            text = getMonthYearDisplay(currentMonth.value),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar grid for selecting dates
        CalendarGrid(
            calendar = currentMonth.value,
            selectedDate = selectedDate.value,
            onDateSelected = { date ->
                selectedDate.value = date
                viewModel.fetchEntryForDate(date)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display the selected entry or a placeholder if none
        if (selectedEntry != null) {
            EntryItem(entry = selectedEntry!!)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text(
                    text = "No entry for this day.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section for recent entries
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    "Recent Entries",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                EntryList(entries = allEntries)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to insert dummy data for demonstration purposes
        InsertDummyButton(viewModel)
    }
}

/**
 * Composable for displaying the calendar grid.
 * @param calendar The calendar instance representing the current month.
 * @param selectedDate Currently selected date.
 * @param onDateSelected Callback for date selection.
 */
@Composable
fun CalendarGrid(calendar: Calendar, selectedDate: String?, onDateSelected: (String) -> Unit) {
    val firstDayOfMonth = getFirstDayOfMonth(calendar)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display week day headers
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

        // Generate rows for days of the month
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
                        CalendarDay(
                            day = day,
                            isToday = isToday(day, calendar),
                            isSelected = selectedDate == date,
                            onClick = { onDateSelected(date) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
    }
}

/**
 * Composable for individual day in the calendar grid.
 * @param day Day of the month.
 * @param isToday Indicates if the day is the current day.
 * @param isSelected Indicates if the day is selected.
 * @param onClick Callback for click action.
 */
@Composable
fun CalendarDay(day: Int, isToday: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            )
            .border(
                width = if (isToday) 3.dp else 0.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}

/**
 * Composable for displaying a list of entries.
 * @param entries List of entries to display.
 */
@Composable
fun EntryList(entries: List<Entry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .verticalScroll(rememberScrollState())
    ) {
        entries.forEach { entry ->
            EntryItem(entry = entry)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Composable for displaying a single entry.
 * @param entry Entry data to display.
 */
@Composable
fun EntryItem(entry: Entry) {
    val dateFormatted = formatDate(entry.date)
    val moodEmoji = getMoodEmoji(entry.mood)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateFormatted,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = moodEmoji,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = entry.notes,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(2f)
        )
    }
}

/**
 * Button to insert dummy data for testing purposes.
 * @param viewModel ViewModel to handle dummy data insertion.
 */
@Composable
fun InsertDummyButton(viewModel: CalendarEntryViewModel) {
    Button(
        onClick = { viewModel.insertDummyEntries() },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = "Demo: Insert Dummy Data",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    }
}

/**
 * Helper function to get an emoji for a given mood.
 * @param mood Mood index.
 * @return Corresponding emoji.
 */
fun getMoodEmoji(mood: Int): String {
    val moodEmojis = listOf("😀", "😊", "😐", "☹️", "😢")
    return if (mood in moodEmojis.indices) moodEmojis[mood] else "😐" // Default to neutral if out of range
}

/**
 * Helper function to get the formatted month-year display.
 * @param calendar Calendar instance.
 * @return Formatted month and year string.
 */
fun getMonthYearDisplay(calendar: Calendar): String {
    val dateFormat = DateFormat.getPatternInstance("MMMM yyyy", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

/**
 * Helper function to check if a day is today.
 * @param day Day of the month.
 * @param calendar Calendar instance.
 * @return True if the day is today, otherwise false.
 */
fun isToday(day: Int, calendar: Calendar): Boolean {
    val today = Calendar.getInstance()
    return today.get(Calendar.DAY_OF_MONTH) == day && today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
}

/**
 * Helper function to get the first day of the month.
 * @param calendar Calendar instance.
 * @return Index of the first day of the month (0-based).
 */
fun getFirstDayOfMonth(calendar: Calendar): Int {
    val firstDayOfMonth = Calendar.getInstance()
    firstDayOfMonth.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
    return firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // Adjust for 0-based index (Sunday = 0)
}

/**
 * Helper function to format a date string.
 * @param date Input date string in "yyyy-MM-dd" format.
 * @return Formatted date string in "dd MMMM yyyy" format, or original string if parsing fails.
 */
fun formatDate(date: String): String {
    val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = java.text.SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return try {
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: "")
    } catch (e: Exception) {
        date // Return the original date if parsing fails
    }
}