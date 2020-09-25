package com.ruthvikbr.runtracker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.ui.viewmodels.StatisticsViewModel
import com.ruthvikbr.runtracker.utilities.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics){
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner,{
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTimeRun
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner,{
            it?.let {
                val km = it/1000f
                val totalDistance = round(km * 10f) / 10f
                val totalDistanceString = "${totalDistance}kms"
                tvTotalDistance.text = totalDistanceString
            }
        })

        viewModel.averageSpeed.observe(viewLifecycleOwner,{
            it?.let {
               val avgSpeed = round(it * 10f) / 10f
                val averageSpeedString = "${avgSpeed}km/h"
                tvAverageSpeed.text = averageSpeedString
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner,{
            it?.let {
                val totalCalories = "${it}kcal"
                tvTotalCalories.text = totalCalories
            }
        })
    }
}
