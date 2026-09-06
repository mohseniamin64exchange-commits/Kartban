package com.example.data

import androidx.room.Embedded
import androidx.room.Relation

data class PersonWithCards(
    @Embedded val person: PersonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "personId"
    )
    val cards: List<BankCardEntity>
)
