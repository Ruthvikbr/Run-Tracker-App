package com.ruthvikbr.runtracker.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.db.RunDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var runDao: RunDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}