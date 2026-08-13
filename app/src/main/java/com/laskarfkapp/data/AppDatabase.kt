package com.laskarfkapp.zakathutang.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.laskarfkapp.zakathutang.data.dao.DebtDao
import com.laskarfkapp.zakathutang.data.dao.NisabConfigDao
import com.laskarfkapp.zakathutang.data.dao.ZakatRecordDao
import com.laskarfkapp.zakathutang.data.entity.DebtEntity
import com.laskarfkapp.zakathutang.data.entity.NisabConfigEntity
import com.laskarfkapp.zakathutang.data.entity.ZakatRecordEntity

@Database(
    entities = [
        DebtEntity::class,
        ZakatRecordEntity::class,
        NisabConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun debtDao(): DebtDao
    abstract fun zakatRecordDao(): ZakatRecordDao
    abstract fun nisabConfigDao(): NisabConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zakat_hutang_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
