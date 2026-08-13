package com.laskarfkapp.zakathutang.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.laskarfkapp.zakathutang.data.entity.ZakatRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZakatRecordDao {

    @Query("SELECT * FROM zakat_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ZakatRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ZakatRecordEntity): Long

    @Update
    suspend fun updateRecord(record: ZakatRecordEntity)

    @Delete
    suspend fun deleteRecord(record: ZakatRecordEntity)

    @Query("DELETE FROM zakat_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)
}
