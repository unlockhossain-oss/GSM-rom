package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.UserRole
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.getAppStrings
import com.example.preferences.ThemeMode
import com.example.ui.admin.AdminCustomerManagementScreen
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminLoginScreen
import com.example.ui.auth.CreateCustomerAccountScreen
import com.example.ui.auth.CustomerLoginScreen
import com.example.ui.components.GsmBottomBar
import com.example.ui.components.GsmTopBar
import com.example.ui.customer.AddEditCustomerScreen
import com.example.ui.customer.CustomerDetailScreen
import com.example.ui.customer.CustomerScreen
import com.example.ui.customer_portal.CustomerDashboardScreen
import com.example.ui.customer_portal.CustomerProfileDetailsScreen
import com.example.ui.diagram.DiagramScreen
import com.example.ui.file.FileScreen
import com.example.ui.home.HomeScreen
import com.example.ui.lcd.LcdScreen
import com.example.ui.model.ModelScreen
import com.example.ui.navigation.Screen
import com.example.ui.search.GlobalSearchScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.GsmServiceTheme
import com.example.viewmodel.GsmViewModel
import com.example.viewmodel.GsmViewModelFactory

sealed interface NavDestination {
    val route: String

    data object CustomerLogin : NavDestination {
        override val route: String = Screen.CustomerLogin.route
    }
    data object CreateAccount : NavDestination {
        override val route: String = Screen.CreateAccount.route
    }
    data object CustomerDashboard : NavDestination {
        override val route: String = Screen.CustomerDashboard.route
    }
    data object CustomerProfileDetails : NavDestination {
        override val route: String = Screen.CustomerProfileDetails.route
    }
    data object AdminLogin : NavDestination {
        override val route: String = Screen.AdminLogin.route
    }
    data object AdminDashboard : NavDestination {
        override val route: String = Screen.AdminDashboard.route
    }
    data object AdminCustomerManagement : NavDestination {
        override val route: String = Screen.AdminCustomerManagement.route
    }
    data object Home : NavDestination {
        override val route: String = Screen.Home.route
    }
    data object Diagram : NavDestination {
        override val route: String = Screen.Diagram.route
    }
    data object Lcd : NavDestination {
        override val route: String = Screen.Lcd.route
    }
    data object Model : NavDestination {
        override val route: String = Screen.Model.route
    }
    data object File : NavDestination {
        override val route: String = Screen.File.route
    }
    data object Customer : NavDestination {
        override val route: String = Screen.Customer.route
    }
    data class AddEditCustomer(val customerId: Long? = null) : NavDestination {
        override val route: String = Screen.AddEditCustomer.route
    }
    data class CustomerDetail(val customerId: Long) : NavDestination {
        override val route: String = Screen.CustomerDetail.route
    }
    data object GlobalSearch : NavDestination {
        override val route: String = Screen.GlobalSearch.route
    }
    data object Settings : NavDestination {
        override val route: String = Screen.Settings.route
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as GsmApplication
            val viewModel: GsmViewModel = viewModel(
                factory = GsmViewModelFactory(app)
            )

            val userSettings by viewModel.userSettings.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (userSettings.themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            val appLanguage = userSettings.appLanguage
            val appStrings = remember(appLanguage) { getAppStrings(appLanguage) }

            CompositionLocalProvider(
                LocalAppLanguage provides appLanguage,
                LocalAppStrings provides appStrings
            ) {
                GsmServiceTheme(darkTheme = isDarkTheme) {
                    GsmMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GsmMainApp(
    viewModel: GsmViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    // Determine initial destination based on stored auth role
    val initialDestination = remember(currentUser.role) {
        when (currentUser.role) {
            UserRole.CUSTOMER -> NavDestination.CustomerDashboard
            UserRole.ADMIN -> NavDestination.AdminDashboard
            UserRole.UNAUTHENTICATED -> NavDestination.CustomerLogin
        }
    }

    val backStack = remember { mutableStateListOf<NavDestination>(initialDestination) }
    val currentDestination = backStack.lastOrNull() ?: initialDestination

    val pendingCount by viewModel.pendingCount.collectAsState()

    val isRootTab = currentDestination is NavDestination.Home ||
            currentDestination is NavDestination.Diagram ||
            currentDestination is NavDestination.Lcd ||
            currentDestination is NavDestination.Model ||
            currentDestination is NavDestination.File ||
            currentDestination is NavDestination.Customer

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

    fun navigateTo(dest: NavDestination) {
        if (dest is NavDestination.Home ||
            dest is NavDestination.Diagram ||
            dest is NavDestination.Lcd ||
            dest is NavDestination.Model ||
            dest is NavDestination.File ||
            dest is NavDestination.Customer ||
            dest is NavDestination.CustomerLogin ||
            dest is NavDestination.CustomerDashboard ||
            dest is NavDestination.AdminDashboard
        ) {
            backStack.clear()
            backStack.add(dest)
        } else {
            backStack.add(dest)
        }
    }

    fun navigateByRoute(route: String) {
        when (route) {
            Screen.Home.route -> navigateTo(NavDestination.Home)
            Screen.CustomerLogin.route -> navigateTo(NavDestination.CustomerLogin)
            Screen.CreateAccount.route -> navigateTo(NavDestination.CreateAccount)
            Screen.CustomerDashboard.route -> navigateTo(NavDestination.CustomerDashboard)
            Screen.CustomerProfileDetails.route -> navigateTo(NavDestination.CustomerProfileDetails)
            Screen.AdminLogin.route -> navigateTo(NavDestination.AdminLogin)
            Screen.AdminDashboard.route -> navigateTo(NavDestination.AdminDashboard)
            Screen.AdminCustomerManagement.route -> navigateTo(NavDestination.AdminCustomerManagement)
            Screen.Diagram.route -> navigateTo(NavDestination.Diagram)
            Screen.Lcd.route -> navigateTo(NavDestination.Lcd)
            Screen.Model.route -> navigateTo(NavDestination.Model)
            Screen.File.route -> navigateTo(NavDestination.File)
            Screen.Customer.route -> navigateTo(NavDestination.Customer)
            Screen.AddEditCustomer.route -> navigateTo(NavDestination.AddEditCustomer(null))
            Screen.GlobalSearch.route -> navigateTo(NavDestination.GlobalSearch)
            Screen.Settings.route -> navigateTo(NavDestination.Settings)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (isRootTab) {
                val userSettings by viewModel.userSettings.collectAsState()
                GsmTopBar(
                    pendingCount = pendingCount,
                    currentLanguage = userSettings.appLanguage,
                    onLanguageChange = { lang -> viewModel.setAppLanguage(lang) },
                    onLogoClick = { navigateTo(NavDestination.Home) },
                    onSearchClick = { navigateTo(NavDestination.GlobalSearch) },
                    onNotificationClick = {
                        viewModel.setStatusFilter("Received")
                        navigateTo(NavDestination.Customer)
                    },
                    onSettingsClick = { navigateTo(NavDestination.Settings) }
                )
            }
        },
        bottomBar = {
            if (isRootTab) {
                GsmBottomBar(
                    currentRoute = currentDestination.route,
                    onNavigate = { route -> navigateByRoute(route) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isRootTab) innerPadding else androidx.compose.foundation.layout.PaddingValues(0.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val dest = currentDestination) {
                // ========================================
                // AUTH & CUSTOMER / ADMIN PORTALS
                // ========================================
                is NavDestination.CustomerLogin -> {
                    CustomerLoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { navigateTo(NavDestination.CustomerDashboard) },
                        onCreateAccountClick = { navigateTo(NavDestination.CreateAccount) },
                        onAdminLoginClick = { navigateTo(NavDestination.AdminLogin) },
                        onGuestBypass = { navigateTo(NavDestination.Home) }
                    )
                }

                is NavDestination.CreateAccount -> {
                    CreateCustomerAccountScreen(
                        viewModel = viewModel,
                        onAccountCreated = { navigateTo(NavDestination.CustomerDashboard) },
                        onBackToLogin = { navigateTo(NavDestination.CustomerLogin) }
                    )
                }

                is NavDestination.CustomerDashboard -> {
                    CustomerDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { navigateTo(NavDestination.CustomerProfileDetails) },
                        onLogout = { navigateTo(NavDestination.CustomerLogin) }
                    )
                }

                is NavDestination.CustomerProfileDetails -> {
                    CustomerProfileDetailsScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            } else {
                                navigateTo(NavDestination.CustomerDashboard)
                            }
                        }
                    )
                }

                is NavDestination.AdminLogin -> {
                    AdminLoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { navigateTo(NavDestination.AdminDashboard) },
                        onBackToCustomerLogin = { navigateTo(NavDestination.CustomerLogin) }
                    )
                }

                is NavDestination.AdminDashboard -> {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToCustomerManagement = { navigateTo(NavDestination.AdminCustomerManagement) },
                        onNavigateToAddCustomer = { navigateTo(NavDestination.AddEditCustomer(null)) },
                        onNavigateToDevices = { navigateTo(NavDestination.Customer) },
                        onNavigateToDiagrams = { navigateTo(NavDestination.Diagram) },
                        onNavigateToFiles = { navigateTo(NavDestination.File) },
                        onNavigateToLcd = { navigateTo(NavDestination.Lcd) },
                        onNavigateToModels = { navigateTo(NavDestination.Model) },
                        onNavigateToSettings = { navigateTo(NavDestination.Settings) },
                        onLogout = { navigateTo(NavDestination.CustomerLogin) }
                    )
                }

                is NavDestination.AdminCustomerManagement -> {
                    AdminCustomerManagementScreen(
                        viewModel = viewModel,
                        onNavigateToAddCustomer = { navigateTo(NavDestination.AddEditCustomer(null)) },
                        onNavigateToEditCustomer = { id -> navigateTo(NavDestination.AddEditCustomer(id)) },
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            } else {
                                navigateTo(NavDestination.AdminDashboard)
                            }
                        }
                    )
                }

                // ========================================
                // CORE WORKBENCH & TECHNICAL SCHEMATICS
                // ========================================
                is NavDestination.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateTo = { route -> navigateByRoute(route) },
                        onCustomerSelected = { id -> navigateTo(NavDestination.CustomerDetail(id)) },
                        onAddNewCustomer = { navigateTo(NavDestination.AddEditCustomer(null)) },
                        onBrandClicked = { brand ->
                            viewModel.setSelectedModelBrand(brand)
                            navigateTo(NavDestination.Model)
                        }
                    )
                }

                is NavDestination.Diagram -> {
                    DiagramScreen(
                        viewModel = viewModel
                    )
                }

                is NavDestination.Lcd -> {
                    LcdScreen(
                        viewModel = viewModel
                    )
                }

                is NavDestination.Model -> {
                    ModelScreen(
                        viewModel = viewModel
                    )
                }

                is NavDestination.File -> {
                    FileScreen(
                        viewModel = viewModel
                    )
                }

                is NavDestination.Customer -> {
                    CustomerScreen(
                        viewModel = viewModel,
                        onAddNewCustomer = { navigateTo(NavDestination.AddEditCustomer(null)) },
                        onCustomerClick = { id -> navigateTo(NavDestination.CustomerDetail(id)) },
                        onEditCustomer = { id -> navigateTo(NavDestination.AddEditCustomer(id)) }
                    )
                }

                is NavDestination.AddEditCustomer -> {
                    AddEditCustomerScreen(
                        viewModel = viewModel,
                        customerId = dest.customerId,
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            } else {
                                navigateTo(NavDestination.Customer)
                            }
                        }
                    )
                }

                is NavDestination.CustomerDetail -> {
                    CustomerDetailScreen(
                        customerId = dest.customerId,
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            } else {
                                navigateTo(NavDestination.Customer)
                            }
                        },
                        onEditCustomer = { id -> navigateTo(NavDestination.AddEditCustomer(id)) }
                    )
                }

                is NavDestination.GlobalSearch -> {
                    GlobalSearchScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            } else {
                                navigateTo(NavDestination.Home)
                            }
                        },
                        onCustomerSelected = { id -> navigateTo(NavDestination.CustomerDetail(id)) },
                        onNavigateToScreen = { route -> navigateByRoute(route) }
                    )
                }

                is NavDestination.Settings -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            } else {
                                navigateTo(NavDestination.Home)
                            }
                        }
                    )
                }
            }
        }
    }
}
