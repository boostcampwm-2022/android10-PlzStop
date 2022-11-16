package com.stop.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.stop.R
import com.stop.databinding.FragmentMapBinding
import com.stop.model.Location

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
        clickLocation()
    }

    private fun clickLocation() {
        tMapView.setOnLongClickListenerCallback { _, _, tMapPoint ->
            makeMarker(
                MARKER,
                R.drawable.ic_baseline_location_on_32,
                Location(tMapPoint.latitude, tMapPoint.longitude)
            )
            tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude)
        }
    }

    private fun makeMarker(id: String, icon: Int, location: Location) {
        val marker = TMapMarkerItem()
        marker.id = id
        marker.icon = ContextCompat.getDrawable(
            requireContext(),
            icon
        )?.toBitmap()
        marker.setTMapPoint(location.latitude, location.longitude)
        tMapView.removeTMapMarkerItem(id)
        tMapView.addTMapMarkerItem(marker)
    }

    private fun initBinding() {
        // TODO lifecycleOwner, viewModel 설정
    }

    private fun initView() {
        binding.imageViewCurrentLocation.setOnClickListener {

        }

        binding.imageViewCompassMode.setOnClickListener {

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
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val T_MAP_API_KEY = "l7xxc7cabdc0790f4cbeacd90982df581610"
        const val MARKER = "marker"
    }
}