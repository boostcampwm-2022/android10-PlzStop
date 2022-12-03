package com.stop.ui.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.stop.R
import com.stop.databinding.FragmentRouteBinding
import com.stop.domain.model.route.tmap.custom.Itinerary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    private val binding: FragmentRouteBinding
        get() = _binding!!

    private val viewModel: RouteViewModel by activityViewModels()

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
        binding.viewModel = viewModel
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
                viewModel.calculateLastTransportTime(itinerary)
            }
        })
        binding.recyclerviewRoute.adapter = adapter
    }

    private fun setObserve() {
        viewModel.routeResponse.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            adapter.submitList(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { errorType ->
                val message = getString(errorType.stringResourcesId)

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.lastTimeResponse.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                binding.root.findNavController().navigate(R.id.action_routeFragment_to_routeDetailFragment)
            }
        }
    }

    private fun setStartAndDestinationText() {
        args.start?.let {
            viewModel.setOrigin(it)
        }
        args.end?.let {
            viewModel.setDestination(it)
        }
        viewModel.getRoute()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}