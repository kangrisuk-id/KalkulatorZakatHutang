package com.laskarfkapp.zakathutang.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.laskarfkapp.zakathutang.data.entity.NisabConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NisabConfigDao {

    @Query("SELECT * FROM nisab_configs")
    fun getAllConfigs(): Flow<List<NisabConfigEntity>>

    @Query("SELECT * FROM nisab_configs WHERE `key` = :key LIMIT 1")
    suspend fun getConfigByKey(key: String): NisabConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: NisabConfigEntity)
}
