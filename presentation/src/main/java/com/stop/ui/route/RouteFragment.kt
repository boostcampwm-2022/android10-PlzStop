package com.stop.ui.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.stop.databinding.FragmentRouteBinding
import com.stop.model.route.Coordinate
import com.stop.model.route.Place
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    private val binding: FragmentRouteBinding
        get() = _binding!!

    private val viewModel: RouteViewModel by viewModels()
    private val adapter = RouteAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBinding()
        setStartAndDestinationText()
        setObserve()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.recyclerviewRoute.adapter = adapter
        binding.executePendingBindings()
    }

    private fun setObserve() {
        viewModel.routeResponse.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            adapter.submitList(it)
        }
    }

    private fun setStartAndDestinationText() {
        viewModel.setOrigin(Place(ORIGIN_NAME, Coordinate(ORIGIN_Y, ORIGIN_X)))
        viewModel.setDestination(Place(DESTINATION_NAME, Coordinate(DESTINATION_Y, DESTINATION_X)))
        viewModel.getRoute()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val ORIGIN_NAME = "이앤씨벤쳐드림타워3차"
        private const val ORIGIN_X = "126.893820"
        private const val ORIGIN_Y = "37.4865002"

        private const val DESTINATION_NAME = "Naver1784"
        private const val DESTINATION_X = "127.105037"
        private const val DESTINATION_Y = "37.3584879"
    }
}