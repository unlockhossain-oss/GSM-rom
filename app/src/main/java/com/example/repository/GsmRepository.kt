package com.example.repository

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.entity.CustomerEntity
import com.example.data.entity.DiagramEntity
import com.example.data.entity.FileEntity
import com.example.data.entity.LcdEntity
import com.example.data.entity.ModelEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class GsmRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val customerDao = database.customerDao()
    private val modelDao = database.modelDao()
    private val diagramDao = database.diagramDao()
    private val lcdDao = database.lcdDao()
    private val fileDao = database.fileDao()

    // Customers
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    val customerCount: Flow<Int> = customerDao.getCustomerCount()
    val pendingCount: Flow<Int> = customerDao.getPendingCount()
    val completedCount: Flow<Int> = customerDao.getCompletedCount()
    val totalDueAmount: Flow<Double?> = customerDao.getTotalDueAmount()

    fun searchCustomers(query: String): Flow<List<CustomerEntity>> = customerDao.searchCustomers(query)
    fun getCustomersByStatus(status: String): Flow<List<CustomerEntity>> = customerDao.getCustomersByStatus(status)
    fun getCustomersByDeliveryDate(date: String): Flow<List<CustomerEntity>> = customerDao.getCustomersByDeliveryDate(date)
    suspend fun getCustomerById(id: Long): CustomerEntity? = customerDao.getCustomerById(id)
    fun getCustomerFlowById(id: Long): Flow<CustomerEntity?> = customerDao.getCustomerFlowById(id)
    suspend fun getPendingScheduledCustomers(): List<CustomerEntity> = customerDao.getPendingScheduledCustomers()

    suspend fun findCustomerForLogin(codeOrEmail: String): CustomerEntity? = 
        customerDao.findCustomerForLogin(codeOrEmail.trim(), codeOrEmail.trim())
    suspend fun insertCustomer(customer: CustomerEntity): Long = customerDao.insertCustomer(customer)
    suspend fun getCustomerByCodeOrId(code: String, id: Long = -1L): CustomerEntity? =
        customerDao.getCustomerByCodeOrId(code, id)
    suspend fun updateCustomerBlockStatus(id: Long, isBlocked: Boolean) =
        customerDao.updateCustomerBlockStatus(id, isBlocked)
    suspend fun updateCustomerPassword(id: Long, newPassword: String) =
        customerDao.updateCustomerPassword(id, newPassword)
    suspend fun updateCustomer(customer: CustomerEntity) = customerDao.updateCustomer(customer)
    suspend fun updateCustomerStatus(id: Long, newStatus: String) = customerDao.updateCustomerStatus(id, newStatus)
    suspend fun updateDeliveryTime(id: Long, date: String, time: String, timestamp: Long) =
        customerDao.updateDeliveryTime(id, date, time, timestamp)
    suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.deleteCustomer(customer)
    suspend fun deleteCustomerById(id: Long) = customerDao.deleteCustomerById(id)

    // Models
    val allModels: Flow<List<ModelEntity>> = modelDao.getAllModels()
    val allBrands: Flow<List<String>> = modelDao.getAllBrands()
    fun getModelsByBrand(brand: String): Flow<List<ModelEntity>> = modelDao.getModelsByBrand(brand)
    fun searchModels(query: String): Flow<List<ModelEntity>> = modelDao.searchModels(query)
    suspend fun getModelById(id: Long): ModelEntity? = modelDao.getModelById(id)
    suspend fun insertModel(model: ModelEntity): Long = modelDao.insertModel(model)
    suspend fun updateModel(model: ModelEntity) = modelDao.updateModel(model)
    suspend fun deleteModel(model: ModelEntity) = modelDao.deleteModel(model)
    suspend fun deleteModelById(id: Long) = modelDao.deleteModelById(id)

    // Diagrams
    val allDiagrams: Flow<List<DiagramEntity>> = diagramDao.getAllDiagrams()
    fun getDiagramsByBrand(brand: String): Flow<List<DiagramEntity>> = diagramDao.getDiagramsByBrand(brand)
    fun getDiagramsByBrandAndModel(brand: String, model: String): Flow<List<DiagramEntity>> =
        diagramDao.getDiagramsByBrandAndModel(brand, model)
    fun searchDiagrams(query: String): Flow<List<DiagramEntity>> = diagramDao.searchDiagrams(query)
    suspend fun getDiagramById(id: Long): DiagramEntity? = diagramDao.getDiagramById(id)
    suspend fun insertDiagram(diagram: DiagramEntity): Long = diagramDao.insertDiagram(diagram)
    suspend fun updateDiagram(diagram: DiagramEntity) = diagramDao.updateDiagram(diagram)
    suspend fun deleteDiagram(diagram: DiagramEntity) = diagramDao.deleteDiagram(diagram)
    suspend fun deleteDiagramById(id: Long) = diagramDao.deleteDiagramById(id)

    // LCDs
    val allLcds: Flow<List<LcdEntity>> = lcdDao.getAllLcds()
    fun getLcdsByBrand(brand: String): Flow<List<LcdEntity>> = lcdDao.getLcdsByBrand(brand)
    fun searchLcds(query: String): Flow<List<LcdEntity>> = lcdDao.searchLcds(query)
    suspend fun getLcdById(id: Long): LcdEntity? = lcdDao.getLcdById(id)
    suspend fun insertLcd(lcd: LcdEntity): Long = lcdDao.insertLcd(lcd)
    suspend fun updateLcd(lcd: LcdEntity) = lcdDao.updateLcd(lcd)
    suspend fun deleteLcd(lcd: LcdEntity) = lcdDao.deleteLcd(lcd)
    suspend fun deleteLcdById(id: Long) = lcdDao.deleteLcdById(id)

    // Files
    val allFiles: Flow<List<FileEntity>> = fileDao.getAllFiles()
    fun getFilesByType(type: String): Flow<List<FileEntity>> = fileDao.getFilesByType(type)
    fun getFilesByBrand(brand: String): Flow<List<FileEntity>> = fileDao.getFilesByBrand(brand)
    fun searchFiles(query: String): Flow<List<FileEntity>> = fileDao.searchFiles(query)
    suspend fun getFileById(id: Long): FileEntity? = fileDao.getFileById(id)
    suspend fun insertFile(file: FileEntity): Long = fileDao.insertFile(file)
    suspend fun updateFile(file: FileEntity) = fileDao.updateFile(file)
    suspend fun deleteFile(file: FileEntity) = fileDao.deleteFile(file)
    suspend fun deleteFileById(id: Long) = fileDao.deleteFileById(id)

    suspend fun clearAllData() {
        customerDao.clearAll()
        modelDao.clearAll()
        diagramDao.clearAll()
        lcdDao.clearAll()
        fileDao.clearAll()
    }

    suspend fun restoreDefaultData() {
        clearAllData()
        AppDatabase.populateInitialData(database)
    }

    // Export entire database as structured JSON
    suspend fun exportDatabaseToJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        root.put("appName", "GSM Service")

        // Customers array
        val customersList = mutableListOf<CustomerEntity>()
        // Get snapshot from DB
        val custArray = JSONArray()
        // Query directly
        val rawCustomers = customerDao.searchCustomers("")
        // For simplicity in export suspend
        val customers = database.openHelper.readableDatabase.query("SELECT * FROM customers")
        while (customers.moveToNext()) {
            val obj = JSONObject()
            obj.put("id", customers.getLong(customers.getColumnIndexOrThrow("id")))
            obj.put("customerName", customers.getString(customers.getColumnIndexOrThrow("customerName")))
            obj.put("mobileNumber", customers.getString(customers.getColumnIndexOrThrow("mobileNumber")))
            obj.put("gmail", customers.getString(customers.getColumnIndexOrThrow("gmail")))
            obj.put("address", customers.getString(customers.getColumnIndexOrThrow("address")))
            obj.put("brand", customers.getString(customers.getColumnIndexOrThrow("brand")))
            obj.put("model", customers.getString(customers.getColumnIndexOrThrow("model")))
            obj.put("imei", customers.getString(customers.getColumnIndexOrThrow("imei")))
            obj.put("serviceType", customers.getString(customers.getColumnIndexOrThrow("serviceType")))
            obj.put("problemDescription", customers.getString(customers.getColumnIndexOrThrow("problemDescription")))
            obj.put("serviceCharge", customers.getDouble(customers.getColumnIndexOrThrow("serviceCharge")))
            obj.put("advancePayment", customers.getDouble(customers.getColumnIndexOrThrow("advancePayment")))
            obj.put("dueAmount", customers.getDouble(customers.getColumnIndexOrThrow("dueAmount")))
            obj.put("deliveryDate", customers.getString(customers.getColumnIndexOrThrow("deliveryDate")))
            obj.put("deliveryTime", customers.getString(customers.getColumnIndexOrThrow("deliveryTime")))
            obj.put("deliveryTimestamp", customers.getLong(customers.getColumnIndexOrThrow("deliveryTimestamp")))
            obj.put("status", customers.getString(customers.getColumnIndexOrThrow("status")))
            val voicePathCol = customers.getColumnIndexOrThrow("voiceFilePath")
            obj.put("voiceFilePath", if (customers.isNull(voicePathCol)) null else customers.getString(voicePathCol))
            obj.put("voiceDurationMs", customers.getLong(customers.getColumnIndexOrThrow("voiceDurationMs")))
            obj.put("createdAt", customers.getLong(customers.getColumnIndexOrThrow("createdAt")))
            custArray.put(obj)
        }
        customers.close()
        root.put("customers", custArray)

        // Models array
        val modArray = JSONArray()
        val models = database.openHelper.readableDatabase.query("SELECT * FROM models")
        while (models.moveToNext()) {
            val obj = JSONObject()
            obj.put("id", models.getLong(models.getColumnIndexOrThrow("id")))
            obj.put("brand", models.getString(models.getColumnIndexOrThrow("brand")))
            obj.put("modelName", models.getString(models.getColumnIndexOrThrow("modelName")))
            obj.put("modelNumber", models.getString(models.getColumnIndexOrThrow("modelNumber")))
            obj.put("chipset", models.getString(models.getColumnIndexOrThrow("chipset")))
            obj.put("androidVersion", models.getString(models.getColumnIndexOrThrow("androidVersion")))
            obj.put("ram", models.getString(models.getColumnIndexOrThrow("ram")))
            obj.put("storage", models.getString(models.getColumnIndexOrThrow("storage")))
            obj.put("network", models.getString(models.getColumnIndexOrThrow("network")))
            obj.put("battery", models.getString(models.getColumnIndexOrThrow("battery")))
            obj.put("charging", models.getString(models.getColumnIndexOrThrow("charging")))
            obj.put("notes", models.getString(models.getColumnIndexOrThrow("notes")))
            modArray.put(obj)
        }
        models.close()
        root.put("models", modArray)

        // LCD array
        val lcdArray = JSONArray()
        val lcds = database.openHelper.readableDatabase.query("SELECT * FROM lcds")
        while (lcds.moveToNext()) {
            val obj = JSONObject()
            obj.put("id", lcds.getLong(lcds.getColumnIndexOrThrow("id")))
            obj.put("brand", lcds.getString(lcds.getColumnIndexOrThrow("brand")))
            obj.put("model", lcds.getString(lcds.getColumnIndexOrThrow("model")))
            obj.put("lcdName", lcds.getString(lcds.getColumnIndexOrThrow("lcdName")))
            obj.put("lcdType", lcds.getString(lcds.getColumnIndexOrThrow("lcdType")))
            obj.put("displaySize", lcds.getString(lcds.getColumnIndexOrThrow("displaySize")))
            obj.put("resolution", lcds.getString(lcds.getColumnIndexOrThrow("resolution")))
            obj.put("touchInfo", lcds.getString(lcds.getColumnIndexOrThrow("touchInfo")))
            obj.put("compatibleModels", lcds.getString(lcds.getColumnIndexOrThrow("compatibleModels")))
            obj.put("price", lcds.getDouble(lcds.getColumnIndexOrThrow("price")))
            obj.put("stock", lcds.getString(lcds.getColumnIndexOrThrow("stock")))
            obj.put("imagePath", lcds.getString(lcds.getColumnIndexOrThrow("imagePath")))
            obj.put("notes", lcds.getString(lcds.getColumnIndexOrThrow("notes")))
            lcdArray.put(obj)
        }
        lcds.close()
        root.put("lcds", lcdArray)

        return root.toString(2)
    }

    // Import and restore from JSON
    suspend fun importDatabaseFromJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            if (root.has("customers")) {
                val custArray = root.getJSONArray("customers")
                val importedCustomers = mutableListOf<CustomerEntity>()
                for (i in 0 until custArray.length()) {
                    val obj = custArray.getJSONObject(i)
                    importedCustomers.add(
                        CustomerEntity(
                            id = obj.optLong("id", 0L),
                            customerName = obj.optString("customerName", "Customer"),
                            mobileNumber = obj.optString("mobileNumber", ""),
                            gmail = obj.optString("gmail", ""),
                            address = obj.optString("address", ""),
                            brand = obj.optString("brand", "Other"),
                            model = obj.optString("model", "Device"),
                            imei = obj.optString("imei", ""),
                            serviceType = obj.optString("serviceType", "Repair"),
                            problemDescription = obj.optString("problemDescription", ""),
                            serviceCharge = obj.optDouble("serviceCharge", 0.0),
                            advancePayment = obj.optDouble("advancePayment", 0.0),
                            dueAmount = obj.optDouble("dueAmount", 0.0),
                            deliveryDate = obj.optString("deliveryDate", ""),
                            deliveryTime = obj.optString("deliveryTime", ""),
                            deliveryTimestamp = obj.optLong("deliveryTimestamp", 0L),
                            status = obj.optString("status", "Received"),
                            voiceFilePath = if (obj.isNull("voiceFilePath")) null else obj.optString("voiceFilePath"),
                            voiceDurationMs = obj.optLong("voiceDurationMs", 0L),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (importedCustomers.isNotEmpty()) {
                    customerDao.insertAll(importedCustomers)
                }
            }

            if (root.has("models")) {
                val modArray = root.getJSONArray("models")
                val importedModels = mutableListOf<ModelEntity>()
                for (i in 0 until modArray.length()) {
                    val obj = modArray.getJSONObject(i)
                    importedModels.add(
                        ModelEntity(
                            id = obj.optLong("id", 0L),
                            brand = obj.optString("brand", "Other"),
                            modelName = obj.optString("modelName", ""),
                            modelNumber = obj.optString("modelNumber", ""),
                            chipset = obj.optString("chipset", ""),
                            androidVersion = obj.optString("androidVersion", ""),
                            ram = obj.optString("ram", ""),
                            storage = obj.optString("storage", ""),
                            network = obj.optString("network", ""),
                            battery = obj.optString("battery", ""),
                            charging = obj.optString("charging", ""),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
                if (importedModels.isNotEmpty()) {
                    modelDao.insertAll(importedModels)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
