package com.shujaa.ops.data

import com.shujaa.ops.data.local.entities.BreakdownEntity
import com.shujaa.ops.data.local.entities.MaintenanceJobEntity
import com.shujaa.ops.data.local.entities.ProductionRecordEntity
import com.shujaa.ops.domain.OEEMetrics
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsService {
    fun calculateDailyOEE(productions: List<ProductionRecordEntity>): Double {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val todayProductions = productions.filter { it.date == today }
        if (todayProductions.isEmpty()) return 0.0

        val plannedMinutes = 480.0
        val downtimeMinutes = todayProductions.sumOf { it.downtimeMinutes }
        val availability = (plannedMinutes - downtimeMinutes) / plannedMinutes

        val totalActual = todayProductions.sumOf { it.actual }
        val totalGood = todayProductions.sumOf { it.good }
        val quality = if (totalActual > 0) totalGood / totalActual else 0.0
        val performance = quality

        return (availability * performance * quality).coerceIn(0.0, 1.0)
    }

    fun getMachineBreakdownFrequency(breakdowns: List<BreakdownEntity>, machineId: String): Int {
        return breakdowns.count { it.machineId == machineId && it.status == "CLOSED" }
    }

    fun getMachineDowntime(productions: List<ProductionRecordEntity>, machineId: String): Int {
        return productions.filter { it.machineId == machineId }.sumOf { it.downtimeMinutes }
    }

    fun getOpenBreakdownsCount(breakdowns: List<BreakdownEntity>): Int {
        return breakdowns.count { it.status == "OPEN" }
    }

    fun getOverduePMCount(maintenance: List<MaintenanceJobEntity>): Int {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return maintenance.count { it.status == "PENDING" && it.dueDate < today }
    }

    fun getLowStockCount(spareParts: List<com.shujaa.ops.data.local.entities.SparePartEntity>): Int {
        return spareParts.count { it.quantity <= it.minimumQuantity }
    }
}
