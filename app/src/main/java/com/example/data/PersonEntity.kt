package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val kind: String = "man", // "man", "woman", "store", "company"
    val notes: String = "",
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
