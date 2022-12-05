package com.stop.ui.mission

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.skt.tmap.TMapPoint
import com.stop.R
import com.stop.databinding.FragmentMissionBinding
import com.stop.model.Location
import com.stop.ui.util.Marker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MissionFragment : Fragment(), MissionHandler {

    private var _binding: FragmentMissionBinding? = null
    private val binding: FragmentMissionBinding
        get() = _binding!!

    private val viewModel: MissionViewModel by viewModels()

    private lateinit var tMap: MissionTMap

    private var beforeLocation = INIT_LOCATION

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
            tMap.isTransportTracking = false
        }

        binding.layoutBusCurrent.setOnClickListener {
            tMap.tMapView.setCenterPoint(
                viewModel.busCurrentLocation.latitude,
                viewModel.busCurrentLocation.longitude,
                true
            )

            tMap.isTracking = false
            tMap.isTransportTracking = true
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

    private fun drawBusLocationLine() {
        viewModel.busNowLocationInfo.observe(viewLifecycleOwner) { nowLocations ->
            val nowLocation = nowLocations.first()

            if (beforeLocation != INIT_LOCATION) {
                tMap.drawMoveLine(
                    TMapPoint(nowLocation.latitude.toDouble(), nowLocation.longitude.toDouble()),
                    TMapPoint(beforeLocation.latitude, beforeLocation.longitude),
                    Marker.BUS_LINE +  BUS_LINE_NUM.toString(),
                    Marker.BUS_LINE_COLOR
                )
                BUS_LINE_NUM += 1
            }
            beforeLocation = Location(nowLocation.latitude.toDouble(), nowLocation.longitude.toDouble())

            viewModel.busCurrentLocation = beforeLocation

            tMap.addMarker(
                Marker.BUS_MARKER,
                Marker.BUS_MARKER_IMG,
                TMapPoint(nowLocation.latitude.toDouble(), nowLocation.longitude.toDouble())
            )
            tMap.trackingTransport(beforeLocation)
        }
    }

    private fun drawSubwayLocationLine() {
        viewModel.subwayRoute.observe(viewLifecycleOwner) { subwayRoute ->
            viewLifecycleOwner.lifecycleScope.launch {
                val timeUnit = (subwayRoute.sectionTime * SECOND_UNIT / subwayRoute.line.size).toLong()
                subwayRoute.line.forEachIndexed { index, nowLocation ->
                    if (index == 0) {
                        return@forEachIndexed
                    }

                    val beforeLocation = subwayRoute.line[index - 1]
                    tMap.drawMoveLine(
                        TMapPoint(nowLocation.latitude, nowLocation.longitude),
                        TMapPoint(beforeLocation.latitude, beforeLocation.longitude),
                        Marker.SUBWAY_LINE + (index - 1).toString(),
                        Marker.SUBWAY_LINE_COLOR
                    )

                    viewModel.busCurrentLocation = Location(nowLocation.latitude, nowLocation.longitude)

                    tMap.addMarker(
                        Marker.SUBWAY_MARKER,
                        Marker.SUBWAY_MARKER_IMG,
                        TMapPoint(nowLocation.latitude, nowLocation.longitude)
                    )

                    delay(timeUnit)
                }
            }

        }
    }

    override fun alertTMapReady() {
        //mimicUserMove()
        tMap.setTrackingMode()
        drawBusLocationLine()
        drawSubwayLocationLine()
    }

    override fun setOnLocationChangeListener(nowLocation: TMapPoint, beforeLocation: TMapPoint, canMakeLine: Boolean) {
        if (canMakeLine) {
            tMap.drawMoveLine(
                nowLocation,
                beforeLocation,
                Marker.PERSON_LINE + PERSON_LINE_NUM.toString(),
                Marker.PERSON_LINE_COLOR
            )
            PERSON_LINE_NUM += 1
        }
        viewModel.personCurrentLocation = Location(nowLocation.latitude, nowLocation.longitude)
    }

    override fun setOnEnableScrollWithZoomLevelListener() {
        tMap.apply {
            tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
                isTracking = false
                isTransportTracking = false
            }
        }
    }

    companion object {

        private const val DESTINATION = "구로3동현대아파트"
        private const val PLUS = "+"
        private const val MINUS = ""
        private const val LEFT_TIME = 60

        private var PERSON_LINE_NUM = 0

        private var BUS_LINE_NUM = 0

        private val INIT_LOCATION = Location(0.0, 0.0)

        private const val SECOND_UNIT = 1_000

    }
}