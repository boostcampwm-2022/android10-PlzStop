package com.stop.ui.mission

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.skt.tmap.TMapPoint
import com.stop.R
import com.stop.databinding.FragmentMissionBinding
import com.stop.model.Location
import dagger.hilt.android.AndroidEntryPoint

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

//    private fun mimicUserMove() {
//        val lines = readFromAssets()
//
//        CoroutineScope(Dispatchers.IO).launch {
//            lines.forEach { line ->
//                val (longitude, latitude) = line.split(",")
//                tMap.moveLocation(longitude, latitude)
//                delay(500)
//            }
//        }
//    }

//    private fun readFromAssets(): List<String> {
//        val reader =
//            BufferedReader(InputStreamReader(requireContext().assets.open(FAKE_USER_FILE_PATH)))
//        val lines = arrayListOf<String>()
//        var line = reader.readLine()
//        while (line != null) {
//            lines.add(line)
//            line = reader.readLine()
//        }
//        reader.close()
//        return lines
//    }

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

        binding.layoutBusCurrent.setOnClickListener {
            tMap.tMapView.setCenterPoint(
                viewModel.busCurrentLocation.latitude,
                viewModel.busCurrentLocation.longitude,
                true
            )

            tMap.isTracking = false
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
    }

    private fun drawBusLocationLine() {
        viewModel.busNowLocationInfo.observe(viewLifecycleOwner) { nowLocation ->
            if (beforeLocation != INIT_LOCATION) {
                tMap.drawMoveLine(
                    TMapPoint(nowLocation.latitude, nowLocation.longitude),
                    TMapPoint(beforeLocation.latitude, beforeLocation.longitude),
                    BUS_LINE + BUS_LINE_NUM.toString(),
                    BUS_LINE_COLOR
                )
                BUS_LINE_NUM += 1
            }
            beforeLocation = Location(nowLocation.latitude, nowLocation.longitude)

            viewModel.busCurrentLocation = beforeLocation

            tMap.makeMarker(
                BUS_MARKER,
                BUS_MARKER_IMG,
                TMapPoint(nowLocation.latitude, nowLocation.longitude)
            )
        }
    }

    override fun alertTMapReady() {
        //mimicUserMove()
        tMap.setTrackingMode()
        drawBusLocationLine()
    }

    override fun setOnLocationChangeListener(nowLocation: TMapPoint, beforeLocation: TMapPoint, canMakeLine: Boolean) {
        if (canMakeLine) {
            tMap.drawMoveLine(
                nowLocation,
                beforeLocation,
                PERSON_LINE + PERSON_LINE_NUM.toString(),
                PERSON_LINE_COLOR
            )
            PERSON_LINE_NUM += 1
        }
        viewModel.personCurrentLocation = Location(nowLocation.latitude, nowLocation.longitude)

    }

    override fun setOnEnableScrollWithZoomLevelListener() {
        tMap.apply {
            tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
                isTracking = false
            }
        }
    }

    companion object {

        private const val DESTINATION = "구로3동현대아파트"
        private const val PLUS = "+"
        private const val MINUS = ""
        private const val LEFT_TIME = 60

        private const val PERSON_LINE = "person_line"
        private const val PERSON_LINE_COLOR = Color.MAGENTA
        private var PERSON_LINE_NUM = 0

        private const val BUS_LINE = "bus_line"
        private const val BUS_LINE_COLOR = Color.BLUE
        private var BUS_LINE_NUM = 0

        private val INIT_LOCATION = Location(0.0, 0.0)

        private const val BUS_MARKER = "marker_bus_pin"
        private const val BUS_MARKER_IMG = R.drawable.ic_bus_marker

    }
}