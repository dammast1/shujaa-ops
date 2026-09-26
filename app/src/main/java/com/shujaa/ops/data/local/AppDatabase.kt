package com.shujaa.ops.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shujaa.ops.data.local.dao.*
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
        StockMovementEntity::class,
        DocumentEntity::class,
        ShiftEntity::class,
        CategoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun machineDao(): MachineDao
    abstract fun productionDao(): ProductionDao
    abstract fun breakdownDao(): BreakdownDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun sparePartDao(): SparePartDao
    abstract fun taskDao(): TaskDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "shujaa_ops.db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                .also { INSTANCE = it }
        }
    }
}

private val MIGRATION_1_2 = androidx.room.migration.Migration(1, 2) { database ->
    database.execSQL("CREATE TABLE IF NOT EXISTS documents (id TEXT PRIMARY KEY NOT NULL, title TEXT NOT NULL, type TEXT NOT NULL, filePath TEXT NOT NULL, machineId TEXT, category TEXT NOT NULL DEFAULT 'SOP', isCached INTEGER NOT NULL DEFAULT 0)")
    database.execSQL("CREATE TABLE IF NOT EXISTS shifts (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, startTime TEXT NOT NULL, endTime TEXT NOT NULL, crossesMidnight INTEGER NOT NULL DEFAULT 0)")
    database.execSQL("CREATE TABLE IF NOT EXISTS categories (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, type TEXT NOT NULL)")
}

private val MIGRATION_2_3 = androidx.room.migration.Migration(2, 3) { database: SupportSQLiteDatabase ->
    database.execSQL("DELETE FROM machines WHERE rowid NOT IN (SELECT MIN(rowid) FROM machines GROUP BY machineId)")
    database.execSQL("DELETE FROM products WHERE rowid NOT IN (SELECT MIN(rowid) FROM products GROUP BY code)")
    database.execSQL("DELETE FROM spare_parts WHERE rowid NOT IN (SELECT MIN(rowid) FROM spare_parts GROUP BY partNumber)")
    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_machines_machineId ON machines(machineId)")
    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_products_code ON products(code)")
    database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_spare_parts_partNumber ON spare_parts(partNumber)")
    database.execSQL("CREATE INDEX IF NOT EXISTS index_production_machineId_date_shiftName ON production_records(machineId, date, shiftName)")
}
