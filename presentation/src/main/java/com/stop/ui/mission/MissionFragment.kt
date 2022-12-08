package com.stop.ui.mission

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.skt.tmap.TMapPoint
import com.stop.R
import com.stop.databinding.FragmentMissionBinding
import com.stop.domain.model.route.tmap.custom.Place
import com.stop.domain.model.route.tmap.custom.WalkRoute
import com.stop.model.Location
import com.stop.ui.alarmsetting.AlarmSettingViewModel
import com.stop.ui.util.Marker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MissionFragment : Fragment(), MissionHandler {

    private var _binding: FragmentMissionBinding? = null
    private val binding: FragmentMissionBinding
        get() = _binding!!

    private val missionViewModel: MissionViewModel by viewModels()
    private val alarmSettingViewModel: AlarmSettingViewModel by activityViewModels<AlarmSettingViewModel>()

    private lateinit var tMap: MissionTMap

    var personCurrentLocation = Location(37.553836, 126.969652)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataBinding()
        initViewModel()
        initTMap()
        setObserve()
        setMissionOver()

    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    private fun setDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = missionViewModel
        binding.fragment = this@MissionFragment
    }

    private fun initTMap() {
        tMap = MissionTMap(requireActivity(), this)
        tMap.init()

        binding.constraintLayoutContainer.addView(tMap.tMapView)
    }

    private fun initViewModel() {
        missionViewModel.countDownWith(LEFT_TIME)
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
        Snackbar.make(requireActivity().findViewById(R.id.constraint_layout_container), "미션을 취소합니다", Snackbar.LENGTH_SHORT).show()
        missionViewModel.isMissionOver.value = true
        findNavController().navigate(R.id.action_missionFragment_to_mapFragment)
    }

    private fun setObserve() {
        val shortAnimationDuration =
            resources.getInteger(android.R.integer.config_shortAnimTime)

        missionViewModel.timeIncreased.observe(viewLifecycleOwner) {
            val sign = if (it > 0) {
                PLUS
            } else {
                MINUS
            }
            binding.textViewChangedTime.text =
                resources.getString(R.string.number_format).format(sign, it)
            binding.textViewChangedTime.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            animate().alpha(0f)
                                .setDuration(shortAnimationDuration.toLong())
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator?) {
                                        binding.textViewChangedTime.visibility = View.GONE
                                    }
                                })
                        }
                    })
            }
        }
    }

    override fun alertTMapReady() {
        requestPermissionsLauncher.launch(PERMISSIONS)
        getAlarmInfo()
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
        var first = 0
        lateinit var beforeLocation: Location
        lifecycleScope.launch {
            missionViewModel.userLocation.collect { userLocation ->
                when (first) {
                    0 -> {
                        first += 1
                    }
                    1 -> {
                        initMarker(userLocation)
                        beforeLocation = userLocation
                        first += 1
                    }
                    else -> {
                        drawNowLocationLine(TMapPoint(userLocation.latitude, userLocation.longitude), TMapPoint(beforeLocation.latitude, beforeLocation.longitude))
                        personCurrentLocation = userLocation
                        if (tMap.isTracking) {
                            tMap.tMapView.setCenterPoint(userLocation.latitude, userLocation.longitude)
                        }
                        beforeLocation = userLocation
                        arriveDestination(userLocation.latitude, userLocation.longitude)
                    }
                }
            }
        }
    }

    private fun initMarker(nowLocation: Location) {
        with(tMap) {
            addMarker(
                Marker.PERSON_MARKER,
                Marker.PERSON_MARKER_IMG,
                TMapPoint(nowLocation.latitude, nowLocation.longitude)
            )
            personCurrentLocation = nowLocation
            latitudes.add(nowLocation.latitude)
            longitudes.add(nowLocation.longitude)
            setRouteDetailFocus()
            arriveDestination(nowLocation.latitude, nowLocation.longitude)
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
        alarmSettingViewModel.getAlarm()
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
            missionViewModel.isMissionOver.value = true
            Snackbar.make(requireActivity().findViewById(R.id.constraint_layout_container), "정류장에 도착했습니다!", Snackbar.LENGTH_SHORT).show()
            setSuccessAnimation()

        }
    }

    private fun setSuccessAnimation() {
        with(binding.lottieSuccess) {
            visibility = View.VISIBLE
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    findNavController().navigate(R.id.action_missionFragment_to_mapFragment)
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
            addAnimatorListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    findNavController().navigate(R.id.action_missionFragment_to_mapFragment)
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
            missionViewModel.isMissionOver.collectLatest { isMissionOver ->
                if (isMissionOver) {
                    missionViewModel.cancelMission()
                    alarmSettingViewModel.deleteAlarm()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MissionWorker","onDestroy")
    }

    companion object {
        private const val PLUS = "+"
        private const val MINUS = ""
        private const val LEFT_TIME = 60

        private var PERSON_LINE_NUM = 0

        private val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}