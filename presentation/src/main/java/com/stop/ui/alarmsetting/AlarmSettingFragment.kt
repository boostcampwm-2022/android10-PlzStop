package com.stop.ui.alarmsetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.stop.AlarmFunctions
import com.stop.AlarmWorker
import com.stop.R
import com.stop.databinding.FragmentAlarmSettingBinding
import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.ui.route.RouteResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@AndroidEntryPoint
class AlarmSettingFragment : Fragment() {

    private var _binding: FragmentAlarmSettingBinding? = null
    private val binding get() = _binding!!

    private val alarmSettingViewModel by activityViewModels<AlarmSettingViewModel>()
    private val routeResultViewModel: RouteResultViewModel by navGraphViewModels(R.id.route_nav_graph)

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
        setToggleListener()

        // TODO 뷰모델 가져와서 경로 막차시간 등 연결 작업해야함
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
            startPosition = itinerary.routes.first().start.name
            endPosition = itinerary.routes.last().end.name
            lastTime = transportLastTime.timeToBoard
            walkTime = (itinerary.routes.first().sectionTime.div(60)).roundToInt()
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
        val itinerary = routeResultViewModel.itinerary.value ?: throw IllegalArgumentException()

        val transportLastTimes = routeResultViewModel.lastTimes.value
            ?: throw IllegalArgumentException()

        val transportLastTime = transportLastTimes.filterNotNull().sortedBy {
            it.timeToBoard
        }.first()

        val alarmUseCaseItem = AlarmUseCaseItem(
            startPosition = itinerary.routes.first().start.name,
            endPosition = itinerary.routes.last().end.name,
            routes = itinerary.routes,
            lastTime = transportLastTime.timeToBoard,
            walkTime = (itinerary.routes.first().sectionTime.div(60)).roundToInt(),
            0,
            ALARM_CODE,
            true
        )
        alarmSettingViewModel.saveAlarm(alarmUseCaseItem)
        makeAlarm()
        //makeAlarmWorker()
        val navController = findNavController()
        navController.getBackStackEntry(R.id.mapFragment).savedStateHandle.set(BACK_STACK_KEY, true)
        navController.setGraph(R.navigation.nav_graph)
        navController.popBackStack(R.id.action_global_mapFragment, false)
    }

    private fun makeAlarm() {
        //TODO 알람 바꿔야함
        alarmFunctions.callAlarm("00:00:05", 4, ALARM_CODE, "막차알림")
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

    companion object {
        const val ALARM_CODE = 123
        const val BACK_STACK_KEY = "isCreatedFromPopBackStack"
    }

}