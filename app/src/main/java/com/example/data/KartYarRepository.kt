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

    suspend fun findCardByNumber(normalizedCardNumber: String): BankCardEntity? =
        dao.findCardByNumber(normalizedCardNumber)

    suspend fun isCardNumberDuplicate(cardNumber: String, excludeCardId: Int? = null): Boolean {
        val normalized = IranianBankHelper.normalizeCardNumber(cardNumber)
        if (normalized.isEmpty()) return false
        val existing = dao.findCardByNumber(normalized)
        return existing != null && (excludeCardId == null || existing.id != excludeCardId)
    }

    suspend fun setDefaultCard(personId: Int, cardId: Int) = dao.setDefaultCard(personId, cardId)

    suspend fun clearDefaultCardsForPerson(personId: Int) = dao.clearDefaultCardsForPerson(personId)

    suspend fun setPersonPinned(personId: Int, isPinned: Boolean) = dao.setPersonPinned(personId, isPinned)

    suspend fun setCardPinned(cardId: Int, isPinned: Boolean) = dao.setCardPinned(cardId, isPinned)

    suspend fun updateCardOrderIndex(cardId: Int, orderIndex: Int) = dao.updateCardOrderIndex(cardId, orderIndex)

    suspend fun swapCardOrders(card1Id: Int, order1: Int, card2Id: Int, order2: Int) =
        dao.swapCardOrders(card1Id, order1, card2Id, order2)

    suspend fun getCardById(cardId: Int): BankCardEntity? = dao.getCardById(cardId)

    suspend fun getPersonById(personId: Int): PersonEntity? = dao.getPersonById(personId)
}
