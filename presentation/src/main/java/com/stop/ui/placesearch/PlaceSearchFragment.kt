package com.stop.ui.placesearch

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.stop.R
import com.stop.databinding.FragmentPlaceSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaceSearchFragment : Fragment() {

    private var _binding: FragmentPlaceSearchBinding? = null
    private val binding get() = _binding!!

    private val placeSearchViewModel: PlaceSearchViewModel by activityViewModels()

    private lateinit var nearPlaceAdapter: NearPlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_search, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        buttonClick()
        initBinding()
        listenEditTextChange()
        logErrorMessage()
    }

    private fun initAdapter() {
        nearPlaceAdapter = NearPlaceAdapter()
        binding.recyclerViewPlace.adapter = nearPlaceAdapter

        nearPlaceAdapter.onItemClick = {
            placeSearchViewModel.setClickPlace(it)
            placeSearchViewModel.setNearPlaceListEmpty()

            binding.root.findNavController().navigate(R.id.action_placeSearchFragment_to_mapFragment)
        }
    }

    private fun buttonClick() {
        with(binding) {
            textViewCurrentLocation.setOnClickListener {
                placeSearchViewModel.setClickCurrentLocation()
                binding.root.findNavController().navigate(R.id.action_placeSearchFragment_to_mapFragment)
            }

            textViewSelectMap.setOnClickListener {
                binding.root.findNavController().navigate(R.id.action_placeSearchFragment_to_mapFragment)
            }
        }
    }

    private fun initBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = placeSearchViewModel
        }
    }

    private fun listenEditTextChange() {
        with(binding) {
            textInputEditTextPlaceSearch.setOnFocusChangeListener { _, hasFocus ->

                if (hasFocus.not()) {
                    hideKeyBoard()
                }
            }

            textInputEditTextPlaceSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyBoard()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun hideKeyBoard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    private fun logErrorMessage() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                placeSearchViewModel.errorMessage
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect {
                        if (it.isNotBlank()) {
                            Log.e(PLACE_SEARCH_FRAGMENT, it)
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

    companion object {
        private const val PLACE_SEARCH_FRAGMENT = "PLACE_SEARCH_FRAGMENT"
    }

}