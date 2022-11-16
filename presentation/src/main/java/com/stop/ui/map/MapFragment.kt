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
        binding.imageViewCurrentLocation.setOnClickListener {
            tMapView.setCenterPoint(tMapView.locationPoint.latitude, tMapView.locationPoint.longitude, true)
            setTrackingMode(true)
        }

        binding.imageViewCompassMode.setOnClickListener {
            tMapView.isCompassMode = tMapView.isCompassMode.not()
        }
    }

    private fun initTMap() {
        tMapView = TMapView(requireContext())
        tMapView.setSKTMapApiKey(T_MAP_API_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.mapType = TMapView.MapType.NIGHT
            tMapView.zoomLevel = 16
        }

        binding.frameLayoutContainer.addView(tMapView)
        setTrackingMode(true)
    }

    private fun setTrackingMode(isTracking: Boolean) {
        val manager = TMapGpsManager(requireContext())

        if (isTracking) {
            if (isLocationPermissionsGranted()) {
                setManagerDefaultOptions(manager)
                manager.setOnLocationChangeListener(onLocationChangeListener)
            }
        } else {
            manager.setOnLocationChangeListener(null)
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

    private fun setManagerDefaultOptions(manager: TMapGpsManager) {
        manager.minDistance = 2.5F
        manager.provider = TMapGpsManager.PROVIDER_GPS
        manager.openGps()
        manager.provider = TMapGpsManager.PROVIDER_NETWORK
        manager.openGps()
    }

    private val onLocationChangeListener = TMapGpsManager.OnLocationChangedListener { location ->
        if (location != null) {
            val marker = TMapMarkerItem().apply {
                this.id = "marker_person_pin"
                this.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_person_pin)?.toBitmap()
                this.setTMapPoint(location.latitude, location.longitude)
            }

            tMapView.removeTMapMarkerItem("marker_person_pin")
            tMapView.addTMapMarkerItem(marker)
            tMapView.setCenterPoint(location.latitude, location.longitude, true)
            tMapView.setLocationPoint(location.latitude, location.longitude)
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