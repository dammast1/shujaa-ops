package com.shujaa.ops.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "machines",
    indices = [Index(value = ["machineId"], unique = true)]
)
data class MachineEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val machineId: String,
    val category: String,
    val location: String,
    val status: String = "RUNNING",
    val isActive: Boolean = true,
    val syncState: String = "SYNCED"
)

@Entity(
    tableName = "products",
    indices = [Index(value = ["code"], unique = true)]
)
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val code: String,
    val category: String,
    val unit: String,
    val isActive: Boolean = true,
    val syncState: String = "SYNCED"
)

@Entity(
    tableName = "spare_parts",
    indices = [Index(value = ["partNumber"], unique = true)]
)
data class SparePartEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val partNumber: String,
    val category: String,
    val quantity: Int,
    val minimumQuantity: Int,
    val unit: String,
    val storageLocation: String,
    val compatibleMachines: String = "",
    val syncState: String = "SYNCED"
)

@Entity(
    tableName = "production_records",
    indices = [Index(value = ["machineId", "date", "shiftName"], unique = false)]
)
data class ProductionRecordEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val machineId: String,
    val productName: String,
    val shiftName: String,
    val date: String,
    val target: Double,
    val actual: Double,
    val good: Double,
    val rejected: Double,
    val waste: Double,
    val downtimeMinutes: Int,
    val syncState: String = "PENDING_SYNC"
)

@Entity(tableName = "breakdowns")
data class BreakdownEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val machineId: String,
    val machineName: String,
    val problem: String,
    val priority: String,
    val description: String,
    val status: String = "OPEN",
    val reportedAt: String,
    val syncState: String = "PENDING_SYNC"
)

@Entity(tableName = "maintenance_jobs")
data class MaintenanceJobEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val machineId: String,
    val machineName: String,
    val title: String,
    val type: String,
    val status: String,
    val priority: String,
    val dueDate: String,
    val notes: String = "",
    val syncState: String = "PENDING_SYNC"
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val machineName: String,
    val assignedTo: String,
    val priority: String,
    val status: String,
    val dueDate: String,
    val syncState: String = "PENDING_SYNC"
)

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sparePartId: String,
    val partName: String,
    val movementType: String,
    val quantity: Int,
    val reason: String,
    val date: String,
    val syncState: String = "PENDING_SYNC"
)
