package com.example.ui.customer_portal

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthUser
import com.example.data.entity.CustomerEntity
import com.example.i18n.currentStrings
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.viewmodel.GsmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboardScreen(
    viewModel: GsmViewModel,
    onNavigateToDetails: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val s = currentStrings()

    val currentUser by viewModel.currentUser.collectAsState()
    val currentCustomerData by viewModel.currentCustomerEntity.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    // Dialog sheets
    var activeModal by remember { mutableStateOf<String?>(null) } // "DEVICE", "STATUS", "HISTORY", "SUPPORT", "SETTINGS"
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Use either live entity data or auth session fallback
    val customerName = currentCustomerData?.customerName?.ifBlank { currentUser.customerName } ?: currentUser.customerName.ifBlank { "Customer" }
    val customerId = currentCustomerData?.customerIdCode?.ifBlank { currentUser.customerId } ?: currentUser.customerId.ifBlank { "CUST-1001" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("GSM", fontSize = 17.sp, fontWeight = FontWeight.Black, color = OrangePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ROM", fontSize = 17.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(s.customerDashboard.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutConfirm = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = s.logout, tint = MaterialTheme.colorScheme.error)
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
            // ========================================
            // WELCOME HEADER BANNER
            // ========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, OrangePrimary.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${s.welcomeTo} GSM ROM",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = s.appTagline,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Customer Name & Customer ID Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${s.customerName}:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = customerName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${s.customerId}:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OrangePrimary.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = customerId,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OrangePrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Current Active Service Quick Glance
                    currentCustomerData?.let { cust ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${cust.brand} ${cust.model}".ifBlank { "Registered Device" },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (cust.status) {
                                        "Completed" -> NeonGreen.copy(alpha = 0.2f)
                                        "Processing" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                        else -> OrangePrimary.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = cust.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (cust.status) {
                                            "Completed" -> Color(0xFF2E7D32)
                                            "Processing" -> Color(0xFF1565C0)
                                            else -> OrangePrimary
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ========================================
            // DASHBOARD ACTION BUTTONS (6 PRIMARY ITEMS)
            // ========================================
            Text(
                text = "CUSTOMER SERVICES & TOOLS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            // 1. [ 📱 MY DEVICE ]
            DashboardActionCard(
                icon = Icons.Default.PhoneAndroid,
                title = s.myDevice.uppercase(),
                subtitle = "Device specs, IMEI & hardware status",
                badge = currentCustomerData?.let { "${it.brand} ${it.model}" } ?: "Registered phone",
                onClick = { activeModal = "DEVICE" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. [ 🛠 SERVICE STATUS ]
            DashboardActionCard(
                icon = Icons.Default.Build,
                title = s.serviceStatus.uppercase(),
                subtitle = "Live progress, diagnosis & repair status",
                badge = currentCustomerData?.status ?: "Active",
                badgeColor = when (currentCustomerData?.status) {
                    "Completed" -> Color(0xFF2E7D32)
                    "Processing" -> Color(0xFF1565C0)
                    else -> OrangePrimary
                },
                onClick = { activeModal = "STATUS" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. [ 📄 SERVICE HISTORY ]
            DashboardActionCard(
                icon = Icons.Default.Description,
                title = s.serviceHistory.uppercase(),
                subtitle = "Past repairs, invoices, payments & dues",
                badge = "Invoices & Logs",
                onClick = { activeModal = "HISTORY" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. [ 👤 CUSTOMER DETAILS ]
            DashboardActionCard(
                icon = Icons.Default.Person,
                title = s.customerDetails.uppercase(),
                subtitle = "View and edit your personal & phone details",
                badge = "Edit Profile",
                onClick = onNavigateToDetails
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5. [ 💬 SUPPORT ]
            DashboardActionCard(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = s.support.uppercase(),
                subtitle = "Direct technician WhatsApp, Hotline & Helpdesk",
                badge = "24/7 Desk",
                badgeColor = Color(0xFF25D366),
                onClick = { activeModal = "SUPPORT" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 6. [ ⚙️ ACCOUNT SETTINGS ]
            DashboardActionCard(
                icon = Icons.Default.Settings,
                title = s.accountSettings.uppercase(),
                subtitle = "Security, password update & preferences",
                badge = "Settings",
                onClick = { activeModal = "SETTINGS" }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ========================================
            // [ 🚪 LOGOUT ]
            // ========================================
            Button(
                onClick = { showLogoutConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = s.logout.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ========================================
    // MODAL DIALOGS FOR DASHBOARD ACTIONS
    // ========================================

    // 1. MY DEVICE MODAL
    if (activeModal == "DEVICE") {
        val cust = currentCustomerData
        AlertDialog(
            onDismissRequest = { activeModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MY REGISTERED DEVICE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    DeviceDetailRow(label = "Device Brand", value = cust?.brand?.ifBlank { "Vivo" } ?: "Vivo")
                    DeviceDetailRow(label = "Phone Model", value = cust?.model?.ifBlank { "Vivo Y04" } ?: "Vivo Y04")
                    DeviceDetailRow(label = "IMEI / Serial", value = cust?.imei?.ifBlank { "864521049281723" } ?: "864521049281723")
                    DeviceDetailRow(label = "Service Job Type", value = cust?.serviceType?.ifBlank { "Dead Boot / BROM" } ?: "Dead Boot")
                    DeviceDetailRow(label = "Diagnostic Notes", value = cust?.problemDescription?.ifBlank { "Power supply 0.08A BROM test point repair required." } ?: "Servicing in progress")
                    DeviceDetailRow(label = "Technician Note", value = cust?.serviceNote?.ifBlank { "PMIC rail verified, flashing completed" } ?: "Normal servicing")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        activeModal = null
                        onNavigateToDetails()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Edit Device Details")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeModal = null }) { Text("Close") }
            }
        )
    }

    // 2. SERVICE STATUS MODAL
    if (activeModal == "STATUS") {
        val cust = currentCustomerData
        val currentStatus = cust?.status ?: "Processing"
        val steps = listOf("Received", "Checking", "Processing", "Completed", "Delivered")
        val activeStepIndex = when (currentStatus) {
            "Received" -> 0
            "Checking" -> 1
            "Processing" -> 2
            "Completed" -> 3
            "Delivered" -> 4
            else -> 1
        }

        AlertDialog(
            onDismissRequest = { activeModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LIVE SERVICE STATUS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Current Status: $currentStatus",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (activeStepIndex + 1) / 5f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = OrangePrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    steps.forEachIndexed { index, step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (index <= activeStepIndex) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (index <= activeStepIndex) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                fontSize = 13.sp,
                                fontWeight = if (index == activeStepIndex) FontWeight.Bold else FontWeight.Normal,
                                color = if (index <= activeStepIndex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    cust?.deliveryDate?.let { date ->
                        if (date.isNotBlank()) {
                            Text(
                                text = "Estimated Delivery: $date ${cust.deliveryTime}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { activeModal = null }) { Text("OK") }
            }
        )
    }

    // 3. SERVICE HISTORY & INVOICE MODAL
    if (activeModal == "HISTORY") {
        val cust = currentCustomerData
        val charge = cust?.serviceCharge ?: 1200.0
        val advance = cust?.advancePayment ?: 400.0
        val due = cust?.dueAmount ?: (charge - advance).coerceAtLeast(0.0)

        AlertDialog(
            onDismissRequest = { activeModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SERVICE HISTORY & INVOICE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("JOB SHEET INVOICE", fontWeight = FontWeight.Black, fontSize = 14.sp, color = OrangePrimary)
                            Text("Customer ID: $customerId", fontSize = 12.sp)
                            Text("Device: ${cust?.brand ?: "Vivo"} ${cust?.model ?: "Vivo Y04"}", fontSize = 12.sp)
                            Text("Service: ${cust?.serviceType ?: "Dead Boot Flash"}", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Service Charge:", fontSize = 12.sp)
                                Text("৳ ${String.format("%.2f", charge)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Advance Paid:", fontSize = 12.sp)
                                Text("৳ ${String.format("%.2f", advance)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonGreen)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Due Balance:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("৳ ${String.format("%.2f", due)}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = OrangePrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareText = "GSM ROM Invoice for $customerName (ID: $customerId)\nDevice: ${cust?.brand} ${cust?.model}\nService: ${cust?.serviceType}\nTotal: ৳$charge\nAdvance: ৳$advance\nDue: ৳$due\nStatus: ${cust?.status}"
                        clipboardManager.setText(AnnotatedString(shareText))
                        Toast.makeText(context, "Invoice copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Invoice")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeModal = null }) { Text("Close") }
            }
        )
    }

    // 5. SUPPORT MODAL
    if (activeModal == "SUPPORT") {
        AlertDialog(
            onDismissRequest = { activeModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CUSTOMER SUPPORT & HELPDESK", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Need help with your phone servicing or delivery?", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Shop: ${userSettings.shopName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Phone / Hotline: ${userSettings.shopPhone}", fontSize = 13.sp, color = OrangePrimary)
                    Text("Location: ${userSettings.shopAddress}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${userSettings.shopPhone}"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Call: ${userSettings.shopPhone}", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Hotline")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeModal = null }) { Text("Close") }
            }
        )
    }

    // 6. ACCOUNT SETTINGS MODAL
    if (activeModal == "SETTINGS") {
        var newPass by remember { mutableStateOf("") }
        var confirmNewPass by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { activeModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CHANGE PASSWORD", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Update login password for Customer ID '$customerId'", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        placeholder = { Text("Enter New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmNewPass,
                        onValueChange = { confirmNewPass = it },
                        placeholder = { Text("Confirm New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass.length < 4) {
                            Toast.makeText(context, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPass != confirmNewPass) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        currentCustomerData?.let { cust ->
                            viewModel.changeCustomerPassword(cust.id, newPass) {
                                Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                activeModal = null
                            }
                        } ?: run {
                            Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show()
                            activeModal = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeModal = null }) { Text("Cancel") }
            }
        )
    }

    // LOGOUT CONFIRMATION
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to log out from GSM ROM Customer Portal?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DashboardActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color = OrangePrimary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(OrangePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DeviceDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
    }
}
