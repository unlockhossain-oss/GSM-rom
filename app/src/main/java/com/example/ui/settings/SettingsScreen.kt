package com.example.ui.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomerEntity
import com.example.i18n.AppLanguage
import com.example.i18n.currentStrings
import com.example.preferences.ThemeMode
import com.example.preferences.UserSettings
import com.example.ui.theme.DarkCrimson
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.SapphireBlue
import com.example.viewmodel.GsmViewModel

enum class SettingsSection(val title: String, val subtitle: String, val icon: ImageVector) {
    CUSTOMER_AUTH("Customer Sign-In", "Sign-in, rules & accounts", Icons.Default.People),
    ADMIN_DETAILS("Admin Details", "Identity, security & shop", Icons.Default.AdminPanelSettings),
    PRIVACY_POLICY("Privacy & Policy", "Terms, warranty & data", Icons.Default.Policy),
    SYSTEM_PREFERENCES("System & General", "Theme, backup & sound", Icons.Default.Tune)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GsmViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val s = currentStrings()
    val userSettings by viewModel.userSettings.collectAsState()
    val allCustomers by viewModel.customersList.collectAsState()

    var selectedSection by remember { mutableStateOf(SettingsSection.CUSTOMER_AUTH) }

    // Dialogs for System & Preferences
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    // Customer Accounts management in Section 1
    var editingCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var showAddCustomerDialog by remember { mutableStateOf(false) }

    // Policy preview in Section 3
    var showPolicyPreviewDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Settings & Customization",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Control Portal, Admin Identity & Legal Policies",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OrangePrimary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ADMIN PANEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Section Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsSection.entries.forEach { section ->
                    val isSelected = selectedSection == section
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surface,
                        label = "tab_bg"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        label = "tab_content"
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = bgColor,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedSection = section }
                            .testTag("settings_tab_${section.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = section.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = contentColor
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Main Tab Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedSection) {
                    SettingsSection.CUSTOMER_AUTH -> {
                        CustomerSignInSettingsTab(
                            userSettings = userSettings,
                            customersList = allCustomers,
                            onSave = { portalTitle, notice, defaultPass, allowReg, allowGoogle, reqPhone, prefix, supportPhone, supportEmail ->
                                viewModel.updateCustomerSignInSettings(
                                    portalTitle = portalTitle,
                                    loginNotice = notice,
                                    defaultPass = defaultPass,
                                    allowRegistration = allowReg,
                                    allowGoogle = allowGoogle,
                                    requirePhone = reqPhone,
                                    idPrefix = prefix,
                                    supportPhone = supportPhone,
                                    supportEmail = supportEmail
                                ) {
                                    Toast.makeText(context, "Customer Sign-In Settings saved successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEditCustomer = { cust -> editingCustomer = cust },
                            onAddNewCustomer = { showAddCustomerDialog = true },
                            onToggleBlock = { cust ->
                                val newBlocked = !cust.isBlocked
                                viewModel.blockCustomer(cust.id, newBlocked)
                                Toast.makeText(
                                    context,
                                    if (newBlocked) "Customer account blocked" else "Customer account activated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }

                    SettingsSection.ADMIN_DETAILS -> {
                        AdminDetailsSettingsTab(
                            userSettings = userSettings,
                            onSave = { name, id, pass, email, phone, designation, badge, shopName, shopPhone, shopAddress ->
                                viewModel.updateAdminProfile(
                                    adminName = name,
                                    adminId = id,
                                    adminPass = pass,
                                    adminEmail = email,
                                    adminPhone = phone,
                                    adminDesignation = designation,
                                    adminRoleBadge = badge,
                                    shopName = shopName,
                                    shopPhone = shopPhone,
                                    shopAddress = shopAddress
                                ) {
                                    Toast.makeText(context, "Administrator profile & credentials updated!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    SettingsSection.PRIVACY_POLICY -> {
                        PrivacyPolicySettingsTab(
                            userSettings = userSettings,
                            onSave = { privacyText, termsText, warrantyText, date, version, showOnPortal ->
                                viewModel.updatePrivacyPolicySettings(
                                    privacyPolicyText = privacyText,
                                    termsOfServiceText = termsText,
                                    warrantyTermsText = warrantyText,
                                    policyEffectiveDate = date,
                                    policyVersion = version,
                                    showPolicyOnPortal = showOnPortal
                                ) {
                                    Toast.makeText(context, "Privacy & Service Policies published successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onPreview = { showPolicyPreviewDialog = true }
                        )
                    }

                    SettingsSection.SYSTEM_PREFERENCES -> {
                        SystemPreferencesTab(
                            userSettings = userSettings,
                            viewModel = viewModel,
                            onSelectLanguage = { showLanguageDialog = true },
                            onSelectTheme = { showThemeDialog = true },
                            onExportBackup = {
                                viewModel.exportBackup { json ->
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_SUBJECT, "GSM_Database_Backup_${System.currentTimeMillis()}.json")
                                        putExtra(Intent.EXTRA_TEXT, json)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Save GSM Database Backup"))
                                }
                            },
                            onImportBackup = { showImportDialog = true },
                            onResetDefaults = { showResetDialog = true },
                            onClearData = { showClearDialog = true }
                        )
                    }
                }
            }
        }

        // ====================================================================
        // DIALOGS & MODALS
        // ====================================================================

        // 1. Edit Customer Account Dialog
        editingCustomer?.let { cust ->
            EditCustomerAccountDialog(
                customer = cust,
                onDismiss = { editingCustomer = null },
                onSave = { updatedCust ->
                    viewModel.updateCustomerDetails(updatedCust) {
                        Toast.makeText(context, "Customer account updated!", Toast.LENGTH_SHORT).show()
                        editingCustomer = null
                    }
                }
            )
        }

        // 2. Add New Customer Dialog
        if (showAddCustomerDialog) {
            AddCustomerAccountDialog(
                defaultPrefix = userSettings.customerIdPrefix,
                defaultPassword = userSettings.defaultCustomerPassword,
                onDismiss = { showAddCustomerDialog = false },
                onSave = { name, idCode, phone, email, pass ->
                    viewModel.registerCustomer(
                        name = name,
                        customerId = idCode,
                        mobileNumber = phone,
                        email = email,
                        password = pass,
                        confirmPassword = pass
                    ) { result ->
                        showAddCustomerDialog = false
                        Toast.makeText(context, "New customer account created!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // 3. Privacy Policy Customer-Facing Live Preview Dialog
        if (showPolicyPreviewDialog) {
            PolicyPreviewModal(
                userSettings = userSettings,
                onDismiss = { showPolicyPreviewDialog = false }
            )
        }

        // 4. Language Selection Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text(s.selectLanguage) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.entries.forEach { lang ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.setAppLanguage(lang)
                                        showLanguageDialog = false
                                        Toast.makeText(context, s.languageChangedNotice, Toast.LENGTH_SHORT).show()
                                    },
                                color = if (userSettings.appLanguage == lang) OrangePrimary.copy(alpha = 0.12f) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = lang.flag, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = lang.nativeName,
                                            fontWeight = if (userSettings.appLanguage == lang) FontWeight.Bold else FontWeight.Medium,
                                            color = if (userSettings.appLanguage == lang) OrangePrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = lang.displayName,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    RadioButton(
                                        selected = userSettings.appLanguage == lang,
                                        onClick = {
                                            viewModel.setAppLanguage(lang)
                                            showLanguageDialog = false
                                            Toast.makeText(context, s.languageChangedNotice, Toast.LENGTH_SHORT).show()
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // 5. Theme Mode Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Choose Theme Mode") },
                text = {
                    Column {
                        ThemeOptionRow(
                            title = "System Default",
                            selected = userSettings.themeMode == ThemeMode.SYSTEM,
                            onClick = {
                                viewModel.setThemeMode(ThemeMode.SYSTEM)
                                showThemeDialog = false
                            }
                        )
                        ThemeOptionRow(
                            title = "Light Mode",
                            selected = userSettings.themeMode == ThemeMode.LIGHT,
                            onClick = {
                                viewModel.setThemeMode(ThemeMode.LIGHT)
                                showThemeDialog = false
                            }
                        )
                        ThemeOptionRow(
                            title = "Dark Mode",
                            selected = userSettings.themeMode == ThemeMode.DARK,
                            onClick = {
                                viewModel.setThemeMode(ThemeMode.DARK)
                                showThemeDialog = false
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // 6. Import Backup JSON Dialog
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import Database JSON") },
                text = {
                    Column {
                        Text("Paste your exported JSON database backup text below:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = importJsonText,
                            onValueChange = { importJsonText = it },
                            placeholder = { Text("{\"customers\": [...], ...}") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            maxLines = 6
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.restoreBackup(importJsonText) { success ->
                                if (success) {
                                    Toast.makeText(context, "Database restored successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Invalid JSON backup data", Toast.LENGTH_SHORT).show()
                                }
                                showImportDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        Text("Import & Restore")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // 7. Reset to Defaults Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Restore Default Database") },
                text = { Text("This will reset all models, schematics, LCDs, and files to default factory servicing data.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.restoreDefaultData {
                                Toast.makeText(context, "Default database restored!", Toast.LENGTH_SHORT).show()
                                showResetDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        Text("Restore Defaults")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // 8. Clear All Data Dialog
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Erase All Database Records") },
                text = { Text("Are you sure you want to delete all customer records, files, models, and diagrams? This cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllData {
                                Toast.makeText(context, "All data wiped", Toast.LENGTH_SHORT).show()
                                showClearDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCrimson)
                    ) {
                        Text("Wipe All Data")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ============================================================================
// SECTION 1: CUSTOMER SIGN-IN DETAILS TAB
// ============================================================================
@Composable
private fun CustomerSignInSettingsTab(
    userSettings: UserSettings,
    customersList: List<CustomerEntity>,
    onSave: (
        portalTitle: String,
        notice: String,
        defaultPass: String,
        allowReg: Boolean,
        allowGoogle: Boolean,
        reqPhone: Boolean,
        prefix: String,
        supportPhone: String,
        supportEmail: String
    ) -> Unit,
    onEditCustomer: (CustomerEntity) -> Unit,
    onAddNewCustomer: () -> Unit,
    onToggleBlock: (CustomerEntity) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var portalTitle by remember(userSettings) { mutableStateOf(userSettings.customerPortalTitle) }
    var loginNotice by remember(userSettings) { mutableStateOf(userSettings.customerLoginNotice) }
    var defaultPass by remember(userSettings) { mutableStateOf(userSettings.defaultCustomerPassword) }
    var allowReg by remember(userSettings) { mutableStateOf(userSettings.allowCustomerRegistration) }
    var allowGoogle by remember(userSettings) { mutableStateOf(userSettings.allowGoogleSignIn) }
    var reqPhone by remember(userSettings) { mutableStateOf(userSettings.requirePhoneLogin) }
    var prefix by remember(userSettings) { mutableStateOf(userSettings.customerIdPrefix) }
    var supportPhone by remember(userSettings) { mutableStateOf(userSettings.customerSupportPhone) }
    var supportEmail by remember(userSettings) { mutableStateOf(userSettings.customerSupportEmail) }

    var customerSearchQuery by remember { mutableStateOf("") }

    val filteredCustomers = remember(customersList, customerSearchQuery) {
        if (customerSearchQuery.isBlank()) {
            customersList
        } else {
            customersList.filter {
                it.customerName.contains(customerSearchQuery, ignoreCase = true) ||
                it.customerIdCode.contains(customerSearchQuery, ignoreCase = true) ||
                it.mobileNumber.contains(customerSearchQuery, ignoreCase = true) ||
                it.gmail.contains(customerSearchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overview Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(OrangePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Customer Sign-In & Portal Management", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Customize login instructions, self-registration rules, default passwords, and manage user accounts.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Card 1: Portal Branding & Sign-In Instructions
        item {
            SettingsCategoryCard(title = "Portal Branding & Sign-In Instructions") {
                Text("Portal Header Title", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = portalTitle,
                    onValueChange = { portalTitle = it },
                    placeholder = { Text("e.g. GSM ROM Customer Portal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_cust_portal_title"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Sign-In Instructions Banner Notice", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = loginNotice,
                    onValueChange = { loginNotice = it },
                    placeholder = { Text("Instructions displayed on Customer Login Screen") },
                    modifier = Modifier.fillMaxWidth().testTag("input_cust_login_notice"),
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Default Password", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = defaultPass,
                            onValueChange = { defaultPass = it },
                            placeholder = { Text("123456") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_cust_default_pass"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Customer ID Prefix", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = prefix,
                            onValueChange = { prefix = it },
                            placeholder = { Text("CUST-") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_cust_id_prefix"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Card 2: Authentication Rules & Permissions
        item {
            SettingsCategoryCard(title = "Sign-In Access & Security Rules") {
                SettingsRowSwitch(
                    title = "Allow Customer Self-Registration",
                    subtitle = "Permit new walk-in customers to create account directly on login screen",
                    icon = Icons.Default.Person,
                    iconColor = OrangePrimary,
                    checked = allowReg,
                    onCheckedChange = { allowReg = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowSwitch(
                    title = "Enable Google 1-Tap Sign-In",
                    subtitle = "Allow customers to log in using their verified Google Account",
                    icon = Icons.Default.Security,
                    iconColor = SapphireBlue,
                    checked = allowGoogle,
                    onCheckedChange = { allowGoogle = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowSwitch(
                    title = "Require Mobile Number Sign-In",
                    subtitle = "Require customer mobile number verification for account access",
                    icon = Icons.Default.Phone,
                    iconColor = NeonGreen,
                    checked = reqPhone,
                    onCheckedChange = { reqPhone = it }
                )
            }
        }

        // Card 3: Support Contact Displayed to Customers
        item {
            SettingsCategoryCard(title = "Customer Helpdesk & Support Hotline") {
                Text("Support Phone / WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = supportPhone,
                    onValueChange = { supportPhone = it },
                    placeholder = { Text("+880 1712-345678") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = OrangePrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_cust_support_phone"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Support Email", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = supportEmail,
                    onValueChange = { supportEmail = it },
                    placeholder = { Text("support@gsmrom.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = OrangePrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_cust_support_email"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // SAVE BUTTON FOR CUSTOMER SIGN-IN SETTINGS
                Button(
                    onClick = {
                        onSave(portalTitle, loginNotice, defaultPass, allowReg, allowGoogle, reqPhone, prefix, supportPhone, supportEmail)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_customer_settings"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE CUSTOMER SIGN-IN SETTINGS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Card 4: Customer Accounts Live Management
        item {
            SettingsCategoryCard(title = "Customer Accounts Database (${customersList.size})") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Authorized administrators can view, edit, block, or reset credentials for any customer.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = onAddNewCustomer,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_add_customer_account")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Account", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Customer Search Bar
                OutlinedTextField(
                    value = customerSearchQuery,
                    onValueChange = { customerSearchQuery = it },
                    placeholder = { Text("Search by name, ID, phone, or email...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // List of Customer Account Cards
        if (filteredCustomers.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text(
                        "No customer accounts found. Click '+ Add Account' above to create one.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(filteredCustomers, key = { it.id }) { customer ->
                CustomerAccountAdminItemCard(
                    customer = customer,
                    onEdit = { onEditCustomer(customer) },
                    onToggleBlock = { onToggleBlock(customer) },
                    onCopyCredentials = {
                        val creds = "Customer ID: ${customer.customerIdCode}\nPassword: ${customer.password.ifBlank { "123456" }}\nPhone: ${customer.mobileNumber}"
                        clipboardManager.setText(AnnotatedString(creds))
                        Toast.makeText(context, "Credentials copied for ${customer.customerName}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CustomerAccountAdminItemCard(
    customer: CustomerEntity,
    onEdit: () -> Unit,
    onToggleBlock: () -> Unit,
    onCopyCredentials: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (customer.isBlocked) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (customer.isBlocked) DarkCrimson.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = OrangePrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = customer.customerIdCode.ifBlank { "CUST-${customer.id}" },
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = OrangePrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = customer.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (customer.isBlocked) DarkCrimson.copy(alpha = 0.2f) else NeonGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (customer.isBlocked) "BLOCKED" else "ACTIVE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (customer.isBlocked) DarkCrimson else NeonGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "📱 ${customer.mobileNumber.ifBlank { "No mobile" }} • ✉️ ${customer.gmail.ifBlank { "No email" }}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Password: ${customer.password.ifBlank { "123456" }}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCopyCredentials, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Security, contentDescription = "Copy Login Credentials", modifier = Modifier.size(16.dp), tint = OrangePrimary)
                    }
                    IconButton(onClick = onToggleBlock, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (customer.isBlocked) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = "Toggle Block",
                            modifier = Modifier.size(16.dp),
                            tint = if (customer.isBlocked) NeonGreen else DarkCrimson
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Customer Account", modifier = Modifier.size(16.dp), tint = SapphireBlue)
                    }
                }
            }
        }
    }
}

// ============================================================================
// SECTION 2: ADMIN DETAILS TAB
// ============================================================================
@Composable
private fun AdminDetailsSettingsTab(
    userSettings: UserSettings,
    onSave: (
        adminName: String,
        adminId: String,
        adminPass: String,
        adminEmail: String,
        adminPhone: String,
        adminDesignation: String,
        adminRoleBadge: String,
        shopName: String,
        shopPhone: String,
        shopAddress: String
    ) -> Unit
) {
    var adminName by remember(userSettings) { mutableStateOf(userSettings.adminName) }
    var adminId by remember(userSettings) { mutableStateOf(userSettings.adminId) }
    var adminPass by remember(userSettings) { mutableStateOf(userSettings.adminPassword) }
    var confirmPass by remember(userSettings) { mutableStateOf(userSettings.adminPassword) }
    var adminEmail by remember(userSettings) { mutableStateOf(userSettings.adminEmail) }
    var adminPhone by remember(userSettings) { mutableStateOf(userSettings.adminPhone) }
    var adminDesignation by remember(userSettings) { mutableStateOf(userSettings.adminDesignation) }
    var adminRoleBadge by remember(userSettings) { mutableStateOf(userSettings.adminRoleBadge) }

    var shopName by remember(userSettings) { mutableStateOf(userSettings.shopName) }
    var shopPhone by remember(userSettings) { mutableStateOf(userSettings.shopPhone) }
    var shopAddress by remember(userSettings) { mutableStateOf(userSettings.shopAddress) }

    var passVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, SapphireBlue.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SapphireBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SapphireBlue, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Administrator Profile & Security Management", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Authorized administrators can edit identity, credentials, roles, and workshop information.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Error message
        if (errorMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(errorMessage ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }

        // Card 1: Identity & Credentials
        item {
            SettingsCategoryCard(title = "Administrator Identity & Authentication") {
                Text("Administrator Full Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = adminName,
                    onValueChange = {
                        adminName = it
                        errorMessage = null
                    },
                    placeholder = { Text("e.g. Master Hossain / System Admin") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SapphireBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_name"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Admin Username / ID", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = adminId,
                            onValueChange = {
                                adminId = it
                                errorMessage = null
                            },
                            placeholder = { Text("admin") },
                            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = SapphireBlue) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_admin_id"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Role Level Badge", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = adminRoleBadge,
                            onValueChange = { adminRoleBadge = it },
                            placeholder = { Text("Super Administrator") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_admin_role_badge"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Designation & Professional Title", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = adminDesignation,
                    onValueChange = { adminDesignation = it },
                    placeholder = { Text("e.g. Chief Hardware & Firmware Engineer") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_designation"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password & Confirm Password
                Text("Admin Password / Security PIN", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = adminPass,
                    onValueChange = {
                        adminPass = it
                        errorMessage = null
                    },
                    placeholder = { Text("Enter Admin Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SapphireBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passVisible = !passVisible }) {
                            Icon(
                                imageVector = if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_password"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Confirm Password", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = {
                        confirmPass = it
                        errorMessage = null
                    },
                    placeholder = { Text("Re-enter Admin Password") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = SapphireBlue) },
                    visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_confirm_password"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Card 2: Contact & Workshop Information
        item {
            SettingsCategoryCard(title = "Workshop & Enterprise Details") {
                Text("Admin Direct Email", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = adminEmail,
                    onValueChange = { adminEmail = it },
                    placeholder = { Text("admin@gsmrom.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SapphireBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_email"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Admin Direct Hotline / Phone", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = adminPhone,
                    onValueChange = { adminPhone = it },
                    placeholder = { Text("+880 1712-345678") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SapphireBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_phone"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Workshop / Servicing Center Name", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    placeholder = { Text("GSM ROM Mobile Servicing Master") },
                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = SapphireBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_shop_name"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Workshop Physical Address", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = shopAddress,
                    onValueChange = { shopAddress = it },
                    placeholder = { Text("Central Plaza, 3rd Floor, Dhaka") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = SapphireBlue) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_admin_shop_address"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // SAVE BUTTON FOR ADMIN PROFILE
                Button(
                    onClick = {
                        if (adminId.isBlank()) {
                            errorMessage = "Admin ID cannot be blank"
                            return@Button
                        }
                        if (adminPass.isBlank()) {
                            errorMessage = "Admin password cannot be blank"
                            return@Button
                        }
                        if (adminPass != confirmPass) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }

                        onSave(
                            adminName,
                            adminId,
                            adminPass,
                            adminEmail,
                            adminPhone,
                            adminDesignation,
                            adminRoleBadge,
                            shopName,
                            shopPhone,
                            shopAddress
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_admin_profile"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SapphireBlue)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE & UPDATE ADMIN PROFILE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ============================================================================
// SECTION 3: PRIVACY & POLICY DETAILS TAB
// ============================================================================
@Composable
private fun PrivacyPolicySettingsTab(
    userSettings: UserSettings,
    onSave: (
        privacyText: String,
        termsText: String,
        warrantyText: String,
        date: String,
        version: String,
        showOnPortal: Boolean
    ) -> Unit,
    onPreview: () -> Unit
) {
    var privacyText by remember(userSettings) { mutableStateOf(userSettings.privacyPolicyText) }
    var termsText by remember(userSettings) { mutableStateOf(userSettings.termsOfServiceText) }
    var warrantyText by remember(userSettings) { mutableStateOf(userSettings.warrantyTermsText) }
    var date by remember(userSettings) { mutableStateOf(userSettings.policyEffectiveDate) }
    var version by remember(userSettings) { mutableStateOf(userSettings.policyVersion) }
    var showOnPortal by remember(userSettings) { mutableStateOf(userSettings.showPolicyOnPortal) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Privacy Policy & Terms Management", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "Manage customer privacy terms, repair warranties, data protection clauses, and live portal disclaimers.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Preset Templates
        item {
            SettingsCategoryCard(title = "Policy Presets & Auto-Fill Templates") {
                Text(
                    "Select a standard pre-configured template to populate legally vetted repair and privacy statements:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            privacyText = "At GSM ROM Servicing, customer confidentiality is our highest priority. We do not access, copy, or store personal files, photos, or data on repaired devices. All flashing, firmware programming, and hardware diagnostic procedures are conducted in strict compliance with international privacy and electronics servicing standards."
                            termsText = "1. Service Warranty: 30 days warranty on replaced displays, touch ICs, and motherboard repairs (excluding physical/water damage).\n2. Device Delivery: Please collect your repaired device within 60 days of completion notice.\n3. Data Protection: Technicians are not liable for data loss during firmware flashing or NAND partitioning. Customers are advised to backup data before submission."
                            warrantyText = "30-Day Hardware Repair Guarantee (Original OEM Parts)"
                            version = "v2.4"
                            date = "August 2026"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("Standard GSM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            privacyText = "Zero-Data Access Guarantee: All technician diagnostic workstations operate with strict read/write NAND isolation. No customer data, cloud tokens, or personal identifiers are retained beyond active testing verification."
                            termsText = "1. Strict 60-Day Hardware Warranty on motherboard micro-soldering.\n2. Liquid damage repairs carry no warranty due to latent oxidation risks.\n3. Unclaimed units past 90 days are recycled."
                            warrantyText = "60-Day Strict Hardware Micro-Soldering Guarantee"
                            version = "v3.0-Strict"
                            date = "August 2026"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("Strict Hardware", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            privacyText = "Data Privacy Shield Policy: Compliant with consumer electronics protection guidelines. Customer storage is wiped only upon explicit authorization for dead boot revival."
                            termsText = "1. Quick 15-Day Display Touch Warranty.\n2. Firmware restoration requires customer authorization.\n3. Customer inspection mandatory upon delivery."
                            warrantyText = "15-Day Display & Fast Service Warranty"
                            version = "v1.8-Lite"
                            date = "August 2026"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text("Data Shield", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Card 1: Privacy Policy Statement
        item {
            SettingsCategoryCard(title = "1. Customer Privacy Policy Statement") {
                Text("Explain how device data, IMEIs, personal files, and NAND partitions are protected:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = privacyText,
                    onValueChange = { privacyText = it },
                    placeholder = { Text("Enter Privacy Policy text...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("input_privacy_policy_text"),
                    maxLines = 6,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Card 2: Terms of Service & Repair Agreement
        item {
            SettingsCategoryCard(title = "2. Terms of Service & Repair Agreement") {
                Text("Specify repair risk disclaimers, pickup deadlines, and customer obligations:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = termsText,
                    onValueChange = { termsText = it },
                    placeholder = { Text("Enter Terms of Service & Repair Agreement...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("input_terms_of_service_text"),
                    maxLines = 6,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Card 3: Warranty Terms & Metadata
        item {
            SettingsCategoryCard(title = "3. Warranty Guarantee & Metadata") {
                Text("Default Repair Warranty Guarantee", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = warrantyText,
                    onValueChange = { warrantyText = it },
                    placeholder = { Text("30-Day Hardware Repair Guarantee") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_warranty_terms_text"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Policy Version", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = version,
                            onValueChange = { version = it },
                            placeholder = { Text("v2.4") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_policy_version"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Effective Date", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            placeholder = { Text("August 2026") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_policy_date"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                SettingsRowSwitch(
                    title = "Show Policies on Customer Portal",
                    subtitle = "Display link and dialog for customers to review before login and in dashboard",
                    icon = Icons.Default.Description,
                    iconColor = NeonGreen,
                    checked = showOnPortal,
                    onCheckedChange = { showOnPortal = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onPreview,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_preview_policy"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NeonGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LIVE PREVIEW", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onSave(privacyText, termsText, warrantyText, date, version, showOnPortal)
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("btn_save_privacy_policy"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SAVE POLICIES", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ============================================================================
// SECTION 4: SYSTEM & PREFERENCES TAB (Preserving all existing functionality)
// ============================================================================
@Composable
private fun SystemPreferencesTab(
    userSettings: UserSettings,
    viewModel: GsmViewModel,
    onSelectLanguage: () -> Unit,
    onSelectTheme: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onResetDefaults: () -> Unit,
    onClearData: () -> Unit
) {
    val s = currentStrings()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language Selection Card
        item {
            SettingsCategoryCard(title = s.languageSection) {
                SettingsRowClickable(
                    title = s.selectLanguage,
                    subtitle = "${userSettings.appLanguage.flag} ${userSettings.appLanguage.nativeName} (${userSettings.appLanguage.displayName})",
                    icon = Icons.Default.Language,
                    iconColor = OrangePrimary,
                    onClick = onSelectLanguage
                )
            }
        }

        // Theme Mode Card
        item {
            SettingsCategoryCard(title = s.appearance) {
                SettingsRowClickable(
                    title = s.themeMode,
                    subtitle = when (userSettings.themeMode) {
                        ThemeMode.LIGHT -> s.lightTheme
                        ThemeMode.DARK -> s.darkTheme
                        ThemeMode.SYSTEM -> s.systemTheme
                    },
                    icon = Icons.Default.Brightness4,
                    iconColor = OrangePrimary,
                    onClick = onSelectTheme
                )
            }
        }

        // Notification & Reminder Settings
        item {
            SettingsCategoryCard(title = "Delivery Reminders & Audio Alerts") {
                SettingsRowSwitch(
                    title = "Delivery Deadline Notifications",
                    subtitle = "Notify when a phone is scheduled for customer delivery",
                    icon = Icons.Default.Notifications,
                    iconColor = OrangePrimary,
                    checked = userSettings.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowSwitch(
                    title = s.voiceReminders,
                    subtitle = "Automatically play recorded customer audio note when reminder triggers",
                    icon = Icons.Default.GraphicEq,
                    iconColor = SapphireBlue,
                    checked = userSettings.voiceReminderEnabled,
                    onCheckedChange = { viewModel.setVoiceReminderEnabled(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowSwitch(
                    title = s.soundEffects,
                    subtitle = "Play alert tone on delivery alarms",
                    icon = Icons.Default.VolumeUp,
                    iconColor = NeonGreen,
                    checked = userSettings.soundEnabled,
                    onCheckedChange = { viewModel.setSoundEnabled(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowSwitch(
                    title = s.vibration,
                    subtitle = "Vibrate device for scheduled customer reminders",
                    icon = Icons.Default.Vibration,
                    iconColor = OrangePrimary,
                    checked = userSettings.vibrationEnabled,
                    onCheckedChange = { viewModel.setVibrationEnabled(it) }
                )
            }
        }

        // Backup & Restore Card
        item {
            SettingsCategoryCard(title = "Database Backup & Sync") {
                SettingsRowClickable(
                    title = "Export Backup (JSON)",
                    subtitle = "Export all customers, models, diagrams, and files",
                    icon = Icons.Default.Backup,
                    iconColor = NeonGreen,
                    onClick = onExportBackup
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowClickable(
                    title = "Restore from JSON",
                    subtitle = "Import customer job records and database backup",
                    icon = Icons.Default.Restore,
                    iconColor = SapphireBlue,
                    onClick = onImportBackup
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowClickable(
                    title = "Restore Default Database",
                    subtitle = "Re-seed sample phone models, circuit diagrams, and files",
                    icon = Icons.Default.Store,
                    iconColor = OrangePrimary,
                    onClick = onResetDefaults
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                SettingsRowClickable(
                    title = "Clear All App Data",
                    subtitle = "Erase all customer records and saved files",
                    icon = Icons.Default.DeleteSweep,
                    iconColor = DarkCrimson,
                    onClick = onClearData
                )
            }
        }

        // About App
        item {
            SettingsCategoryCard(title = "About GSM Service") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(OrangePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = OrangePrimary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text("GSM ROM Firmware & Servicing Master", fontWeight = FontWeight.Bold)
                        Text("Version 1.0.0 • Customizable Settings Architecture", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Designed for Mobile Repair Technicians & Flashing Specialists", fontSize = 11.sp, color = OrangePrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ============================================================================
// EDIT / ADD CUSTOMER DIALOGS
// ============================================================================
@Composable
private fun EditCustomerAccountDialog(
    customer: CustomerEntity,
    onDismiss: () -> Unit,
    onSave: (CustomerEntity) -> Unit
) {
    var name by remember { mutableStateOf(customer.customerName) }
    var idCode by remember { mutableStateOf(customer.customerIdCode) }
    var mobile by remember { mutableStateOf(customer.mobileNumber) }
    var email by remember { mutableStateOf(customer.gmail) }
    var password by remember { mutableStateOf(customer.password.ifBlank { "123456" }) }
    var isBlocked by remember { mutableStateOf(customer.isBlocked) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Customer Account", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Customer Name", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Customer ID Code", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = idCode,
                    onValueChange = { idCode = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Mobile Number", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Email Address", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Account Password", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Block Account Access", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isBlocked) DarkCrimson else MaterialTheme.colorScheme.onSurface)
                    Switch(
                        checked = isBlocked,
                        onCheckedChange = { isBlocked = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = DarkCrimson)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        customer.copy(
                            customerName = name.trim(),
                            customerIdCode = idCode.trim(),
                            mobileNumber = mobile.trim(),
                            gmail = email.trim(),
                            password = password.trim(),
                            isBlocked = isBlocked
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddCustomerAccountDialog(
    defaultPrefix: String,
    defaultPassword: String,
    onDismiss: () -> Unit,
    onSave: (name: String, idCode: String, phone: String, email: String, pass: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var idCode by remember { mutableStateOf("${defaultPrefix}${System.currentTimeMillis() % 10000}") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf(defaultPassword) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Customer Account", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Customer Name *", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Customer ID *", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = idCode,
                    onValueChange = { idCode = it },
                    placeholder = { Text("e.g. CUST-1002") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Mobile Number *", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("+880 1700-000000") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Email Address (Optional)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("customer@example.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Initial Password *", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    placeholder = { Text("123456") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name.trim(), idCode.trim(), phone.trim(), email.trim(), pass.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Create Account", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun PolicyPreviewModal(
    userSettings: UserSettings,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Policy, contentDescription = null, tint = NeonGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Customer-Facing Privacy & Policy", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("Version: ${userSettings.policyVersion} • Effective: ${userSettings.policyEffectiveDate}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonGreen.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("🛡️ WARRANTY GUARANTEE", fontWeight = FontWeight.Black, fontSize = 11.sp, color = NeonGreen)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(userSettings.warrantyTermsText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Text("1. PRIVACY & DATA PROTECTION", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OrangePrimary)
                Text(userSettings.privacyPolicyText, fontSize = 12.sp, lineHeight = 17.sp)

                HorizontalDivider()

                Text("2. SERVICE AGREEMENT & REPAIR TERMS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SapphireBlue)
                Text(userSettings.termsOfServiceText, fontSize = 12.sp, lineHeight = 17.sp)

                HorizontalDivider()

                Text("3. SERVICING CENTER CONTACT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${userSettings.shopName}\nHotline: ${userSettings.shopPhone}\nAddress: ${userSettings.shopAddress}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) {
                Text("Close Preview", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// ============================================================================
// HELPER UI COMPOSABLES
// ============================================================================
@Composable
private fun SettingsCategoryCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsRowClickable(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsRowSwitch(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OrangePrimary)
        )
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 14.sp)
    }
}
