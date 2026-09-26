package com.shujaa.ops.alerts

import java.time.LocalDate

data class AlertItem(
    val title: String,
    val message: String,
    val priority: String,
    val date: String = LocalDate.now().toString()
)

class AlertService {
    fun createLowStockAlert(partName: String, quantity: Int, minimum: Int): AlertItem {
        return AlertItem(
            title = "Low stock alert",
            message = "$partName has $quantity units left. Minimum is $minimum.",
            priority = "HIGH"
        )
    }

    fun createBreakdownAlert(machineName: String): AlertItem {
        return AlertItem(
            title = "Breakdown alert",
            message = "$machineName requires immediate attention.",
            priority = "URGENT"
        )
    }

    fun createOverduePMAlert(machineName: String): AlertItem {
        return AlertItem(
            title = "Preventive maintenance overdue",
            message = "$machineName preventive maintenance is overdue.",
            priority = "MEDIUM"
        )
    }
}
