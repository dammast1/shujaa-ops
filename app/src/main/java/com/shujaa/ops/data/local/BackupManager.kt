package com.shujaa.ops.data.local

import androidx.room.RoomDatabase

interface BackupManager {
    suspend fun exportDatabase(): ByteArray?
    suspend fun importDatabase(data: ByteArray): Boolean
}

class LocalBackupManager(private val db: RoomDatabase) : BackupManager {
    override suspend fun exportDatabase(): ByteArray? {
        return try {
            val dbFile = db.openHelper.readableDatabase.attachedDbs.firstOrNull()?.second
            dbFile?.readBytes()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun importDatabase(data: ByteArray): Boolean {
        return try {
            val dbFile = db.openHelper.readableDatabase.attachedDbs.firstOrNull()?.second
            if (dbFile != null) {
                dbFile.writeBytes(data)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
