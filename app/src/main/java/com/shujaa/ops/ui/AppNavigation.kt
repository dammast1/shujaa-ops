package com.shujaa.ops.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shujaa.ops.ui.screens.*

sealed class AppScreen(val route: String) {
    data object Login : AppScreen("login")
    data object Dashboard : AppScreen("dashboard")
    data object Machines : AppScreen("machines")
    data object MachineDetails : AppScreen("machine_details")
    data object Production : AppScreen("production")
    data object Breakdown : AppScreen("breakdown")
    data object Maintenance : AppScreen("maintenance")
    data object Stock : AppScreen("stock")
    data object Reports : AppScreen("reports")
    data object Admin : AppScreen("admin")
}

@Composable
fun AppShell() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val viewModel = remember { ShujaaViewModel(context) }
    val currentRole by viewModel.userRole.collectAsState()

    if (currentRole == null) {
        LoginScreen(
            onLogin = { role ->
                viewModel.login(role)
                navController.navigate(AppScreen.Dashboard.route)
            }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == AppScreen.Dashboard.route,
                    onClick = { navController.navigate(AppScreen.Dashboard.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == AppScreen.Machines.route,
                    onClick = { navController.navigate(AppScreen.Machines.route) },
                    icon = { Icon(Icons.Default.List, contentDescription = "Machines") },
                    label = { Text("Machines") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == AppScreen.Stock.route,
                    onClick = { navController.navigate(AppScreen.Stock.route) },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = "Stock") },
                    label = { Text("Stock") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == AppScreen.Maintenance.route,
                    onClick = { navController.navigate(AppScreen.Maintenance.route) },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Maintenance") },
                    label = { Text("PM") }
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == AppScreen.Admin.route,
                    onClick = { navController.navigate(AppScreen.Admin.route) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
                    label = { Text("Admin") }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(AppScreen.Login.route) {
                LoginScreen(onLogin = { role ->
                    viewModel.login(role)
                    navController.navigate(AppScreen.Dashboard.route)
                })
            }
            composable(AppScreen.Dashboard.route) {
                DashboardScreen(viewModel)
            }
            composable(AppScreen.Machines.route) {
                MachinesScreen(viewModel) { machineId ->
                    navController.navigate(AppScreen.MachineDetails.route + "/$machineId")
                }
            }
            composable(AppScreen.MachineDetails.route + "/{machineId}") { backStackEntry ->
                val machineId = backStackEntry.arguments?.getString("machineId") ?: ""
                MachineDetailsScreen(viewModel, machineId)
            }
            composable(AppScreen.Production.route) {
                ProductionScreen(viewModel)
            }
            composable(AppScreen.Breakdown.route) {
                BreakdownScreen(viewModel)
            }
            composable(AppScreen.Maintenance.route) {
                MaintenanceScreen(viewModel)
            }
            composable(AppScreen.Stock.route) {
                StockScreen(viewModel)
            }
            composable(AppScreen.Reports.route) {
                ReportsScreen(viewModel)
            }
            composable(AppScreen.Admin.route) {
                AdminScreen(viewModel)
            }
        }
    }
}
