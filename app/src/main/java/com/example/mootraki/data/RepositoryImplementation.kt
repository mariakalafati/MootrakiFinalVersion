package com.example.mootraki.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository implementation for offline data storage using Room database.
 */
class RepositoryImplementation(private val entryDao: EntryDao) : EntriesRepository {

    override fun getAllEntriesStream(): Flow<List<Entry>> = entryDao.getAllEntries()

    override fun getEntryStream(date: String): Flow<Entry?> = entryDao.getEntryByDate(date)

    override suspend fun insertEntry(entry: Entry) = entryDao.insertEntry(entry)

    override suspend fun deleteEntry(entry: Entry) = entryDao.deleteEntry(entry)

    override suspend fun updateEntry(entry: Entry) = entryDao.updateEntry(entry)

}