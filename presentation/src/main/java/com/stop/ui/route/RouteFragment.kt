package com.stop.ui.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.stop.R
import com.stop.databinding.FragmentRouteBinding
import com.stop.model.route.OrderType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    private val binding: FragmentRouteBinding
        get() = _binding!!

    private val args: RouteFragmentArgs by navArgs()
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

        setStartAndDestinationText()
        setDropDownMenu()
        setRecyclerView()
        setObserve()
    }

    private fun setRecyclerView() {
        binding.recyclerviewRoute.adapter = adapter
    }

    private fun setObserve() {
        viewModel.routeResponse.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            adapter.submitList(it)
        }
    }

    private fun setDropDownMenu() {
        val options = OrderType.values().map { it.typeName }
        val adapter = ArrayAdapter(requireContext(), R.layout.order_list_item, options)
        binding.autoCompleteTextViewOrderType.setText(options.first())
        binding.autoCompleteTextViewOrderType.setAdapter(adapter)
    }

    private fun setStartAndDestinationText() {
        binding.textInputEditTextOrigin.setText(args.routeRequest.originName)
        binding.textInputEditTextDestination.setText(args.routeRequest.destinationName)

        viewModel.setOrigin(args.routeRequest.getOrigin())
        viewModel.setDestination(args.routeRequest.getDestination())
        viewModel.getRoute()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}