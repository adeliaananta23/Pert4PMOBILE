package com.adel.pertemuan4

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {
    @Insert
    fun insert(data: Data)

    @Query("SELECT * from data_warga ORDER BY nama ASC")
    fun getAllData(): LiveData<List<Data>>

    @Query("DELETE FROM data_warga")
    fun deleteAll()
}
