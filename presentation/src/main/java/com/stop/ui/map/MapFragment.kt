package com.stop.ui.map

import android.Manifest.permission
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.skt.tmap.TMapPoint
import com.stop.R
import com.stop.databinding.FragmentMapBinding
import com.stop.model.Location
import com.stop.ui.alarmsetting.AlarmViewModel
import com.stop.ui.placesearch.PlaceSearchViewModel
import kotlinx.coroutines.launch

class MapFragment : Fragment(), MapHandler {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val placeSearchViewModel: PlaceSearchViewModel by activityViewModels()

    private lateinit var tMap: MapTMap
    private var mapUIVisibility = View.GONE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        initBinding()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTMap()
        initView()
        initNavigateAction()
        initBottomSheetBehavior()
    }

    override fun alertTMapReady() {
        requestPermissionsLauncher.launch(PERMISSIONS)
        tMap.initListener()

        addBookmarkMarker()
        observeClickPlace()
        observeClickCurrentLocation()
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.alarmViewModel = alarmViewModel
        binding.placeSearchViewModel = placeSearchViewModel
    }

    private fun initTMap() {
        tMap = MapTMap(requireContext(), this)
        tMap.init()

        binding.frameLayoutContainer.addView(tMap.tMapView)
    }

    private fun initView() {
        binding.layoutCompass.setOnClickListener {
            tMap.tMapView.isCompassMode = tMap.tMapView.isCompassMode.not()
        }

        binding.layoutCurrent.setOnClickListener {
            requestPermissionsLauncher.launch(PERMISSIONS)
            tMap.tMapView.setCenterPoint(
                placeSearchViewModel.currentLocation.latitude,
                placeSearchViewModel.currentLocation.longitude,
                true
            )
            tMap.addMarker(
                PERSON_MARKER,
                PERSON_MARKER_IMG,
                TMapPoint(
                    placeSearchViewModel.currentLocation.latitude,
                    placeSearchViewModel.currentLocation.longitude
                )
            )
            tMap.isTracking = true
        }

        binding.layoutBookmark.setOnClickListener {
            alarmViewModel.isBottomSheetVisible.value?.let {
                alarmViewModel.setVisibility(it)
            }
        }
    }

    private fun initNavigateAction() {
        binding.textViewSearch.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_placeSearchFragment)
        }

        /*
        binding.layoutBookmark.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_bookMarkFragment)
        }
        */

        binding.layoutPanel.findViewById<View>(R.id.view_panel_start).setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_routeFragment)
        }

        binding.layoutPanel.findViewById<View>(R.id.view_panel_end).setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_routeFragment)
        }
    }

    private fun initBottomSheetBehavior() {
        val behavior = BottomSheetBehavior.from(binding.layoutHomeBottomSheet)

        alarmViewModel.isBottomSheetVisible.observe(viewLifecycleOwner) { isBottomSheetVisible ->
            if (isBottomSheetVisible) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.maxHeight = convertDpToPx(200)
            } else {
                behavior.maxHeight = convertDpToPx(100)
            }
        }
    }

    private fun addBookmarkMarker() {
        placeSearchViewModel.bookmarks.forEachIndexed { index, location ->
            tMap.addMarker(
                index.toString(),
                BOOKMARK_MARKER_IMG,
                TMapPoint(location.latitude, location.longitude)
            )
        }
    }

    private fun observeClickPlace() {
        placeSearchViewModel.clickPlace.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { clickPlace ->
                val clickTMapPoint = TMapPoint(clickPlace.centerLat, clickPlace.centerLon)

                tMap.tMapView.setCenterPoint(clickTMapPoint.latitude, clickTMapPoint.longitude, true)
                tMap.addMarker(PLACE_MARKER, PLACE_MARKER_IMG, clickTMapPoint)
                setPanel(clickTMapPoint)
            }
        }
    }

    private fun observeClickCurrentLocation() {
        lifecycleScope.launch {
            placeSearchViewModel.clickCurrentLocation
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    val currentLocation = placeSearchViewModel.currentLocation
                    val currentTMapPoint =
                        TMapPoint(currentLocation.latitude, currentLocation.longitude)

                    tMap.tMapView.setCenterPoint(
                        currentTMapPoint.latitude,
                        currentTMapPoint.longitude
                    )
                    tMap.addMarker(PLACE_MARKER, PLACE_MARKER_IMG, currentTMapPoint)
                    setPanel(currentTMapPoint)
                }
        }
    }

    override fun setPanel(tMapPoint: TMapPoint) {
        placeSearchViewModel.getGeoLocationInfo(tMapPoint.latitude, tMapPoint.longitude)
    }

    private fun setViewVisibility() {
        with(binding) {
            layoutSearch.visibility = mapUIVisibility
            layoutCompass.visibility = mapUIVisibility
            layoutCurrent.visibility = mapUIVisibility
            layoutBookmark.visibility = mapUIVisibility
        }
    }

    private fun convertDpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun setOnLocationChangeListener(location: android.location.Location) {
        placeSearchViewModel.currentLocation = Location(location.latitude, location.longitude)
    }

    override fun setOnDisableScrollWIthZoomLevelListener() {
        if (binding.layoutPanel.visibility == View.VISIBLE) {
            binding.layoutPanel.visibility = View.GONE
            tMap.tMapView.removeTMapMarkerItem(PLACE_MARKER)
        } else {
            setViewVisibility()
            mapUIVisibility = mapUIVisibility.xor(View.GONE)
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { it.value }) {
            tMap.setTrackingMode()
        }
    }

    companion object {
        private const val PLACE_MARKER = "place_marker"
        private const val PLACE_MARKER_IMG = R.drawable.ic_place_marker

        private const val PERSON_MARKER = "person_marker"
        private const val PERSON_MARKER_IMG = R.drawable.ic_person_marker

        private const val BOOKMARK_MARKER_IMG = R.drawable.ic_bookmark_marker

        private val PERMISSIONS =
            arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    }
}
