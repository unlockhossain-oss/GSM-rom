package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagrams")
data class DiagramEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val diagramType: String, // Schematic Diagram, PCB Diagram, Test Point, Boot Point, Charging Section, Power Section, Network Section, Display Section, Audio Section
    val title: String = "",
    val filePath: String = "",
    val description: String = "",
    val testPoints: String = "", // e.g. "TP1: VBUS 5.0V, TP2: VBAT 4.2V, TP3: PMIC EN 1.8V"
    val voltageSpecs: String = "" // e.g. "VPH_PWR: 3.8V, VREG_L6_1P8: 1.8V"
)
