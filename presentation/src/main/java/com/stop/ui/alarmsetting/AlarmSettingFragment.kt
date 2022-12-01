package com.stop.ui.alarmsetting

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.stop.AlarmFunctions
import com.stop.AlarmWorker
import com.stop.R
import com.stop.databinding.FragmentAlarmSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AlarmSettingFragment : Fragment() {

    private var _binding: FragmentAlarmSettingBinding? = null
    private val binding get() = _binding!!

    private val alarmSettingViewModel by viewModels<AlarmSettingViewModel>()

    private lateinit var alarmFunctions: AlarmFunctions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmSettingBinding.inflate(inflater, container, false)

        initBinding()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmFunctions = AlarmFunctions(requireActivity())
        initView()
        setButtonListener()
        setToggleListener()
    }

    private fun initBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = alarmSettingViewModel
            fragment = this@AlarmSettingFragment
        }
    }

    private fun initView() {
        with(binding) {
            textViewLastTime.text = getString(R.string.last_transport_arrival_time, 23, 30)
            textViewWalk.text = getString(R.string.last_transport_walking_time, 10)

            numberPickerAlarmTime.minValue = 0
            numberPickerAlarmTime.maxValue = 60

            buttonSound.isCheckable = true
            buttonMissionOn.isCheckable = true
        }
    }

    private fun setButtonListener() {
        with(binding) {
            textViewRouteContent.setOnClickListener {
                if (textViewTransportContent.visibility == View.VISIBLE) {
                    setTransportViewGone()
                } else {
                    setTransportViewVisible()
                }
            }
        }
    }

    private fun setTransportViewGone() {
        with(binding) {
            TransitionManager.beginDelayedTransition(cardViewRoute, AutoTransition())
            textViewTransportContent.visibility = View.GONE
            textViewRouteContent.setCompoundDrawables(null, null, null, null)
            textViewRouteContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_down_24, 0)
        }
    }

    private fun setTransportViewVisible() {
        with(binding) {
            TransitionManager.beginDelayedTransition(cardViewRoute, AutoTransition())
            textViewTransportContent.visibility = View.VISIBLE
            textViewRouteContent.setCompoundDrawables(null, null, null, null)
            textViewRouteContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_keyboard_arrow_up_24, 0)
        }
    }

    private fun setToggleListener() {
        with(binding) {
            toggleGroupAlarm.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.button_sound -> alarmSettingViewModel.alarmMethod = true
                        else -> alarmSettingViewModel.alarmMethod = false
                    }
                }
            }

            toggleGroupMission.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.button_mission_on -> alarmSettingViewModel.isMission = true
                        else -> alarmSettingViewModel.isMission = false
                    }
                }
            }
        }
    }

    fun setAlarmRegisterListener() {
        alarmSettingViewModel.saveAlarm()
        makeAlarm()
        //makeAlarmWorker()
        binding.root.findNavController().navigate(R.id.action_alarmSetting_to_mapFragment)
    }

    private fun makeAlarm() {
        //TODO 알람 바꿔야함
        alarmFunctions.callAlarm("00:00:05", 4, 123, "막차알림")
    }

    private fun makeAlarmWorker() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<AlarmWorker>(15, TimeUnit.MINUTES)
            .build()
        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(periodicWorkRequest)
        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
            .observe(viewLifecycleOwner) { info ->
                val outPutData = info.outputData.getString("WORK_RESULT")
                Log.e("ABC", outPutData.toString())
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}