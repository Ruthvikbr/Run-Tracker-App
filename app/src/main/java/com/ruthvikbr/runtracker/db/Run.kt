package com.ruthvikbr.runtracker.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Run")
data class Run(

    var image : Bitmap? = null,
    var timeStamp:Long = 0L,
    var averageSpeed:Float = 0f,
    var distance:Int = 0,
    var timeInMillis:Long = 0L,
    val caloriesBurned:Int = 0

    ){

    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}
