package com.example.project5

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WaterEntryDao {

    @Insert
    suspend fun insert(waterEntry: WaterEntry)

    @Query("SELECT * FROM water_entries ORDER BY date DESC")
    fun getAllEntries(): LiveData<List<WaterEntry>>

    @Query("SELECT AVG(volume) FROM water_entries")
    suspend fun getAverageWaterIntake(): Float?

    @Query("SELECT date, SUM(volume) AS totalVolume FROM water_entries GROUP BY date ORDER BY date ASC")
    suspend fun getTotalWaterIntakePerDay(): List<DailyIntake>

    @Query("DELETE FROM water_entries")
    suspend fun clearAll() // Add this to clear all entries
}

