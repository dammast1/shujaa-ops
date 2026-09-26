package com.shujaa.ops.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shujaa.ops.ui.ShujaaViewModel

@Composable
fun StockManagementScreen(viewModel: ShujaaViewModel) {
    val spareParts by viewModel.spareParts.collectAsState(initial = emptyList())
    val stockMovements by viewModel.stockMovements.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Spare parts & stock management", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Text("Critical stock levels", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(spareParts.filter { it.quantity <= it.minimumQuantity }) { part ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(part.name, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                    Text("Qty: ${part.quantity} ${part.unit} (Min: ${part.minimumQuantity})", style = MaterialTheme.typography.bodySmall)
                    Text("Location: ${part.storageLocation}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        item {
            Text("All parts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(spareParts) { part ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(part.name, fontWeight = FontWeight.Bold)
                        Text("${part.quantity} ${part.unit}", color = if (part.quantity <= part.minimumQuantity) Color(0xFFF44336) else Color.Unspecified)
                    }
                    Text("Part #${part.partNumber} | ${part.category}")
                }
            }
        }
    }
}

@Composable
fun ConfigurationScreen(viewModel: ShujaaViewModel) {
    val machines by viewModel.machines.collectAsState(initial = emptyList())
    val spareParts by viewModel.spareParts.collectAsState(initial = emptyList())
    val role by viewModel.userRole.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("System configuration", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Text("Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ConfigSummaryRow("Machines configured", machines.size.toString())
                    ConfigSummaryRow("Spare parts tracked", spareParts.size.toString())
                    ConfigSummaryRow("Current role", role?.name ?: "Unknown")
                    ConfigSummaryRow("Sync status", "Ready")
                }
            }
        }

        item {
            Text("Admin actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Button(onClick = { viewModel.createDemoData() }, modifier = Modifier.fillMaxWidth()) {
                Text("Seed demo data")
            }
        }

        item {
            Button(onClick = { /* Export backup */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Export database backup")
            }
        }

        item {
            Button(onClick = { /* Import backup */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Import database backup")
            }
        }
    }
}

@Composable
private fun ConfigSummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}
