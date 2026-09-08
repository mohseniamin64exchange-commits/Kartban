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
    @Query("SELECT * FROM persons ORDER BY createdAt DESC")
    fun getAllPersonsWithCards(): Flow<List<PersonWithCards>>

    @Transaction
    @Query("SELECT * FROM persons WHERE id = :personId")
    fun getPersonWithCardsById(personId: Int): Flow<PersonWithCards?>

    @Query("SELECT * FROM bank_cards WHERE personId = :personId ORDER BY isDefault DESC, createdAt DESC")
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
}
