package com.example.mootraki.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single entry in the database.
 */
@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey
    val date: String, // Primary key in "yyyy-MM-dd" format
    val mood: Int, // Index for mood (e.g., 0 = 😀, 1 = 😊, etc.)
    val emotions: String, // Comma-separated list of emotions (e.g., "Happy,Relaxed")
    val notes: String // User's note for the day
)
