package com.example.mootraki

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Provides a Factory to create instances of ViewModels for the Mootraki app.
 *
 * This object defines initializers for each ViewModel used in the application,
 * ensuring they are properly instantiated with the required dependencies.
 */
object AppViewModelProvider {
    /**
     * Factory instance for creating ViewModels with appropriate initializers.
     */
    val Factory = viewModelFactory {
        // Initializer for SubmitEntryViewModel
        initializer {
            val application = mootrakiApplication() // Get the application instance
            val repository = application.container.entriesRepository // Obtain repository
            SubmitEntryViewModel(repository) // Create ViewModel instance
        }

        // Initializer for CalendarEntryViewModel
        initializer {
            val application = mootrakiApplication() // Get the application instance
            val repository = application.container.entriesRepository // Obtain repository
            CalendarEntryViewModel(repository) // Create ViewModel instance
        }

        // Initializer for ChartViewModel
        initializer {
            val application = mootrakiApplication() // Get the application instance
            val repository = application.container.entriesRepository // Obtain repository
            ChartViewModel(repository) // Create ViewModel instance
        }
    }
}

/**
 * Extension function to retrieve the [Application] object and cast it to
 * [MootrakiApplication]. This is used to access the application's dependency container.
 *
 * @return An instance of [MootrakiApplication].
 */
fun CreationExtras.mootrakiApplication(): MootrakiApplication {
    return (this[AndroidViewModelFactory.APPLICATION_KEY] as MootrakiApplication)
}