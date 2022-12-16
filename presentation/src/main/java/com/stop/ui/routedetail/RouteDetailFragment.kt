package com.stop.ui.routedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.stop.R
import com.stop.databinding.FragmentRouteDetailBinding
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.ui.route.RouteResultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteDetailFragment : Fragment(), RouteDetailHandler {

    private var _binding: FragmentRouteDetailBinding? = null
    private val binding: FragmentRouteDetailBinding
        get() = _binding!!

    private val routeResultViewModel: RouteResultViewModel by navGraphViewModels(R.id.route_nav_graph)

    private lateinit var tMap: RouteDetailTMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteDetailBinding.inflate(inflater, container, false)

        initBinding()

        return binding.root
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.routeResultViewModel = routeResultViewModel
        binding.itinerary = routeResultViewModel.itinerary.value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTMap()
        initView()
        setRecyclerView()
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
            if (routeResultViewModel.isLastTimeAvailable.value == true) {
                findNavController().navigate(R.id.action_routeDetailFragment_to_alarmSetting)
            }
        }

        binding.imageViewClose.setOnClickListener {
            findNavController().apply {
                popBackStack(R.id.mapFragment, false)
                requireActivity().viewModelStore.clear()
            }
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
        adapter.submitList(routeResultViewModel.getRouteItems())
    }

    override fun alertTMapReady() {
        routeResultViewModel.itinerary.value?.let {
            tMap.drawRoutes(it.routes)
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

}