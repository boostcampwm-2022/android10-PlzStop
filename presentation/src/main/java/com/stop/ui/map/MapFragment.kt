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
import com.skt.tmap.overlay.TMapMarkerItem2
import com.stop.BuildConfig
import com.stop.R
import com.stop.databinding.FragmentMapBinding
import com.stop.model.Location
import com.stop.ui.mission.Marker2Example
import com.stop.ui.placesearch.PlaceSearchViewModel
import kotlinx.coroutines.launch

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val placeSearchViewModel: PlaceSearchViewModel by activityViewModels()

    private lateinit var tMapView: TMapView
    private var isTracking = true
    private var mapUIVisibility = false

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

        clickSearchButton()
        clickEndLocation()
        initView()
        initTMap()
    }

    private fun initBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = placeSearchViewModel
        }
    }

    private fun clickEndLocation() {
        binding.textViewEndLocation.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_routeFragment)
        }
    }

    private fun clickSearchButton() {
        binding.textViewSearch.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_placeSearchFragment)
        }
    }

    private fun setMapUIVisibility() {
        if (mapUIVisibility) {
            setViewVisibility(View.VISIBLE)
        } else {
            setViewVisibility(View.GONE)
        }
    }

    private fun setViewVisibility(visibility: Int) {
        with(binding) {
            textViewSearch.visibility = visibility
            imageViewCompassMode.visibility = visibility
            imageViewCurrentLocation.visibility = visibility
            imageViewBookmark.visibility = visibility
        }
    }

    private fun clickMap() {
        val enablePoint = mutableSetOf<Location>()
        tMapView.setOnEnableScrollWithZoomLevelListener { _, centerPoint ->
            enablePoint.add(Location(centerPoint.latitude, centerPoint.longitude))
            isTracking = false
        }

        tMapView.zoomLevel
        tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
            if (enablePoint.size == SAME_POINT) {
                if (binding.layoutPanel.visibility == View.VISIBLE) {
                    binding.layoutPanel.visibility = View.GONE
                    tMapView.removeTMapMarkerItem(MARKER)
                } else {
                    setMapUIVisibility()
                    mapUIVisibility = mapUIVisibility.not()
                }
            }
            enablePoint.clear()
        }
    }

    private fun clickLocation() {
        tMapView.setOnLongClickListenerCallback { _, _, tMapPoint ->
            makeMarker(
                MARKER,
                R.drawable.ic_baseline_location_on_32,
                tMapPoint
            )
//            maker2Test(tMapPoint)

            tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude, true)
            setPanel(tMapPoint)
        }
    }

    private fun setPanel(tMapPoint: TMapPoint) {
        placeSearchViewModel.getGeoLocationInfo(tMapPoint.latitude, tMapPoint.longitude)
    }

    private fun makeMarker(id: String, icon: Int, location: TMapPoint) {
        val marker = TMapMarkerItem().apply {
            this.id = id
            this.icon = ContextCompat.getDrawable(
                requireContext(),
                icon
            )?.toBitmap()
            tMapPoint = location
        }

        tMapView.removeTMapMarkerItem(MARKER)
        tMapView.addTMapMarkerItem(marker)
    }


    fun maker2Test(location: TMapPoint){
        val view = Marker2Example(context)
        view.setText("마커2", "입니다")

        val marker = TMapMarkerItem2("marker2").apply {
            iconView = view
            tMapPoint = location
            isAnimation = true
            animationDuration = 0
        }

        tMapView.removeTMapMarkerItem2("marker2")
        tMapView.addTMapMarkerItem2View(marker)
        tMapView.setCenterPoint(location.latitude, location.longitude)
    }



    private fun initView() {
        binding.imageViewCompassMode.setOnClickListener {
            tMapView.isCompassMode = tMapView.isCompassMode.not()
        }

        binding.imageViewCurrentLocation.setOnClickListener {
            requestPermissionsLauncher.launch(PERMISSIONS)
            tMapView.setCenterPoint(
                placeSearchViewModel.currentLocation.latitude,
                placeSearchViewModel.currentLocation.longitude,
                true
            )
            makeMarker(
                PERSON_MARKER,
                R.drawable.ic_person_pin,
                TMapPoint(
                    placeSearchViewModel.currentLocation.latitude,
                    placeSearchViewModel.currentLocation.longitude
                )
            )
            isTracking = true
        }

        binding.imageViewBookmark.setOnClickListener {

        }
    }

    private fun initTMap() {
        tMapView = TMapView(requireContext())
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_APP_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
            tMapView.zoomLevel = 16
            requestPermissionsLauncher.launch(PERMISSIONS)

            tMapView.setCenterPoint(37.496986,126.887303) //초기위치 센터 바꿈

            clickLocation()
            clickMap()
            setBookmarkMarker()

            observeClickPlace()
            observeClickCurrentLocation()
        }
        binding.frameLayoutContainer.addView(tMapView)
    }

    private fun setBookmarkMarker() {
        placeSearchViewModel.bookmarks.forEachIndexed { index, location ->
            makeMarker(
                index.toString(),
                R.drawable.ic_baseline_stars_32,
                TMapPoint(location.latitude, location.longitude)
            )
        }
    }

    private fun setTrackingMode() {
        val manager = TMapGpsManager(requireContext()).apply {
            minDistance = 2.5F
            provider = TMapGpsManager.PROVIDER_GPS
            openGps()
            provider = TMapGpsManager.PROVIDER_NETWORK
            openGps()
        }

        manager.setOnLocationChangeListener(onLocationChangeListener)
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
            makeMarker(
                PERSON_MARKER,
                R.drawable.ic_person_pin,
                TMapPoint(location.latitude, location.longitude)
            )

            tMapView.setLocationPoint(location.latitude, location.longitude)
            placeSearchViewModel.currentLocation = Location(location.latitude, location.longitude)

            if (isTracking) {
                tMapView.setCenterPoint(location.latitude, location.longitude, true)
            }
        }
    }

    private fun observeClickPlace() {
        placeSearchViewModel.clickPlace.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { clickPlace ->
                val clickTMapPoint = TMapPoint(clickPlace.centerLat, clickPlace.centerLon)

                tMapView.setCenterPoint(clickTMapPoint.latitude, clickTMapPoint.longitude, true)

                setPanel(clickTMapPoint)

                makeMarker(
                    MARKER,
                    R.drawable.ic_baseline_location_on_32,
                    clickTMapPoint
                )
            }
        }
    }

    private fun observeClickCurrentLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                placeSearchViewModel.clickCurrentLocation
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect {
                        val currentLocation = placeSearchViewModel.currentLocation
                        val currentTMapPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)

                        tMapView.setCenterPoint(currentTMapPoint.latitude, currentTMapPoint.longitude)

                        setPanel(currentTMapPoint)

                        makeMarker(
                            MARKER,
                            R.drawable.ic_baseline_location_on_32,
                            currentTMapPoint
                        )
                    }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val MARKER = "marker"
        private const val PERSON_MARKER = "marker_person_pin"
        private const val SAME_POINT = 1
        val PERMISSIONS = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    }
}