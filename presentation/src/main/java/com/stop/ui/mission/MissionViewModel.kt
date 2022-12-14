package com.stop.ui.mission

import android.util.Log
import androidx.lifecycle.*
import com.stop.domain.model.ApiChangedException
import com.stop.domain.model.AvailableTrainNoExistException
import com.stop.domain.model.nowlocation.BusCurrentInformationUseCaseItem
import com.stop.domain.model.nowlocation.SubwayRouteLocationUseCaseItem
import com.stop.domain.model.nowlocation.TrainLocationInfoDomain
import com.stop.domain.model.nowlocation.TransportState
import com.stop.domain.model.route.Area
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.TransportMoveType
import com.stop.domain.model.route.TransportStation
import com.stop.domain.model.route.seoul.subway.TransportDirectionType
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.usecase.nowlocation.*
import com.stop.model.ErrorType
import com.stop.model.Event
import com.stop.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val getBusNowLocationUseCase: GetBusNowLocationUseCase,
    private val getSubwayTrainNowStationUseCase: GetSubwayTrainNowStationUseCase,
    private val getNowStationLocationUseCase: GetNowStationLocationUseCase,
    private val getSubwayRouteUseCase: GetSubwayRouteUseCase,
    private val getBusesOnRouteUseCase: GetBusesOnRouteUseCase,
) : ViewModel() {

    class AlreadyHandledException : Exception()

    private val random = Random(System.currentTimeMillis())

    // TODO: TransportLastTime은 RouteViewModel에서 전달해주는 데이터를 사용함
    private val _transportLastTime = MutableLiveData<TransportLastTime>()
    val transportLastTime: LiveData<TransportLastTime>
        get() = _transportLastTime

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

    private val _busNowLocationInfo = MutableLiveData<List<BusCurrentInformationUseCaseItem>>()
    val busNowLocationInfo: LiveData<List<BusCurrentInformationUseCaseItem>>
        get() = _busNowLocationInfo

    private val _subwayRoute = MutableLiveData<SubwayRouteLocationUseCaseItem>()
    val subwayRoute: LiveData<SubwayRouteLocationUseCaseItem> = _subwayRoute

    var personCurrentLocation = Location(37.553836, 126.969652)
    var busCurrentLocation = Location(37.553836, 126.969652)

    lateinit var startSubwayStation: String

    init {
        // 구로09 버스
        _transportLastTime.value = TransportLastTime(
            transportMoveType = TransportMoveType.BUS,
            area = Area.SEOUL,
            lastTime = "23:50:50",
            destinationStationName = "에이스테크노타워",
            stationsUntilStart = listOf(
                TransportStation(stationName = "동아1차APT105동", stationId = "116900095"),
                TransportStation(stationName = "신도림중학교", stationId = "116900091"),
                TransportStation(stationName = "우성아파트", stationId = "116000358"),
                TransportStation(stationName = "신도림역.아이파크아파트", stationId = "116900285"),
                TransportStation(stationName = "동아2차아파트상가", stationId = "116900286"),
                TransportStation(stationName = "대림6차태영프라자", stationId = "116900088"),
                TransportStation(stationName = "대림5차아파트.신도림주민센터", stationId = "116900215"),
                TransportStation(stationName = "대림5차아파트702동", stationId = "116900213"),
                TransportStation(stationName = "대림3차아파트", stationId = "116900067"),
                TransportStation(stationName = "신도림미성아파트", stationId = "116900216"),
                TransportStation(stationName = "월드아파트", stationId = "116900052"),
                TransportStation(stationName = "구로역.구로기계공구상가", stationId = "116000061"),
                TransportStation(stationName = "구로역·NC신구로점", stationId = "116000058"),
                TransportStation(stationName = "항아리", stationId = "116900220"),
                TransportStation(stationName = "구로보건소", stationId = "116000149"),
                TransportStation(stationName = "구로고.구로도서관", stationId = "116900012"),
                TransportStation(stationName = "영림중학교", stationId = "116900007"),
                TransportStation(stationName = "구로구민회관", stationId = "116900001"),
                TransportStation(stationName = "구로구청", stationId = "116900210"),
                TransportStation(stationName = "구로중학교", stationId = "116900222"),
                TransportStation(stationName = "동구로새마을금고", stationId = "116900223"),
                TransportStation(stationName = "구로시장.남구로시장", stationId = "116900191"),
                TransportStation(stationName = "구로3동주민센터.삼성래미안아파트", stationId = "116900182"),
                TransportStation(stationName = "구로3파출소", stationId = "116900175"),
                TransportStation(stationName = "에이스테크노타워", stationId = "116900169"),
                TransportStation(stationName = "KEB하나은행", stationId = "116900116"),
                TransportStation(stationName = "구로3동현대아파트", stationId = "116900159"),
                TransportStation(stationName = "구로디지털단지역", stationId = "116900093"),
                TransportStation(stationName = "구.사조참치", stationId = "116900098"),
                TransportStation(stationName = "한국산업단지공단.이마트구로점", stationId = "116000036"),
                TransportStation(stationName = "에이스테크노타워", stationId = "116900147"),
            ),
            enableDestinationStation = listOf(),
            transportDirectionType = TransportDirectionType.TO_END,
            routeId = "116900007",
        )
        startMission()
    }

    fun startMission() {
        val transportLastTime = _transportLastTime.value ?: return
        when (transportLastTime.transportMoveType) {
            TransportMoveType.BUS -> getBusNowLocation(transportLastTime)
            TransportMoveType.SUBWAY -> getSubwayRoute()
        }
    }

    fun setDestination(inputDestination: String) {
        _destination.value = inputDestination
    }

    private fun getBusNowLocation(transportLastTime: TransportLastTime) {
        viewModelScope.launch {
            /**
             * 이 작업은 알람 화면에서 진행되고,
             * 탑승해야 하는 버스 아이디 1개만 전해줍니다.
             * 여기서는 임의로 중간에 있는 버스를 선택했습니다.
             */
            var busVehicleIds = getBusesOnRouteUseCase(transportLastTime)
            if (busVehicleIds.isEmpty()) {
                _errorMessage.value = Event(ErrorType.AVAILABLE_BUS_NO_EXIST_YET)
                throw AlreadyHandledException()
            }

            val temporalIndex = busVehicleIds.size / 2
            busVehicleIds = listOf(busVehicleIds[temporalIndex])


            while (busVehicleIds.isNotEmpty()) {
                val busCurrentInformation = getBusNowLocationUseCase(transportLastTime, busVehicleIds)
                this@MissionViewModel._busNowLocationInfo.value = busCurrentInformation
                Log.d("CHECK", busCurrentInformation.first().licensePlateNumber)

                busVehicleIds = busVehicleIds.foldIndexed(listOf()) { index, ids, id ->
                    when(busCurrentInformation[index].transportState) {
                        TransportState.ARRIVE -> {
                            busArrivedAtDestination()
                            return@launch
                        }
                        TransportState.DISAPPEAR -> {
                            _errorMessage.value = Event(ErrorType.BUS_DISAPPEAR_SUDDENLY)
                            ids
                        }
                        TransportState.RUN -> ids + id
                    }
                }
                delay(5000)
            }
            val transportIsArrived = _transportIsArrived.value?.peekContent()
            if (transportIsArrived != true) {
                _errorMessage.value = Event(ErrorType.MISSION_SOMETHING_WRONG)
                return@launch
            }
        }
    }

    // TODO: 버스가 도착했을 때 처리하기
    private fun busArrivedAtDestination() {
        _transportIsArrived.value = Event(true)
    }

    private suspend fun getSubwayTrainNowLocation(): TrainLocationInfoDomain {
        val lastTimeValue = transportLastTime.value

        if (lastTimeValue == null) {
            _errorMessage.value = Event(ErrorType.TRANSPORT_LAST_TIME_IS_NOT_RECEIVED_YET)
            throw AlreadyHandledException()
        }

        return getSubwayTrainNowStationUseCase(lastTimeValue, TEST_SUBWAY_LINE_NUMBER)
    }

    private suspend fun getNowStationLocation() = withContext(Dispatchers.Main) {
        val trainLocationInfo = getSubwayTrainNowLocation()

        getNowStationLocationUseCase(
            trainLocationInfo.currentStationName,
            personCurrentLocation.longitude,
            personCurrentLocation.latitude
        )
    }

    private fun getSubwayRoute() {
        viewModelScope.launch {
            val startLocation = getNowStationLocation()
            try {
                this@MissionViewModel._subwayRoute.value = getSubwayRouteUseCase(
                    RouteRequest(
                        startLocation.longitude,
                        startLocation.latitude,
                        TEST_SUBWAY_LONG,
                        TEST_SUBWAY_LAT
                    ),
                    TEST_SUBWAY_LINE_NUMBER.toString() + LINE,
                    startSubwayStation.dropLast(1), //"역" 버리기
                    TEST_END_SUBWAY_STATION
                )
            } catch (exception: IllegalArgumentException) {
                _errorMessage.value = Event(ErrorType.NO_ROUTE_RESULT)
            } catch (exception: ApiChangedException) {
                _errorMessage.value = Event(ErrorType.API_CHANGED)
            } catch (exception: AvailableTrainNoExistException) {
                _errorMessage.value = Event(ErrorType.AVAILABLE_TRAIN_NO_EXIST_YET)
            } catch (_: AlreadyHandledException) {
            }
        }
    }

    companion object {
        private const val DELAY_TIME = 1000L
        private const val TIME_ZERO = 0
        private const val TIME_UNIT = 60
        private const val TIME_DIGIT = 2
        private const val ONE_SECOND = 1
        private const val RANDOM_LIMIT = 5
        private const val ZERO = 0

        private const val TEST_SUBWAY_LINE_NUMBER = 4
        private const val LINE = "호선" //임시로.. 종성님이 어떻게 넘겨주시느냐에 따라 달림

        private const val TEST_SUBWAY_LAT = "37.30973177"
        private const val TEST_SUBWAY_LONG = "126.85359515"
        private const val TEST_END_SUBWAY_STATION = "한대앞"

    }

}