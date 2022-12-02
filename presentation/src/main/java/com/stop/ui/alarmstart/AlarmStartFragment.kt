package com.stop.ui.alarmstart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.stop.R
import com.stop.SoundService
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenButton()
    }

    private fun listenButton() {
        with(binding) {
            textViewAlarmQuit.setOnClickListener {
                val intent = Intent(context, SoundService::class.java)
                context?.stopService(intent)
            }

            textViewMissionStart.setOnClickListener {
                root.findNavController().navigate(R.id.action_alarmStartFragment_to_missionFragment)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

}