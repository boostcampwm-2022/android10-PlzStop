package com.stop.ui.route

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stop.R
import com.stop.databinding.FragmentRouteBinding
import com.stop.domain.model.route.tmap.custom.Itinerary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    private val binding: FragmentRouteBinding
        get() = _binding!!

    private lateinit var adapter: RouteAdapter
    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var alertDialog: AlertDialog

    private var args: RouteFragmentArgs? = null

    private val routeViewModel: RouteViewModel by activityViewModels()
    private val routeResultViewModel: RouteResultViewModel by navGraphViewModels(R.id.route_nav_graph)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.mapFragment, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentArgs: RouteFragmentArgs by navArgs()
        args = currentArgs
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteBinding.inflate(layoutInflater)

        initBinding()

        return binding.root
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = routeViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
        setRecyclerView()
        setStartAndDestinationText()
        initDialog()
        setObserve()
    }

    private fun setListener() {
        binding.textViewOrigin.setOnClickListener {
            val navController = findNavController()
            navController.setGraph(R.navigation.nav_graph)
            navController.navigate(R.id.action_global_placeSearchFragment)
        }

        binding.textViewDestination.setOnClickListener {
            val navController = findNavController()
            navController.setGraph(R.navigation.nav_graph)
            navController.navigate(R.id.action_global_placeSearchFragment)
        }

        binding.imageViewSwapOriginWithDestination.setOnClickListener {
            routeViewModel.changeOriginAndDestination()
        }

        binding.imageViewExit.setOnClickListener {
            val navController = findNavController()
            navController.setGraph(R.navigation.nav_graph)
            navController.popBackStack(R.id.mapFragment, false)
        }
    }

    private fun setRecyclerView() {
        adapter = RouteAdapter(object : OnItineraryClickListener {
            override fun onItineraryClick(itinerary: Itinerary) {
                alertDialog.show()
                routeViewModel.calculateLastTransportTime(itinerary)
                routeResultViewModel.setItineraries(itinerary)
            }
        })
        binding.recyclerviewRoute.adapter = adapter
    }

    private fun setStartAndDestinationText() {
        args?.start?.let {
            routeViewModel.setOrigin(it)
        }
        args?.end?.let {
            routeViewModel.setDestination(it)
        }

        requireArguments().clear()

        if (args?.start != null || args?.end != null) {
            routeViewModel.patchRoute()
        }
    }

    private fun initDialog() {
        val viewModelDialog = routeViewModel.alertDialog
        if (viewModelDialog != null) {
            alertDialog = viewModelDialog
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_progress, null)
        alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        routeViewModel.alertDialog = alertDialog
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
                val message = getString(errorType.stringResourcesId)

                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        routeViewModel.lastTimeResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                routeResultViewModel.setLastTimes(response)
                routeResultViewModel.setOrigin(routeViewModel.origin.value)
                routeResultViewModel.setDestination(routeViewModel.destination.value)
                alertDialog.dismiss()

                binding.root.findNavController()
                    .navigate(R.id.action_routeFragment_to_routeDetailFragment)
            }
        }
        routeViewModel.isLoading.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { isLoading ->
                if (isLoading) {
                    alertDialog.show()
                    return@let
                }
                alertDialog.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        args = null

        super.onDestroyView()
    }

}