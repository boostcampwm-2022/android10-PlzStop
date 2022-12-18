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
import androidx.navigation.fragment.findNavController
import com.stop.R
import com.stop.bindingadapter.textChangesToFlow
import com.stop.databinding.FragmentPlaceSearchBinding
import com.stop.domain.model.nearplace.PlaceUseCaseItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaceSearchFragment : Fragment() {

    private var _binding: FragmentPlaceSearchBinding? = null
    private val binding: FragmentPlaceSearchBinding
        get() = _binding!!

    private val placeSearchViewModel: PlaceSearchViewModel by activityViewModels()

    private lateinit var placeSearchAdapter: PlaceSearchAdapter
    private lateinit var recentPlaceSearchAdapter: RecentPlaceSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_search, container, false)

        initBinding()

        return binding.root
    }

    private fun initBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = placeSearchViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextEditText()
        initAdapter()
        buttonClick()
        listenEditTextChange()
        logErrorMessage()
        listenSearchEditText()
    }

    private fun initTextEditText() {
        binding.textInputEditTextPlaceSearch.requestFocus()
        showKeyBoard()
    }

    private fun initAdapter() {
        placeSearchAdapter = PlaceSearchAdapter {
            clickPlace(it)
        }
        recentPlaceSearchAdapter = RecentPlaceSearchAdapter {
            clickPlace(it)
        }

        binding.recyclerViewPlace.adapter = placeSearchAdapter
        binding.layoutRecentSearch.recyclerViewRecentSearch.adapter = recentPlaceSearchAdapter
    }

    private fun clickPlace(placeUseCaseItem: PlaceUseCaseItem) {
        placeSearchViewModel.setClickPlace(placeUseCaseItem)
        placeSearchViewModel.setNearPlacesEmpty()
        placeSearchViewModel.insertRecentSearchPlace(placeUseCaseItem)

        findNavController().popBackStack(R.id.mapFragment, false)
    }

    private fun buttonClick() {
        with(binding) {
            layoutRecentSearch.textViewCurrentLocation.setOnClickListener {
                placeSearchViewModel.setClickCurrentLocation()

                findNavController().popBackStack(R.id.mapFragment, false)
            }

            layoutRecentSearch.textViewSelectMap.setOnClickListener {
                findNavController().popBackStack(R.id.mapFragment, false)
            }
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

    private fun showKeyBoard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.textInputEditTextPlaceSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyBoard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    private fun logErrorMessage() {
        lifecycleScope.launch {

            placeSearchViewModel.errorMessage
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    if (it.isNotBlank()) {
                        Log.e(PLACE_SEARCH_FRAGMENT, it)
                    }
                }
        }
    }

    private fun listenSearchEditText() {
        lifecycleScope.launch(Dispatchers.IO) {
            val editTextFlow = binding.textInputEditTextPlaceSearch.textChangesToFlow()

            editTextFlow
                .debounce(500)
                .onEach {
                    if (it.toString().isBlank()) {
                        placeSearchViewModel.setNearPlacesEmpty()
                    } else {
                        placeSearchViewModel.getNearPlaces(it.toString())
                    }
                }
                .launchIn(this)
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