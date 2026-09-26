package com.shujaa.ops.admin

import com.shujaa.ops.data.local.entities.*

class AdminConfigManager {
    fun createDefaultMachineCategories(): List<String> {
        return listOf(
            "Chainlink",
            "Barbed Wire",
            "BRC (Brick Reinforcement)",
            "Welded Wire",
            "Others"
        )
    }

    fun createDefaultSparePartCategories(): List<String> {
        return listOf(
            "Mechanical",
            "Electrical",
            "Hydraulics",
            "Fasteners",
            "Bearings",
            "Belts",
            "Others"
        )
    }

    fun createDefaultDowntimeReasons(): List<String> {
        return listOf(
            "Mechanical failure",
            "Electrical failure",
            "Material shortage",
            "Changeover",
            "Preventive maintenance",
            "Operator absence",
            "Quality check",
            "Planned stop",
            "Other"
        )
    }

    fun createDefaultUnits(): List<String> {
        return listOf(
            "kg",
            "metres",
            "rolls",
            "pieces",
            "bundles",
            "hours",
            "boxes",
            "tons"
        )
    }
}
