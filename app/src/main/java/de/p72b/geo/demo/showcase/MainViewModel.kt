package de.p72b.geo.demo.showcase

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.demo.usecase.DrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.WalkingDirectionsUseCase
import de.p72b.geo.demo.util.BaseViewModel
import de.p72b.geo.google.DirectionsRoute
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel(
    private val mainThread: Scheduler,
    private val networkThread: Scheduler,
    private val drivingDirectionsUseCase: DrivingDirectionsUseCase,
    private val walkingDirectionsUseCase: WalkingDirectionsUseCase
) : BaseViewModel(), LifecycleObserver {

    companion object {
        private val originDefault = LatLng(52.532583, 13.382362)
        private val destinationDefault = LatLng(52.507710, 13.364320)
    }

    val origin = MutableLiveData<String>()
    val destination = MutableLiveData<String>()

    init {
        origin.postValue("${originDefault.latitude},${originDefault.longitude}")
        destination.postValue("${destinationDefault.latitude},${destinationDefault.longitude}")
    }

    fun onWalkingRouteClicked() {
        if (!isInputValid()) return

        calculateWalkingRoute(parseLatLng(origin.value!!), parseLatLng(destination.value!!))
    }

    fun onDrivingRouteClicked() {
        if (!isInputValid()) return

        calculateDrivingRoute(parseLatLng(origin.value!!), parseLatLng(destination.value!!))
    }

    private fun parseLatLng(value: String): LatLng {
        val components = value.split(",")
        return LatLng(components[0].toDouble(), components[1].toDouble())
    }

    private fun isInputValid(): Boolean {
        if (origin.value.isNullOrEmpty()) return false
        if (destination.value.isNullOrEmpty()) return false
        return true
    }

    private fun calculateWalkingRoute(origin: LatLng, destination: LatLng) {
        walkingDirectionsUseCase.invoke(origin, destination)
            .subscribeOn(networkThread)
            .observeOn(mainThread)
            .doFinally {
                System.out.println("doFinally")
            }
            .subscribeBy(
                onError = {
                    System.out.println("ERROR $it")
                },
                onSuccess = {
                    handleDirectionsResult(it)
                }
            ).autoDispose()
    }

    private fun calculateDrivingRoute(origin: LatLng, destination: LatLng) {
        drivingDirectionsUseCase.invoke(origin, destination)
            .subscribeOn(networkThread)
            .observeOn(mainThread)
            .doFinally {
                System.out.println("doFinally")
            }
            .subscribeBy(
                onError = {
                    System.out.println("ERROR $it")
                },
                onSuccess = {
                    handleDirectionsResult(it)
                }
            ).autoDispose()
    }

    private fun handleDirectionsResult(directionsRoute: DirectionsRoute?) {
        System.out.println("handleDirectionsResult")
    }
}