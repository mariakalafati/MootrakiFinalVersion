package com.example.mootraki.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the entries database
 */
@Dao
interface EntryDao {

    // Get all entries ordered by date in descending order
    @Query("SELECT * FROM entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<Entry>>

    // Get a specific entry by date
    @Query("SELECT * FROM entries WHERE date = :date")
    fun getEntryByDate(date: String): Flow<Entry?>

    // Insert a new entry, replacing any existing entry with the same date
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntry(entry: Entry)

    // Update an existing entry
    @Update
    suspend fun updateEntry(entry: Entry)

    // Delete a specific entry
    @Delete
    suspend fun deleteEntry(entry: Entry)
}