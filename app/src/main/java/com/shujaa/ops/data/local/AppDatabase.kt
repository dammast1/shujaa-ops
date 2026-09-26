package com.shujaa.ops.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shujaa.ops.data.local.dao.BreakdownDao
import com.shujaa.ops.data.local.dao.MachineDao
import com.shujaa.ops.data.local.dao.MaintenanceDao
import com.shujaa.ops.data.local.dao.ProductionDao
import com.shujaa.ops.data.local.dao.SparePartDao
import com.shujaa.ops.data.local.dao.TaskDao
import com.shujaa.ops.data.local.entities.*

@Database(
    entities = [
        MachineEntity::class,
        ProductEntity::class,
        SparePartEntity::class,
        ProductionRecordEntity::class,
        BreakdownEntity::class,
        MaintenanceJobEntity::class,
        TaskEntity::class,
        StockMovementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun machineDao(): MachineDao
    abstract fun productionDao(): ProductionDao
    abstract fun breakdownDao(): BreakdownDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun sparePartDao(): SparePartDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shujaa_ops.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
