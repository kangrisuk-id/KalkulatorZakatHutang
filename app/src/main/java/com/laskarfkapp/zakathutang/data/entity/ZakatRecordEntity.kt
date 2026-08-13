package com.laskarfkapp.zakathutang.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zakat_records")
data class ZakatRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "PROFESI", "MAAL", "FITRAH", "PERDAGANGAN"
    val title: String,
    val totalAssets: Double,
    val zakatAmount: Double,
    val nisabUsed: Double,
    val isPaid: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
