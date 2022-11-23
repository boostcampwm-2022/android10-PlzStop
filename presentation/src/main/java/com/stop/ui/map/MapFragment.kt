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
import com.skt.tmap.TMapData
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.address.TMapAddressInfo
import com.skt.tmap.overlay.TMapMarkerItem
import com.stop.BuildConfig
import com.stop.R
import com.stop.databinding.FragmentMapBinding
import com.stop.model.Location
import com.stop.ui.placesearch.PlaceSearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        buttonClick()
        initView()
        initTMap()
        clickLocation()
        clickMap()
    }

    private fun buttonClick() {
        binding.textViewSearch.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_mapFragment_to_placeSearchFragment)
        }
    }

    private fun setMapUIVisibility() {
        with(binding) {
            if (mapUIVisibility) {
                textViewSearch.visibility = View.VISIBLE
                imageViewCompassMode.visibility = View.VISIBLE
                imageViewCurrentLocation.visibility = View.VISIBLE
                imageViewBookmark.visibility = View.VISIBLE

            } else {
                textViewSearch.visibility = View.GONE
                imageViewCompassMode.visibility = View.GONE
                imageViewCurrentLocation.visibility = View.GONE
                imageViewBookmark.visibility = View.GONE
            }
        }
    }

    private fun clickMap() {
        val enablePoint = mutableSetOf<Location>()
        tMapView.setOnEnableScrollWithZoomLevelListener { _, centerPoint ->
            enablePoint.add(Location(centerPoint.latitude, centerPoint.longitude))
        }

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
                R.drawable.ic_baseline_location_on_32,
                tMapPoint
            )

            tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude, true)
            setPanel(tMapPoint)
        }
    }

    private fun setPanel(tMapPoint: TMapPoint) {
        CoroutineScope(Dispatchers.Main).launch {
            lateinit var lotAddressInfo: TMapAddressInfo
            lateinit var roadAddressInfo: TMapAddressInfo
            withContext(Dispatchers.IO) {
                lotAddressInfo = TMapData().reverseGeocoding(tMapPoint.latitude, tMapPoint.longitude, LOT_ADDRESS_TYPE)
                roadAddressInfo =
                    TMapData().reverseGeocoding(tMapPoint.latitude, tMapPoint.longitude, ROAD_ADDRESS_TYPE)
            }
            setAddressInfo(lotAddressInfo, roadAddressInfo)
        }
    }

    private fun setAddressInfo(lotAddressInfo: TMapAddressInfo, roadAddressInfo: TMapAddressInfo) {
        with(binding) {
            textViewTitle.visibility = setVisibility(roadAddressInfo.strBuildingName)
            textViewLotAddress.visibility = setVisibility(lotAddressInfo.strFullAddress)
            textViewRoadAddress.visibility = setVisibility(roadAddressInfo.strFullAddress)

            textViewTitle.text = roadAddressInfo.strBuildingName
            textViewLotAddress.text = lotAddressInfo.strFullAddress
            textViewRoadAddress.text = roadAddressInfo.strFullAddress.replace(roadAddressInfo.strBuildingName, "")
            layoutPanel.visibility = View.VISIBLE
        }
    }

    private fun setVisibility(address: String): Int {
        return if (address.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun makeMarker(icon: Int, location: TMapPoint) {
        val marker = TMapMarkerItem().apply {
            this.id = MARKER
            this.icon = ContextCompat.getDrawable(
                requireContext(),
                icon
            )?.toBitmap()
            this.tMapPoint = location
        }

        tMapView.removeTMapMarkerItem(MARKER)
        tMapView.addTMapMarkerItem(marker)
    }

    private fun initBinding() {
        // TODO lifecycleOwner, viewModel 설정
    }

    private fun initView() {
        binding.imageViewCompassMode.setOnClickListener {
            tMapView.isCompassMode = tMapView.isCompassMode.not()
        }

        binding.imageViewCurrentLocation.setOnClickListener {
            requestPermissionsLauncher.launch(PERMISSIONS)
            tMapView.setCenterPoint(tMapView.locationPoint.latitude, tMapView.locationPoint.longitude, true)
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

            observeClickPlace()
            observeClickCurrentLocation()
        }
        tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
            isTracking = false
        }

        binding.frameLayoutContainer.addView(tMapView)
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
            val marker = TMapMarkerItem().apply {
                id = "marker_person_pin"
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_person_pin)?.toBitmap()
                setTMapPoint(location.latitude, location.longitude)
            }

            tMapView.removeTMapMarkerItem("marker_person_pin")
            tMapView.addTMapMarkerItem(marker)
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
                val clickTmapPoint = TMapPoint(clickPlace.centerLat, clickPlace.centerLon)

                tMapView.setCenterPoint(clickTmapPoint.latitude, clickTmapPoint.longitude, true)

                setPanel(clickTmapPoint)

                makeMarker(
                    R.drawable.ic_baseline_location_on_32,
                    clickTmapPoint
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
                        val currentTmapPoint = TMapPoint(currentLocation.latitude, currentLocation.longitude)

                        tMapView.setCenterPoint(currentTmapPoint.latitude, currentTmapPoint.longitude)

                        setPanel(currentTmapPoint)

                        makeMarker(
                            R.drawable.ic_baseline_location_on_32,
                            currentTmapPoint
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
        private const val LOT_ADDRESS_TYPE = "A02"
        private const val ROAD_ADDRESS_TYPE = "A04"
        private const val SAME_POINT = 1
        val PERMISSIONS = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    }
}
