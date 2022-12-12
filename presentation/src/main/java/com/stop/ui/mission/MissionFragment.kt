package com.stop.ui.mission

import android.Manifest
import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skt.tmap.TMapPoint
import com.stop.R
import com.stop.databinding.FragmentMissionBinding
import com.stop.domain.model.route.tmap.custom.Place
import com.stop.domain.model.route.tmap.custom.WalkRoute
import com.stop.isMoreThanOreo
import com.stop.model.AlarmStatus
import com.stop.model.Location
import com.stop.model.MissionStatus
import com.stop.ui.alarmsetting.AlarmSettingViewModel
import com.stop.ui.mission.MissionService.Companion.MISSION_LAST_TIME
import com.stop.ui.mission.MissionService.Companion.MISSION_LOCATIONS
import com.stop.ui.mission.MissionService.Companion.MISSION_OVER
import com.stop.ui.util.Marker
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

class MissionFragment : Fragment(), MissionHandler {

    private var _binding: FragmentMissionBinding? = null
    private val binding: FragmentMissionBinding
        get() = _binding!!

    private val missionViewModel: MissionViewModel by activityViewModels()
    private val alarmSettingViewModel: AlarmSettingViewModel by activityViewModels()

    private lateinit var tMap: MissionTMap

    private lateinit var missionServiceIntent: Intent

    var personCurrentLocation = Location(37.553836, 126.969652)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setMissionService()
        setBroadcastReceiver()
        missionViewModel.missionStatus.value = MissionStatus.ONGOING
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTimer()
        setDataBinding()
        initTMap()
        setMissionOver()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        tMap.onDestroy()
    }

    private fun setMissionService() {
        missionServiceIntent = Intent(requireActivity(), MissionService::class.java)
        if (isMoreThanOreo()) {
            requireActivity().startForegroundService(missionServiceIntent)
        } else {
            requireActivity().startService(missionServiceIntent)
        }
    }

    private fun setBroadcastReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(MissionService.MISSION_USER_INFO)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                missionViewModel.lastTime.value = intent?.getStringExtra(MISSION_LAST_TIME)
                missionViewModel.userLocations.value =
                    intent?.getParcelableArrayListExtra<Location>(MISSION_LOCATIONS) as ArrayList<Location>

                if (intent.getBooleanExtra(MISSION_OVER, false)) {
                    setFailAnimation()
                }

            }
        }

        requireActivity().registerReceiver(receiver, intentFilter)
    }

    private fun setTimer() {
        missionServiceIntent.putExtra(MISSION_LAST_TIME, alarmSettingViewModel.alarmItem.value?.lastTime)
        if (isMoreThanOreo()) {
            requireActivity().startForegroundService(missionServiceIntent)
        } else {
            requireActivity().startService(missionServiceIntent)
        }
    }

    private fun setDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.missionViewModel = missionViewModel
        binding.alarmSettingViewModel = alarmSettingViewModel
        binding.fragment = this@MissionFragment
    }

    private fun initTMap() {
        tMap = MissionTMap(requireActivity(), this)
        tMap.init()

        binding.constraintLayoutContainer.addView(tMap.tMapView)
    }

    fun setCompassMode() {
        tMap.tMapView.isCompassMode = tMap.tMapView.isCompassMode.not()
    }

    fun setPersonCurrent() {
        tMap.tMapView.setCenterPoint(
            personCurrentLocation.latitude,
            personCurrentLocation.longitude,
            true
        )

        tMap.isTracking = true
        tMap.tMapView.zoomLevel = 16
    }

    fun setZoomOut() {
        with(tMap) {
            latitudes.clear()
            longitudes.clear()
            latitudes.add(missionViewModel.destination.value.coordinate.latitude.toDouble())
            longitudes.add(missionViewModel.destination.value.coordinate.longitude.toDouble())
            latitudes.add(personCurrentLocation.latitude)
            longitudes.add(personCurrentLocation.longitude)
            setRouteDetailFocus()
            isTracking = false
        }
    }

    fun clickMissionOver() {
        Snackbar.make(
            requireActivity().findViewById(R.id.constraint_layout_container),
            "미션을 취소합니다",
            Snackbar.LENGTH_SHORT
        ).show()
        missionViewModel.missionStatus.value = MissionStatus.OVER
    }

    override fun alertTMapReady() {
        requestPermissionsLauncher.launch(PERMISSIONS)
        getAlarmInfo()
        alarmSettingViewModel.alarmStatus.value = AlarmStatus.MISSION
        drawPersonLine()
    }

    override fun setOnEnableScrollWithZoomLevelListener() {
        tMap.apply {
            tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
                isTracking = false
            }
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { it.value }) {
            tMap.isTracking = false
        }
    }

    private fun drawPersonLine() {
        lateinit var beforeLocation: Location
        lifecycleScope.launch {
            missionViewModel.userLocations.collectIndexed { index, userLocation ->
                if (index == 1) {
                    initMarker(userLocation)
                    beforeLocation = userLocation.last()
                } else if (index > 1) {
                    drawNowLocationLine(
                        TMapPoint(userLocation.last().latitude, userLocation.last().longitude),
                        TMapPoint(beforeLocation.latitude, beforeLocation.longitude)
                    )
                    personCurrentLocation = userLocation.last()
                    if (tMap.isTracking) {
                        tMap.tMapView.setCenterPoint(userLocation.last().latitude, userLocation.last().longitude)
                    }
                    beforeLocation = userLocation.last()
                    arriveDestination(userLocation.last().latitude, userLocation.last().longitude)
                }
            }
        }
    }

    private fun initMarker(nowLocation: ArrayList<Location>) {
        with(tMap) {
            addMarker(
                Marker.PERSON_MARKER,
                Marker.PERSON_MARKER_IMG,
                TMapPoint(nowLocation.last().latitude, nowLocation.last().longitude)
            )
            personCurrentLocation = nowLocation.last()
            latitudes.add(nowLocation.last().latitude)
            longitudes.add(nowLocation.last().longitude)
            setRouteDetailFocus()
            arriveDestination(nowLocation.last().latitude, nowLocation.last().longitude)

            drawWalkLines(
                nowLocation.map { TMapPoint(it.latitude, it.longitude) } as ArrayList<TMapPoint>,
                Marker.PERSON_LINE + PERSON_LINE_NUM.toString(),
                Marker.PERSON_LINE_COLOR
            )
            PERSON_LINE_NUM += 1

        }
    }

    private fun drawNowLocationLine(nowLocation: TMapPoint, beforeLocation: TMapPoint) {
        tMap.drawMoveLine(
            nowLocation,
            beforeLocation,
            Marker.PERSON_LINE + PERSON_LINE_NUM.toString(),
            Marker.PERSON_LINE_COLOR
        )
        PERSON_LINE_NUM += 1

        tMap.addMarker(Marker.PERSON_MARKER, Marker.PERSON_MARKER_IMG, nowLocation)
    }

    private fun getAlarmInfo() {
        alarmSettingViewModel.getAlarm(missionViewModel.missionStatus.value)
        val linePoints = arrayListOf<TMapPoint>()
        val walkInfo = alarmSettingViewModel.alarmItem.value?.routes?.first() as WalkRoute
        tMap.drawWalkRoute(walkInfo, linePoints)
        tMap.drawWalkLines(linePoints, Marker.WALK_LINE, Marker.WALK_LINE_COLOR)

        missionViewModel.destination.value = walkInfo.end
        makeDestinationMarker(walkInfo.end)
    }

    private fun makeDestinationMarker(destination: Place) {
        val latitude = destination.coordinate.latitude.toDouble()
        val longitude = destination.coordinate.longitude.toDouble()
        tMap.addMarker(
            Marker.DESTINATION_MARKER,
            Marker.DESTINATION_MARKER_IMG,
            TMapPoint(latitude, longitude)
        )
        tMap.latitudes.add(latitude)
        tMap.longitudes.add(longitude)
    }

    private fun arriveDestination(nowLatitude: Double, nowLongitude: Double) {
        if (tMap.getDistance(
                nowLatitude,
                nowLongitude,
                missionViewModel.destination.value.coordinate.latitude.toDouble(),
                missionViewModel.destination.value.coordinate.longitude.toDouble()
            ) <= 10
        ) {
            missionViewModel.missionStatus.value = MissionStatus.OVER
            Snackbar.make(
                requireActivity().findViewById(R.id.constraint_layout_container),
                "정류장에 도착했습니다!",
                Snackbar.LENGTH_SHORT
            ).show()
            setSuccessAnimation()

        }
    }

    private fun setSuccessAnimation() {
        with(binding.lottieSuccess) {
            visibility = View.VISIBLE
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    missionViewModel.missionStatus.value = MissionStatus.OVER
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
    }

    private fun setFailAnimation() {
        with(binding.lottieFail) {
            visibility = View.VISIBLE
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    missionViewModel.missionStatus.value = MissionStatus.OVER
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
    }

    private fun setMissionOver() {
        lifecycleScope.launch {
            missionViewModel.missionStatus.collect { missionStatus ->
                if (missionStatus == MissionStatus.OVER) {
                    alarmSettingViewModel.deleteAlarm()
                    missionServiceIntent.putExtra(MISSION_OVER, true)
                    if (isMoreThanOreo()) {
                        requireActivity().startForegroundService(missionServiceIntent)
                    } else {
                        requireActivity().startService(missionServiceIntent)
                    }
                    requireActivity().stopService(missionServiceIntent)
                    missionViewModel.missionStatus.value = MissionStatus.BEFORE
                    alarmSettingViewModel.alarmStatus.value = AlarmStatus.NON_EXIST
                    findNavController().navigate(R.id.action_missionFragment_to_mapFragment)
                }
            }
        }
    }

    companion object {
        private var PERSON_LINE_NUM = 0

        private val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}