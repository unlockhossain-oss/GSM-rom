package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val modelName: String,
    val modelNumber: String = "",
    val chipset: String = "",
    val androidVersion: String = "",
    val ram: String = "",
    val storage: String = "",
    val network: String = "",
    val battery: String = "",
    val charging: String = "",
    val notes: String = ""
)
