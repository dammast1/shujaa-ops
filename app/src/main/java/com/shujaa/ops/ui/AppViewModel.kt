package com.shujaa.ops.ui

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shujaa.ops.data.AppRepository
import com.shujaa.ops.data.local.entities.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class UserRole { ADMIN, SUPERVISOR }

data class MachineUiModel(
    val id: String,
    val name: String,
    val machineId: String,
    val category: String,
    val location: String,
    val status: String,
    val isActive: Boolean
)

class ShujaaViewModel(context: Context) : ViewModel() {
    private val repository = AppRepository(context)

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole.asStateFlow()

    val machines = repository.machinesFlow
    val production = repository.productionFlow
    val breakdowns = repository.breakdownsFlow
    val maintenance = repository.maintenanceFlow
    val spareParts = repository.sparePartsFlow
    val tasks = repository.tasksFlow

    fun login(role: UserRole) {
        _userRole.value = role
    }

    fun addMachine(machine: MachineEntity) = viewModelScope.launch {
        repository.addMachine(machine)
    }

    fun addProduction(record: ProductionRecordEntity) = viewModelScope.launch {
        repository.addProduction(record)
    }

    fun addBreakdown(breakdown: BreakdownEntity) = viewModelScope.launch {
        repository.addBreakdown(breakdown)
    }

    fun addMaintenance(job: MaintenanceJobEntity) = viewModelScope.launch {
        repository.addMaintenance(job)
    }

    fun addSparePart(part: SparePartEntity) = viewModelScope.launch {
        repository.addSparePart(part)
    }

    fun addTask(task: TaskEntity) = viewModelScope.launch {
        repository.addTask(task)
    }

    fun addStockMovement(movement: StockMovementEntity) = viewModelScope.launch {
        repository.addStockMovement(movement)
    }

    fun createDemoData() {
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            val machine1 = MachineEntity(
                name = "Chainlink 01",
                machineId = "CH-01",
                category = "Chainlink",
                location = "Line A",
                status = "RUNNING",
                syncState = "SYNCED"
            )
            repository.addMachine(machine1)

            val machine2 = MachineEntity(
                name = "Barbed Wire 02",
                machineId = "BW-02",
                category = "Barbed Wire",
                location = "Line B",
                status = "STOPPED",
                syncState = "SYNCED"
            )
            repository.addMachine(machine2)

            val product1 = ProductEntity(
                name = "Chainlink Mesh",
                code = "CL-100",
                category = "Chainlink",
                unit = "kg",
                syncState = "SYNCED"
            )
            repository.addProduction(
                ProductionRecordEntity(
                    machineId = machine1.id,
                    productName = product1.name,
                    shiftName = "Morning",
                    date = today,
                    target = 950.0,
                    actual = 920.0,
                    good = 890.0,
                    rejected = 15.0,
                    waste = 15.0,
                    downtimeMinutes = 32,
                    syncState = "SYNCED"
                )
            )

            repository.addSparePart(
                SparePartEntity(
                    name = "Bearing 6205",
                    partNumber = "6205",
                    category = "Mechanical",
                    quantity = 9,
                    minimumQuantity = 5,
                    unit = "pcs",
                    storageLocation = "Warehouse A",
                    compatibleMachines = "Chainlink 01",
                    syncState = "SYNCED"
                )
            )

            repository.addBreakdown(
                BreakdownEntity(
                    machineId = machine2.id,
                    machineName = machine2.name,
                    problem = "Drive motor overheated",
                    priority = "HIGH",
                    description = "Operator reported abnormal heat.",
                    status = "OPEN",
                    reportedAt = today,
                    syncState = "SYNCED"
                )
            )

            repository.addMaintenance(
                MaintenanceJobEntity(
                    machineId = machine1.id,
                    machineName = machine1.name,
                    title = "Scheduled lubrication check",
                    type = "PM",
                    status = "PENDING",
                    priority = "MEDIUM",
                    dueDate = today,
                    notes = "Verify chain drive and lubrication points.",
                    syncState = "SYNCED"
                )
            )

            repository.addTask(
                TaskEntity(
                    title = "Inspect bearing 6205",
                    description = "Check vibration and temperature.",
                    machineName = machine2.name,
                    assignedTo = "A. Hassan",
                    priority = "HIGH",
                    status = "PENDING",
                    dueDate = today,
                    syncState = "SYNCED"
                )
            )
        }
    }
}
