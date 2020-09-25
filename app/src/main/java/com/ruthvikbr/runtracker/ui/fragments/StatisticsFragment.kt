package com.ruthvikbr.runtracker.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.ui.viewmodels.StatisticsViewModel
import com.ruthvikbr.runtracker.utilities.CustomMarkerView
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

    private fun setUpBarChart(){
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }

        barChart.apply {
            description.text = "Average speed vs Time"
            legend.isEnabled = false
        }
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

        viewModel.runsSortedByDate.observe(viewLifecycleOwner,{
            it?.let {
                val allAverageSpeeds = it.indices.map { i ->
                    BarEntry(i.toFloat(),it[i].averageSpeed)
                }
                val barDataSet = BarDataSet(allAverageSpeeds,
                    "Average speed vs Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(),R.color.colorAccent)
                }
                barChart.data = BarData(barDataSet)
                barChart.marker = CustomMarkerView(it.reversed(),requireContext(),
                    R.layout.marker_view)
                barChart.invalidate()
            }
        })
    }
}
