package com.shujaa.ops.config

import com.shujaa.ops.data.local.entities.ShiftEntity

class ShiftManager {
    private val defaultShifts = listOf(
        ShiftEntity(name = "Morning", startTime = "06:00", endTime = "14:00"),
        ShiftEntity(name = "Afternoon", startTime = "14:00", endTime = "22:00"),
        ShiftEntity(name = "Night", startTime = "22:00", endTime = "06:00", crossesMidnight = true)
    )

    fun getDefaultShifts(): List<ShiftEntity> = defaultShifts

    fun getCurrentShift(): String {
        val hour = java.time.LocalTime.now().hour
        return when {
            hour >= 6 && hour < 14 -> "Morning"
            hour >= 14 && hour < 22 -> "Afternoon"
            else -> "Night"
        }
    }

    fun isShiftBoundary(hour: Int): Boolean {
        return hour == 6 || hour == 14 || hour == 22
    }
}
