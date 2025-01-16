package com.example.mootraki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mootraki.data.EntriesRepository
import com.example.mootraki.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChartViewModel(private val entriesRepository: EntriesRepository) : ViewModel() {
    private val _allEntries = MutableStateFlow<List<Entry>>(emptyList())
    val allEntries: StateFlow<List<Entry>> get() = _allEntries.asStateFlow()

    init {
        fetchAllEntries()
    }

    fun fetchAllEntries() {
        viewModelScope.launch {
            entriesRepository.getAllEntriesStream().collect { entries ->
                _allEntries.value = entries
            }
        }
    }

    // Count moods for days 10-16 of January 2025 in order to have data when showing example
    fun countMoodsForLastWeek(): Map<Int, Int> {
        val entries = _allEntries.value
        val moodCount = mutableMapOf<Int, Int>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        // Define the date range
        val startDate = dateFormat.parse("2025-01-10")!!
        val endDate = dateFormat.parse("2025-01-16")!!

        entries.forEach { entry ->
            try {
                // Parse the date from the entry
                val date = dateFormat.parse(entry.date)!!

                // Check if the date is within the specified range
                if (date in startDate..endDate) {
                    // Count the mood
                    moodCount[entry.mood] = moodCount.getOrDefault(entry.mood, 0) + 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return moodCount
    }

    // Count occurrences of a specified mood for each month in 2024
    fun countMoodForEachMonth(mood: Int): Map<Int, Int> {
        val entries = _allEntries.value
        val moodCount = mutableMapOf<Int, Int>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()

        entries.forEach { entry ->
            try {
                val date = dateFormat.parse(entry.date)!!
                calendar.time = date

                if (calendar.get(Calendar.YEAR) == 2024 && entry.mood == mood) {
                    val month = calendar.get(Calendar.MONTH) + 1
                    moodCount[month] = moodCount.getOrDefault(month, 0) + 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return moodCount
    }

    // Count occurrences of a specified emotion for each month in 2024
    fun countEmotionForEachMonth(emotion: String): Map<Int, Int> {
        val entries = _allEntries.value
        val emotionCount = mutableMapOf<Int, Int>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()

        entries.forEach { entry ->
            try {
                val date = dateFormat.parse(entry.date)!!
                calendar.time = date

                if (calendar.get(Calendar.YEAR) == 2024) {
                    if (entry.emotions.split(",").map { it.trim() }.contains(emotion)) {
                        val month = calendar.get(Calendar.MONTH) + 1
                        emotionCount[month] = emotionCount.getOrDefault(month, 0) + 1
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return emotionCount
    }
}