package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): CustomerEntity?

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerFlowById(id: Long): Flow<CustomerEntity?>

    @Query("SELECT * FROM customers WHERE customerIdCode = :code OR id = :idLong LIMIT 1")
    suspend fun getCustomerByCodeOrId(code: String, idLong: Long = -1L): CustomerEntity?

    @Query("SELECT * FROM customers WHERE customerIdCode = :code OR gmail = :emailOrCode OR mobileNumber = :emailOrCode LIMIT 1")
    suspend fun findCustomerForLogin(code: String, emailOrCode: String): CustomerEntity?

    @Query("""
        SELECT * FROM customers 
        WHERE customerName LIKE '%' || :query || '%' 
           OR customerIdCode LIKE '%' || :query || '%'
           OR mobileNumber LIKE '%' || :query || '%' 
           OR gmail LIKE '%' || :query || '%' 
           OR model LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR imei LIKE '%' || :query || '%' 
           OR serviceType LIKE '%' || :query || '%' 
           OR status LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE status = :status ORDER BY createdAt DESC")
    fun getCustomersByStatus(status: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE deliveryDate = :date ORDER BY deliveryTimestamp ASC, createdAt DESC")
    fun getCustomersByDeliveryDate(date: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE deliveryTimestamp > 0 AND status != 'Delivered' AND status != 'Cancelled'")
    suspend fun getPendingScheduledCustomers(): List<CustomerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Query("UPDATE customers SET status = :newStatus WHERE id = :id")
    suspend fun updateCustomerStatus(id: Long, newStatus: String)

    @Query("UPDATE customers SET isBlocked = :blocked WHERE id = :id")
    suspend fun updateCustomerBlockStatus(id: Long, blocked: Boolean)

    @Query("UPDATE customers SET password = :newPassword WHERE id = :id")
    suspend fun updateCustomerPassword(id: Long, newPassword: String)

    @Query("UPDATE customers SET deliveryDate = :date, deliveryTime = :time, deliveryTimestamp = :timestamp WHERE id = :id")
    suspend fun updateDeliveryTime(id: Long, date: String, time: String, timestamp: Long)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("DELETE FROM customers WHERE id = :id")
    suspend fun deleteCustomerById(id: Long)

    @Query("DELETE FROM customers")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM customers")
    fun getCustomerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM customers WHERE status != 'Delivered' AND status != 'Cancelled'")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM customers WHERE status = 'Completed'")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT SUM(dueAmount) FROM customers WHERE status != 'Delivered' AND status != 'Cancelled'")
    fun getTotalDueAmount(): Flow<Double?>
}
