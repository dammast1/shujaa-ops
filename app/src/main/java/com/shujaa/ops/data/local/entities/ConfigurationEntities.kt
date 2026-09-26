package com.shujaa.ops.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startTime: String,
    val endTime: String,
    val crossesMidnight: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String
)
