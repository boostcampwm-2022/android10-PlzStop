package com.stop.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.stop.R
import com.stop.model.route.RouteRequest

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
            val routeRequest = RouteRequest(
                originName = ORIGIN_NAME,
                originX = ORIGIN_X,
                originY = ORIGIN_Y,
                destinationName = DESTINATION_NAME,
                destinationX = DESTINATION_X,
                destinationY = DESTINATION_Y,
            )
            val action = SearchFragmentDirections.actionSearchFragmentToRouteFragment(routeRequest)

            findNavController().navigate(action)
        }
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