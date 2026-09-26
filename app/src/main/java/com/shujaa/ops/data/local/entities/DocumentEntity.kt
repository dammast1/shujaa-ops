package com.shujaa.ops.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: String,
    val filePath: String,
    val machineId: String? = null,
    val category: String = "SOP",
    val isCached: Boolean = false
)
