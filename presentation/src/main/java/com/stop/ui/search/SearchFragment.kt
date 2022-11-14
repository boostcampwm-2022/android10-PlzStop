package com.stop.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.stop.R

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_move_to_route).setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToRouteFragment(ORIGIN, DESTINATION)

            findNavController().navigate(action)
        }
    }

    companion object {
        private const val ORIGIN = "출발지"
        private const val DESTINATION = "도착지"
    }
}