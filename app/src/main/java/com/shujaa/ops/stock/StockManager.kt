package com.shujaa.ops.stock

import com.shujaa.ops.data.local.entities.SparePartEntity
import com.shujaa.ops.data.local.entities.StockMovementEntity

class StockManager {
    fun consumePart(part: SparePartEntity, quantity: Int): SparePartEntity? {
        val newQuantity = part.quantity - quantity
        return if (newQuantity >= 0) {
            part.copy(quantity = newQuantity)
        } else {
            null
        }
    }

    fun checkLowStock(part: SparePartEntity): Boolean {
        return part.quantity <= part.minimumQuantity
    }

    fun createStockMovement(
        sparePartId: String,
        partName: String,
        movementType: String,
        quantity: Int,
        reason: String,
        date: String
    ): StockMovementEntity {
        return StockMovementEntity(
            sparePartId = sparePartId,
            partName = partName,
            movementType = movementType,
            quantity = quantity,
            reason = reason,
            date = date
        )
    }

    fun getStockHistory(movements: List<StockMovementEntity>, sparePartId: String): List<StockMovementEntity> {
        return movements.filter { it.sparePartId == sparePartId }
    }
}
