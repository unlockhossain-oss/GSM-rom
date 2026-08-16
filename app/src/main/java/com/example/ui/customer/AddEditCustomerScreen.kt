package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomerEntity
import com.example.ui.components.AppDatePickerDialog
import com.example.ui.components.VoiceRecorderCard
import com.example.ui.components.showAppTimePicker
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.viewmodel.GsmViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val SERVICE_TYPES = listOf(
    "Hardware Repair",
    "Software & Flashing",
    "Display / LCD Replacement",
    "Charging Port / Ribbon",
    "Battery Replacement",
    "Water Damage Recovery",
    "Dead Phone Troubleshooting",
    "FRP / Google Account Unlock",
    "Network / Baseband / Signal",
    "Audio / Speaker / Mic",
    "Camera Module Replacement",
    "Back Glass / Body Housing",
    "Other Servicing"
)

val BRAND_OPTIONS = listOf(
    "Samsung", "Vivo", "Xiaomi", "Redmi", "POCO", "OPPO", "Realme",
    "OnePlus", "Tecno", "Infinix", "Huawei", "Honor", "Motorola", "Nokia", "IQOO", "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerScreen(
    viewModel: GsmViewModel,
    customerId: Long? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var customerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var brand by remember { mutableStateOf("Vivo") }
    var model by remember { mutableStateOf("") }
    var imei by remember { mutableStateOf("") }

    var serviceType by remember { mutableStateOf(SERVICE_TYPES[0]) }
    var problemDescription by remember { mutableStateOf("") }

    var serviceChargeText by remember { mutableStateOf("1500") }
    var advancePaymentText by remember { mutableStateOf("500") }

    // Delivery time calculations
    val defaultDeliveryCal = remember {
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }

    var deliveryDate by remember {
        mutableStateOf(SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).format(defaultDeliveryCal.time))
    }
    var deliveryTime by remember {
        mutableStateOf("05:00 PM")
    }
    var deliveryTimestamp by remember {
        mutableLongStateOf(defaultDeliveryCal.timeInMillis)
    }

    var status by remember { mutableStateOf("Received") }
    var voiceFilePath by remember { mutableStateOf<String?>(null) }
    var voiceDurationMs by remember { mutableLongStateOf(0L) }

    var showDatePicker by remember { mutableStateOf(false) }
    var brandExpanded by remember { mutableStateOf(false) }
    var serviceTypeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Load existing customer if editing
    LaunchedEffect(customerId) {
        if (customerId != null && customerId > 0L) {
            val existing = viewModel.customersList.value.find { it.id == customerId }
            if (existing != null) {
                customerName = existing.customerName
                mobileNumber = existing.mobileNumber
                gmail = existing.gmail
                address = existing.address
                brand = existing.brand
                model = existing.model
                imei = existing.imei
                serviceType = existing.serviceType
                problemDescription = existing.problemDescription
                serviceChargeText = existing.serviceCharge.toInt().toString()
                advancePaymentText = existing.advancePayment.toInt().toString()
                deliveryDate = existing.deliveryDate
                deliveryTime = existing.deliveryTime
                deliveryTimestamp = existing.deliveryTimestamp
                status = existing.status
                voiceFilePath = existing.voiceFilePath
                voiceDurationMs = existing.voiceDurationMs
            }
        }
    }

    val charge = serviceChargeText.toDoubleOrNull() ?: 0.0
    val advance = advancePaymentText.toDoubleOrNull() ?: 0.0
    val calculatedDue = (charge - advance).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (customerId != null && customerId > 0L) "Edit Customer Job" else "New Customer Job Sheet") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Section 1: Customer Contact Info
            item {
                FormSectionCard(title = "1. Customer Information") {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Full Name *", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile / WhatsApp Number *", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = gmail,
                        onValueChange = { gmail = it },
                        label = { Text("Gmail / Email Address (Optional)", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Customer Address (Optional)", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            }

            // Section 2: Device Details
            item {
                FormSectionCard(title = "2. Device & Problem Specifications") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = brandExpanded,
                            onExpandedChange = { brandExpanded = !brandExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = brand,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Brand *", fontSize = 12.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = brandExpanded,
                                onDismissRequest = { brandExpanded = false }
                            ) {
                                BRAND_OPTIONS.forEach { b ->
                                    DropdownMenuItem(
                                        text = { Text(b, fontSize = 12.sp) },
                                        onClick = {
                                            brand = b
                                            brandExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Model Name *", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = imei,
                        onValueChange = { imei = it },
                        label = { Text("IMEI / Serial Number (Optional)", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = serviceTypeExpanded,
                        onExpandedChange = { serviceTypeExpanded = !serviceTypeExpanded }
                    ) {
                        OutlinedTextField(
                            value = serviceType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Service Type / Job Category *", fontSize = 12.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceTypeExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = serviceTypeExpanded,
                            onDismissRequest = { serviceTypeExpanded = false }
                        ) {
                            SERVICE_TYPES.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st, fontSize = 12.sp) },
                                    onClick = {
                                        serviceType = st
                                        serviceTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = problemDescription,
                        onValueChange = { problemDescription = it },
                        label = { Text("Problem Description & Notes *", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 2
                    )
                }
            }

            // Section 3: Financial & Delivery Date/Time
            item {
                FormSectionCard(title = "3. Payment & Delivery Schedule") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = serviceChargeText,
                            onValueChange = { serviceChargeText = it },
                            label = { Text("Charge (৳)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = advancePaymentText,
                            onValueChange = { advancePaymentText = it },
                            label = { Text("Advance (৳)", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Due Amount display card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(OrangePrimary.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Calculated Due Amount:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "৳${calculatedDue.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (calculatedDue > 0) OrangePrimary else NeonGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date & Time pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Date Picker Button
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable { showDatePicker = true }
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Delivery Date", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(deliveryDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // Time Picker Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable {
                                    showAppTimePicker(context) { formattedTime, hour, min ->
                                        deliveryTime = formattedTime
                                        // recalculate timestamp
                                        val cal = Calendar.getInstance().apply {
                                            timeInMillis = deliveryTimestamp
                                            set(Calendar.HOUR_OF_DAY, hour)
                                            set(Calendar.MINUTE, min)
                                            set(Calendar.SECOND, 0)
                                        }
                                        deliveryTimestamp = cal.timeInMillis
                                    }
                                }
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Time", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(deliveryTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Dropdown
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Initial Service Status", fontSize = 12.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            CUSTOMER_STATUS_LIST.filter { it != "All" }.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st, fontSize = 12.sp) },
                                    onClick = {
                                        status = st
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Voice Note Recording
            item {
                FormSectionCard(title = "4. Voice Memo & Customer Audio") {
                    VoiceRecorderCard(
                        audioRecorder = viewModel.audioRecorder,
                        audioPlayer = viewModel.audioPlayer,
                        existingVoicePath = voiceFilePath,
                        onVoiceSaved = { path, duration ->
                            voiceFilePath = path
                            voiceDurationMs = duration
                        }
                    )
                }
            }

            // Section 5: Submit Button
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (customerName.isNotBlank() && model.isNotBlank()) {
                            viewModel.saveCustomer(
                                id = customerId ?: 0L,
                                customerName = customerName,
                                mobileNumber = mobileNumber,
                                gmail = gmail,
                                address = address,
                                brand = brand,
                                model = model,
                                imei = imei,
                                serviceType = serviceType,
                                problemDescription = problemDescription,
                                serviceCharge = charge,
                                advancePayment = advance,
                                deliveryDate = deliveryDate,
                                deliveryTime = deliveryTime,
                                deliveryTimestamp = deliveryTimestamp,
                                status = status,
                                voiceFilePath = voiceFilePath,
                                voiceDurationMs = voiceDurationMs,
                                onSaved = {
                                    onNavigateBack()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    enabled = customerName.isNotBlank() && model.isNotBlank()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (customerId != null && customerId > 0L) "Update Job Card" else "Create Customer Job Sheet",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Date Picker Modal
        if (showDatePicker) {
            AppDatePickerDialog(
                initialDateMillis = deliveryTimestamp,
                onDateSelected = { formattedDate, millis ->
                    deliveryDate = formattedDate
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = millis
                        // Keep current hour/minute
                        val prevCal = Calendar.getInstance().apply { timeInMillis = deliveryTimestamp }
                        set(Calendar.HOUR_OF_DAY, prevCal.get(Calendar.HOUR_OF_DAY))
                        set(Calendar.MINUTE, prevCal.get(Calendar.MINUTE))
                    }
                    deliveryTimestamp = cal.timeInMillis
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@Composable
private fun FormSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
