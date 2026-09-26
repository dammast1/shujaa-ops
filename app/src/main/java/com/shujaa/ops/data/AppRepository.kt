package com.shujaa.ops.data

import android.content.Context
import com.shujaa.ops.data.local.AppDatabase
import com.shujaa.ops.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)

    val machinesFlow: Flow<List<MachineEntity>> = db.machineDao().observeMachines()
    val productionFlow: Flow<List<ProductionRecordEntity>> = db.productionDao().observeProduction()
    val breakdownsFlow: Flow<List<BreakdownEntity>> = db.breakdownsDao().observeBreakdowns()
    val maintenanceFlow: Flow<List<MaintenanceJobEntity>> = db.maintenanceDao().observeMaintenance()
    val sparePartsFlow: Flow<List<SparePartEntity>> = db.sparePartDao().observeSpareParts()
    val tasksFlow: Flow<List<TaskEntity>> = db.taskDao().observeTasks()

    suspend fun addMachine(machine: MachineEntity) {
        db.machineDao().insertMachine(machine)
    }

    suspend fun addProduction(record: ProductionRecordEntity) {
        db.productionDao().insertProduction(record)
    }

    suspend fun addBreakdown(breakdown: BreakdownEntity) {
        db.breakdownDao().insertBreakdown(breakdown)
    }

    suspend fun addMaintenance(job: MaintenanceJobEntity) {
        db.maintenanceDao().insertMaintenance(job)
    }

    suspend fun addSparePart(part: SparePartEntity) {
        db.sparePartDao().insertSparePart(part)
    }

    suspend fun addTask(task: TaskEntity) {
        db.taskDao().insertTask(task)
    }

    suspend fun addStockMovement(movement: StockMovementEntity) {
        db.stockMovementDao().insertMovement(movement)
    }

    fun dashboardSummary(): DashboardSummary {
        val machines = db.machineDao().getMachinesSync()
        val breakdowns = db.breakdownDao().getBreakdownsSync()
        val tasks = db.taskDao().getTasksSync()
        val todaysDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val productionToday = db.productionDao().getProductionForDateToday(todaysDate)
        val lowStock = db.sparePartDao().getLowStockCount()

        return DashboardSummary(
            machineCount = machines.size,
            openBreakdowns = breakdowns.count { it.status == "OPEN" },
            pendingTasks = tasks.count { it.status == "PENDING" },
            productionToday = productionToday.sumOf { it.actual.toInt() },
            lowStock = lowStock
        )
    }
}

data class DashboardSummary(
    val machineCount: Int,
    val openBreakdowns: Int,
    val pendingTasks: Int,
    val productionToday: Int,
    val lowStock: Int
)
