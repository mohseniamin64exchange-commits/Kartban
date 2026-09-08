package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KartYarDao {
    @Transaction
    @Query("SELECT * FROM persons ORDER BY isPinned DESC, createdAt DESC")
    fun getAllPersonsWithCards(): Flow<List<PersonWithCards>>

    @Transaction
    @Query("SELECT * FROM persons WHERE id = :personId")
    fun getPersonWithCardsById(personId: Int): Flow<PersonWithCards?>

    @Query("SELECT * FROM bank_cards WHERE personId = :personId ORDER BY isDefault DESC, isPinned DESC, orderIndex ASC, createdAt DESC")
    fun getCardsForPerson(personId: Int): Flow<List<BankCardEntity>>

    @Query("SELECT * FROM bank_cards WHERE id = :cardId LIMIT 1")
    suspend fun getCardById(cardId: Int): BankCardEntity?

    @Query("SELECT * FROM persons WHERE id = :personId LIMIT 1")
    suspend fun getPersonById(personId: Int): PersonEntity?

    @Query("SELECT * FROM bank_cards WHERE cardNumber = :normalizedCardNumber LIMIT 1")
    suspend fun findCardByNumber(normalizedCardNumber: String): BankCardEntity?

    @Query("UPDATE bank_cards SET isDefault = 0 WHERE personId = :personId")
    suspend fun clearDefaultCardsForPerson(personId: Int)

    @Query("UPDATE bank_cards SET isDefault = 1 WHERE id = :cardId")
    suspend fun setCardAsDefault(cardId: Int)

    @Transaction
    suspend fun setDefaultCard(personId: Int, cardId: Int) {
        clearDefaultCardsForPerson(personId)
        setCardAsDefault(cardId)
    }

    @Query("UPDATE persons SET isPinned = :isPinned WHERE id = :personId")
    suspend fun setPersonPinned(personId: Int, isPinned: Boolean)

    @Query("UPDATE bank_cards SET isPinned = :isPinned WHERE id = :cardId")
    suspend fun setCardPinned(cardId: Int, isPinned: Boolean)

    @Query("UPDATE bank_cards SET orderIndex = :orderIndex WHERE id = :cardId")
    suspend fun updateCardOrderIndex(cardId: Int, orderIndex: Int)

    @Transaction
    suspend fun swapCardOrders(card1Id: Int, order1: Int, card2Id: Int, order2: Int) {
        updateCardOrderIndex(card1Id, order2)
        updateCardOrderIndex(card2Id, order1)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: BankCardEntity): Long

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Update
    suspend fun updateCard(card: BankCardEntity)

    @Delete
    suspend fun deletePerson(person: PersonEntity)

    @Delete
    suspend fun deleteCard(card: BankCardEntity)

    @Query("SELECT COUNT(*) FROM persons")
    fun getPersonCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM bank_cards")
    fun getCardCount(): Flow<Int>

    @Query("SELECT * FROM persons")
    suspend fun getAllPersonsList(): List<PersonEntity>

    @Query("SELECT * FROM bank_cards")
    suspend fun getAllCardsList(): List<BankCardEntity>

    @Query("SELECT * FROM persons WHERE name = :name LIMIT 1")
    suspend fun findPersonByName(name: String): PersonEntity?

    @Query("DELETE FROM bank_cards WHERE id IN (:cardIds)")
    suspend fun deleteCardsByIds(cardIds: List<Int>)

    @Query("DELETE FROM persons WHERE id IN (:personIds)")
    suspend fun deletePersonsByIds(personIds: List<Int>)
}
