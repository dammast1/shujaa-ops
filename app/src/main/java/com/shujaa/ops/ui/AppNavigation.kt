package com.shujaa.ops.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shujaa.ops.ui.screens.*

sealed class AppScreen(val route: String) {
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
    val navController = rememberNavController()
    val viewModel = androidx.compose.runtime.remember { ShujaaViewModel(LocalContext.current) }
    val currentRole by viewModel.userRole.collectAsState()

    if (currentRole == null) {
        LoginScreen { role -> viewModel.login(role) }
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(true, { navController.navigate(AppScreen.Dashboard.route) }, { Icon(Icons.Default.Home, "Dashboard") }, label = { Text("Home") })
                NavigationBarItem(false, { navController.navigate(AppScreen.Machines.route) }, { Icon(Icons.Default.List, "Machines") }, label = { Text("Machines") })
                NavigationBarItem(false, { navController.navigate(AppScreen.Production.route) }, { Icon(Icons.Default.Inventory, "Production") }, label = { Text("Production") })
                NavigationBarItem(false, { navController.navigate(AppScreen.Maintenance.route) }, { Icon(Icons.Default.Build, "Maintenance") }, label = { Text("PM") })
                NavigationBarItem(false, { navController.navigate(AppScreen.Admin.route) }, { Icon(Icons.Default.Settings, "Admin") }, label = { Text("Admin") })
            }
        }
    ) { padding ->
        NavHost(navController, AppScreen.Dashboard.route, Modifier.fillMaxSize().padding(padding)) {
            composable(AppScreen.Dashboard.route) { DashboardScreen(viewModel) }
            composable(AppScreen.Machines.route) { MachinesScreen(viewModel) { id -> navController.navigate("${AppScreen.MachineDetails.route}/$id") } }
            composable("${AppScreen.MachineDetails.route}/{machineId}") { entry -> MachineDetailsScreen(viewModel, entry.arguments?.getString("machineId").orEmpty()) }
            composable(AppScreen.Production.route) { ProductionScreen(viewModel) }
            composable(AppScreen.Breakdown.route) { BreakdownScreen(viewModel) }
            composable(AppScreen.Maintenance.route) { MaintenanceScreen(viewModel) }
            composable(AppScreen.Stock.route) { StockScreen(viewModel) }
            composable(AppScreen.Reports.route) { ReportsScreen(viewModel) }
            composable(AppScreen.Admin.route) { AdminScreen(viewModel) }
        }
    }
}
