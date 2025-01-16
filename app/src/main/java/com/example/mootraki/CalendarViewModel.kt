package com.example.mootraki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mootraki.data.EntriesRepository
import com.example.mootraki.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarEntryViewModel(private val entriesRepository: EntriesRepository) : ViewModel() {

    private val _allEntries = MutableStateFlow<List<Entry>>(emptyList())
    val allEntries: StateFlow<List<Entry>> get() = _allEntries.asStateFlow()

    private val _selectedEntry = MutableStateFlow<Entry?>(null)
    val selectedEntry: StateFlow<Entry?> get() = _selectedEntry.asStateFlow()

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

    fun fetchEntryForDate(date: String) {
        viewModelScope.launch {
            entriesRepository.getEntryStream(date).collect { entry ->
                _selectedEntry.value = entry
            }
        }
    }

    val dummyEntries = listOf(
        // Παλαιές καταχωρήσεις
        Entry(date = "2025-01-01", emotions = "Happy", mood = 0, notes = "Πρώτη καταχώρηση"),
        Entry(date = "2025-01-02", emotions = "Sad, Tired", mood = 1, notes = "Δεύτερη καταχώρηση"),
        Entry(date = "2025-01-03", emotions = "Relaxed", mood = 2, notes = "Τρίτη καταχώρηση"),
        Entry(date = "2025-01-04", emotions = "Excited", mood = 3, notes = "Τέταρτη καταχώρηση"),
        Entry(date = "2025-01-05", emotions = "Calm, Grateful", mood = 4, notes = "Πέμπτη καταχώρηση"),

        // Νέες καταχωρήσεις
        Entry(date = "2024-01-07", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-02-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-02-16", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-03-05", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-03-18", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-03-21", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-04-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-04-14", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-05-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-05-25", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-06-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-07-05", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-07-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-07-14", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-08-28", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-09-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-10-12", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-10-25", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-11-06", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-11-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-11-11", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2024-12-09", emotions = "Stressed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2025-01-09", emotions = "Happy", mood = 0, notes = "Kαταχώρηση"),
        Entry(date = "2025-01-10", emotions = "Happy", mood = 0, notes = "Kαταχώρηση"),
        Entry(date = "2025-01-11", emotions = "Happy", mood = 4, notes = "Kαταχώρηση"),
        Entry(date = "2025-01-12", emotions = "Tired", mood = 1, notes = "Kαταχώρηση"),
        Entry(date = "2025-01-13", emotions = "Relaxed", mood = 2, notes = "Kαταχώρηση"),
        Entry(date = "2025-01-14", emotions = "Excited", mood = 3, notes = "Kαταχώρηση"),
        Entry(date = "2025-01-15", emotions = "Hopeful", mood = 4, notes = "Kαταχώρηση")
    )

    fun insertDummyEntries() {
        viewModelScope.launch {
            dummyEntries.forEach { entry ->
                entriesRepository.insertEntry(entry)
            }
        }
    }
}