package com.example.mootraki

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mootraki.data.EntriesRepository
import com.example.mootraki.data.Entry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel responsible for handling the submission of entries and managing their UI state.
 * Validates user input and interacts with the EntriesRepository to save data.
 */
class SubmitEntryViewModel(private val entriesRepository: EntriesRepository) : ViewModel() {

    // State to manage the UI for the entry form
    var entryUiState by mutableStateOf(EntryUiState())
        private set

    /**
     * Updates the UI state with new entry details and validates the input.
     * @param entryDetails Updated details of the entry.
     */
    fun updateUiState(entryDetails: EntryDetails) {
        entryUiState = EntryUiState(
            entryDetails = entryDetails,
            isEntryValid = validateInput(entryDetails) // Validate input whenever UI state changes
        )
    }

    /**
     * Submits the current entry details to the database after validation.
     */
    suspend fun submitEntry() {
        if (validateInput()) {
            // Set the date to the current date if not provided
            val updatedDetails = entryUiState.entryDetails.copy(
                date = entryUiState.entryDetails.date.ifBlank {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                }
            )

            // Insert the entry into the repository
            entriesRepository.insertEntry(updatedDetails.toEntry())
        }
    }

    /**
     * Validates the input fields to ensure they meet the required criteria.
     * @param uiState Entry details to validate. Defaults to the current UI state's details.
     * @return True if input is valid; false otherwise.
     */
    private fun validateInput(uiState: EntryDetails = entryUiState.entryDetails): Boolean {
        return with(uiState) {
            mood != -1 && emotions.isNotEmpty() && note.isNotBlank() // Ensure all fields are filled
        }
    }
}

/**
 * Represents the overall UI state for the entry form.
 * @param entryDetails Current details entered by the user.
 * @param isEntryValid Indicates whether the current input is valid for submission.
 */
data class EntryUiState(
    val entryDetails: EntryDetails = EntryDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Represents the details of an entry to be submitted by the user.
 * @param date Date of the entry in "yyyy-MM-dd" format. Defaults to an empty string.
 * @param mood Integer representing the selected mood. Defaults to -1 (not selected).
 * @param emotions Set of selected emotions. Defaults to an empty set.
 * @param note Text note provided by the user. Defaults to an empty string.
 */
data class EntryDetails(
    val date: String = "",
    val mood: Int = -1,
    val emotions: Set<String> = emptySet(),
    val note: String = ""
)

/**
 * Extension function to convert an [EntryDetails] instance into an [Entry] entity for the database.
 * @return An [Entry] object with the corresponding data.
 */
fun EntryDetails.toEntry(): Entry = Entry(
    date = date,
    mood = mood,
    emotions = emotions.joinToString(","), // Convert set of emotions to a comma-separated string
    notes = note
)

