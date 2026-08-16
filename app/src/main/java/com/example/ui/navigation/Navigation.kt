package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "GSM ROM")
    object CustomerLogin : Screen("customer_login", "Customer Login")
    object CreateAccount : Screen("create_account", "Create Customer Account")
    object CustomerDashboard : Screen("customer_dashboard", "Customer Dashboard")
    object CustomerProfileDetails : Screen("customer_profile_details", "Customer Details")
    object AdminLogin : Screen("admin_login", "Admin Login")
    object AdminDashboard : Screen("admin_dashboard", "Admin Dashboard")
    object AdminCustomerManagement : Screen("admin_customer_management", "Customer Management")
    object Diagram : Screen("diagram", "Circuit Diagrams")
    object Lcd : Screen("lcd", "LCD Database")
    object Model : Screen("model", "Phone Models")
    object File : Screen("file", "Service Files")
    object Customer : Screen("customer", "Customer Services")
    object AddEditCustomer : Screen("add_edit_customer", "Add Customer")
    object CustomerDetail : Screen("customer_detail", "Customer Details")
    object DiagramViewer : Screen("diagram_viewer", "Diagram Viewer")
    object GlobalSearch : Screen("global_search", "Global Search")
    object Settings : Screen("settings", "App Settings")
}

enum class BottomNavItem(
    val route: String,
    val label: String,
    val iconName: String
) {
    HOME("home", "Home", "home"),
    DIAGRAM("diagram", "Diagram", "circuit"),
    LCD("lcd", "LCD", "display"),
    MODEL("model", "Model", "phone"),
    FILE("file", "File", "folder"),
    CUSTOMER("customer", "Customer", "service")
}
