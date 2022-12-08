package com.stop.ui.mission

import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.custom.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val workManager: WorkManager,
    missionManager: MissionManager
) : ViewModel() {

    val destination = MutableStateFlow(Place("null",Coordinate("37.553836", "126.969652")))

    val userLocation = missionManager.userLocation
    val isMissionOver = missionManager.isMissionOver

    lateinit var requestId: UUID

    init {
        makeMissionWorker()
    }

    private fun makeMissionWorker() {
        val workRequest = OneTimeWorkRequestBuilder<MissionWorker>()
            .build()

        requestId = workRequest.id

        workManager.enqueue(workRequest)
    }

    fun cancelMission() {
        workManager.cancelWorkById(requestId)
    }

}
