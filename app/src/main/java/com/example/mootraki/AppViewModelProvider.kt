package com.example.mootraki

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory


/**
 * Provides Factory to create instances of ViewModels for the Mootraki app.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for SubmitEntryViewModel
        initializer {
            val application = mootrakiApplication()
            val repository = application.container.entriesRepository
            SubmitEntryViewModel(repository)
        }
        // Initializer for CalendarEntryViewModel
        initializer {
            val application = mootrakiApplication()
            val repository = application.container.entriesRepository
            CalendarEntryViewModel(repository)
        }
        // Initializer for ChartViewModel
        initializer {
            val application = mootrakiApplication()
            val repository = application.container.entriesRepository
            ChartViewModel(repository)
        }
    }
}

/**
 * Extension function to query for [Application] object and return an instance of
 * [MootrakiApplication].
 */
fun CreationExtras.mootrakiApplication(): MootrakiApplication {
    return (this[AndroidViewModelFactory.APPLICATION_KEY] as MootrakiApplication)
}