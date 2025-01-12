package com.example.mootraki.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object for managing entries.
 */
@Database(entities = [Entry::class], version = 1, exportSchema = false)
abstract class EntriesDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao

    companion object {
        @Volatile
        private var Instance: EntriesDatabase? = null

        fun getDatabase(context: Context): EntriesDatabase {
            // Return the current instance if it's not null, otherwise create a new one.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    EntriesDatabase::class.java,
                    "entries_database"
                )
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}