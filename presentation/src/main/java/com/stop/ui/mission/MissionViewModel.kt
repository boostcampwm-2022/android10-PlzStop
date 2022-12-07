package com.stop.ui.mission

import android.util.Log
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.stop.model.ErrorType
import com.stop.model.Event
import com.stop.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val workManager: WorkManager,
    missionManager: MissionManager
) : ViewModel() {

    private val random = Random(System.currentTimeMillis())

    private val _destination = MutableLiveData<String>()
    val destination: LiveData<String>
        get() = _destination

    private val _timeIncreased = MutableLiveData<Int>()
    val timeIncreased: LiveData<Int>
        get() = _timeIncreased

    private val _estimatedTimeRemaining = MutableLiveData<Int>()
    val estimatedTimeRemaining: LiveData<Int>
        get() = _estimatedTimeRemaining

    private val _errorMessage = MutableLiveData<Event<ErrorType>>()
    val errorMessage: LiveData<Event<ErrorType>>
        get() = _errorMessage

    private val _transportIsArrived = MutableLiveData<Event<Boolean>>()
    val transportIsArrived: LiveData<Event<Boolean>>
        get() = _transportIsArrived

    val leftMinute: LiveData<String> = Transformations.switchMap(estimatedTimeRemaining) {
        MutableLiveData<String>().apply {
            value = (it / TIME_UNIT).toString().padStart(TIME_DIGIT, '0')
        }
    }

    val leftSecond: LiveData<String> = Transformations.switchMap(estimatedTimeRemaining) {
        MutableLiveData<String>().apply {
            value = (it % TIME_UNIT).toString().padStart(TIME_DIGIT, '0')
        }
    }

    var personCurrentLocation = Location(37.553836, 126.969652)

    val userLocation = missionManager.userLocation

    lateinit var requestId: UUID

    init {
        makeMissionWorker()
    }

    fun setDestination(inputDestination: String) {
        _destination.value = inputDestination
    }

    fun countDownWith(startTime: Int) {
        _estimatedTimeRemaining.value = startTime
        var leftTime = startTime
        viewModelScope.launch {
            while (leftTime > TIME_ZERO) {
                delay(DELAY_TIME)
                leftTime -= ONE_SECOND
                if (leftTime <= TIME_ZERO) {
                    break
                }
                _estimatedTimeRemaining.value = leftTime
            }
        }

        viewModelScope.launch {
            while (leftTime > TIME_ZERO) {
                delay(DELAY_TIME)
                if (random.nextInt(ZERO, RANDOM_LIMIT) == ZERO) {
                    val increasedTime = random.nextInt(-RANDOM_LIMIT, RANDOM_LIMIT)
                    if (increasedTime == ZERO) {
                        continue
                    }
                    leftTime += increasedTime
                    _timeIncreased.value = increasedTime
                }
                if (leftTime < TIME_ZERO) {
                    leftTime = 0
                }
                _estimatedTimeRemaining.value = leftTime
            }
        }
    }

    private fun makeMissionWorker() {
        val workRequest = OneTimeWorkRequestBuilder<MissionWorker>()
            .build()

        requestId = workRequest.id

        workManager.enqueue(workRequest)
    }

    fun cancelMission() {
        Log.d("MissionWorker","취소 되나연")
        workManager.cancelWorkById(requestId)
    }

    companion object {
        private const val DELAY_TIME = 1000L
        private const val TIME_ZERO = 0
        private const val TIME_UNIT = 60
        private const val TIME_DIGIT = 2
        private const val ONE_SECOND = 1
        private const val RANDOM_LIMIT = 5
        private const val ZERO = 0

    }
}
