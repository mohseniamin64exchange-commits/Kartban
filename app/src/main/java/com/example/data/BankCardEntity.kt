package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bank_cards",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personId"])]
)
data class BankCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val personId: Int,
    val bankName: String,
    val bankType: String, // "mellat", "melli", "saderat", "tejarat", "saman", "pasargad", "sepah", "keshavarzi", "resalat", "shahr", "blubank", "other"
    val cardNumber: String,
    val accountNumber: String = "",
    val iban: String = "",
    val cardKind: String = "customer", // "customer" or "personal"
    val cvv2: String = "", // ONLY allowed for "personal", locked/empty for "customer"
    val expiryDate: String = "", // ONLY allowed for "personal", locked/empty for "customer"
    val cardColorStart: String = "#80142A",
    val cardColorEnd: String = "#FF5B71",
    val useCustomAppearance: Boolean = false,
    val isDefault: Boolean = false,
    val isPinned: Boolean = false,
    val orderIndex: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
