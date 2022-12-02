package com.stop.ui.routedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.stop.R
import com.stop.databinding.FragmentRouteDetailBinding
import com.stop.ui.route.RouteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteDetailFragment : Fragment() {

    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!

    private val parentViewModel: RouteViewModel by activityViewModels()

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

        initView()
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.parentViewModel = parentViewModel
    }

    private fun initView() {
        //TODO 임시 화면이동 로직
        binding.root.setOnClickListener {
            binding.root.findNavController().navigate(R.id.action_routeDetailFragment_to_alarmSetting)
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}