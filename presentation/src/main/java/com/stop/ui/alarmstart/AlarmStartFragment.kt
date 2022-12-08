package com.stop.ui.alarmstart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.stop.R
import com.stop.SoundService
import com.stop.databinding.FragmentAlarmStartBinding
import com.stop.ui.alarmsetting.AlarmSettingViewModel

class AlarmStartFragment : Fragment() {

    private var _binding: FragmentAlarmStartBinding? = null
    private val binding get() = _binding!!

    private val alarmSettingViewModel by activityViewModels<AlarmSettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmStartBinding.inflate(
            inflater,
            container,
            false
        )

        initBinding()

        return binding.root
    }

    private fun initBinding(){
        binding.apply {
            alarmSettingViewModel.getAlarm()
            lifecycleOwner = viewLifecycleOwner
            viewModel = alarmSettingViewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenButton()
    }

    private fun listenButton() {
        with(binding) {
            buttonAlarmTurnOff.setOnClickListener {
                turnOffSoundService()
                alarmSettingViewModel.deleteAlarm()
                requireActivity().finish()
            }

            textViewMissionStart.setOnClickListener {
                Log.e("ABC","ABC")
                turnOffSoundService()
                root.findNavController().navigate(R.id.action_alarmStartFragment_to_missionFragment)
            }
        }
    }

    private fun turnOffSoundService() {
        val intent = Intent(context, SoundService::class.java)
        requireContext().stopService(intent)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

}