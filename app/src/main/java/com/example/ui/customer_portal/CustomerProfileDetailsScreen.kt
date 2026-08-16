package com.example.ui.customer_portal

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomerEntity
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.viewmodel.GsmViewModel

private val BRAND_LIST = listOf(
    "Samsung", "Vivo", "Xiaomi", "Redmi", "POCO", "OPPO", "Realme",
    "OnePlus", "Tecno", "Infinix", "Huawei", "Honor", "Motorola", "Nokia", "IQOO", "Other"
)

private val SERVICE_TYPE_LIST = listOf(
    "Hardware Repair",
    "Software & Flashing",
    "Display / LCD Replacement",
    "Charging Port / Ribbon",
    "Battery Replacement",
    "Water Damage Recovery",
    "Dead Boot / BROM",
    "FRP / Google Account Unlock",
    "Network / Baseband / Signal",
    "Audio / Speaker / Mic",
    "Camera Module Replacement",
    "Other Servicing"
)

private val STATUS_LIST = listOf("Pending", "Processing", "Completed")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileDetailsScreen(
    viewModel: GsmViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val currentCustomerData by viewModel.currentCustomerEntity.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerId by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Vivo") }
    var model by remember { mutableStateOf("") }
    var imei by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("Software & Flashing") }
    var status by remember { mutableStateOf("Processing") }
    var serviceNote by remember { mutableStateOf("") }

    var brandExpanded by remember { mutableStateOf(false) }
    var serviceExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(currentCustomerData, currentUser) {
        val cust = currentCustomerData
        if (cust != null) {
            customerName = cust.customerName
            customerId = cust.customerIdCode.ifBlank { "CUST-${cust.id}" }
            mobileNumber = cust.mobileNumber
            email = cust.gmail
            brand = cust.brand.ifBlank { "Vivo" }
            model = cust.model
            imei = cust.imei
            serviceType = cust.serviceType.ifBlank { "Software & Flashing" }
            status = cust.status.ifBlank { "Processing" }
            serviceNote = cust.serviceNote
        } else {
            customerName = currentUser.customerName
            customerId = currentUser.customerId.ifBlank { "CUST-1001" }
            mobileNumber = currentUser.mobileNumber
            email = currentUser.email
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CUSTOMER DETAILS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header Subtitle
                    Text(
                        text = "ACCOUNT & SERVICING PROFILE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                    Text(
                        text = "Fill in or update your device details and servicing record.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 1. Customer Name
                    Text("Customer Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        placeholder = { Text("Enter Customer Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Mobile Number
                    Text("Mobile Number", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        placeholder = { Text("Enter Mobile Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Email
                    Text("Email", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. Device Brand (Dropdown)
                    Text("Device Brand", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuBox(
                        expanded = brandExpanded,
                        onExpandedChange = { brandExpanded = !brandExpanded }
                    ) {
                        OutlinedTextField(
                            value = brand,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = brandExpanded,
                            onDismissRequest = { brandExpanded = false }
                        ) {
                            BRAND_LIST.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b) },
                                    onClick = {
                                        brand = b
                                        brandExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 6. Phone Model
                    Text("Phone Model", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        placeholder = { Text("e.g. Vivo Y21 / Galaxy A23") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 7. IMEI / Serial
                    Text("IMEI / Serial", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = imei,
                        onValueChange = { imei = it },
                        placeholder = { Text("15-digit IMEI number") },
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = OrangePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 8. Service Type (Dropdown)
                    Text("Service Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    ExposedDropdownMenuBox(
                        expanded = serviceExpanded,
                        onExpandedChange = { serviceExpanded = !serviceExpanded }
                    ) {
                        OutlinedTextField(
                            value = serviceType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = serviceExpanded,
                            onDismissRequest = { serviceExpanded = false }
                        ) {
                            SERVICE_TYPE_LIST.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        serviceType = s
                                        serviceExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 9. Service Status
                    Text("Service Status", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        STATUS_LIST.forEach { st ->
                            val isSelected = status.equals(st, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { status = st },
                                label = { Text(st, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (st) {
                                        "Completed" -> NeonGreen.copy(alpha = 0.25f)
                                        "Processing" -> Color(0xFF2196F3).copy(alpha = 0.25f)
                                        else -> OrangePrimary.copy(alpha = 0.25f)
                                    },
                                    selectedLabelColor = when (st) {
                                        "Completed" -> Color(0xFF2E7D32)
                                        "Processing" -> Color(0xFF1565C0)
                                        else -> OrangePrimary
                                    }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 10. Service Note
                    Text("Service Note", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = serviceNote,
                        onValueChange = { serviceNote = it },
                        placeholder = { Text("Details or remarks on phone condition / parts...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // [ 💾 SAVE DETAILS ]
                    Button(
                        onClick = {
                            val cust = currentCustomerData
                            if (cust != null) {
                                val updated = cust.copy(
                                    customerName = customerName.trim(),
                                    customerIdCode = customerId.trim(),
                                    mobileNumber = mobileNumber.trim(),
                                    gmail = email.trim(),
                                    brand = brand.trim(),
                                    model = model.trim(),
                                    imei = imei.trim(),
                                    serviceType = serviceType,
                                    status = status,
                                    serviceNote = serviceNote.trim()
                                )
                                viewModel.updateCustomerDetails(updated) {
                                    Toast.makeText(context, "Customer details saved successfully!", Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                }
                            } else {
                                viewModel.saveCustomer(
                                    customerName = customerName,
                                    mobileNumber = mobileNumber,
                                    gmail = email,
                                    address = "",
                                    brand = brand,
                                    model = model,
                                    imei = imei,
                                    serviceType = serviceType,
                                    problemDescription = serviceNote,
                                    serviceCharge = 1000.0,
                                    advancePayment = 0.0,
                                    deliveryDate = "",
                                    deliveryTime = "",
                                    deliveryTimestamp = 0L,
                                    status = status,
                                    voiceFilePath = null,
                                    voiceDurationMs = 0L,
                                    onSaved = {
                                        Toast.makeText(context, "Details saved!", Toast.LENGTH_SHORT).show()
                                        onNavigateBack()
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE DETAILS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
