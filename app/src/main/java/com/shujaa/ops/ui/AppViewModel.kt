package com.shujaa.ops.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shujaa.ops.data.AppRepository
import com.shujaa.ops.data.local.entities.*
import com.shujaa.ops.validation.ProductionValidator
import com.shujaa.ops.validation.BreakdownValidator
import com.shujaa.ops.validation.ValidationError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class UserRole { ADMIN, SUPERVISOR }

class ShujaaViewModel(context: Context) : ViewModel() {
    private val repository = AppRepository(context)
    private val productionValidator = ProductionValidator()
    private val breakdownValidator = BreakdownValidator()

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole.asStateFlow()

    private val _validationErrors = MutableStateFlow<List<ValidationError>>(emptyList())
    val validationErrors: StateFlow<List<ValidationError>> = _validationErrors.asStateFlow()

    val machines = repository.machinesFlow
    val production = repository.productionFlow
    val breakdowns = repository.breakdownsFlow
    val maintenance = repository.maintenanceFlow
    val spareParts = repository.sparePartsFlow
    val tasks = repository.tasksFlow
    val stockMovements = repository.stockMovementsFlow

    fun login(role: UserRole) {
        _userRole.value = role
    }

    fun submitProduction(
        machineId: String,
        productName: String,
        shiftName: String,
        target: Double,
        actual: Double,
        good: Double,
        rejected: Double,
        waste: Double,
        downtimeMinutes: Int
    ) {
        val record = ProductionRecordEntity(
            machineId = machineId,
            productName = productName,
            shiftName = shiftName,
            date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            target = target,
            actual = actual,
            good = good,
            rejected = rejected,
            waste = waste,
            downtimeMinutes = downtimeMinutes,
            syncState = "PENDING_SYNC"
        )

        val errors = productionValidator.validate(record)
        if (errors.isNotEmpty()) {
            _validationErrors.value = errors
            return
        }

        _validationErrors.value = emptyList()
        viewModelScope.launch {
            repository.addProduction(record)
        }
    }

    fun submitBreakdown(machineId: String, machineName: String, problem: String, description: String) {
        val errors = breakdownValidator.validate(machineId, problem, description)
        if (errors.isNotEmpty()) {
            _validationErrors.value = errors
            return
        }

        _validationErrors.value = emptyList()
        viewModelScope.launch {
            val breakdown = BreakdownEntity(
                machineId = machineId,
                machineName = machineName,
                problem = problem,
                priority = "HIGH",
                description = description,
                status = "OPEN",
                reportedAt = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                syncState = "PENDING_SYNC"
            )
            repository.addBreakdown(breakdown)
        }
    }

    fun addMachine(machine: MachineEntity) = viewModelScope.launch {
        repository.addMachine(machine)
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

            repository.addMachine(
                MachineEntity(
                    name = "Chainlink 01",
                    machineId = "CH-01",
                    category = "Chainlink",
                    location = "Line A",
                    status = "RUNNING",
                    syncState = "SYNCED"
                )
            )

            repository.addMachine(
                MachineEntity(
                    name = "Barbed Wire 02",
                    machineId = "BW-02",
                    category = "Barbed Wire",
                    location = "Line B",
                    status = "STOPPED",
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

            repository.addProduction(
                ProductionRecordEntity(
                    machineId = "CH-01",
                    productName = "Chainlink Mesh",
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

            repository.addBreakdown(
                BreakdownEntity(
                    machineId = "BW-02",
                    machineName = "Barbed Wire 02",
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
                    machineId = "CH-01",
                    machineName = "Chainlink 01",
                    title = "Scheduled lubrication check",
                    type = "PM",
                    status = "PENDING",
                    priority = "MEDIUM",
                    dueDate = today,
                    notes = "Verify chain drive and lubrication points.",
                    syncState = "SYNCED"
                )
            )
        }
    }
}
