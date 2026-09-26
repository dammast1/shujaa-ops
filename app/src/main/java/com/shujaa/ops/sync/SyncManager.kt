package com.shujaa.ops.sync

import com.shujaa.ops.data.local.entities.*

enum class SyncState {
    SYNCED,
    PENDING_SYNC,
    SYNCING,
    FAILED,
    CONFLICT
}

data class SyncRecord(
    val id: String,
    val entityType: String,
    val entityId: String,
    val state: SyncState,
    val lastAttempt: Long = System.currentTimeMillis(),
    val attemptCount: Int = 0,
    val error: String? = null
)

class SyncManager {
    private val maxRetries = 5
    private val retryDelayMs = 1000L

    fun shouldRetry(record: SyncRecord): Boolean {
        return record.state == SyncState.FAILED && record.attemptCount < maxRetries
    }

    fun getNextRetryDelay(attemptCount: Int): Long {
        return retryDelayMs * (1L shl attemptCount)
    }

    fun buildSyncPayload(production: ProductionRecordEntity): Map<String, Any?> {
        return mapOf(
            "id" to production.id,
            "machineId" to production.machineId,
            "productName" to production.productName,
            "shiftName" to production.shiftName,
            "date" to production.date,
            "target" to production.target,
            "actual" to production.actual,
            "good" to production.good,
            "rejected" to production.rejected,
            "waste" to production.waste,
            "downtimeMinutes" to production.downtimeMinutes,
            "syncState" to production.syncState
        )
    }

    fun buildSyncPayload(breakdown: BreakdownEntity): Map<String, Any?> {
        return mapOf(
            "id" to breakdown.id,
            "machineId" to breakdown.machineId,
            "machineName" to breakdown.machineName,
            "problem" to breakdown.problem,
            "priority" to breakdown.priority,
            "description" to breakdown.description,
            "status" to breakdown.status,
            "reportedAt" to breakdown.reportedAt,
            "syncState" to breakdown.syncState
        )
    }
}
