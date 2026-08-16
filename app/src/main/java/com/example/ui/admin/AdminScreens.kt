package com.example.ui.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthResult
import com.example.data.entity.CustomerEntity
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.viewmodel.GsmViewModel

// ============================================================================
// ADMIN LOGIN SCREEN
// ============================================================================
@Composable
fun AdminLoginScreen(
    viewModel: GsmViewModel,
    onLoginSuccess: () -> Unit,
    onBackToCustomerLogin: () -> Unit
) {
    val context = LocalContext.current
    var adminId by remember { mutableStateOf("admin") }
    var adminPassword by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row {
                                Text("GSM", fontSize = 20.sp, fontWeight = FontWeight.Black, color = OrangePrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ROM", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Text("MOBILE SERVICING SOLUTION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Admin Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, OrangePrimary.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ADMIN LOGIN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangePrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Technician and Service Control Center",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                    )

                    errorMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Admin ID
                    Text("Admin ID", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = adminId,
                        onValueChange = {
                            adminId = it
                            errorMessage = null
                        },
                        placeholder = { Text("Enter Admin ID (default: admin)") },
                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = OrangePrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password
                    Text("Password", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = {
                            adminPassword = it
                            errorMessage = null
                        },
                        placeholder = { Text("Enter Admin Password (default: admin123)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = OrangePrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // [ 🔐 ADMIN LOGIN ]
                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            viewModel.loginAdmin(adminId, adminPassword) { result ->
                                isLoading = false
                                when (result) {
                                    is AuthResult.Success -> {
                                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                        onLoginSuccess()
                                    }
                                    is AuthResult.Error -> {
                                        errorMessage = result.message
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifying...")
                        } else {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ADMIN LOGIN", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 0.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TextButton(onClick = onBackToCustomerLogin) {
                        Text("← Back to Customer Portal", fontSize = 12.sp, color = OrangePrimary)
                    }
                }
            }
        }
    }
}

// ============================================================================
// ADMIN DASHBOARD SCREEN
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: GsmViewModel,
    onNavigateToCustomerManagement: () -> Unit,
    onNavigateToAddCustomer: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToDiagrams: () -> Unit,
    onNavigateToFiles: () -> Unit,
    onNavigateToLcd: () -> Unit,
    onNavigateToModels: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                        Text("ADMINISTRATION SYSTEM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
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
                .padding(16.dp)
        ) {
            // Header greeting
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Welcome Admin", fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Text("Full administrative & technician privileges active", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("ADMIN MODULES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            // [ 👥 CUSTOMER MANAGEMENT ]
            AdminNavCard(
                icon = Icons.Default.People,
                title = "CUSTOMER MANAGEMENT",
                subtitle = "Manage accounts, block/unblock, reset passwords",
                badge = "Full Control",
                onClick = onNavigateToCustomerManagement
            )

            Spacer(modifier = Modifier.height(10.dp))

            // [ ➕ ADD CUSTOMER ]
            AdminNavCard(
                icon = Icons.Default.Add,
                title = "ADD NEW CUSTOMER JOB",
                subtitle = "Register service ticket, record problem & audio",
                badge = "+ Create Ticket",
                badgeColor = NeonGreen,
                onClick = onNavigateToAddCustomer
            )

            Spacer(modifier = Modifier.height(10.dp))

            // [ 📱 DEVICE MANAGEMENT ]
            AdminNavCard(
                icon = Icons.Default.PhoneAndroid,
                title = "DEVICE MANAGEMENT",
                subtitle = "Service status, repair jobs, hardware logs",
                badge = "Services",
                onClick = onNavigateToDevices
            )

            Spacer(modifier = Modifier.height(10.dp))

            // [ 📊 DIAGRAM MANAGEMENT ]
            AdminNavCard(
                icon = Icons.Default.Build,
                title = "DIAGRAM MANAGEMENT",
                subtitle = "Hardware schematics, charging, power & LCD lines",
                badge = "Schematics",
                onClick = onNavigateToDiagrams
            )

            Spacer(modifier = Modifier.height(10.dp))

            // [ 📁 FILE MANAGEMENT ]
            AdminNavCard(
                icon = Icons.Default.Folder,
                title = "FILE MANAGEMENT",
                subtitle = "ROM firmwares, scatter files, FRP dump & modems",
                badge = "Firmwares",
                onClick = onNavigateToFiles
            )

            Spacer(modifier = Modifier.height(10.dp))

            // [ 📱 LCD MANAGEMENT ]
            AdminNavCard(
                icon = Icons.Default.Tv,
                title = "LCD MANAGEMENT",
                subtitle = "Display compatibility, pinouts, test points",
                badge = "Displays",
                onClick = onNavigateToLcd
            )

            Spacer(modifier = Modifier.height(10.dp))

            // [ 📝 NAME / MODEL MANAGEMENT ]
            AdminNavCard(
                icon = Icons.Default.PhoneAndroid,
                title = "NAME / MODEL MANAGEMENT",
                subtitle = "Chipsets, CPU specs, brand model directory",
                badge = "Hardware Specs",
                onClick = onNavigateToModels
            )

            Spacer(modifier = Modifier.height(10.dp))

            // [ ⚙️ SETTINGS ]
            AdminNavCard(
                icon = Icons.Default.Settings,
                title = "SETTINGS",
                subtitle = "Admin credentials, backup export/restore, shop details",
                badge = "Config",
                onClick = onNavigateToSettings
            )

            Spacer(modifier = Modifier.height(20.dp))

            // [ 🚪 LOGOUT ]
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOGOUT ADMIN", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout Admin") },
            text = { Text("Are you sure you want to end Admin session?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ============================================================================
// ADMIN CUSTOMER MANAGEMENT SCREEN
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCustomerManagementScreen(
    viewModel: GsmViewModel,
    onNavigateToAddCustomer: () -> Unit,
    onNavigateToEditCustomer: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val customers by viewModel.customersList.collectAsState()
    val searchQuery by viewModel.customerSearchQuery.collectAsState()
    val selectedFilter by viewModel.selectedStatusFilter.collectAsState()

    var customerToChangePass by remember { mutableStateOf<CustomerEntity?>(null) }
    var customerToDelete by remember { mutableStateOf<CustomerEntity?>(null) }
    var newPasswordInput by remember { mutableStateOf("") }

    val filterOptions = listOf("All", "Pending", "Processing", "Completed", "Delivered")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CUSTOMER MANAGEMENT", fontSize = 16.sp, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddCustomer) {
                        Icon(Icons.Default.Add, contentDescription = "Add Customer", tint = OrangePrimary)
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setCustomerSearchQuery(it) },
                placeholder = { Text("Search by name, ID, phone, model, IMEI...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = selectedFilter.equals(filter, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStatusFilter(filter) },
                        label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Total Customers: ${customers.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(customers, key = { it.id }) { customer ->
                    AdminCustomerCard(
                        customer = customer,
                        onEdit = { onNavigateToEditCustomer(customer.id) },
                        onChangePassword = {
                            customerToChangePass = customer
                            newPasswordInput = ""
                        },
                        onToggleBlock = {
                            val newBlockState = !customer.isBlocked
                            viewModel.blockCustomer(customer.id, newBlockState)
                            Toast.makeText(
                                context,
                                if (newBlockState) "Customer '${customer.customerName}' BLOCKED" else "Customer '${customer.customerName}' UNBLOCKED",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDelete = { customerToDelete = customer }
                    )
                }
            }
        }
    }

    // Change Password Dialog
    customerToChangePass?.let { cust ->
        AlertDialog(
            onDismissRequest = { customerToChangePass = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Customer Password", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Update password for ${cust.customerName} (${cust.customerIdCode.ifBlank { "CUST-${cust.id}" }})", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        placeholder = { Text("Enter New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasswordInput.length < 4) {
                            Toast.makeText(context, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.changeCustomerPassword(cust.id, newPasswordInput) {
                            Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                            customerToChangePass = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Save Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToChangePass = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Confirmation Dialog
    customerToDelete?.let { cust ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("Delete Customer Record") },
            text = { Text("Are you sure you want to permanently delete '${cust.customerName}' (${cust.brand} ${cust.model})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(cust)
                        Toast.makeText(context, "Customer record deleted", Toast.LENGTH_SHORT).show()
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminCustomerCard(
    customer: CustomerEntity,
    onEdit: () -> Unit,
    onChangePassword: () -> Unit,
    onToggleBlock: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (customer.isBlocked) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (customer.isBlocked) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = customer.customerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (customer.isBlocked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.error
                            ) {
                                Text(
                                    text = "BLOCKED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "ID: ${customer.customerIdCode.ifBlank { "CUST-${customer.id}" }}  •  ${customer.mobileNumber.ifBlank { customer.gmail }}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (customer.status) {
                        "Completed" -> NeonGreen.copy(alpha = 0.2f)
                        "Processing" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                        else -> OrangePrimary.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = customer.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (customer.status) {
                            "Completed" -> Color(0xFF2E7D32)
                            "Processing" -> Color(0xFF1565C0)
                            else -> OrangePrimary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Device & Problem
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Device: ${customer.brand} ${customer.model}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Due: ৳${String.format("%.0f", customer.dueAmount)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (customer.dueAmount > 0) OrangePrimary else NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row: Edit | Change Password | Block/Unblock | Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Edit
                OutlinedButton(
                    onClick = onEdit,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 11.sp)
                }

                // 2. Change Password
                OutlinedButton(
                    onClick = onChangePassword,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Password", fontSize = 11.sp)
                }

                // 3. Block / Unblock Toggle
                Button(
                    onClick = onToggleBlock,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (customer.isBlocked) NeonGreen else MaterialTheme.colorScheme.errorContainer,
                        contentColor = if (customer.isBlocked) Color.Black else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = if (customer.isBlocked) Icons.Default.Check else Icons.Default.Block,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (customer.isBlocked) "Unblock" else "Block", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // 4. Delete
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminNavCard(
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(OrangePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    Surface(shape = RoundedCornerShape(4.dp), color = badgeColor.copy(alpha = 0.15f)) {
                        Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
