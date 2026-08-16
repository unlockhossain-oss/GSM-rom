package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lcds")
data class LcdEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val groupName: String = "",
    val model: String,
    val modelCode: String = "",
    val lcdName: String = "",
    val lcdType: String = "IPS LCD", // IPS LCD, AMOLED, Super AMOLED, OLED, TFT, Incell
    val displaySize: String = "", // e.g. "6.22 inch"
    val resolution: String = "", // e.g. "720 × 1520"
    val connectorType: String = "", // e.g. "Compatible LCD Connector 34-pin FPC"
    val touchInfo: String = "",
    val compatibleModels: String = "",
    val price: Double = 0.0,
    val stock: String = "In Stock", // In Stock, Low Stock, Out of Stock
    val imagePath: String = "",
    val notes: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)
