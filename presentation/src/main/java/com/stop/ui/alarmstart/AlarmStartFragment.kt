package com.stop.ui.alarmstart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stop.databinding.FragmentAlarmStartBinding

class AlarmStartFragment : Fragment() {

    private var _binding: FragmentAlarmStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmStartBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

}