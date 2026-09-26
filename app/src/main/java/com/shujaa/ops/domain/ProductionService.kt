package com.shujaa.ops.domain

import com.shujaa.ops.data.local.entities.BreakdownEntity
import com.shujaa.ops.data.local.entities.MaintenanceJobEntity
import com.shujaa.ops.data.local.entities.ProductionRecordEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProductionService {
    fun createBreakdownMaintenanceJob(breakdown: BreakdownEntity): MaintenanceJobEntity {
        return MaintenanceJobEntity(
            machineId = breakdown.machineId,
            machineName = breakdown.machineName,
            title = "Corrective maintenance: ${breakdown.problem}",
            type = "CORRECTIVE",
            status = "PENDING",
            priority = breakdown.priority,
            dueDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            notes = "Breakdown ID: ${breakdown.id}. Issue: ${breakdown.description}"
        )
    }

    fun calculateShiftOEE(
        productions: List<ProductionRecordEntity>,
        shiftName: String,
        date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    ): Double {
        val shiftProductions = productions.filter { it.shiftName == shiftName && it.date == date }
        if (shiftProductions.isEmpty()) return 0.0

        val shiftMinutes = 480.0
        val downtimeMinutes = shiftProductions.sumOf { it.downtimeMinutes }
        val availability = (shiftMinutes - downtimeMinutes) / shiftMinutes

        val totalActual = shiftProductions.sumOf { it.actual }
        val totalGood = shiftProductions.sumOf { it.good }
        val quality = if (totalActual > 0) totalGood / totalActual else 0.0

        return (availability * quality).coerceIn(0.0, 1.0)
    }

    fun isDuplicateProduction(
        existing: List<ProductionRecordEntity>,
        machineId: String,
        date: String,
        shiftName: String,
        actual: Double
    ): Boolean {
        return existing.any {
            it.machineId == machineId &&
            it.date == date &&
            it.shiftName == shiftName &&
            it.actual == actual
        }
    }
}
