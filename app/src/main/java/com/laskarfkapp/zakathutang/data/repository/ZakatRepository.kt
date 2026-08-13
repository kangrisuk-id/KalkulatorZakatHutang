package com.laskarfkapp.zakathutang.data.repository

import com.laskarfkapp.zakathutang.data.dao.NisabConfigDao
import com.laskarfkapp.zakathutang.data.dao.ZakatRecordDao
import com.laskarfkapp.zakathutang.data.entity.NisabConfigEntity
import com.laskarfkapp.zakathutang.data.entity.ZakatRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class NisabRates(
    val goldPricePerGram: Double = 1350000.0,
    val silverPricePerGram: Double = 15000.0,
    val ricePricePerKg: Double = 15000.0
) {
    val goldNisab85g: Double get() = goldPricePerGram * 85.0
    val monthlyGoldNisab: Double get() = goldNisab85g / 12.0
    val silverNisab595g: Double get() = silverPricePerGram * 595.0
    val fitrahPerPersonMoney: Double get() = ricePricePerKg * 2.5
}

class ZakatRepository(
    private val zakatRecordDao: ZakatRecordDao,
    private val nisabConfigDao: NisabConfigDao
) {

    val allZakatRecords: Flow<List<ZakatRecordEntity>> = zakatRecordDao.getAllRecords()

    val nisabRates: Flow<NisabRates> = nisabConfigDao.getAllConfigs().map { configs ->
        var gold = 1350000.0
        var silver = 15000.0
        var rice = 15000.0

        for (c in configs) {
            when (c.key) {
                "GOLD_PRICE_PER_GRAM" -> gold = c.value
                "SILVER_PRICE_PER_GRAM" -> silver = c.value
                "RICE_PRICE_PER_KG" -> rice = c.value
            }
        }
        NisabRates(goldPricePerGram = gold, silverPricePerGram = silver, ricePricePerKg = rice)
    }

    suspend fun updateNisabRate(key: String, value: Double) {
        nisabConfigDao.insertOrUpdateConfig(NisabConfigEntity(key = key, value = value))
    }

    suspend fun saveZakatRecord(record: ZakatRecordEntity): Long {
        return zakatRecordDao.insertRecord(record)
    }

    suspend fun togglePaidStatus(record: ZakatRecordEntity) {
        zakatRecordDao.updateRecord(record.copy(isPaid = !record.isPaid))
    }

    suspend fun deleteRecordById(id: Long) {
        zakatRecordDao.deleteRecordById(id)
    }
}
