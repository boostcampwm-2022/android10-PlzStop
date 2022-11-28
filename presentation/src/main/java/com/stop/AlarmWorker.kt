package com.stop

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.stop.domain.model.nearplace.Place
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

// 일단은 NearPlaceUseCase 이용하여서 데이터 가져오기 구현

@HiltWorker
class AlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getNearPlacesUseCase: GetNearPlacesUseCase
) : CoroutineWorker(context, workerParameters) {

    lateinit var resultList: List<Place>

    override suspend fun doWork(): Result {
        return try {
            callApi()
            val output: Data = workDataOf("WORK_RESULT" to resultList.toString())
            Result.success(output)
        } catch (e: Exception) {
            Log.e("ABC", e.toString())
            Result.failure()
        }
    }

    private suspend fun callApi() {
        withContext(Dispatchers.IO) {
            getNearPlacesUseCase.getNearPlaces(
                1,
                "아남타워",
                126.969652,
                37.553836,
                BuildConfig.TMAP_APP_KEY
            ).collectLatest {
                resultList = it
            }
        }

    }

}