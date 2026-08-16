package com.example.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.auth.AuthUser
import com.example.auth.UserRole
import com.example.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gsm_settings")

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val notificationsEnabled: Boolean = true,
    val voiceReminderEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val shopName: String = "GSM ROM Mobile Servicing",
    val shopPhone: String = "+880 1712-345678",
    val shopAddress: String = "Central Plaza, Dhaka",

    // 1. Customer Sign-In Details
    val customerPortalTitle: String = "GSM ROM Customer Portal",
    val customerLoginNotice: String = "Enter your Customer ID or Email to track your device repair status live.",
    val defaultCustomerPassword: String = "123456",
    val allowCustomerRegistration: Boolean = true,
    val allowGoogleSignIn: Boolean = true,
    val requirePhoneLogin: Boolean = false,
    val customerIdPrefix: String = "CUST-",
    val customerSupportPhone: String = "+880 1712-345678",
    val customerSupportEmail: String = "support@gsmrom.com",

    // 2. Admin Details
    val adminName: String = "System Administrator",
    val adminId: String = "admin",
    val adminPassword: String = "admin123",
    val adminEmail: String = "admin@gsmrom.com",
    val adminPhone: String = "+880 1712-345678",
    val adminDesignation: String = "Chief Hardware & Firmware Engineer",
    val adminRoleBadge: String = "Super Administrator",

    // 3. Privacy & Policy Details
    val privacyPolicyText: String = "At GSM ROM Servicing, customer confidentiality is our highest priority. We do not access, copy, or store personal files, photos, or data on repaired devices. All flashing, firmware programming, and hardware diagnostic procedures are conducted in strict compliance with international privacy and electronics servicing standards.",
    val termsOfServiceText: String = "1. Service Warranty: 30 days warranty on replaced displays, touch ICs, and motherboard repairs (excluding physical/water damage).\n2. Device Delivery: Please collect your repaired device within 60 days of completion notice.\n3. Data Protection: Technicians are not liable for data loss during firmware flashing or NAND partitioning. Customers are advised to backup data before submission.",
    val warrantyTermsText: String = "30-Day Hardware Repair Guarantee (Original OEM Parts)",
    val policyEffectiveDate: String = "August 2026",
    val policyVersion: String = "v2.4",
    val showPolicyOnPortal: Boolean = true
)

class UserPreferences(private val context: Context) {

    private val KEY_THEME = stringPreferencesKey("theme_mode")
    private val KEY_APP_LANGUAGE = stringPreferencesKey("app_language")
    private val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    private val KEY_VOICE_REMINDER = booleanPreferencesKey("voice_reminder_enabled")
    private val KEY_SOUND = booleanPreferencesKey("sound_enabled")
    private val KEY_VIBRATION = booleanPreferencesKey("vibration_enabled")
    private val KEY_SHOP_NAME = stringPreferencesKey("shop_name")
    private val KEY_SHOP_PHONE = stringPreferencesKey("shop_phone")
    private val KEY_SHOP_ADDRESS = stringPreferencesKey("shop_address")

    // 1. Customer Sign-In Keys
    private val KEY_CUST_PORTAL_TITLE = stringPreferencesKey("cust_portal_title")
    private val KEY_CUST_LOGIN_NOTICE = stringPreferencesKey("cust_login_notice")
    private val KEY_CUST_DEFAULT_PASS = stringPreferencesKey("cust_default_pass")
    private val KEY_CUST_ALLOW_REG = booleanPreferencesKey("cust_allow_reg")
    private val KEY_CUST_ALLOW_GOOGLE = booleanPreferencesKey("cust_allow_google")
    private val KEY_CUST_REQ_PHONE = booleanPreferencesKey("cust_req_phone")
    private val KEY_CUST_ID_PREFIX = stringPreferencesKey("cust_id_prefix")
    private val KEY_CUST_SUPPORT_PHONE = stringPreferencesKey("cust_support_phone")
    private val KEY_CUST_SUPPORT_EMAIL = stringPreferencesKey("cust_support_email")

    // 2. Admin Details Keys
    private val KEY_ADMIN_NAME = stringPreferencesKey("admin_name")
    private val KEY_ADMIN_ID = stringPreferencesKey("admin_id")
    private val KEY_ADMIN_PASSWORD = stringPreferencesKey("admin_password")
    private val KEY_ADMIN_EMAIL = stringPreferencesKey("admin_email")
    private val KEY_ADMIN_PHONE = stringPreferencesKey("admin_phone")
    private val KEY_ADMIN_DESIGNATION = stringPreferencesKey("admin_designation")
    private val KEY_ADMIN_ROLE_BADGE = stringPreferencesKey("admin_role_badge")

    // 3. Privacy & Policy Keys
    private val KEY_PRIVACY_POLICY = stringPreferencesKey("privacy_policy_text")
    private val KEY_TERMS_OF_SERVICE = stringPreferencesKey("terms_of_service_text")
    private val KEY_WARRANTY_TERMS = stringPreferencesKey("warranty_terms_text")
    private val KEY_POLICY_DATE = stringPreferencesKey("policy_effective_date")
    private val KEY_POLICY_VERSION = stringPreferencesKey("policy_version")
    private val KEY_SHOW_POLICY_ON_PORTAL = booleanPreferencesKey("show_policy_on_portal")

    // Auth Session keys
    private val KEY_AUTH_ROLE = stringPreferencesKey("auth_role")
    private val KEY_AUTH_CUSTOMER_ID = stringPreferencesKey("auth_customer_id")
    private val KEY_AUTH_CUSTOMER_NAME = stringPreferencesKey("auth_customer_name")
    private val KEY_AUTH_EMAIL = stringPreferencesKey("auth_email")
    private val KEY_AUTH_MOBILE = stringPreferencesKey("auth_mobile")
    private val KEY_AUTH_DB_ID = longPreferencesKey("auth_db_id")

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val themeStr = preferences[KEY_THEME] ?: ThemeMode.SYSTEM.name
        val themeMode = try {
            ThemeMode.valueOf(themeStr)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }

        val langStr = preferences[KEY_APP_LANGUAGE] ?: AppLanguage.ENGLISH.code
        val appLanguage = AppLanguage.fromCode(langStr)

        UserSettings(
            themeMode = themeMode,
            appLanguage = appLanguage,
            notificationsEnabled = preferences[KEY_NOTIFICATIONS] ?: true,
            voiceReminderEnabled = preferences[KEY_VOICE_REMINDER] ?: true,
            soundEnabled = preferences[KEY_SOUND] ?: true,
            vibrationEnabled = preferences[KEY_VIBRATION] ?: true,
            shopName = preferences[KEY_SHOP_NAME] ?: "GSM ROM Mobile Servicing",
            shopPhone = preferences[KEY_SHOP_PHONE] ?: "+880 1712-345678",
            shopAddress = preferences[KEY_SHOP_ADDRESS] ?: "Central Plaza, Dhaka",

            // 1. Customer Sign-In Details
            customerPortalTitle = preferences[KEY_CUST_PORTAL_TITLE] ?: "GSM ROM Customer Portal",
            customerLoginNotice = preferences[KEY_CUST_LOGIN_NOTICE] ?: "Enter your Customer ID or Email to track your device repair status live.",
            defaultCustomerPassword = preferences[KEY_CUST_DEFAULT_PASS] ?: "123456",
            allowCustomerRegistration = preferences[KEY_CUST_ALLOW_REG] ?: true,
            allowGoogleSignIn = preferences[KEY_CUST_ALLOW_GOOGLE] ?: true,
            requirePhoneLogin = preferences[KEY_CUST_REQ_PHONE] ?: false,
            customerIdPrefix = preferences[KEY_CUST_ID_PREFIX] ?: "CUST-",
            customerSupportPhone = preferences[KEY_CUST_SUPPORT_PHONE] ?: "+880 1712-345678",
            customerSupportEmail = preferences[KEY_CUST_SUPPORT_EMAIL] ?: "support@gsmrom.com",

            // 2. Admin Details
            adminName = preferences[KEY_ADMIN_NAME] ?: "System Administrator",
            adminId = preferences[KEY_ADMIN_ID] ?: "admin",
            adminPassword = preferences[KEY_ADMIN_PASSWORD] ?: "admin123",
            adminEmail = preferences[KEY_ADMIN_EMAIL] ?: "admin@gsmrom.com",
            adminPhone = preferences[KEY_ADMIN_PHONE] ?: "+880 1712-345678",
            adminDesignation = preferences[KEY_ADMIN_DESIGNATION] ?: "Chief Hardware & Firmware Engineer",
            adminRoleBadge = preferences[KEY_ADMIN_ROLE_BADGE] ?: "Super Administrator",

            // 3. Privacy & Policy Details
            privacyPolicyText = preferences[KEY_PRIVACY_POLICY] ?: "At GSM ROM Servicing, customer confidentiality is our highest priority. We do not access, copy, or store personal files, photos, or data on repaired devices. All flashing, firmware programming, and hardware diagnostic procedures are conducted in strict compliance with international privacy and electronics servicing standards.",
            termsOfServiceText = preferences[KEY_TERMS_OF_SERVICE] ?: "1. Service Warranty: 30 days warranty on replaced displays, touch ICs, and motherboard repairs (excluding physical/water damage).\n2. Device Delivery: Please collect your repaired device within 60 days of completion notice.\n3. Data Protection: Technicians are not liable for data loss during firmware flashing or NAND partitioning. Customers are advised to backup data before submission.",
            warrantyTermsText = preferences[KEY_WARRANTY_TERMS] ?: "30-Day Hardware Repair Guarantee (Original OEM Parts)",
            policyEffectiveDate = preferences[KEY_POLICY_DATE] ?: "August 2026",
            policyVersion = preferences[KEY_POLICY_VERSION] ?: "v2.4",
            showPolicyOnPortal = preferences[KEY_SHOW_POLICY_ON_PORTAL] ?: true
        )
    }

    val authSessionFlow: Flow<AuthUser> = context.dataStore.data.map { preferences ->
        val roleStr = preferences[KEY_AUTH_ROLE] ?: UserRole.UNAUTHENTICATED.name
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.UNAUTHENTICATED
        }

        AuthUser(
            role = role,
            customerId = preferences[KEY_AUTH_CUSTOMER_ID] ?: "",
            customerName = preferences[KEY_AUTH_CUSTOMER_NAME] ?: "",
            email = preferences[KEY_AUTH_EMAIL] ?: "",
            mobileNumber = preferences[KEY_AUTH_MOBILE] ?: "",
            dbCustomerId = preferences[KEY_AUTH_DB_ID] ?: 0L
        )
    }

    suspend fun saveAuthSession(user: AuthUser) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_ROLE] = user.role.name
            preferences[KEY_AUTH_CUSTOMER_ID] = user.customerId
            preferences[KEY_AUTH_CUSTOMER_NAME] = user.customerName
            preferences[KEY_AUTH_EMAIL] = user.email
            preferences[KEY_AUTH_MOBILE] = user.mobileNumber
            preferences[KEY_AUTH_DB_ID] = user.dbCustomerId
        }
    }

    suspend fun clearAuthSession() {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_ROLE] = UserRole.UNAUTHENTICATED.name
            preferences[KEY_AUTH_CUSTOMER_ID] = ""
            preferences[KEY_AUTH_CUSTOMER_NAME] = ""
            preferences[KEY_AUTH_EMAIL] = ""
            preferences[KEY_AUTH_MOBILE] = ""
            preferences[KEY_AUTH_DB_ID] = 0L
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = mode.name
        }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_LANGUAGE] = language.code
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setVoiceReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VOICE_REMINDER] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SOUND] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VIBRATION] = enabled
        }
    }

    suspend fun setShopInfo(name: String, phone: String, address: String = "Central Plaza, Dhaka") {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHOP_NAME] = name
            preferences[KEY_SHOP_PHONE] = phone
            preferences[KEY_SHOP_ADDRESS] = address
        }
    }

    suspend fun setAdminCredentials(adminId: String, adminPass: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ADMIN_ID] = adminId
            preferences[KEY_ADMIN_PASSWORD] = adminPass
        }
    }

    // 1. Customer Sign-In Details Update
    suspend fun updateCustomerSignInSettings(
        portalTitle: String,
        loginNotice: String,
        defaultPass: String,
        allowRegistration: Boolean,
        allowGoogle: Boolean,
        requirePhone: Boolean,
        idPrefix: String,
        supportPhone: String,
        supportEmail: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CUST_PORTAL_TITLE] = portalTitle
            preferences[KEY_CUST_LOGIN_NOTICE] = loginNotice
            preferences[KEY_CUST_DEFAULT_PASS] = defaultPass
            preferences[KEY_CUST_ALLOW_REG] = allowRegistration
            preferences[KEY_CUST_ALLOW_GOOGLE] = allowGoogle
            preferences[KEY_CUST_REQ_PHONE] = requirePhone
            preferences[KEY_CUST_ID_PREFIX] = idPrefix
            preferences[KEY_CUST_SUPPORT_PHONE] = supportPhone
            preferences[KEY_CUST_SUPPORT_EMAIL] = supportEmail
        }
    }

    // 2. Admin Details Update
    suspend fun updateAdminProfile(
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
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ADMIN_NAME] = adminName
            preferences[KEY_ADMIN_ID] = adminId
            preferences[KEY_ADMIN_PASSWORD] = adminPass
            preferences[KEY_ADMIN_EMAIL] = adminEmail
            preferences[KEY_ADMIN_PHONE] = adminPhone
            preferences[KEY_ADMIN_DESIGNATION] = adminDesignation
            preferences[KEY_ADMIN_ROLE_BADGE] = adminRoleBadge
            preferences[KEY_SHOP_NAME] = shopName
            preferences[KEY_SHOP_PHONE] = shopPhone
            preferences[KEY_SHOP_ADDRESS] = shopAddress
        }
    }

    // 3. Privacy & Policy Details Update
    suspend fun updatePrivacyPolicySettings(
        privacyPolicyText: String,
        termsOfServiceText: String,
        warrantyTermsText: String,
        policyEffectiveDate: String,
        policyVersion: String,
        showPolicyOnPortal: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PRIVACY_POLICY] = privacyPolicyText
            preferences[KEY_TERMS_OF_SERVICE] = termsOfServiceText
            preferences[KEY_WARRANTY_TERMS] = warrantyTermsText
            preferences[KEY_POLICY_DATE] = policyEffectiveDate
            preferences[KEY_POLICY_VERSION] = policyVersion
            preferences[KEY_SHOW_POLICY_ON_PORTAL] = showPolicyOnPortal
        }
    }
}
