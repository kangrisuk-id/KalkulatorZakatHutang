package com.laskarfkapp.zakathutang.model

enum class DebtType {
    HUTANG,  // Money user owes to others
    PIUTANG  // Money others owe to user
}

data class DebtRecord(
    val id: Long = 0,
    val type: DebtType,
    val personName: String,
    val amount: Double,
    val paidAmount: Double = 0.0,
    val notes: String = "",
    val contact: String = "",
    val dueDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isPaid: Boolean = false
) {
    val remainingAmount: Double
        get() = (amount - paidAmount).coerceAtLeast(0.0)

    val progressRatio: Float
        get() = if (amount > 0) (paidAmount / amount).coerceIn(0.0, 1.0).toFloat() else 0f
}
