package com.shujaa.ops.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shujaa.ops.data.local.entities.*
import com.shujaa.ops.ui.ShujaaViewModel
import com.shujaa.ops.ui.UserRole
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun LoginScreen(onLogin: (UserRole) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "SHUJAA OPS", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Digital Manufacturing Management System", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(36.dp))
            Button(onClick = { onLogin(UserRole.ADMIN) }, modifier = Modifier.fillMaxWidth()) {
                Text("Login as ADMIN")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = { onLogin(UserRole.SUPERVISOR) }, modifier = Modifier.fillMaxWidth()) {
                Text("Login as SUPERVISOR")
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: ShujaaViewModel) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    val breakdowns by viewModel.breakdowns.collectAsState(initial = emptyList())
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val production by viewModel.production.collectAsState(initial = emptyList())
    val spareParts by viewModel.spareParts.collectAsState(initial = emptyList())
    val maintenance by viewModel.maintenance.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Factory dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(title = "Machines", value = machines.size.toString(), color = Color(0xFF4CAF50))
                StatCard(title = "Open breakdowns", value = breakdowns.count { it.status == "OPEN" }.toString(), color = Color(0xFFF44336))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(title = "Pending tasks", value = tasks.count { it.status == "PENDING" }.toString(), color = Color(0xFFFF9800))
                StatCard(title = "Today production", value = production.sumOf { it.actual.toInt() }.toString(), color = Color(0xFF2196F3))
            }
        }
        item {
            Text("Alerts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            val lowStock = spareParts.count { it.quantity <= it.minimumQuantity }
            AlertBanner("Low stock items: $lowStock", if (lowStock > 0) Color(0xFFFFF3CD) else Color(0xFFE8F5E9))
            AlertBanner("Active PM jobs: ${maintenance.size}", Color(0xFFE3F2FD))
        }
    }
}

@Composable
fun MachinesScreen(viewModel: ShujaaViewModel, onNavigate: (String) -> Unit) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    val query = remember { mutableStateOf("") }
    val filteredMachines = machines.filter {
        it.name.contains(query.value, ignoreCase = true) || it.machineId.contains(query.value, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Machines", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = query.value, onValueChange = { query.value = it }, placeholder = { Text("Search machine name or ID") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredMachines) { machine ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onNavigate(machine.id) }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(machine.name, fontWeight = FontWeight.Bold)
                            Text("ID: ${machine.machineId} | ${machine.category} | ${machine.location}")
                        }
                        Text(machine.status)
                    }
                }
            }
        }
    }
}

@Composable
fun MachineDetailsScreen(viewModel: ShujaaViewModel, machineId: String) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    val machine = machines.find { it.id == machineId }

    if (machine == null) {
        Text("Machine not found")
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(machine.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Machine ID: ${machine.machineId}")
        Text("Category: ${machine.category}")
        Text("Location: ${machine.location}")
        Text("Status: ${machine.status}")
        Spacer(Modifier.height(8.dp))
        Button(onClick = { /* future history */ }, modifier = Modifier.fillMaxWidth()) { Text("View history") }
        Button(onClick = { /* future maintenance */ }, modifier = Modifier.fillMaxWidth()) { Text("Open breakdown") }
    }
}

@Composable
fun ProductionScreen(viewModel: ShujaaViewModel) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    var machineId by remember { mutableStateOf(machines.firstOrNull()?.id ?: "") }
    var productName by remember { mutableStateOf("Chainlink Mesh") }
    var shiftName by remember { mutableStateOf("Morning") }
    var target by remember { mutableStateOf("950") }
    var actual by remember { mutableStateOf("920") }
    var good by remember { mutableStateOf("890") }
    var rejected by remember { mutableStateOf("15") }
    var waste by remember { mutableStateOf("15") }
    var downtime by remember { mutableStateOf("32") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Production entry", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = machineId, onValueChange = { machineId = it }, label = { Text("Machine ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Product") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = shiftName, onValueChange = { shiftName = it }, label = { Text("Shift") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = actual, onValueChange = { actual = it }, label = { Text("Actual") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = good, onValueChange = { good = it }, label = { Text("Good") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rejected, onValueChange = { rejected = it }, label = { Text("Rejected") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = waste, onValueChange = { waste = it }, label = { Text("Waste") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = downtime, onValueChange = { downtime = it }, label = { Text("Downtime (minutes)") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                viewModel.submitProduction(
                    machineId = machineId,
                    productName = productName,
                    shiftName = shiftName,
                    target = target.toDoubleOrNull() ?: 0.0,
                    actual = actual.toDoubleOrNull() ?: 0.0,
                    good = good.toDoubleOrNull() ?: 0.0,
                    rejected = rejected.toDoubleOrNull() ?: 0.0,
                    waste = waste.toDoubleOrNull() ?: 0.0,
                    downtimeMinutes = downtime.toIntOrNull() ?: 0
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save production record") }
    }
}

@Composable
fun BreakdownScreen(viewModel: ShujaaViewModel) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    var machineId by remember { mutableStateOf(machines.firstOrNull()?.id ?: "") }
    var problem by remember { mutableStateOf("Drive motor failure") }
    var description by remember { mutableStateOf("Machine stopped unexpectedly during operation") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Report breakdown", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = machineId, onValueChange = { machineId = it }, label = { Text("Machine ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = problem, onValueChange = { problem = it }, label = { Text("Problem") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                val machineName = machines.find { it.id == machineId }?.name ?: "Unknown"
                viewModel.submitBreakdown(machineId, machineName, problem, description)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Submit breakdown") }
    }
}

@Composable
fun MaintenanceScreen(viewModel: ShujaaViewModel) {
    val maintenance by viewModel.maintenance.collectAsState(initial = emptyList())
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Preventive & corrective maintenance", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        items(maintenance) { job ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(job.title, fontWeight = FontWeight.Bold)
                    Text(job.machineName)
                    Text("Type: ${job.type} | Status: ${job.status}")
                    Text("Priority: ${job.priority}")
                }
            }
        }
    }
}

@Composable
fun StockScreen(viewModel: ShujaaViewModel) {
    val parts by viewModel.spareParts.collectAsState(initial = emptyList())
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Stock & Spare parts", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        items(parts) { part ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(part.name, fontWeight = FontWeight.Bold)
                    Text("Part number: ${part.partNumber} | Qty: ${part.quantity} ${part.unit}")
                    Text("Min: ${part.minimumQuantity} | Location: ${part.storageLocation}")
                }
            }
        }
    }
}

@Composable
fun ReportsScreen(viewModel: ShujaaViewModel) {
    val production by viewModel.production.collectAsState(initial = emptyList())
    val totalProduction = production.sumOf { it.actual.toInt() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reports", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Production total today: $totalProduction")
        Text("OEE view: availability, performance, quality will be calculated from production and downtime data.")
    }
}

@Composable
fun AdminScreen(viewModel: ShujaaViewModel) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    val role by viewModel.userRole.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Admin configuration", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Current role: ${role ?: "Not logged in"}")
        Button(onClick = { viewModel.createDemoData() }, modifier = Modifier.fillMaxWidth()) { Text("Seed demo data") }
        Button(onClick = { /* future config */ }, modifier = Modifier.fillMaxWidth()) { Text("Manage machines") }
        Button(onClick = { /* future config */ }, modifier = Modifier.fillMaxWidth()) { Text("Manage products") }
        Button(onClick = { /* future config */ }, modifier = Modifier.fillMaxWidth()) { Text("Manage spare parts") }
        Text("Configured machine count: ${machines.size}")
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color) {
    Card(modifier = Modifier.weight(1f)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun AlertBanner(message: String, tint: Color) {
    Box(modifier = Modifier.fillMaxWidth().background(tint).padding(16.dp)) {
        Text(message)
    }
}
