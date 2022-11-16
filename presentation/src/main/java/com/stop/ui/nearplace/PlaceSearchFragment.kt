package com.stop.ui.nearplace

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.stop.R
import com.stop.databinding.FragmentPlaceSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceSearchFragment : Fragment() {

    private var _binding: FragmentPlaceSearchBinding? = null
    private val binding get() = _binding!!

    private val placeSearchViewModel: PlaceSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_search, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = placeSearchViewModel
        }

        listenEditTextChange()
    }

    private fun listenEditTextChange() {
        with(binding) {
            textInputEditTextPlaceSearch.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}