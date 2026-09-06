package com.example.data

import kotlinx.coroutines.flow.Flow

class KartYarRepository(private val dao: KartYarDao) {
    val allPersonsWithCards: Flow<List<PersonWithCards>> = dao.getAllPersonsWithCards()
    val totalPersonCount: Flow<Int> = dao.getPersonCount()
    val totalCardCount: Flow<Int> = dao.getCardCount()

    fun getPersonWithCardsById(personId: Int): Flow<PersonWithCards?> = dao.getPersonWithCardsById(personId)

    fun getCardsForPerson(personId: Int): Flow<List<BankCardEntity>> = dao.getCardsForPerson(personId)

    suspend fun insertPerson(person: PersonEntity): Long = dao.insertPerson(person)

    suspend fun insertCard(card: BankCardEntity): Long = dao.insertCard(card)

    suspend fun updatePerson(person: PersonEntity) = dao.updatePerson(person)

    suspend fun updateCard(card: BankCardEntity) = dao.updateCard(card)

    suspend fun deletePerson(person: PersonEntity) = dao.deletePerson(person)

    suspend fun deleteCard(card: BankCardEntity) = dao.deleteCard(card)
}
