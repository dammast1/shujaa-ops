package com.shujaa.ops.domain

import com.shujaa.ops.data.local.entities.BreakdownEntity
import com.shujaa.ops.data.local.entities.MaintenanceJobEntity
import com.shujaa.ops.data.local.entities.TaskEntity

class WorkflowEngine {
    fun createTaskFromIssue(machineId: String, issue: String, priority: String = "MEDIUM"): TaskEntity {
        return TaskEntity(
            title = "Investigate: $issue",
            description = issue,
            machineName = machineId,
            assignedTo = "",
            priority = priority,
            status = "PENDING",
            dueDate = ""
        )
    }

    fun createMaintenanceFromBreakdown(breakdown: BreakdownEntity): MaintenanceJobEntity {
        return MaintenanceJobEntity(
            machineId = breakdown.machineId,
            machineName = breakdown.machineName,
            title = "Corrective maintenance: ${breakdown.problem}",
            type = "CORRECTIVE",
            status = "PENDING",
            priority = breakdown.priority,
            dueDate = breakdown.reportedAt,
            notes = "Breakdown reported: ${breakdown.description}"
        )
    }

    fun getBreakdownResolutionStatus(breakdown: BreakdownEntity): String {
        return when (breakdown.status) {
            "OPEN" -> "URGENT: Open breakdown"
            "IN_PROGRESS" -> "In repair"
            "RESOLVED" -> "Repaired, pending closure"
            "CLOSED" -> "Completed"
            else -> "Unknown"
        }
    }
}
