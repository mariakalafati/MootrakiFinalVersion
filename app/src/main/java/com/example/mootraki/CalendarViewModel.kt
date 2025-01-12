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
        Entry(date = "2025-01-01", emotions = "Happy", mood = 0, notes = "Πρώτη καταχώρηση"),
        Entry(date = "2025-01-02", emotions = "Sad, Tired", mood = 1, notes = "Δεύτερη καταχώρηση"),
        Entry(date = "2025-01-03", emotions = "Relaxed", mood = 2, notes = "Τρίτη καταχώρηση"),
        Entry(date = "2025-01-04", emotions = "Excited", mood = 3, notes = "Τέταρτη καταχώρηση"),
        Entry(date = "2025-01-05", emotions = "Calm, Grateful", mood = 4, notes = "Πέμπτη καταχώρηση")
    )
    fun insertDummyEntries() {
        viewModelScope.launch {
            dummyEntries.forEach { entry ->
                entriesRepository.insertEntry(entry)
            }
        }
    }
}