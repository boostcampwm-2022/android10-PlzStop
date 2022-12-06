package com.stop.ui.routedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.stop.R
import com.stop.databinding.FragmentRouteDetailBinding
import com.stop.ui.route.RouteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteDetailFragment : Fragment(), RouteDetailHandler {
    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!

    private val routeViewModel: RouteViewModel by activityViewModels()

    private lateinit var tMap: RouteDetailTMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteDetailBinding.inflate(inflater, container, false)
        initBinding()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTMap()
        initView()
    }

    override fun alertTMapReady() {
        tMap.drawRoutes(routeViewModel.tempItinerary.routes)
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.routeViewModel = routeViewModel
        binding.itinerary = routeViewModel.tempItinerary
    }

    private fun initTMap() {
        tMap = RouteDetailTMap(requireActivity(), this)
        tMap.init()

        binding.frameLayoutContainer.addView(tMap.tMapView)
    }

    private fun initView() {
        binding.drawerLayout.openDrawer(GravityCompat.START)

        binding.routeDetailDrawer.viewAlarm.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_routeDetailFragment_to_alarmSetting)
        }

        binding.imageViewClose.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_routeDetailFragment_to_mapFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}