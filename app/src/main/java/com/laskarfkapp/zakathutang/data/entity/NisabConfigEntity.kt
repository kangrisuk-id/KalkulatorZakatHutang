package com.laskarfkapp.zakathutang.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nisab_configs")
data class NisabConfigEntity(
    @PrimaryKey
    val key: String, // "GOLD_PRICE_PER_GRAM", "SILVER_PRICE_PER_GRAM", "RICE_PRICE_PER_KG"
    val value: Double,
    val updatedAt: Long = System.currentTimeMillis()
)
