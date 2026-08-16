package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerIdCode: String = "", // e.g. "CUST-1001"
    val password: String = "123456", // Password for Customer ID Login
    val isBlocked: Boolean = false, // Admin can Block / Unblock customer
    val customerName: String,
    val mobileNumber: String,
    val gmail: String = "",
    val address: String = "",
    val brand: String = "",
    val model: String = "",
    val imei: String = "",
    val serviceType: String = "Software", // Software, Flash, FRP, Dead Boot, Display, Charging, Network, Hardware, IC Repair, Other
    val problemDescription: String = "",
    val serviceNote: String = "",
    val serviceCharge: Double = 0.0,
    val advancePayment: Double = 0.0,
    val dueAmount: Double = 0.0,
    val deliveryDate: String = "", // e.g. "15 Aug 2026"
    val deliveryTime: String = "", // e.g. "05:00 PM"
    val deliveryTimestamp: Long = 0L,
    val status: String = "Pending", // Pending, Received, Checking, Processing, Waiting for Parts, Completed, Delivered, Cancelled
    val voiceFilePath: String? = null,
    val voiceDurationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)
