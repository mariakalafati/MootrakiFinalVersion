package com.example.mootraki

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mootraki.data.Entry
import com.example.mootraki.data.EntriesRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel to validate and insert entries in the Room database.
 */
class SubmitEntryViewModel(private val entriesRepository: EntriesRepository) : ViewModel() {

    var entryUiState by mutableStateOf(EntryUiState())
        private set

    fun updateUiState(entryDetails: EntryDetails) {
        entryUiState =
            EntryUiState(entryDetails = entryDetails, isEntryValid = validateInput(entryDetails))
    }

    suspend fun submitEntry() {
        if (validateInput()) {
            // Ensure the date is set correctly
            val updatedDetails = entryUiState.entryDetails.copy(
                date = if (entryUiState.entryDetails.date.isBlank()) {
                    // Use the current date if none is set
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                } else {
                    entryUiState.entryDetails.date
                }
            )

            entriesRepository.insertEntry(updatedDetails.toEntry())
        }
    }

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

