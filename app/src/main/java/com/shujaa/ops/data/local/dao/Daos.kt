package com.shujaa.ops.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shujaa.ops.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDao {
    @Query("SELECT * FROM machines ORDER BY name ASC")
    fun observeMachines(): Flow<List<MachineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachine(machine: MachineEntity)

    @Query("SELECT * FROM machines WHERE id = :id LIMIT 1")
    suspend fun getMachine(id: String): MachineEntity?
}

@Dao
interface ProductionDao {
    @Query("SELECT * FROM production_records ORDER BY date DESC")
    fun observeProduction(): Flow<List<ProductionRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduction(record: ProductionRecordEntity)
}

@Dao
interface BreakdownDao {
    @Query("SELECT * FROM breakdowns ORDER BY reportedAt DESC")
    fun observeBreakdowns(): Flow<List<BreakdownEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakdown(breakdown: BreakdownEntity)
}

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_jobs ORDER BY dueDate ASC")
    fun observeMaintenance(): Flow<List<MaintenanceJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenance(job: MaintenanceJobEntity)
}

@Dao
interface SparePartDao {
    @Query("SELECT * FROM spare_parts ORDER BY name ASC")
    fun observeSpareParts(): Flow<List<SparePartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSparePart(part: SparePartEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
}
