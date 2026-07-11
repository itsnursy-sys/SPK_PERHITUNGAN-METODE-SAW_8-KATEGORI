package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.AlternativeEntity
import com.example.data.model.AlternativeValueEntity
import com.example.data.model.CriteriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SawDao {
    @Query("SELECT * FROM criteria ORDER BY code ASC")
    fun getAllCriteria(): Flow<List<CriteriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCriteria(criteria: List<CriteriaEntity>)

    @Query("SELECT * FROM alternatives ORDER BY id ASC")
    fun getAllAlternatives(): Flow<List<AlternativeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternative(alternative: AlternativeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternatives(alternatives: List<AlternativeEntity>)

    @Query("SELECT * FROM alternative_values")
    fun getAllAlternativeValues(): Flow<List<AlternativeValueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternativeValues(values: List<AlternativeValueEntity>)

    @Query("DELETE FROM criteria")
    suspend fun clearCriteria()

    @Query("DELETE FROM alternatives")
    suspend fun clearAlternatives()

    @Query("DELETE FROM alternative_values")
    suspend fun clearAlternativeValues()

    @Query("UPDATE criteria SET name = :name, weight = :weight, isBenefit = :isBenefit WHERE code = :code")
    suspend fun updateCriteria(code: String, name: String, weight: Double, isBenefit: Boolean)

    @Query("UPDATE alternatives SET name = :name WHERE id = :id")
    suspend fun updateAlternativeName(id: Int, name: String)

    @Transaction
    suspend fun resetToDefault(
        defaultCriteria: List<CriteriaEntity>,
        defaultAlternatives: List<AlternativeEntity>,
        defaultValues: List<AlternativeValueEntity>
    ) {
        clearCriteria()
        clearAlternatives()
        clearAlternativeValues()
        
        insertCriteria(defaultCriteria)
        insertAlternatives(defaultAlternatives)
        insertAlternativeValues(defaultValues)
    }
}
