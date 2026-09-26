package com.shujaa.ops.validation

import com.shujaa.ops.data.local.entities.ProductionRecordEntity

data class ValidationError(val field: String, val message: String)

class ProductionValidator {
    fun validate(record: ProductionRecordEntity): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (record.target < 0) errors.add(ValidationError("target", "Target cannot be negative"))
        if (record.actual < 0) errors.add(ValidationError("actual", "Actual cannot be negative"))
        if (record.good < 0) errors.add(ValidationError("good", "Good cannot be negative"))
        if (record.rejected < 0) errors.add(ValidationError("rejected", "Rejected cannot be negative"))
        if (record.waste < 0) errors.add(ValidationError("waste", "Waste cannot be negative"))
        if (record.downtimeMinutes < 0) errors.add(ValidationError("downtimeMinutes", "Downtime cannot be negative"))

        val sum = record.good + record.rejected + record.waste
        if (sum > record.actual + 1) errors.add(ValidationError("quantities", "Good + Rejected + Waste exceeds Actual"))

        if (record.shiftName !in listOf("Morning", "Afternoon", "Night")) {
            errors.add(ValidationError("shiftName", "Invalid shift name"))
        }

        return errors
    }
}

class BreakdownValidator {
    fun validate(machineId: String, problem: String, description: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (machineId.isBlank()) errors.add(ValidationError("machineId", "Machine ID is required"))
        if (problem.isBlank()) errors.add(ValidationError("problem", "Problem description is required"))
        if (description.isBlank()) errors.add(ValidationError("description", "Detailed description is required"))

        return errors
    }
}
