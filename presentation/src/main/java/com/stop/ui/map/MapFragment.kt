package com.stop.ui.map

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.stop.R
import com.stop.databinding.FragmentMapBinding

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var tMapView: TMapView
    private var isTracking = true

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
        initTMap()
    }

    private fun initBinding() {
        // TODO lifecycleOwner, viewModel 설정
    }

    private fun initView() {
        binding.imageViewCompassMode.setOnClickListener {
            tMapView.isCompassMode = tMapView.isCompassMode.not()
        }

        binding.imageViewCurrentLocation.setOnClickListener {
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
            setTrackingMode()
        }
        tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
            isTracking = false
        }

        binding.frameLayoutContainer.addView(tMapView)
    }

    private fun setTrackingMode() {
        if (isLocationPermissionsGranted()) {
            val manager = TMapGpsManager(requireContext()).apply {
                minDistance = 2.5F
                provider = TMapGpsManager.PROVIDER_GPS
                openGps()
                provider = TMapGpsManager.PROVIDER_NETWORK
                openGps()
            }

            manager.setOnLocationChangeListener(onLocationChangeListener)
        }
    }

    private fun isLocationPermissionsGranted(): Boolean {
        val permissions = listOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
        val deniedPermissions = permissions.filter { permission ->
            checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_DENIED
        }

        if (deniedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), deniedPermissions.toTypedArray(), 100)
        }

        return permissions.any { permission ->
            checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
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
        const val T_MAP_API_KEY = "l7xxc7cabdc0790f4cbeacd90982df581610"
    }
}