package com.stop.ui.route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.fragment.app.activityViewModels
import com.stop.R
import com.stop.databinding.FragmentRouteBinding
import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.model.ErrorType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    private val binding: FragmentRouteBinding
        get() = _binding!!

    private val routeViewModel: RouteViewModel by activityViewModels()
    private val clickRouteViewModel : ClickRouteViewModel by activityViewModels()

    private val args: RouteFragmentArgs by navArgs()

    private lateinit var adapter: RouteAdapter

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
        setListener()
        setRecyclerView()
        setStartAndDestinationText()
        setObserve()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = routeViewModel
    }

    private fun setListener() {
        binding.textViewOrigin.setOnClickListener {
            val action = RouteFragmentDirections.actionRouteFragmentToPlaceSearchFragment()
            binding.root.findNavController().navigate(action)
        }
        binding.textViewDestination.setOnClickListener {
            val action = RouteFragmentDirections.actionRouteFragmentToPlaceSearchFragment()
            binding.root.findNavController().navigate(action)
        }
    }

    private fun setRecyclerView() {
        adapter = RouteAdapter(object : OnItineraryClickListener {
            override fun onItineraryClick(itinerary: Itinerary) {
                /**
                 * UI가 ViewModel을 직접 호출하면 안 되지만, 테스트를 위해 막차 조회 함수를 호출했습니다.
                 * 여기서 UI가 ViewModel을 직접 호출하지 않으면서 막차 조회 함수를 호출할 수 있을까요?
                 */
                routeViewModel.calculateLastTransportTime(itinerary)
                clickRouteViewModel.clickRoute = itinerary
            }
        })
        binding.recyclerviewRoute.adapter = adapter
    }

    private fun setObserve() {
        routeViewModel.routeResponse.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            adapter.submitList(it)
        }

        routeViewModel.errorMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { errorType ->
                val message = when (errorType) {
                    ErrorType.NO_START -> getString(R.string.no_start_input)
                    ErrorType.NO_END -> getString(R.string.no_end_input)
                }

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        routeViewModel.lastTimeResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                routeViewModel.lastTimes = response.toMutableList()
                binding.root.findNavController().navigate(R.id.action_routeFragment_to_routeDetailFragment)
            }
        }
    }

    private fun setStartAndDestinationText() {
        args.start?.let {
            routeViewModel.setOrigin(it)
        }
        args.end?.let {
            routeViewModel.setDestination(it)
        }
        routeViewModel.getRoute()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}