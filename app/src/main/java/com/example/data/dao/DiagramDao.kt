package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.DiagramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagramDao {
    @Query("SELECT * FROM diagrams ORDER BY brand ASC, model ASC")
    fun getAllDiagrams(): Flow<List<DiagramEntity>>

    @Query("SELECT * FROM diagrams WHERE brand = :brand ORDER BY model ASC")
    fun getDiagramsByBrand(brand: String): Flow<List<DiagramEntity>>

    @Query("SELECT * FROM diagrams WHERE brand = :brand AND model = :model ORDER BY diagramType ASC")
    fun getDiagramsByBrandAndModel(brand: String, model: String): Flow<List<DiagramEntity>>

    @Query("SELECT * FROM diagrams WHERE id = :id")
    suspend fun getDiagramById(id: Long): DiagramEntity?

    @Query("""
        SELECT * FROM diagrams 
        WHERE brand LIKE '%' || :query || '%' 
           OR model LIKE '%' || :query || '%' 
           OR diagramType LIKE '%' || :query || '%' 
           OR title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
        ORDER BY brand ASC, model ASC
    """)
    fun searchDiagrams(query: String): Flow<List<DiagramEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagram(diagram: DiagramEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diagrams: List<DiagramEntity>)

    @Update
    suspend fun updateDiagram(diagram: DiagramEntity)

    @Delete
    suspend fun deleteDiagram(diagram: DiagramEntity)

    @Query("DELETE FROM diagrams WHERE id = :id")
    suspend fun deleteDiagramById(id: Long)

    @Query("DELETE FROM diagrams")
    suspend fun clearAll()
}
