package com.shujaa.ops.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.shujaa.ops.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDao {
    @Query("SELECT * FROM machines ORDER BY name ASC")
    fun observeMachines(): Flow<List<MachineEntity>>

    @Query("SELECT * FROM machines")
    suspend fun getAllMachines(): List<MachineEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMachine(machine: MachineEntity): Long

    @Query("SELECT * FROM machines WHERE id = :id LIMIT 1")
    suspend fun getMachine(id: String): MachineEntity?

    @Query("SELECT * FROM machines WHERE machineId = :machineId LIMIT 1")
    suspend fun getMachineByMachineId(machineId: String): MachineEntity?
}

@Dao
interface ProductionDao {
    @Query("SELECT * FROM production_records ORDER BY date DESC")
    fun observeProduction(): Flow<List<ProductionRecordEntity>>

    @Query("SELECT * FROM production_records WHERE date = :date")
    suspend fun getProductionForDate(date: String): List<ProductionRecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduction(record: ProductionRecordEntity): Long
}

@Dao
interface BreakdownDao {
    @Query("SELECT * FROM breakdowns ORDER BY reportedAt DESC")
    fun observeBreakdowns(): Flow<List<BreakdownEntity>>

    @Query("SELECT * FROM breakdowns WHERE status = 'OPEN'")
    suspend fun getOpenBreakdowns(): List<BreakdownEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakdown(breakdown: BreakdownEntity)
}

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_jobs ORDER BY dueDate ASC")
    fun observeMaintenance(): Flow<List<MaintenanceJobEntity>>

    @Query("SELECT * FROM maintenance_jobs WHERE status = 'PENDING' AND dueDate < :today")
    suspend fun getOverdueMaintenance(today: String): List<MaintenanceJobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(job: MaintenanceJobEntity)
}

@Dao
interface SparePartDao {
    @Query("SELECT * FROM spare_parts ORDER BY name ASC")
    fun observeSpareParts(): Flow<List<SparePartEntity>>

    @Query("SELECT * FROM spare_parts WHERE quantity <= minimumQuantity")
    suspend fun getLowStockParts(): List<SparePartEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSparePart(part: SparePartEntity): Long

    @Query("SELECT * FROM spare_parts WHERE partNumber = :partNumber LIMIT 1")
    suspend fun getPartByNumber(partNumber: String): SparePartEntity?

    @Transaction
    suspend fun consumePart(partId: String, quantity: Int): Boolean {
        if (quantity <= 0) return false
        val part = getPartById(partId) ?: return false
        if (part.quantity < quantity) return false
        updatePart(part.copy(quantity = part.quantity - quantity))
        return true
    }

    @Query("SELECT * FROM spare_parts WHERE id = :id LIMIT 1")
    suspend fun getPartById(id: String): SparePartEntity?

    @Update
    suspend fun updatePart(part: SparePartEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
}

@Dao
interface StockMovementDao {
    @Query("SELECT * FROM stock_movements ORDER BY date DESC")
    fun observeMovements(): Flow<List<StockMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: StockMovementEntity)
}
