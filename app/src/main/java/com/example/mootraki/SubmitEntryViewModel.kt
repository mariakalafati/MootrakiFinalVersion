package com.example.mootraki

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mootraki.data.Entry
import com.example.mootraki.data.EntriesRepository

/**
 * ViewModel to validate and insert entries in the Room database.
 */
class SubmitEntryViewModel(private val entriesRepository: EntriesRepository) : ViewModel() {

    /**
     * Holds current entry UI state.
     */
    var entryUiState by mutableStateOf(EntryUiState())
        private set

    /**
     * Updates the [entryUiState] with the provided entry details. Triggers validation.
     */
    fun updateUiState(entryDetails: EntryDetails) {
        entryUiState =
            EntryUiState(entryDetails = entryDetails, isEntryValid = validateInput(entryDetails))
    }

    /**
     * Submits an [Entry] to the Room database.
     */
    suspend fun submitEntry() {
        if (validateInput()) {
            entriesRepository.insertEntry(entryUiState.entryDetails.toEntry())
        }
    }

    /**
     * Validates the input from the UI.
     */
    private fun validateInput(uiState: EntryDetails = entryUiState.entryDetails): Boolean {
        return with(uiState) {
            mood != -1 && emotions.isNotEmpty() && note.isNotBlank()
        }
    }
}

/**
 * Represents UI State for an Entry.
 */
data class EntryUiState(
    val entryDetails: EntryDetails = EntryDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Holds details about the entry to be submitted.
 */
data class EntryDetails(
    val date: String = "",
    val mood: Int = -1,
    val emotions: Set<String> = emptySet(),
    val note: String = ""
)

/**
 * Extension function to convert [EntryDetails] to [Entry].
 */
fun EntryDetails.toEntry(): Entry = Entry(
    date = date,
    mood = mood,
    emotions = emotions.joinToString(","),
    notes = note
)

