package com.shujaa.ops.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shujaa.ops.data.local.dao.DocumentDao
import com.shujaa.ops.data.local.entities.DocumentEntity

@Database(
    entities = [DocumentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DocsDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}
