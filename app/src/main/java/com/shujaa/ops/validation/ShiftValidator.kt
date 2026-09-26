package com.shujaa.ops.validation

import java.time.LocalDate

class ShiftValidator {
    fun validateShiftName(shiftName: String): Boolean {
        val valid = listOf("Morning", "Afternoon", "Night")
        return shiftName in valid
    }

    fun validateProductionEntry(
        target: Double,
        actual: Double,
        good: Double,
        rejected: Double,
        waste: Double,
        downtimeMinutes: Int
    ): Boolean {
        return target >= 0 && actual >= 0 && good >= 0 && rejected >= 0 && waste >= 0 && downtimeMinutes >= 0
    }

    fun validateDateIsCurrentOrPast(date: String): Boolean {
        return try {
            LocalDate.parse(date).isBefore(LocalDate.now().plusDays(1))
        } catch (_: Exception) {
            false
        }
    }
}
