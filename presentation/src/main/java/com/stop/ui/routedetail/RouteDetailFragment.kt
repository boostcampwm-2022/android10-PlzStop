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
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.stop.R
import com.stop.databinding.FragmentRouteDetailBinding
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.ui.route.RouteViewModel
import com.stop.ui.route.RouteResultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteDetailFragment : Fragment(), RouteDetailHandler {
    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!

    private val routeViewModel: RouteViewModel by activityViewModels()

    private lateinit var tMap: RouteDetailTMap
    private val routeResultViewModel: RouteResultViewModel by navGraphViewModels(R.id.route_nav_graph)

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
        setRecyclerView()
    }

    override fun alertTMapReady() {
        tMap.drawRoutes(routeViewModel.tempItinerary.routes)
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.routeResultViewModel = routeResultViewModel
        binding.routeViewModel = routeViewModel
        binding.itinerary = routeViewModel.tempItinerary
    }

    private fun initTMap() {
        tMap = RouteDetailTMap(requireActivity(), this)
        tMap.init()

        binding.layoutContainer.addView(tMap.tMapView)
    }

    private fun initView() {
        binding.layoutDrawer.openDrawer(GravityCompat.START)

        binding.imageViewArrowDrawer.setOnClickListener {
            binding.layoutDrawer.openDrawer(GravityCompat.START)
        }

        binding.routeDetailDrawer.viewAlarm.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_routeDetailFragment_to_alarmSetting)
        }

        binding.imageViewClose.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_routeDetailFragment_to_mapFragment)
        }
    }

    private fun setRecyclerView() {
        val adapter = RouteDetailAdapter(object : OnRouteItemClickListener {
            override fun clickRouteItem(coordinate: Coordinate) {
                binding.layoutDrawer.closeDrawer(GravityCompat.START)
                tMap.setRouteItemFocus(coordinate)
            }
        })

        binding.routeDetailDrawer.recyclerViewRouteDetail.adapter = adapter
        adapter.submitList(routeViewModel.getRouteItems())
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}