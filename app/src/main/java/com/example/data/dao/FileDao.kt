package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.FileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM files ORDER BY createdAt DESC")
    fun getAllFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE fileType = :fileType ORDER BY createdAt DESC")
    fun getFilesByType(fileType: String): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE brand = :brand ORDER BY createdAt DESC")
    fun getFilesByBrand(brand: String): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getFileById(id: Long): FileEntity?

    @Query("""
        SELECT * FROM files 
        WHERE fileName LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR model LIKE '%' || :query || '%' 
           OR fileType LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchFiles(query: String): Flow<List<FileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<FileEntity>)

    @Update
    suspend fun updateFile(file: FileEntity)

    @Delete
    suspend fun deleteFile(file: FileEntity)

    @Query("DELETE FROM files WHERE id = :id")
    suspend fun deleteFileById(id: Long)

    @Query("DELETE FROM files")
    suspend fun clearAll()
}
