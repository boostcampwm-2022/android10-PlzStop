package com.stop.ui.alarmstart

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.stop.R
import com.stop.alarm.SoundService
import com.stop.databinding.FragmentAlarmStartBinding
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_HIGH_ID
import com.stop.ui.alarmsetting.AlarmSettingViewModel
import kotlinx.coroutines.launch

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

    private fun initBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = alarmSettingViewModel
            fragment = this@AlarmStartFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        alarmSettingViewModel.getAlarm()

        lifecycleScope.launch {
            alarmSettingViewModel.alarmItem
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    it?.let {
                        alarmSettingViewModel.startCountDownTimer(it.lastTime)
                    }
                }
        }

        binding.executePendingBindings()
    }

    fun clickAlarmTurnOff() {
        turnOffSoundService()
        alarmSettingViewModel.deleteAlarm()
        cancelNotification()
        requireActivity().finish()
    }

    fun clickMissionStart() {
        turnOffSoundService()
        cancelNotification()
        binding.root.findNavController().navigate(R.id.action_alarmStartFragment_to_missionFragment)
    }

    private fun turnOffSoundService() {
        val intent = Intent(requireContext(), SoundService::class.java)
        requireContext().stopService(intent)
        SoundService.normalExit = true
    }

    private fun cancelNotification() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_HIGH_ID)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

}