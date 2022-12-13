package com.stop.ui.alarmsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.stop.R
import com.stop.databinding.FragmentAlarmSettingBinding
import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.ui.route.RouteResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class AlarmSettingFragment : Fragment() {

    private var _binding: FragmentAlarmSettingBinding? = null
    private val binding get() = _binding!!

    private val alarmSettingViewModel by activityViewModels<AlarmSettingViewModel>()
    private val routeResultViewModel: RouteResultViewModel by navGraphViewModels(R.id.route_nav_graph)

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
    }

    private fun initBinding() {
        val itinerary = routeResultViewModel.itinerary.value ?: throw IllegalArgumentException()

        val transportLastTimes = routeResultViewModel.lastTimes.value
            ?: throw IllegalArgumentException()

        val transportLastTime = transportLastTimes.filterNotNull().sortedBy {
            it.timeToBoard
        }.first()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            alarmViewModel = alarmSettingViewModel
            startPosition = routeResultViewModel.origin.value?.name
            endPosition = routeResultViewModel.destination.value?.name
            lastTime = transportLastTime.timeToBoard
            walkTime = (itinerary.routes.first().sectionTime.div(60)).roundToInt()
            fragment = this@AlarmSettingFragment
        }
    }

    private fun initView() {
        with (binding) {
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
        val itinerary = routeResultViewModel.itinerary.value ?: throw IllegalArgumentException()

        val transportLastTimes = routeResultViewModel.lastTimes.value
            ?: throw IllegalArgumentException()

        val transportLastTime = transportLastTimes.filterNotNull().sortedBy {
            it.timeToBoard
        }.first()

        val alarmUseCaseItem = AlarmUseCaseItem(
            startPosition = routeResultViewModel.origin.value?.name ?: "",
            endPosition = routeResultViewModel.destination.value?.name ?: "",
            routes = itinerary.routes.first(),
            lastTime = transportLastTime.timeToBoard,
            walkTime = (itinerary.routes.first().sectionTime.div(60)).roundToInt(),
            0,
            ALARM_CODE,
            true
        )

        alarmSettingViewModel.saveAlarm(alarmUseCaseItem)
        alarmSettingViewModel.callAlarm(transportLastTime.timeToBoard)
        //alarmSettingViewModel.makeAlarmWorker(transportLastTime.timeToBoard)

        val navController = findNavController()
        navController.popBackStack(R.id.mapFragment, false)
        requireActivity().viewModelStore.clear()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ALARM_CODE = 123
        const val ALARM_MAP_CODE = 122
        const val ALARM_NOTIFICATION_ID = 121
        const val ALARM_NOTIFICATION_HIGH_ID = 120
        const val LAST_TIME = "lastTime"
        const val ALARM_TIME = "alarmTime"
    }

}