package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.ModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Query("SELECT * FROM models ORDER BY brand ASC, modelName ASC")
    fun getAllModels(): Flow<List<ModelEntity>>

    @Query("SELECT DISTINCT brand FROM models ORDER BY brand ASC")
    fun getAllBrands(): Flow<List<String>>

    @Query("SELECT * FROM models WHERE brand = :brand ORDER BY modelName ASC")
    fun getModelsByBrand(brand: String): Flow<List<ModelEntity>>

    @Query("SELECT * FROM models WHERE id = :id")
    suspend fun getModelById(id: Long): ModelEntity?

    @Query("""
        SELECT * FROM models 
        WHERE modelName LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR modelNumber LIKE '%' || :query || '%' 
           OR chipset LIKE '%' || :query || '%'
        ORDER BY brand ASC, modelName ASC
    """)
    fun searchModels(query: String): Flow<List<ModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ModelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(models: List<ModelEntity>)

    @Update
    suspend fun updateModel(model: ModelEntity)

    @Delete
    suspend fun deleteModel(model: ModelEntity)

    @Query("DELETE FROM models WHERE id = :id")
    suspend fun deleteModelById(id: Long)

    @Query("DELETE FROM models")
    suspend fun clearAll()
}
