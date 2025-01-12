package com.example.mootraki.data


import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val entriesRepository: EntriesRepository
}

/**
 * [AppContainer] implementation that provides an instance of [RepositoryImplementation].
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [EntriesRepository].
     */
    override val entriesRepository: EntriesRepository by lazy {
        RepositoryImplementation(EntriesDatabase.getDatabase(context).entryDao())
    }
}