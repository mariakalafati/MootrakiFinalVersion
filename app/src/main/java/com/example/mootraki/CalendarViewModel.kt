package com.example.mootraki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mootraki.data.EntriesRepository
import com.example.mootraki.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing calendar entries. It interacts with the [EntriesRepository]
 * to fetch and manipulate entry data and exposes state flows for UI updates.
 */
class CalendarEntryViewModel(private val entriesRepository: EntriesRepository) : ViewModel() {

    // StateFlow holding all entries
    private val _allEntries = MutableStateFlow<List<Entry>>(emptyList())
    val allEntries: StateFlow<List<Entry>> get() = _allEntries.asStateFlow()

    // StateFlow holding the currently selected entry
    private val _selectedEntry = MutableStateFlow<Entry?>(null)
    val selectedEntry: StateFlow<Entry?> get() = _selectedEntry.asStateFlow()

    init {
        fetchAllEntries()
    }

    /**
     * Fetches all entries from the repository and updates [_allEntries].
     */
    private fun fetchAllEntries() {
        viewModelScope.launch {
            entriesRepository.getAllEntriesStream().collect { entries ->
                _allEntries.value = entries
            }
        }
    }

    /**
     * Fetches an entry for a specific date from the repository and updates [_selectedEntry].
     *
     * @param date The date for which to fetch the entry.
     */
    fun fetchEntryForDate(date: String) {
        viewModelScope.launch {
            entriesRepository.getEntryStream(date).collect { entry ->
                _selectedEntry.value = entry
            }
        }
    }

    // Predefined dummy entries for demonstration or testing purposes
    private val dummyEntries = listOf(
        Entry(date = "2024-01-07", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-02-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-02-16", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-03-05", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-03-18", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-03-21", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-04-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-04-14", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-05-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-05-25", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-06-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-07-05", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-07-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-07-14", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-08-28", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-09-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-10-12", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-10-25", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-11-06", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-11-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-11-11", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2024-12-09", emotions = "Stressed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2025-01-09", emotions = "Happy", mood = 0, notes = "Sample Entry"),
        Entry(date = "2025-01-10", emotions = "Happy", mood = 0, notes = "Sample Entry"),
        Entry(date = "2025-01-11", emotions = "Happy", mood = 4, notes = "Sample Entry"),
        Entry(date = "2025-01-12", emotions = "Tired", mood = 1, notes = "Sample Entry"),
        Entry(date = "2025-01-13", emotions = "Relaxed", mood = 2, notes = "Sample Entry"),
        Entry(date = "2025-01-14", emotions = "Excited", mood = 3, notes = "Sample Entry"),
        Entry(date = "2025-01-15", emotions = "Hopeful", mood = 4, notes = "Sample Entry")
    )

    /**
     * Inserts predefined dummy entries into the repository.
     */
    fun insertDummyEntries() {
        viewModelScope.launch {
            dummyEntries.forEach { entry ->
                entriesRepository.insertEntry(entry)
            }
        }
    }
}