package com.shujaa.ops.data.local

import androidx.room.RoomDatabase

/**
 * Database backup contract. The active app database is AppDatabase; this class
 * intentionally contains no second Room database implementation.
 */
interface BackupManager {
    suspend fun exportDatabase(): ByteArray?
    suspend fun importDatabase(data: ByteArray): Boolean
}

class LocalBackupManager(private val db: RoomDatabase) : BackupManager {
    override suspend fun exportDatabase(): ByteArray? = null
    override suspend fun importDatabase(data: ByteArray): Boolean = false
}
