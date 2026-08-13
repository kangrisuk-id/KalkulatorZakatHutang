package com.laskarfkapp.zakathutang.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "HUTANG" (Money I owe) or "PIUTANG" (Money owed to me)
    val personNameEncrypted: String,
    val amountEncrypted: String,
    val paidAmountEncrypted: String = "",
    val notesEncrypted: String = "",
    val contactEncrypted: String = "",
    val dueDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isPaid: Boolean = false
)
