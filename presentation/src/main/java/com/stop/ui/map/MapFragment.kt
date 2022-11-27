package com.stop.ui.map

import android.Manifest.permission
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.skt.tmap.TMapPoint
import com.stop.R
import com.stop.databinding.FragmentMapBinding
import com.stop.model.Location
import com.stop.ui.placesearch.PlaceSearchViewModel
import kotlinx.coroutines.launch

class MapFragment : Fragment(), MapHandler {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val placeSearchViewModel: PlaceSearchViewModel by activityViewModels()

    private lateinit var tMap: MapTMap
    private var mapUIVisibility = View.GONE // false

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
        clickSearchButton()
        clickEndLocation()

    }

    private fun initBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = placeSearchViewModel
        }
    }

    private fun initTMap() {
        tMap = MapTMap(requireContext(), this)
        tMap.init()

        binding.frameLayoutContainer.addView(tMap.tMapView)
    }

    private fun initView() {
        binding.imageViewCompassMode.setOnClickListener {
            tMap.tMapView.isCompassMode = tMap.tMapView.isCompassMode.not()
        }

        binding.imageViewCurrentLocation.setOnClickListener {
            requestPermissionsLauncher.launch(PERMISSIONS)
            tMap.tMapView.setCenterPoint(
                placeSearchViewModel.currentLocation.latitude,
                placeSearchViewModel.currentLocation.longitude,
                true
            )
            tMap.makeMarker(
                PERSON_MARKER,
                PERSON_MARKER_IMG,
                TMapPoint(
                    placeSearchViewModel.currentLocation.latitude,
                    placeSearchViewModel.currentLocation.longitude
                )
            )
            tMap.isTracking = true
        }

        binding.imageViewBookmark.setOnClickListener {

        }
    }

    private fun clickSearchButton() {
        binding.textViewSearch.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_placeSearchFragment)
        }
    }

    private fun clickEndLocation() {
        binding.textViewEndLocation.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_routeFragment)
        }
    }

    private fun setViewVisibility() {
        with(binding) {
            textViewSearch.visibility = mapUIVisibility
            imageViewCompassMode.visibility = mapUIVisibility
            imageViewCurrentLocation.visibility = mapUIVisibility
            imageViewBookmark.visibility = mapUIVisibility
        }
    }

    private fun setBookmarkMarker() {
        placeSearchViewModel.bookmarks.forEachIndexed { index, location ->
            tMap.makeMarker(
                index.toString(),
                R.drawable.ic_baseline_stars_32,
                TMapPoint(location.latitude, location.longitude)
            )
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.any { it.value }) {
            tMap.setTrackingMode()
            Log.d("MapFragment","setTrackingMode")
        }
    }

    private fun observeClickPlace() {
        placeSearchViewModel.clickPlace.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { clickPlace ->
                val clickTMapPoint = TMapPoint(clickPlace.centerLat, clickPlace.centerLon)

                tMap.tMapView.setCenterPoint(clickTMapPoint.latitude, clickTMapPoint.longitude, true)

                setPanel(clickTMapPoint)

                tMap.makeMarker(
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

                        tMap.tMapView.setCenterPoint(currentTMapPoint.latitude, currentTMapPoint.longitude, true)

                        setPanel(currentTMapPoint)

                        tMap.makeMarker(
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

    override fun alertTMapReady() {
        requestPermissionsLauncher.launch(PERMISSIONS)

        tMap.clickLocation()
        tMap.clickMap()
        setBookmarkMarker()

        observeClickPlace()
        observeClickCurrentLocation()
    }

    override fun setOnLocationChangeListener(location: android.location.Location) {
        placeSearchViewModel.currentLocation = Location(location.latitude, location.longitude)
    }

    override fun setOnDisableScrollWIthZoomLevelListener() {
        if (binding.layoutPanel.visibility == View.VISIBLE) {
            binding.layoutPanel.visibility = View.GONE
            tMap.tMapView.removeTMapMarkerItem(MARKER)
        } else {
            setViewVisibility()
            mapUIVisibility = mapUIVisibility.xor(View.GONE)
        }
    }

    override fun setPanel(tMapPoint: TMapPoint) {
        placeSearchViewModel.getGeoLocationInfo(tMapPoint.latitude, tMapPoint.longitude)
    }

    companion object {
        private const val MARKER = "marker"
        private const val PERSON_MARKER = "marker_person_pin"
        private const val PERSON_MARKER_IMG = R.drawable.ic_person_pin
        val PERMISSIONS = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    }
}