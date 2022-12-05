package com.stop.ui.routedetail

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.R
import com.stop.databinding.FragmentRouteDetailBinding
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.TransportRoute
import com.stop.domain.model.route.tmap.custom.WalkRoute
import com.stop.ui.route.RouteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteDetailFragment : Fragment(), RouteDetailHandler {
    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!

    private val routeViewModel: RouteViewModel by activityViewModels()

    private lateinit var tMap: RouteDetailTMap
    private var minLatitude = 0.0
    private var maxLatitude = 0.0
    private var minLongitude = 0.0
    private var maxLongitude = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteDetailBinding.inflate(inflater, container, false)
        initBinding()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTMap()
        initView()
    }

    override fun alertTMapReady() {
        drawRoute()
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.routeViewModel = routeViewModel
    }

    private fun initTMap() {
        tMap = RouteDetailTMap(requireActivity(), this)
        tMap.init()

        binding.frameLayoutContainer.addView(tMap.tMapView)
    }

    private fun initView() {
        binding.imageViewClose.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_routeDetailFragment_to_mapFragment)
        }
    }

    private fun drawRoute() {
        val routes = routeViewModel.tempItinerary.routes
        val startPoint = convertToTMapPoint(routes.first().start.coordinate)

        tempSet(routes.first().start.coordinate)
        tMap.tMapView.setCenterPoint(startPoint.latitude, startPoint.longitude)
        routes.forEach { route ->
            when (route) {
                is WalkRoute -> drawWalkRoute(route)
                is TransportRoute -> drawTransportRoute(route)
            }
        }

        temp()
    }

    private fun drawWalkRoute(route: WalkRoute) {
        val tMapPoints = arrayListOf(convertToTMapPoint(route.start.coordinate))
        tempAdd(convertToTMapPoint(route.start.coordinate))

        route.steps.forEach { step ->
            step.lineString.split(" ").forEach { coordinate ->
                val points = coordinate.split(",")

                tMapPoints.add(TMapPoint(points.last().toDouble(), points.first().toDouble()))
                tempAdd(TMapPoint(points.last().toDouble(), points.first().toDouble()))
            }
        }
        tMapPoints.add(convertToTMapPoint(route.end.coordinate))
        tempAdd(convertToTMapPoint(route.end.coordinate))

        val polyLine = TMapPolyLine(route.start.name, tMapPoints).apply {
            lineColor = Color.YELLOW
            lineWidth = 7F
            outLineColor = Color.WHITE
        }

        tMap.tMapView.addTMapPolyLine(polyLine)
    }

    private fun drawTransportRoute(route: TransportRoute) {
        val tMapPoints = arrayListOf(convertToTMapPoint(route.start.coordinate))
        tempAdd(convertToTMapPoint(route.start.coordinate))

        route.lines.forEach { line ->
            tMapPoints.add(convertToTMapPoint(Coordinate(line.longitude, line.latitude)))
            tempAdd(convertToTMapPoint(Coordinate(line.longitude, line.latitude)))
        }
        tMapPoints.add(convertToTMapPoint(route.end.coordinate))
        tempAdd(convertToTMapPoint(route.end.coordinate))

        val polyLine = TMapPolyLine(route.start.name + route.end.name, tMapPoints).apply {
            lineColor = if (route.mode == MoveType.SUBWAY) {
                Color.GREEN
            } else {
                Color.BLUE
            }
            lineWidth = 7F
            outLineColor = Color.WHITE
        }

        tMap.tMapView.addTMapPolyLine(polyLine)
    }

    private fun tempSet(coordinate: Coordinate) {
        minLatitude = coordinate.latitude.toDouble()
        maxLatitude = coordinate.latitude.toDouble()
        minLongitude = coordinate.longitude.toDouble()
        maxLongitude = coordinate.longitude.toDouble()
    }

    private fun tempAdd(point: TMapPoint) {
        if (point.latitude < minLatitude) {
            minLatitude = point.latitude
        }

        if (point.latitude > maxLatitude) {
            maxLatitude = point.latitude
        }

        if (point.longitude < minLongitude) {
            minLongitude = point.longitude
        }

        if (point.longitude > maxLongitude) {
            maxLongitude = point.longitude
        }
    }

    fun temp() {
        val center = TMapPoint((minLatitude + maxLatitude) / 2, (minLongitude + maxLongitude) / 2)

        tMap.tMapView.setCenterPoint(center.latitude, center.longitude)

        Log.d("route", tMap.tMapView.leftTopPoint.latitude.toString())
        Log.d("route", tMap.tMapView.rightBottomPoint.toString())
        Log.d("route", minLatitude.toString())
        Log.d("route", maxLatitude.toString())

        tMap.tMapView.zoomToSpan(maxLatitude - minLatitude, maxLongitude - minLongitude)
        tMap.tMapView.zoomLevel -= 1
        /*
        while (true) {
            if (tMap.tMapView.leftTopPoint.latitude > maxLatitude &&
                tMap.tMapView.leftTopPoint.longitude < minLatitude &&
                    tMap.tMapView.rightBottomPoint.latitude < minLatitude &&
                    tMap.tMapView.rightBottomPoint.longitude > maxLatitude) {
                break
            }

            tMap.tMapView.zoomLevel += 1
        }

         */
    }

    private fun convertToTMapPoint(coordinate: Coordinate): TMapPoint {
        return TMapPoint(coordinate.latitude.toDouble(), coordinate.longitude.toDouble())
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}