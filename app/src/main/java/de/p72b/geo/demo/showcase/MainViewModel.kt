package de.p72b.geo.demo.showcase

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.demo.usecase.DrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.WalkingDirectionsUseCase
import de.p72b.geo.demo.util.BaseViewModel
import de.p72b.geo.demo.util.ConverterHelper
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
        private const val CACHE_BOX_HIT_SIZE_DEFAULT = 12
        val originDefault = LatLng(52.526136, 13.416046)
        val destinationDefault = LatLng(52.52901, 13.412728)
    }

    val origin = MutableLiveData<String>()
    val destination = MutableLiveData<String>()
    val boxHitCacheSizeInMeters = MutableLiveData<String>()

    init {
        origin.postValue(ConverterHelper.latLngToString(originDefault))
        destination.postValue(ConverterHelper.latLngToString(destinationDefault))
        boxHitCacheSizeInMeters.postValue(CACHE_BOX_HIT_SIZE_DEFAULT.toString())
    }

    fun onWalkingRouteClicked() {
        if (!isInputValid()) return

        calculateWalkingRoute(
            ConverterHelper.stringToLatLng(origin.value!!),
            ConverterHelper.stringToLatLng(destination.value!!),
            Integer.parseInt(boxHitCacheSizeInMeters.value!!)
        )
    }

    fun onDrivingRouteClicked() {
        if (!isInputValid()) return

        calculateDrivingRoute(
            ConverterHelper.stringToLatLng(origin.value!!),
            ConverterHelper.stringToLatLng(destination.value!!),
            Integer.parseInt(boxHitCacheSizeInMeters.value!!)
        )
    }

    fun cacheHitBoxSizeChanged(value: String) {
        boxHitCacheSizeInMeters.postValue(value)
    }

    fun originChanged(value: String) {
        origin.postValue(value)
    }

    fun destinationChanged(value: String) {
        destination.postValue(value)
    }

    private fun isInputValid(): Boolean {
        if (origin.value.isNullOrEmpty()) return false
        if (destination.value.isNullOrEmpty()) return false
        if (boxHitCacheSizeInMeters.value == null) return false
        return true
    }

    private fun calculateWalkingRoute(origin: LatLng, destination: LatLng, cacheHitSize: Int) {
        walkingDirectionsUseCase.invoke(origin, destination, cacheHitSize, cacheHitSize)
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

    private fun calculateDrivingRoute(origin: LatLng, destination: LatLng, cacheHitSize: Int) {
        drivingDirectionsUseCase.invoke(origin, destination, cacheHitSize, cacheHitSize)
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