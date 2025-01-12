package com.example.mootraki.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Entry] from a given data source.
 */
interface EntriesRepository {

    /**
     * Retrieve all the entries from the given data source.
     */
    fun getAllEntriesStream(): Flow<List<Entry>>

    /**
     * Retrieve an entry from the given data source that matches with the [date].
     */
    fun getEntryStream(date: String): Flow<Entry?>

    /**
     * Insert an entry into the data source.
     */
    suspend fun insertEntry(entry: Entry)

    /**
     * Delete an entry from the data source.
     */
    suspend fun deleteEntry(entry: Entry)

    /**
     * Update an entry in the data source.
     */
    suspend fun updateEntry(entry: Entry)

}