package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val brand: String,
    val model: String,
    val androidVersion: String = "",
    val fileType: String, // Firmware, Flash File, ENG ROM, Dump File, DA File, Auth File, OTA File, Recovery, Boot File, PDF, Tool, Driver, Other
    val filePath: String = "",
    val fileSize: String = "",
    val version: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
