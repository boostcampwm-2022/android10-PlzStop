package com.stop.ui.map

import android.Manifest.permission
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.skt.tmap.TMapData
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.address.TMapAddressInfo
import com.skt.tmap.overlay.TMapMarkerItem
import com.stop.R
import com.stop.databinding.FragmentMapBinding
import com.stop.model.Location
import com.stop.ui.nearplace.PlaceSearchViewModel
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
    private var lastMarker = NONE_LOCATION
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

        placeSearchViewModel.clickPlace.observe(viewLifecycleOwner){
            Log.e("ABC",it.toString())
        }

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
            lastMarker = TMapPoint(tMapPoint.latitude, tMapPoint.longitude)
            makeMarker(
                R.drawable.ic_baseline_location_on_32,
                lastMarker
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
        val marker = TMapMarkerItem()
        marker.id = MARKER
        marker.icon = ContextCompat.getDrawable(
            requireActivity(),
            icon
        )?.toBitmap()
        marker.setTMapPoint(location.latitude, location.longitude)
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
        tMapView.setSKTMapApiKey(T_MAP_API_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
            tMapView.zoomLevel = 16
            requestPermissionsLauncher.launch(PERMISSIONS)
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

            if (isTracking) {
                tMapView.setCenterPoint(location.latitude, location.longitude, true)
            }
        }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val T_MAP_API_KEY = "l7xxc7cabdc0790f4cbeacd90982df581610"
        private const val MARKER = "marker"
        private const val LOT_ADDRESS_TYPE = "A02"
        private const val ROAD_ADDRESS_TYPE = "A04"
        private const val SAME_POINT = 1
        private val NONE_LOCATION = TMapPoint(0.0, 0.0)
        val PERMISSIONS = arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
    }
}
