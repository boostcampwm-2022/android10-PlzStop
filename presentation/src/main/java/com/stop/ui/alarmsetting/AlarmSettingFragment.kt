package com.stop.ui.alarmsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.stop.R
import com.stop.databinding.FragmentAlarmSettingBinding
import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.ui.route.ClickRouteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class AlarmSettingFragment : Fragment() {

    private var _binding: FragmentAlarmSettingBinding? = null
    private val binding get() = _binding!!

    private val alarmSettingViewModel by activityViewModels<AlarmSettingViewModel>()
    private val clickRouteViewModel by activityViewModels<ClickRouteViewModel>()

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

        initView()
        setToggleListener()

        // TODO 뷰모델 가져와서 경로 막차시간 등 연결 작업해야함
    }

    private fun initBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            alarmViewModel = alarmSettingViewModel
            startPosition = clickRouteViewModel.clickRoute?.routes?.first()?.start?.name ?: "출발지 없음"
            endPosition = clickRouteViewModel.clickRoute?.routes?.last()?.end?.name ?: "도착지 없음"
            lastTime = clickRouteViewModel.lastTime
            walkTime = (clickRouteViewModel.clickRoute?.routes?.first()?.sectionTime?.div(60))?.roundToInt() ?: 0
            fragment = this@AlarmSettingFragment
        }
    }

    private fun initView() {
        with(binding) {
            numberPickerAlarmTime.minValue = 0
            numberPickerAlarmTime.maxValue = 60

            buttonSound.isCheckable = true
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
        }
    }

    fun setAlarmRegisterListener() {
        val alarmUseCaseItem = AlarmUseCaseItem(
            startPosition = clickRouteViewModel.clickRoute?.routes?.first()?.start?.name ?: "출발지 없음",
            endPosition = clickRouteViewModel.clickRoute?.routes?.last()?.end?.name ?: "도착지 없음",
            routes = clickRouteViewModel.clickRoute?.routes ?: emptyList(),
            lastTime = clickRouteViewModel.lastTime,
            walkTime = (clickRouteViewModel.clickRoute?.routes?.first()?.sectionTime?.div(60))?.roundToInt() ?: 0,
            0,
            ALARM_CODE,
            true
        )

        alarmSettingViewModel.saveAlarm(alarmUseCaseItem)
        alarmSettingViewModel.callAlarm(clickRouteViewModel.lastTime)
        alarmSettingViewModel.makeAlarmWorker(clickRouteViewModel.lastTime)

        binding.root.findNavController().navigate(R.id.action_alarmSetting_to_mapFragment)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ALARM_CODE = 123
        const val LAST_TIME = "lastTime"
        const val ALARM_TIME = "alarmTime"
    }

}