package com.ruthvikbr.runtracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.db.Run
import com.ruthvikbr.runtracker.services.TrackingServices
import com.ruthvikbr.runtracker.services.polyline
import com.ruthvikbr.runtracker.ui.viewmodels.MainViewModel
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_PAUSE_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_START_OR_RESUME_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_STOP_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.MAP_ZOOM
import com.ruthvikbr.runtracker.utilities.Constants.POLYLINE_COLOR
import com.ruthvikbr.runtracker.utilities.Constants.POLYLINE_WIDTH
import com.ruthvikbr.runtracker.utilities.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

const val CANCEL_TRACKING_DIALOG_TAG = "CANCEL_TRACKING_DIALOG_TAG"

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()
    private var isTracking = false
    private var pathPoints = mutableListOf<polyline>()

    private var map: GoogleMap? = null

    private var currentTimeInMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener {
            toggleRun()
        }

        if(savedInstanceState != null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG
             ) as CancelTrackingDialog?
            cancelTrackingDialog?.setPositiveButtonListener {
                stopRun()
            }
        }

        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDatabase()
        }
        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
    }



    private fun subscribeToObservers(){
        TrackingServices.isTracking.observe(viewLifecycleOwner,{
            updateTracking(it)
        })
        TrackingServices.pathToPoints.observe(viewLifecycleOwner,{
            pathPoints = it
            addLatestPolyLine()
            moveCameraToUserPosition()
        })

        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner,{
            currentTimeInMillis = it
            val formattedTime =TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis,true)
            tvTimer.text = formattedTime
        })
    }

    private fun moveCameraToUserPosition(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu_tracking,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(currentTimeInMillis > 0L ){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.myCancelTracking ->
                showCancelTrackingDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog(){
        CancelTrackingDialog().apply {
            setPositiveButtonListener {
                stopRun()
            }
        }.show(parentFragmentManager,CANCEL_TRACKING_DIALOG_TAG)
    }

    private fun stopRun(){
        tvTimer.text = "00:00:00:00"
        sendActionToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if(!isTracking && currentTimeInMillis > 0L){
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        }
        else if(isTracking){
            btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
            btnFinishRun.visibility = View.GONE
        }
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible = true
            sendActionToService(ACTION_PAUSE_SERVICE)
        }else{
            sendActionToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints){
            for (pos in polyline){
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()

            )
        )
    }

    private fun endRunAndSaveToDatabase(){
        map?.snapshot { bmp ->
            var distance = 0
            for(polyline in pathPoints){
                distance += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distance/1000f)
                    / (currentTimeInMillis/1000f/60/60) * 10) /10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distance/1000f)*weight).toInt()

            val run = Run(bmp,dateTimeStamp,avgSpeed,distance,currentTimeInMillis,caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run Saved",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyLine(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLong = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLong)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendActionToService(action: String) =
        Intent(requireContext(),TrackingServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}