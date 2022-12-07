package com.stop.ui.mission

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.skt.tmap.TMapPoint
import com.stop.R
import com.stop.databinding.FragmentMissionBinding
import com.stop.model.Location
import com.stop.ui.util.Marker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MissionFragment : Fragment(), MissionHandler {

    private var _binding: FragmentMissionBinding? = null
    private val binding: FragmentMissionBinding
        get() = _binding!!

    private val viewModel: MissionViewModel by viewModels()

    private lateinit var tMap: MissionTMap

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
        initView()
        setObserve()
        drawPersonLine()

    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    private fun setDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initTMap() {
        tMap = MissionTMap((requireContext() as ContextWrapper).baseContext, this)
        tMap.init()

        binding.constraintLayoutContainer.addView(tMap.tMapView)
    }

    private fun initViewModel() {
        viewModel.setDestination(DESTINATION)
        viewModel.countDownWith(LEFT_TIME)
    }

    private fun initView() {
        binding.layoutCompass.setOnClickListener {
            tMap.tMapView.isCompassMode = tMap.tMapView.isCompassMode.not()
        }

        binding.layoutPersonCurrent.setOnClickListener {
            tMap.tMapView.setCenterPoint(
                viewModel.personCurrentLocation.latitude,
                viewModel.personCurrentLocation.longitude,
                true
            )

            tMap.isTracking = true
        }

    }

    private fun setObserve() {
        val shortAnimationDuration =
            resources.getInteger(android.R.integer.config_shortAnimTime)

        viewModel.timeIncreased.observe(viewLifecycleOwner) {
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

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { errorType ->
                val message = getString(errorType.stringResourcesId)

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.transportIsArrived.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { isArrived ->
                if (isArrived) {
                    Toast.makeText(requireContext(), "도착했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun alertTMapReady() {
        requestPermissionsLauncher.launch(PERMISSIONS)
        makeDestinationMarker()
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
            viewModel.userLocation.collect { userLocation ->
                when (first) {
                    0 -> {
                        first += 1
                    }
                    1 -> {
                        beforeLocation = userLocation
                        tMap.tMapView.setCenterPoint(userLocation.latitude, userLocation.longitude)
                        tMap.addMarker(
                            Marker.PERSON_MARKER,
                            Marker.PERSON_MARKER_IMG,
                            TMapPoint(userLocation.latitude, userLocation.longitude)
                        )
                        viewModel.personCurrentLocation = userLocation
                        first += 1
                    }
                    else -> {
                        Log.d("MissionWorker", "그리는 중 $userLocation $beforeLocation")
                        val nowLocation = TMapPoint(userLocation.latitude, userLocation.longitude)
                        tMap.drawMoveLine(
                            nowLocation,
                            TMapPoint(beforeLocation.latitude, beforeLocation.longitude),
                            Marker.PERSON_LINE + PERSON_LINE_NUM.toString(),
                            Marker.PERSON_LINE_COLOR
                        )
                        tMap.addMarker(Marker.PERSON_MARKER, Marker.PERSON_MARKER_IMG, nowLocation)
                        viewModel.personCurrentLocation = userLocation
                        if (tMap.isTracking) {
                            tMap.tMapView.setCenterPoint(userLocation.latitude, userLocation.longitude)
                        }
                        beforeLocation = userLocation
                        PERSON_LINE_NUM += 1
                    }
                }

            }
        }
    }

    private fun makeDestinationMarker() {
        tMap.addMarker(Marker.DESTINATION_MARKER, Marker.DESTINATION_MARKER_IMG, TEST_DESTINATION)
    }

    companion object {

        private const val DESTINATION = "구로3동현대아파트"
        private const val PLUS = "+"
        private const val MINUS = ""
        private const val LEFT_TIME = 60

        private var PERSON_LINE_NUM = 0

        private val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        private val TEST_DESTINATION = TMapPoint(37.553836, 126.969652)

    }
}