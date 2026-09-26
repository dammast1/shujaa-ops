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
import androidx.compose.ui.unit.sp
import com.shujaa.ops.domain.OEECalculator
import com.shujaa.ops.ui.ShujaaViewModel

@Composable
fun OEEReportScreen(viewModel: ShujaaViewModel) {
    val production by viewModel.production.collectAsState(initial = emptyList())
    val calculator = OEECalculator()
    val metrics = calculator.calculateOEE(production)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("OEE Report (Today)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OEEMetricRow("Overall OEE", (metrics.oee * 100).toInt(), Color(0xFF4CAF50))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    OEEMetricRow("Availability", (metrics.availability * 100).toInt(), Color(0xFF2196F3))
                    OEEMetricRow("Performance", (metrics.performance * 100).toInt(), Color(0xFFFF9800))
                    OEEMetricRow("Quality", (metrics.quality * 100).toInt(), Color(0xFF9C27B0))
                }
            }
        }

        item {
            Text("Target: 85% | Actual: ${(metrics.oee * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
            val status = when {
                metrics.oee >= 0.85 -> "✓ Target achieved"
                metrics.oee >= 0.70 -> "~ Acceptable"
                else -> "✗ Below target"
            }
            Text(status, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun OEEMetricRow(label: String, percentage: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold)
        Text("$percentage%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun MachineHistoryScreen(viewModel: ShujaaViewModel, machineId: String) {
    val production by viewModel.production.collectAsState(initial = emptyList())
    val breakdowns by viewModel.breakdowns.collectAsState(initial = emptyList())
    val maintenance by viewModel.maintenance.collectAsState(initial = emptyList())

    val machineProductions = production.filter { it.machineId == machineId }
    val machineBreakdowns = breakdowns.filter { it.machineId == machineId }
    val machineMaintenance = maintenance.filter { it.machineId == machineId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Machine History", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Text("Production Records", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(machineProductions.take(5)) { prod ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${prod.date} - ${prod.productName}", fontWeight = FontWeight.Bold)
                    Text("Target: ${prod.target} | Actual: ${prod.actual}")
                }
            }
        }

        item {
            Text("Breakdowns", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(machineBreakdowns.take(5)) { breakdown ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(breakdown.problem, fontWeight = FontWeight.Bold)
                    Text("Status: ${breakdown.status} | Priority: ${breakdown.priority}")
                }
            }
        }

        item {
            Text("Maintenance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(machineMaintenance.take(5)) { maint ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(maint.title, fontWeight = FontWeight.Bold)
                    Text("Type: ${maint.type} | Status: ${maint.status}")
                }
            }
        }
    }
}
