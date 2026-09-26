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
import com.shujaa.ops.alerts.AlertService
import com.shujaa.ops.ui.ShujaaViewModel

@Composable
fun AlertsScreen(viewModel: ShujaaViewModel) {
    val spareParts by viewModel.spareParts.collectAsState(initial = emptyList())
    val breakdowns by viewModel.breakdowns.collectAsState(initial = emptyList())
    val maintenance by viewModel.maintenance.collectAsState(initial = emptyList())
    val alertService = AlertService()

    val alerts = buildList {
        spareParts.filter { it.quantity <= it.minimumQuantity }.forEach { part ->
            add(alertService.createLowStockAlert(part.name, part.quantity, part.minimumQuantity))
        }
        breakdowns.filter { it.status == "OPEN" }.forEach { breakdown ->
            add(alertService.createBreakdownAlert(breakdown.machineName))
        }
        maintenance.filter { it.status == "PENDING" }.forEach { job ->
            if (job.priority == "HIGH") add(alertService.createOverduePMAlert(job.machineName))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Alerts & notifications", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        if (alerts.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text("No active alerts")
                    }
                }
            }
        } else {
            items(alerts) { alert ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(alert.title, fontWeight = FontWeight.Bold, color = when (alert.priority) {
                            "URGENT" -> Color(0xFFF44336)
                            "HIGH" -> Color(0xFFFF9800)
                            else -> Color(0xFF2196F3)
                        })
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(alert.message)
                        Text("Priority: ${alert.priority}", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
