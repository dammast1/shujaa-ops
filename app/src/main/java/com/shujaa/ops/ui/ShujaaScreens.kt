package com.shujaa.ops.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shujaa.ops.data.local.entities.*
import com.shujaa.ops.ui.AppScreen
import com.shujaa.ops.ui.ShujaViewModel
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
            Text(
                text = "SHUJAA OPS",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Digital Manufacturing Management System",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = { onLogin(UserRole.ADMIN) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login as ADMIN")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onLogin(UserRole.SUPERVISOR) },
                modifier = Modifier.fillMaxWidth()
            ) {
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Factory dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(title = "Machines", value = machines.size.toString(), color = Color(0xFF4CAF50))
                StatCard(title = "Open breakdowns", value = breakdowns.count { it.status == "OPEN" }.toString(), color = Color(0xFFF44336))
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(title = "Pending tasks", value = tasks.count { it.status == "PENDING" }.toString(), color = Color(0xFFFF9800))
                StatCard(title = "Today production", value = production.sumOf { it.actual.toInt() }.toString(), color = Color(0xFF2196F3))
            }
        }

        item {
            Text("Alerts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            val lowStock = spareParts.count { it.quantity <= it.minimumQuantity }
            AlertBanner("Low stock items: $lowStock", if (lowStock > 0) Color(0xFFFFF3CD) else Color(0xFFE8F5E9))
            AlertBanner("Active PM jobs: ${viewModel.maintenance.value?.size ?: 0}", Color(0xFFE3F2FD))
        }

        item {
            Text("Quick actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickActionButton("Report breakdown")
                QuickActionButton("Record production")
            }
        }
    }
}

@Composable
fun MachinesScreen(viewModel: ShujaaViewModel, onNavigate: (String) -> Unit) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    val searchable = remember { mutableStateOf("") }
    val filteredMachines = machines.filter {
        it.name.contains(searchable.value, ignoreCase = true) ||
            it.machineId.contains(searchable.value, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Machines", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = searchable.value,
            onValueChange = { searchable.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search machine name or ID") }
        )
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
    val selected = machines.find { it.id == machineId }

    if (selected == null) {
        Text("Machine not found")
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(selected.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Machine ID: ${selected.machineId}")
        Text("Category: ${selected.category}")
        Text("Location: ${selected.location}")
        Text("Status: ${selected.status}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { /* future */ }) { Text("View history") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { /* future */ }) { Text("Open breakdown") }
    }
}

@Composable
fun ProductionScreen(viewModel: ShujaaViewModel) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    var selectedMachine by remember { mutableStateOf(machines.firstOrNull()?.id ?: "") }
    var productName by remember { mutableStateOf("Chainlink Mesh") }
    var target by remember { mutableStateOf("950") }
    var actual by remember { mutableStateOf("920") }
    var good by remember { mutableStateOf("890") }
    var rejected by remember { mutableStateOf("15") }
    var waste by remember { mutableStateOf("15") }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Production entry", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        if (machines.isNotEmpty()) {
            OutlinedTextField(selectedMachine, { selectedMachine = it }, label = { Text("Machine ID") }, modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(productName, { productName = it }, label = { Text("Product") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(target, { target = it }, label = { Text("Target") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(actual, { actual = it }, label = { Text("Actual") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(good, { good = it }, label = { Text("Good") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(rejected, { rejected = it }, label = { Text("Rejected") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(waste, { waste = it }, label = { Text("Waste") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                viewModel.addProduction(
                    ProductionRecordEntity(
                        machineId = selectedMachine,
                        productName = productName,
                        shiftName = "Morning",
                        date = date,
                        target = target.toDoubleOrNull() ?: 0.0,
                        actual = actual.toDoubleOrNull() ?: 0.0,
                        good = good.toDoubleOrNull() ?: 0.0,
                        rejected = rejected.toDoubleOrNull() ?: 0.0,
                        waste = waste.toDoubleOrNull() ?: 0.0,
                        downtimeMinutes = 0
                    )
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
        OutlinedTextField(machineId, { machineId = it }, label = { Text("Machine ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(problem, { problem = it }, label = { Text("Problem") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(description, { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                viewModel.addBreakdown(
                    BreakdownEntity(
                        machineId = machineId,
                        machineName = machines.find { it.id == machineId }?.name ?: "Unknown",
                        problem = problem,
                        priority = "HIGH",
                        description = description,
                        status = "OPEN",
                        reportedAt = today,
                        syncState = "PENDING_SYNC"
                    )
                )
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
        Button(onClick = { viewModel.createDemoData() }, modifier = Modifier.fillMaxWidth()) {
            Text("Seed demo data")
        }
        Button(onClick = { /* future config */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Manage machines")
        }
        Button(onClick = { /* future config */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Manage products")
        }
        Button(onClick = { /* future config */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Manage spare parts")
        }
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(tint)
            .padding(16.dp)
    ) {
        Text(message)
    }
}

@Composable
private fun QuickActionButton(label: String) {
    Card(modifier = Modifier.clickable { /* future */ }) {
        Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
            Text(label)
        }
    }
}
