package com.android.kidstracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminAccountManagement : Screen("admin_account")
    object GuruDashboard : Screen("guru_dashboard")
    object GuruStudentManagement : Screen("guru_student")
    object GuruDevelopmentForm : Screen("guru_form")
    object GuruTaskAndExport : Screen("guru_task")
    object OrtuDashboard : Screen("ortu_dashboard")
    object OrtuDevelopmentResult : Screen("ortu_result")
    object OrtuTaskSubmit : Screen("ortu_task")
    object AdminProfile : Screen("admin_profile")
    object GuruProfile : Screen("guru_profile")
    object OrtuProfile : Screen("ortu_profile")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            com.android.kidstracker.ui.screens.LoginScreen(
                onNavigateToAdmin = { 
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToGuru = { 
                    navController.navigate(Screen.GuruDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToOrtu = { 
                    navController.navigate(Screen.OrtuDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AdminDashboard.route) {
            com.android.kidstracker.ui.screens.AdminDashboardScreen(
                navController = navController,
                onNavigateToAccountManagement = { navController.navigate(Screen.AdminAccountManagement.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AdminDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToGrowth = {
                    // TODO: Admin Growth screen
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.AdminProfile.route) {
                        popUpTo(Screen.AdminDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.AdminAccountManagement.route) {
            com.android.kidstracker.ui.screens.AdminAccountManagementScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Screen.GuruDashboard.route) {
            com.android.kidstracker.ui.screens.GuruDashboardScreen(
                navController = navController,
                onNavigateToMurid = { navController.navigate(Screen.GuruStudentManagement.route) },
                onNavigateToForm = { navController.navigate(Screen.GuruDevelopmentForm.route) },
                onNavigateToTugas = { navController.navigate(Screen.GuruTaskAndExport.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.GuruDashboard.route) {
                        popUpTo(Screen.GuruDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToGrowth = {
                    navController.navigate(Screen.GuruDevelopmentForm.route) {
                        popUpTo(Screen.GuruDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.GuruProfile.route) {
                        popUpTo(Screen.GuruDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.GuruStudentManagement.route) {
            com.android.kidstracker.ui.screens.GuruStudentManagementScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Screen.GuruDevelopmentForm.route) {
            com.android.kidstracker.ui.screens.GuruDevelopmentFormScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Screen.GuruTaskAndExport.route) {
            com.android.kidstracker.ui.screens.GuruTaskAndExportScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Screen.OrtuDashboard.route) {
            com.android.kidstracker.ui.screens.OrtuDashboardScreen(
                navController = navController,
                onNavigateToHasil = { navController.navigate(Screen.OrtuDevelopmentResult.route) },
                onNavigateToTugas = { navController.navigate(Screen.OrtuTaskSubmit.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.OrtuDashboard.route) {
                        popUpTo(Screen.OrtuDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToGrowth = {
                    navController.navigate(Screen.OrtuDevelopmentResult.route) {
                        popUpTo(Screen.OrtuDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.OrtuProfile.route) {
                        popUpTo(Screen.OrtuDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.OrtuDevelopmentResult.route) {
            com.android.kidstracker.ui.screens.OrtuDevelopmentResultScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Screen.OrtuTaskSubmit.route) {
            com.android.kidstracker.ui.screens.OrtuTaskSubmitScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable(Screen.AdminProfile.route) {
            com.android.kidstracker.ui.screens.AdminProfileScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AdminDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToGrowth = {
                    // TODO: Admin Growth screen
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.AdminProfile.route) {
                        popUpTo(Screen.AdminDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.GuruProfile.route) {
            com.android.kidstracker.ui.screens.GuruProfileScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.GuruDashboard.route) {
                        popUpTo(Screen.GuruDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToGrowth = {
                    navController.navigate(Screen.GuruDevelopmentForm.route) {
                        popUpTo(Screen.GuruDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.GuruProfile.route) {
                        popUpTo(Screen.GuruDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.OrtuProfile.route) {
            com.android.kidstracker.ui.screens.OrtuProfileScreen(
                navController = navController,
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.OrtuDashboard.route) {
                        popUpTo(Screen.OrtuDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToGrowth = {
                    navController.navigate(Screen.OrtuDevelopmentResult.route) {
                        popUpTo(Screen.OrtuDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.OrtuProfile.route) {
                        popUpTo(Screen.OrtuDashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
