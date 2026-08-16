package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.GsmApplication
import com.example.audio.AudioPlayer
import com.example.audio.AudioRecorder
import com.example.data.entity.CustomerEntity
import com.example.data.entity.DiagramEntity
import com.example.data.entity.FileEntity
import com.example.data.entity.LcdEntity
import com.example.data.entity.ModelEntity
import com.example.notification.AlarmScheduler
import com.example.preferences.ThemeMode
import com.example.preferences.UserSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.auth.AuthManager
import com.example.auth.AuthResult
import com.example.auth.AuthUser
import com.example.auth.UserRole

@OptIn(ExperimentalCoroutinesApi::class)
class GsmViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as GsmApplication
    private val repository = app.repository
    private val userPreferences = app.userPreferences
    val authManager = AuthManager(app, repository)

    val audioRecorder = AudioRecorder(viewModelScope)
    val audioPlayer = AudioPlayer(viewModelScope)

    // User Settings Flow
    val userSettings: StateFlow<UserSettings> = userPreferences.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    // Auth Session Flow
    val currentUser: StateFlow<AuthUser> = userPreferences.authSessionFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AuthUser()
    )

    // Live customer entity for currently logged in customer
    val currentCustomerEntity: StateFlow<CustomerEntity?> = currentUser.flatMapLatest { auth ->
        if (auth.role == UserRole.CUSTOMER) {
            if (auth.dbCustomerId > 0L) {
                repository.getCustomerFlowById(auth.dbCustomerId)
            } else {
                repository.searchCustomers(auth.customerId).map { it.firstOrNull() }
            }
        } else {
            kotlinx.coroutines.flow.flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Global Search State
    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    // ----------------------------------------------------
    // CUSTOMER SECTION
    // ----------------------------------------------------
    private val _customerSearchQuery = MutableStateFlow("")
    val customerSearchQuery: StateFlow<String> = _customerSearchQuery.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("All")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    val customersList: StateFlow<List<CustomerEntity>> = combine(
        _customerSearchQuery,
        _selectedStatusFilter
    ) { query, status ->
        Pair(query, status)
    }.flatMapLatest { (query, status) ->
        if (query.isNotBlank()) {
            repository.searchCustomers(query).map { list ->
                if (status != "All") list.filter { it.status.equals(status, ignoreCase = true) } else list
            }
        } else if (status != "All") {
            repository.getCustomersByStatus(status)
        } else {
            repository.allCustomers
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Today's Date String
    private val todayDateString: String = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(Date())

    val todayDeliveries: StateFlow<List<CustomerEntity>> = repository.getCustomersByDeliveryDate(todayDateString)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Stats
    val totalCustomerCount: StateFlow<Int> = repository.customerCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val pendingCount: StateFlow<Int> = repository.pendingCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val completedCount: StateFlow<Int> = repository.completedCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalDueAmount: StateFlow<Double> = repository.totalDueAmount.map { it ?: 0.0 }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun setCustomerSearchQuery(query: String) {
        _customerSearchQuery.value = query
    }

    fun setStatusFilter(status: String) {
        _selectedStatusFilter.value = status
    }

    fun saveCustomer(
        id: Long = 0L,
        customerName: String,
        mobileNumber: String,
        gmail: String,
        address: String,
        brand: String,
        model: String,
        imei: String,
        serviceType: String,
        problemDescription: String,
        serviceCharge: Double,
        advancePayment: Double,
        deliveryDate: String,
        deliveryTime: String,
        deliveryTimestamp: Long,
        status: String,
        voiceFilePath: String?,
        voiceDurationMs: Long,
        onSaved: (Long) -> Unit = {}
    ) {
        val calculatedDue = (serviceCharge - advancePayment).coerceAtLeast(0.0)
        val customer = CustomerEntity(
            id = id,
            customerName = customerName.trim(),
            mobileNumber = mobileNumber.trim(),
            gmail = gmail.trim(),
            address = address.trim(),
            brand = brand.trim(),
            model = model.trim(),
            imei = imei.trim(),
            serviceType = serviceType,
            problemDescription = problemDescription.trim(),
            serviceCharge = serviceCharge,
            advancePayment = advancePayment,
            dueAmount = calculatedDue,
            deliveryDate = deliveryDate,
            deliveryTime = deliveryTime,
            deliveryTimestamp = deliveryTimestamp,
            status = status,
            voiceFilePath = voiceFilePath,
            voiceDurationMs = voiceDurationMs,
            createdAt = if (id > 0L) System.currentTimeMillis() else System.currentTimeMillis()
        )

        viewModelScope.launch {
            val generatedId = if (id > 0L) {
                repository.updateCustomer(customer)
                id
            } else {
                repository.insertCustomer(customer)
            }

            // Schedule Delivery Alarm if set
            if (deliveryTimestamp > System.currentTimeMillis() && status != "Delivered" && status != "Cancelled") {
                AlarmScheduler.scheduleDeliveryAlarm(
                    context = app,
                    customerId = generatedId,
                    customerName = customer.customerName,
                    brandModel = "${customer.brand} ${customer.model}",
                    serviceType = customer.serviceType,
                    deliveryTime = "$deliveryDate $deliveryTime",
                    mobileNumber = customer.mobileNumber,
                    triggerTimestampMs = deliveryTimestamp
                )
            }

            onSaved(generatedId)
        }
    }

    fun updateCustomerStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateCustomerStatus(id, newStatus)
            if (newStatus == "Delivered" || newStatus == "Cancelled") {
                AlarmScheduler.cancelDeliveryAlarm(app, id)
            }
        }
    }

    fun updateCustomerDeliveryTime(id: Long, date: String, time: String, timestamp: Long, customerName: String, brandModel: String, serviceType: String, mobileNumber: String) {
        viewModelScope.launch {
            repository.updateDeliveryTime(id, date, time, timestamp)
            if (timestamp > System.currentTimeMillis()) {
                AlarmScheduler.scheduleDeliveryAlarm(
                    context = app,
                    customerId = id,
                    customerName = customerName,
                    brandModel = brandModel,
                    serviceType = serviceType,
                    deliveryTime = "$date $time",
                    mobileNumber = mobileNumber,
                    triggerTimestampMs = timestamp
                )
            }
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancelDeliveryAlarm(app, customer.id)
            customer.voiceFilePath?.let { path ->
                try {
                    val file = File(path)
                    if (file.exists()) file.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            repository.deleteCustomer(customer)
        }
    }

    fun blockCustomer(customerId: Long, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.updateCustomerBlockStatus(customerId, isBlocked)
        }
    }

    fun changeCustomerPassword(customerId: Long, newPass: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateCustomerPassword(customerId, newPass)
            onDone()
        }
    }

    fun updateCustomerDetails(
        customer: CustomerEntity,
        onSaved: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            onSaved(customer.id)
        }
    }

    // ----------------------------------------------------
    // AUTH METHODS
    // ----------------------------------------------------
    fun loginCustomer(
        customerIdOrEmail: String,
        passwordInput: String,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = authManager.loginCustomer(customerIdOrEmail, passwordInput)
            if (result is AuthResult.Success) {
                userPreferences.saveAuthSession(result.user)
            }
            onResult(result)
        }
    }

    fun registerCustomer(
        name: String,
        customerId: String,
        mobileNumber: String,
        email: String,
        password: String,
        confirmPassword: String,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = authManager.createCustomerAccount(
                name = name,
                customerId = customerId,
                mobileNumber = mobileNumber,
                email = email,
                password = password,
                confirmPassword = confirmPassword
            )
            if (result is AuthResult.Success) {
                userPreferences.saveAuthSession(result.user)
            }
            onResult(result)
        }
    }

    fun loginAdmin(
        adminId: String,
        adminPass: String,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val settings = userSettings.value
            val result = authManager.loginAdmin(adminId, adminPass, settings.adminId, settings.adminPassword)
            if (result is AuthResult.Success) {
                userPreferences.saveAuthSession(result.user)
            }
            onResult(result)
        }
    }

    fun signInWithGoogle(
        idToken: String,
        displayName: String?,
        email: String?,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogleCredential(idToken, displayName, email)
            if (result is AuthResult.Success) {
                userPreferences.saveAuthSession(result.user)
            }
            onResult(result)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearAuthSession()
        }
    }

    fun updateAdminCredentials(newId: String, newPass: String) {
        viewModelScope.launch {
            userPreferences.setAdminCredentials(newId, newPass)
        }
    }

    // ----------------------------------------------------
    // PHONE MODEL SECTION
    // ----------------------------------------------------
    private val _selectedModelBrand = MutableStateFlow("All")
    val selectedModelBrand: StateFlow<String> = _selectedModelBrand.asStateFlow()

    private val _modelSearchQuery = MutableStateFlow("")
    val modelSearchQuery: StateFlow<String> = _modelSearchQuery.asStateFlow()

    val allBrands: StateFlow<List<String>> = repository.allBrands.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val modelsList: StateFlow<List<ModelEntity>> = combine(
        _selectedModelBrand,
        _modelSearchQuery
    ) { brand, query ->
        Pair(brand, query)
    }.flatMapLatest { (brand, query) ->
        if (query.isNotBlank()) {
            repository.searchModels(query).map { list ->
                if (brand != "All") list.filter { it.brand.equals(brand, ignoreCase = true) } else list
            }
        } else if (brand != "All") {
            repository.getModelsByBrand(brand)
        } else {
            repository.allModels
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedModelBrand(brand: String) {
        _selectedModelBrand.value = brand
    }

    fun setModelSearchQuery(query: String) {
        _modelSearchQuery.value = query
    }

    fun saveModel(
        id: Long = 0L,
        brand: String,
        modelName: String,
        modelNumber: String,
        chipset: String,
        androidVersion: String,
        ram: String,
        storage: String,
        network: String,
        battery: String,
        charging: String,
        notes: String
    ) {
        val model = ModelEntity(
            id = id,
            brand = brand.trim(),
            modelName = modelName.trim(),
            modelNumber = modelNumber.trim(),
            chipset = chipset.trim(),
            androidVersion = androidVersion.trim(),
            ram = ram.trim(),
            storage = storage.trim(),
            network = network.trim(),
            battery = battery.trim(),
            charging = charging.trim(),
            notes = notes.trim()
        )
        viewModelScope.launch {
            if (id > 0L) {
                repository.updateModel(model)
            } else {
                repository.insertModel(model)
            }
        }
    }

    fun deleteModel(model: ModelEntity) {
        viewModelScope.launch {
            repository.deleteModel(model)
        }
    }

    // ----------------------------------------------------
    // DIAGRAM SECTION
    // ----------------------------------------------------
    private val _selectedDiagramBrand = MutableStateFlow("All")
    val selectedDiagramBrand: StateFlow<String> = _selectedDiagramBrand.asStateFlow()

    private val _selectedDiagramModel = MutableStateFlow("All")
    val selectedDiagramModel: StateFlow<String> = _selectedDiagramModel.asStateFlow()

    private val _selectedDiagramType = MutableStateFlow("All")
    val selectedDiagramType: StateFlow<String> = _selectedDiagramType.asStateFlow()

    private val _diagramSearchQuery = MutableStateFlow("")
    val diagramSearchQuery: StateFlow<String> = _diagramSearchQuery.asStateFlow()

    val diagramsList: StateFlow<List<DiagramEntity>> = combine(
        _selectedDiagramBrand,
        _selectedDiagramModel,
        _selectedDiagramType,
        _diagramSearchQuery
    ) { brand, model, type, query ->
        listOf(brand, model, type, query)
    }.flatMapLatest { filters ->
        val brand = filters[0]
        val model = filters[1]
        val type = filters[2]
        val query = filters[3]

        if (query.isNotBlank()) {
            repository.searchDiagrams(query).map { list ->
                list.filter { item ->
                    (brand == "All" || item.brand.equals(brand, ignoreCase = true)) &&
                    (model == "All" || item.model.equals(model, ignoreCase = true)) &&
                    (type == "All" || item.diagramType.equals(type, ignoreCase = true))
                }
            }
        } else {
            repository.allDiagrams.map { list ->
                list.filter { item ->
                    (brand == "All" || item.brand.equals(brand, ignoreCase = true)) &&
                    (model == "All" || item.model.equals(model, ignoreCase = true)) &&
                    (type == "All" || item.diagramType.equals(type, ignoreCase = true))
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedDiagramBrand(brand: String) {
        _selectedDiagramBrand.value = brand
        _selectedDiagramModel.value = "All"
    }

    fun setSelectedDiagramModel(model: String) {
        _selectedDiagramModel.value = model
    }

    fun setSelectedDiagramType(type: String) {
        _selectedDiagramType.value = type
    }

    fun setDiagramSearchQuery(query: String) {
        _diagramSearchQuery.value = query
    }

    fun saveDiagram(
        id: Long = 0L,
        brand: String,
        model: String,
        diagramType: String,
        title: String,
        filePath: String,
        description: String,
        testPoints: String,
        voltageSpecs: String
    ) {
        val diagram = DiagramEntity(
            id = id,
            brand = brand.trim(),
            model = model.trim(),
            diagramType = diagramType,
            title = title.trim(),
            filePath = filePath.trim(),
            description = description.trim(),
            testPoints = testPoints.trim(),
            voltageSpecs = voltageSpecs.trim()
        )
        viewModelScope.launch {
            if (id > 0L) {
                repository.updateDiagram(diagram)
            } else {
                repository.insertDiagram(diagram)
            }
        }
    }

    fun deleteDiagram(diagram: DiagramEntity) {
        viewModelScope.launch {
            repository.deleteDiagram(diagram)
        }
    }

    // ----------------------------------------------------
    // LCD SECTION
    // ----------------------------------------------------
    private val _selectedLcdBrand = MutableStateFlow("All")
    val selectedLcdBrand: StateFlow<String> = _selectedLcdBrand.asStateFlow()

    private val _lcdSearchQuery = MutableStateFlow("")
    val lcdSearchQuery: StateFlow<String> = _lcdSearchQuery.asStateFlow()

    val lcdsList: StateFlow<List<LcdEntity>> = combine(
        _selectedLcdBrand,
        _lcdSearchQuery
    ) { brand, query ->
        Pair(brand, query)
    }.flatMapLatest { (brand, query) ->
        if (query.isNotBlank()) {
            repository.searchLcds(query).map { list ->
                if (brand != "All") list.filter { it.brand.equals(brand, ignoreCase = true) } else list
            }
        } else if (brand != "All") {
            repository.getLcdsByBrand(brand)
        } else {
            repository.allLcds
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedLcdBrand(brand: String) {
        _selectedLcdBrand.value = brand
    }

    fun setLcdSearchQuery(query: String) {
        _lcdSearchQuery.value = query
    }

    fun saveLcd(
        id: Long = 0L,
        brand: String,
        groupName: String = "",
        model: String,
        modelCode: String = "",
        lcdName: String = "",
        lcdType: String = "IPS LCD",
        displaySize: String = "",
        resolution: String = "",
        connectorType: String = "",
        touchInfo: String = "",
        compatibleModels: String = "",
        price: Double = 0.0,
        stock: String = "In Stock",
        notes: String = "",
        imagePath: String = ""
    ) {
        val effectiveGroupName = groupName.trim().ifBlank { "$brand $model Series" }
        val effectiveLcdName = lcdName.trim().ifBlank { "$model Display" }
        val lcd = LcdEntity(
            id = id,
            brand = brand.trim(),
            groupName = effectiveGroupName,
            model = model.trim(),
            modelCode = modelCode.trim(),
            lcdName = effectiveLcdName,
            lcdType = lcdType.trim().ifBlank { "IPS LCD" },
            displaySize = displaySize.trim(),
            resolution = resolution.trim(),
            connectorType = connectorType.trim(),
            touchInfo = touchInfo.trim(),
            compatibleModels = compatibleModels.trim(),
            price = price,
            stock = stock,
            notes = notes.trim(),
            imagePath = imagePath
        )
        viewModelScope.launch {
            if (id > 0L) {
                repository.updateLcd(lcd)
            } else {
                repository.insertLcd(lcd)
            }
        }
    }

    fun deleteLcd(lcd: LcdEntity) {
        viewModelScope.launch {
            repository.deleteLcd(lcd)
        }
    }

    // ----------------------------------------------------
    // FILE MANAGEMENT SECTION
    // ----------------------------------------------------
    private val _selectedFileType = MutableStateFlow("All")
    val selectedFileType: StateFlow<String> = _selectedFileType.asStateFlow()

    private val _fileSearchQuery = MutableStateFlow("")
    val fileSearchQuery: StateFlow<String> = _fileSearchQuery.asStateFlow()

    val filesList: StateFlow<List<FileEntity>> = combine(
        _selectedFileType,
        _fileSearchQuery
    ) { type, query ->
        Pair(type, query)
    }.flatMapLatest { (type, query) ->
        if (query.isNotBlank()) {
            repository.searchFiles(query).map { list ->
                if (type != "All") list.filter { it.fileType.equals(type, ignoreCase = true) } else list
            }
        } else if (type != "All") {
            repository.getFilesByType(type)
        } else {
            repository.allFiles
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedFileType(type: String) {
        _selectedFileType.value = type
    }

    fun setFileSearchQuery(query: String) {
        _fileSearchQuery.value = query
    }

    fun saveFileEntity(
        id: Long = 0L,
        fileName: String,
        brand: String,
        model: String,
        androidVersion: String,
        fileType: String,
        filePath: String,
        fileSize: String,
        version: String,
        description: String
    ) {
        val fileEntity = FileEntity(
            id = id,
            fileName = fileName.trim(),
            brand = brand.trim(),
            model = model.trim(),
            androidVersion = androidVersion.trim(),
            fileType = fileType,
            filePath = filePath.trim(),
            fileSize = fileSize.trim(),
            version = version.trim(),
            description = description.trim()
        )
        viewModelScope.launch {
            if (id > 0L) {
                repository.updateFile(fileEntity)
            } else {
                repository.insertFile(fileEntity)
            }
        }
    }

    fun deleteFile(fileEntity: FileEntity) {
        viewModelScope.launch {
            repository.deleteFile(fileEntity)
        }
    }

    // ----------------------------------------------------
    // GLOBAL SEARCH
    // ----------------------------------------------------
    fun setGlobalSearchQuery(query: String) {
        _globalSearchQuery.value = query
    }

    data class GlobalSearchResult(
        val customers: List<CustomerEntity> = emptyList(),
        val models: List<ModelEntity> = emptyList(),
        val diagrams: List<DiagramEntity> = emptyList(),
        val lcds: List<LcdEntity> = emptyList(),
        val files: List<FileEntity> = emptyList()
    ) {
        val totalCount: Int get() = customers.size + models.size + diagrams.size + lcds.size + files.size
    }

    val globalSearchResults: StateFlow<GlobalSearchResult> = _globalSearchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            MutableStateFlow(GlobalSearchResult())
        } else {
            combine(
                repository.searchCustomers(query),
                repository.searchModels(query),
                repository.searchDiagrams(query),
                repository.searchLcds(query),
                repository.searchFiles(query)
            ) { c, m, d, l, f ->
                GlobalSearchResult(
                    customers = c,
                    models = m,
                    diagrams = d,
                    lcds = l,
                    files = f
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GlobalSearchResult()
    )

    // ----------------------------------------------------
    // SETTINGS ACTIONS
    // ----------------------------------------------------
    fun updateCustomerSignInSettings(
        portalTitle: String,
        loginNotice: String,
        defaultPass: String,
        allowRegistration: Boolean,
        allowGoogle: Boolean,
        requirePhone: Boolean,
        idPrefix: String,
        supportPhone: String,
        supportEmail: String,
        onSaved: () -> Unit = {}
    ) {
        viewModelScope.launch {
            userPreferences.updateCustomerSignInSettings(
                portalTitle = portalTitle.trim(),
                loginNotice = loginNotice.trim(),
                defaultPass = defaultPass.trim(),
                allowRegistration = allowRegistration,
                allowGoogle = allowGoogle,
                requirePhone = requirePhone,
                idPrefix = idPrefix.trim(),
                supportPhone = supportPhone.trim(),
                supportEmail = supportEmail.trim()
            )
            onSaved()
        }
    }

    fun updateAdminProfile(
        adminName: String,
        adminId: String,
        adminPass: String,
        adminEmail: String,
        adminPhone: String,
        adminDesignation: String,
        adminRoleBadge: String,
        shopName: String,
        shopPhone: String,
        shopAddress: String,
        onSaved: () -> Unit = {}
    ) {
        viewModelScope.launch {
            userPreferences.updateAdminProfile(
                adminName = adminName.trim(),
                adminId = adminId.trim(),
                adminPass = adminPass.trim(),
                adminEmail = adminEmail.trim(),
                adminPhone = adminPhone.trim(),
                adminDesignation = adminDesignation.trim(),
                adminRoleBadge = adminRoleBadge.trim(),
                shopName = shopName.trim(),
                shopPhone = shopPhone.trim(),
                shopAddress = shopAddress.trim()
            )
            onSaved()
        }
    }

    fun updatePrivacyPolicySettings(
        privacyPolicyText: String,
        termsOfServiceText: String,
        warrantyTermsText: String,
        policyEffectiveDate: String,
        policyVersion: String,
        showPolicyOnPortal: Boolean,
        onSaved: () -> Unit = {}
    ) {
        viewModelScope.launch {
            userPreferences.updatePrivacyPolicySettings(
                privacyPolicyText = privacyPolicyText.trim(),
                termsOfServiceText = termsOfServiceText.trim(),
                warrantyTermsText = warrantyTermsText.trim(),
                policyEffectiveDate = policyEffectiveDate.trim(),
                policyVersion = policyVersion.trim(),
                showPolicyOnPortal = showPolicyOnPortal
            )
            onSaved()
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.setThemeMode(mode)
        }
    }

    fun setAppLanguage(language: com.example.i18n.AppLanguage) {
        viewModelScope.launch {
            userPreferences.setAppLanguage(language)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
        }
    }

    fun setVoiceReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setVoiceReminderEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setSoundEnabled(enabled)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setVibrationEnabled(enabled)
        }
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportDatabaseToJson()
            onResult(json)
        }
    }

    fun restoreBackup(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importDatabaseFromJson(json)
            onResult(success)
        }
    }

    fun clearAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllData()
            onDone()
        }
    }

    fun restoreDefaultData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.restoreDefaultData()
            onDone()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.reset()
        audioPlayer.release()
    }
}

class GsmViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GsmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GsmViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
