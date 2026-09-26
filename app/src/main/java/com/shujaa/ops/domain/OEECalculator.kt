package com.shujaa.ops.domain

import com.shujaa.ops.data.local.entities.ProductionRecordEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class OEEMetrics(
    val availability: Double,
    val performance: Double,
    val quality: Double,
    val oee: Double
)

class OEECalculator {
    fun calculateOEE(productions: List<ProductionRecordEntity>, date: String? = null): OEEMetrics {
        val targetDate = date ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dayProductions = productions.filter { it.date == targetDate }

        if (dayProductions.isEmpty()) {
            return OEEMetrics(0.0, 0.0, 0.0, 0.0)
        }

        val plannedMinutes = 480.0
        val downtimeMinutes = dayProductions.sumOf { it.downtimeMinutes }
        val runningMinutes = plannedMinutes - downtimeMinutes

        val availability = if (plannedMinutes > 0) runningMinutes / plannedMinutes else 0.0

        val totalActualProduction = dayProductions.sumOf { it.actual }
        val totalGoodProduction = dayProductions.sumOf { it.good }
        val performance = if (totalActualProduction > 0) totalGoodProduction / totalActualProduction else 0.0

        val totalProduction = dayProductions.sumOf { it.actual }
        val goodProduction = dayProductions.sumOf { it.good }
        val quality = if (totalProduction > 0) goodProduction / totalProduction else 0.0

        val oee = availability * performance * quality

        return OEEMetrics(
            availability = availability.coerceIn(0.0, 1.0),
            performance = performance.coerceIn(0.0, 1.0),
            quality = quality.coerceIn(0.0, 1.0),
            oee = oee.coerceIn(0.0, 1.0)
        )
    }

    fun calculateOEEForMachine(productions: List<ProductionRecordEntity>, machineId: String, date: String? = null): OEEMetrics {
        val machineProductions = productions.filter { it.machineId == machineId }
        return calculateOEE(machineProductions, date)
    }

    fun calculateOEEForShift(productions: List<ProductionRecordEntity>, shift: String, date: String? = null): OEEMetrics {
        val shiftProductions = productions.filter { it.shiftName == shift }
        return calculateOEE(shiftProductions, date)
    }
}
