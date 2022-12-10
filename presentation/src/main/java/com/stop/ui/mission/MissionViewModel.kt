package com.stop.ui.mission

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.custom.Place
import com.stop.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val workManager: WorkManager,
    //missionManager: MissionManager
) : ViewModel() {

    val destination = MutableStateFlow(Place("null",Coordinate("37.553836", "126.969652")))

//    val userLocation = missionManager.userLocation
    val isMissionOver = MutableStateFlow<Boolean>(false)

    lateinit var requestId: UUID

    val lastTime = MutableLiveData<String>()
    var userLocations = MutableStateFlow<ArrayList<Location>>(arrayListOf(Location(37.553836, 126.969652)))

    init {

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
