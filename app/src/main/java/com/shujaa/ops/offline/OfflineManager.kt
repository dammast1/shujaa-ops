package com.shujaa.ops.offline

import com.shujaa.ops.data.local.entities.*
import java.time.LocalDateTime

data class OfflineQueue(
    val records: List<OfflineRecord> = emptyList()
)

data class OfflineRecord(
    val id: String,
    val entityType: String,
    val action: String,
    val data: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

class OfflineManager {
    fun queueProduction(record: ProductionRecordEntity): OfflineRecord {
        return OfflineRecord(
            id = record.id,
            entityType = "PRODUCTION",
            action = "CREATE",
            data = record.toString()
        )
    }

    fun queueBreakdown(breakdown: BreakdownEntity): OfflineRecord {
        return OfflineRecord(
            id = breakdown.id,
            entityType = "BREAKDOWN",
            action = "CREATE",
            data = breakdown.toString()
        )
    }

    fun markSynced(record: OfflineRecord): OfflineRecord {
        return record.copy(isSynced = true)
    }
}
