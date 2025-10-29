package com.adel.pertemuan4

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Data::class], version = 1, exportSchema = false)
abstract class DatabaseData : RoomDatabase() {

    abstract fun DataDao(): DataDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseData? = null

        fun getDatabase(context: Context): DatabaseData {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseData::class.java,
                    "data_warga_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
