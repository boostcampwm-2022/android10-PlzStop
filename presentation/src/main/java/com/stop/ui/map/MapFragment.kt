package com.stop.ui.map

import android.Manifest.permission
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.stop.BuildConfig
import com.stop.R
import com.stop.databinding.FragmentMapBinding
import com.stop.model.Location
import com.stop.ui.alarmsetting.AlarmViewModel
import com.stop.ui.placesearch.PlaceSearchViewModel
import kotlinx.coroutines.launch

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val placeSearchViewModel: PlaceSearchViewModel by activityViewModels()

    private lateinit var tMapView: TMapView
    private val enablePoint = mutableSetOf<Location>()
    private var isTracking = true
    private var isMapUiVisible = false

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

        initView()
        initNavigateAction()
        initTMap()
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.alarmViewModel = alarmViewModel
        binding.placeSearchViewModel = placeSearchViewModel
    }

    private fun initView() {
        binding.layoutCompass.setOnClickListener {
            tMapView.isCompassMode = tMapView.isCompassMode.not()
        }

        binding.layoutCurrent.setOnClickListener {
            requestPermissionsLauncher.launch(PERMISSIONS)
            isTracking = true
            tMapView.setCenterPoint(
                placeSearchViewModel.currentLocation.latitude,
                placeSearchViewModel.currentLocation.longitude,
                true
            )
        }
    }

    private fun initNavigateAction() {
        binding.textViewSearch.setOnClickListener {
            binding.root.findNavController()
                .navigate(R.id.action_mapFragment_to_placeSearchFragment)
        }

        binding.layoutBookmark.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_bookMarkFragment)
        }

        binding.textViewStartLocation.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_routeFragment)
        }

        binding.textViewEndLocation.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_routeFragment)
        }
    }

    private fun initTMap() {
        tMapView = TMapView(requireContext())
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_APP_KEY)
        tMapView.setOnMapReadyListener {
            requestPermissionsLauncher.launch(PERMISSIONS)
            tMapView.mapType = TMapView.MapType.DEFAULT
            tMapView.zoomLevel = TMAP_ZOOM_LEVEL
            tMapView.setVisibleLogo(false)

            addBookmarkMarker()
            initTMapListener()
            observeClickPlace()
            observeClickCurrentLocation()
        }

        binding.frameLayoutContainer.addView(tMapView)
    }

    private fun setTrackingMode() {
        val manager = TMapGpsManager(requireContext()).apply {
            minDistance = TMAP_MIN_DISTANCE
            provider = TMapGpsManager.PROVIDER_GPS
            openGps()
            provider = TMapGpsManager.PROVIDER_NETWORK
            openGps()
        }

        manager.setOnLocationChangeListener(onLocationChangeListener)
    }

    private fun addTMapMarker(id: String, icon: Int, location: TMapPoint) {
        val marker = TMapMarkerItem().apply {
            this.id = id
            this.icon = ContextCompat.getDrawable(requireContext(), icon)?.toBitmap()
            this.tMapPoint = location
        }

        tMapView.removeTMapMarkerItem(marker.id)
        tMapView.addTMapMarkerItem(marker)
    }

    private fun addBookmarkMarker() {
        placeSearchViewModel.bookmarks.forEachIndexed { index, location ->
            addTMapMarker(
                index.toString(),
                R.drawable.ic_bookmark_marker,
                TMapPoint(location.latitude, location.longitude)
            )
        }
    }

    private fun initTMapListener() {
        tMapView.setOnEnableScrollWithZoomLevelListener(onEnableScrollWithZoomLevelListener)
        tMapView.setOnDisableScrollWithZoomLevelListener(onDisableScrollWithZoomLevelListener)
        tMapView.setOnLongClickListenerCallback(onLongClickListener)
    }

    private fun observeClickPlace() {
        placeSearchViewModel.clickPlace.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { clickPlace ->
                val clickTMapPoint = TMapPoint(clickPlace.centerLat, clickPlace.centerLon)

                tMapView.setCenterPoint(clickTMapPoint.latitude, clickTMapPoint.longitude, true)
                addPanel(clickTMapPoint)
                addTMapMarker(POINT_MARKER, R.drawable.ic_point_marker, clickTMapPoint)
            }
        }
    }

    private fun observeClickCurrentLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                placeSearchViewModel.clickCurrentLocation.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect {
                        val currentLocation = placeSearchViewModel.currentLocation
                        val currentTMapPoint =
                            TMapPoint(currentLocation.latitude, currentLocation.longitude)

                        tMapView.setCenterPoint(
                            currentTMapPoint.latitude,
                            currentTMapPoint.longitude
                        )
                        addPanel(currentTMapPoint)
                        addTMapMarker(POINT_MARKER, R.drawable.ic_point_marker, currentTMapPoint)
                    }
            }
        }
    }

    private fun addPanel(tMapPoint: TMapPoint) {
        placeSearchViewModel.getGeoLocationInfo(tMapPoint.latitude, tMapPoint.longitude)
    }

    private fun setMapUiVisibility() {
        val viewVisibility = if (isMapUiVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }

        with (binding) {
            layoutSearch.visibility = viewVisibility
            layoutCompass.visibility = viewVisibility
            layoutCurrent.visibility = viewVisibility
            layoutBookmark.visibility = viewVisibility
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { it.value }) {
            setTrackingMode()
        }
    }

    private val onLocationChangeListener = TMapGpsManager.OnLocationChangedListener { location ->
        if (location != null) {
            if (isTracking) {
                tMapView.setCenterPoint(location.latitude, location.longitude, true)
            }

            placeSearchViewModel.currentLocation = Location(location.latitude, location.longitude)
            addTMapMarker(
                PERSON_MARKER,
                R.drawable.ic_person_marker,
                TMapPoint(location.latitude, location.longitude)
            )
        }
    }

    private val onEnableScrollWithZoomLevelListener =
        TMapView.OnEnableScrollWithZoomLevelCallback { _, tMapPoint ->
            isTracking = false
            enablePoint.add(Location(tMapPoint.latitude, tMapPoint.longitude))
        }

    private val onDisableScrollWithZoomLevelListener =
        TMapView.OnDisableScrollWithZoomLevelCallback { _, _ ->
            if (enablePoint.size == SIZE_ONE) {
                if (binding.layoutPanel.visibility == View.VISIBLE) {
                    binding.layoutPanel.visibility = View.GONE
                    tMapView.removeTMapMarkerItem(POINT_MARKER)
                } else {
                    setMapUiVisibility()
                    isMapUiVisible = isMapUiVisible.not()
                }
            }

            enablePoint.clear()
        }

    private val onLongClickListener = TMapView.OnLongClickListenerCallBack { _, _, tMapPoint ->
        tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude, true)
        addPanel(tMapPoint)
        addTMapMarker(POINT_MARKER, R.drawable.ic_point_marker, tMapPoint)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val TMAP_ZOOM_LEVEL = 16
        private const val TMAP_MIN_DISTANCE = 2.5F
        private const val POINT_MARKER = "point_marker"
        private const val PERSON_MARKER = "person_marker"
        private const val SIZE_ONE = 1
        private val PERMISSIONS =
            arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    }
}