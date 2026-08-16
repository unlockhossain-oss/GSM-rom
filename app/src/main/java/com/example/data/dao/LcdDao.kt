package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.LcdEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LcdDao {
    @Query("SELECT * FROM lcds ORDER BY brand ASC, groupName ASC, model ASC")
    fun getAllLcds(): Flow<List<LcdEntity>>

    @Query("SELECT * FROM lcds WHERE brand = :brand ORDER BY groupName ASC, model ASC")
    fun getLcdsByBrand(brand: String): Flow<List<LcdEntity>>

    @Query("SELECT * FROM lcds WHERE id = :id")
    suspend fun getLcdById(id: Long): LcdEntity?

    @Query("""
        SELECT * FROM lcds 
        WHERE model LIKE '%' || :query || '%' 
           OR modelCode LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR groupName LIKE '%' || :query || '%' 
           OR lcdName LIKE '%' || :query || '%' 
           OR compatibleModels LIKE '%' || :query || '%' 
           OR lcdType LIKE '%' || :query || '%'
        ORDER BY brand ASC, groupName ASC, model ASC
    """)
    fun searchLcds(query: String): Flow<List<LcdEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLcd(lcd: LcdEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lcds: List<LcdEntity>)

    @Update
    suspend fun updateLcd(lcd: LcdEntity)

    @Delete
    suspend fun deleteLcd(lcd: LcdEntity)

    @Query("DELETE FROM lcds WHERE id = :id")
    suspend fun deleteLcdById(id: Long)

    @Query("DELETE FROM lcds")
    suspend fun clearAll()
}
