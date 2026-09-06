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

    @Query("SELECT * FROM bank_cards WHERE personId = :personId ORDER BY createdAt DESC")
    fun getCardsForPerson(personId: Int): Flow<List<BankCardEntity>>

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
