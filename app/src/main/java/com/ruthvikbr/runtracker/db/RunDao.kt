package com.ruthvikbr.runtracker.db

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM Run ORDER BY timeStamp DESC")
    fun getAllRunsSortedByDate() : LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis() : LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY distance DESC")
    fun getAllRunsSortedByDistance() : LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurnt() : LiveData<List<Run>>

    @Query("SELECT * FROM Run ORDER BY averageSpeed DESC")
    fun getAllRunsSortedByAverageSpeed() : LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM Run")
    fun getTotalTimeInMillis() : LiveData<Long>

    @Query("SELECT SUM(distance) FROM Run")
    fun getTotalDistance() : LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM Run")
    fun getTotalCaloriesBurned() : LiveData<Int>

    @Query("SELECT AVG(averageSpeed) FROM Run")
    fun getAverageSpeedOfAllRuns() : LiveData<Float>
}