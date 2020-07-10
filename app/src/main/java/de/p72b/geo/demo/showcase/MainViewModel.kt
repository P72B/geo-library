package de.p72b.geo.demo.showcase

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.demo.usecase.OsrmDrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.GoogleDrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.GoogleWalkingDirectionsUseCase
import de.p72b.geo.demo.usecase.OsrmWalkingDirectionsUseCase
import de.p72b.geo.demo.util.BaseViewModel
import de.p72b.geo.demo.util.ConverterHelper
import de.p72b.geo.google.DirectionsRoute
import de.p72b.geo.util.UnitLocale
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel(
    private val mainThread: Scheduler,
    private val networkThread: Scheduler,
    private val osrmDrivingDirectionsUseCase: OsrmDrivingDirectionsUseCase,
    private val osrmWalkingDirectionsUseCase: OsrmWalkingDirectionsUseCase,
    private val googleDrivingDirectionsUseCase: GoogleDrivingDirectionsUseCase,
    private val googleWalkingDirectionsUseCase: GoogleWalkingDirectionsUseCase
) : BaseViewModel(), LifecycleObserver {

    companion object {
        private const val CACHE_BOX_HIT_SIZE_DEFAULT = 12
        private const val GOOGLE_SERVICE_ID = "google"
        private const val OSRM_SERVICE_ID = "osrm"
        val originDefault = LatLng(52.5329704, 13.3832851)
        val destinationDefault = LatLng(52.5076062, 13.3646353)
    }

    val origin = MutableLiveData<String>()
        .apply { postValue(ConverterHelper.latLngToString(originDefault)) }
    val destination = MutableLiveData<String>()
        .apply { postValue(ConverterHelper.latLngToString(destinationDefault)) }
    val boxHitCacheSizeInMeters = MutableLiveData<String>()
        .apply { postValue(CACHE_BOX_HIT_SIZE_DEFAULT.toString()) }
    val osrmTripSummary = MutableLiveData<String>()
    val googleTripSummary = MutableLiveData<String>()
    val osrmRoute = MutableLiveData<Pair<String, List<LatLng>>>()
    val googleRoute = MutableLiveData<Pair<String, List<LatLng>>>()
    val progressActive = MutableLiveData<Boolean>()
        .apply { postValue(false) }
    private var onFinallyCount = 0

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
        progressActive.postValue(true)
        osrmWalkingDirectionsUseCase.invoke(origin, destination, cacheHitSize, cacheHitSize)
            .subscribeOn(networkThread)
            .observeOn(mainThread)
            .doFinally {
                doOnFinally()
            }
            .subscribeBy(
                onError = {
                    System.out.println("ERROR $it")
                },
                onSuccess = {
                    handleDirectionsResult(OSRM_SERVICE_ID, it, osrmTripSummary, osrmRoute)
                }
            ).autoDispose()
        googleWalkingDirectionsUseCase.invoke(origin, destination, cacheHitSize, cacheHitSize)
            .subscribeOn(networkThread)
            .observeOn(mainThread)
            .doFinally {
                doOnFinally()
            }
            .subscribeBy(
                onError = {
                    System.out.println("ERROR $it")
                },
                onSuccess = {
                    handleDirectionsResult(GOOGLE_SERVICE_ID, it, googleTripSummary, googleRoute)
                }
            ).autoDispose()
    }

    private fun calculateDrivingRoute(origin: LatLng, destination: LatLng, cacheHitSize: Int) {
        progressActive.postValue(true)
        osrmDrivingDirectionsUseCase.invoke(origin, destination, cacheHitSize, cacheHitSize)
            .subscribeOn(networkThread)
            .observeOn(mainThread)
            .doFinally {
                doOnFinally()
            }
            .subscribeBy(
                onError = {
                    System.out.println("ERROR $it")
                },
                onSuccess = {
                    handleDirectionsResult(OSRM_SERVICE_ID, it, osrmTripSummary, osrmRoute)
                }
            ).autoDispose()
        googleDrivingDirectionsUseCase.invoke(origin, destination, cacheHitSize, cacheHitSize)
            .subscribeOn(networkThread)
            .observeOn(mainThread)
            .doFinally {
                doOnFinally()
            }
            .subscribeBy(
                onError = {
                    System.out.println("ERROR $it")
                },
                onSuccess = {
                    handleDirectionsResult(GOOGLE_SERVICE_ID, it, googleTripSummary, googleRoute)
                }
            ).autoDispose()
    }

    private fun doOnFinally() {
        onFinallyCount++
        if (onFinallyCount < 2) return
        onFinallyCount = 0
        progressActive.postValue(false)
    }

    private fun handleDirectionsResult(
        serviceResultName: String,
        result: DirectionsRoute?,
        summary: MutableLiveData<String>,
        route: MutableLiveData<Pair<String, List<LatLng>>>
    ) {
        result?.let { directionsRoute ->
            directionsRoute.polyLine?.let {
                if (route.value?.first != it.points) {
                    route.postValue(Pair(it.points, it.decodePath()))
                }
            }
            val distance = directionsRoute.getDistance?.inMeters ?: 0L
            val duration = directionsRoute.getDuration?.inSeconds ?: 0L
            if (distance > 0L && duration > 0L) {
                summary.postValue(
                    "$serviceResultName: ${duration / 60} Min. (${UnitLocale.default.fromMeters(
                        distance
                    )})"
                )
            } else {
                summary.postValue("")
            }
        }
    }
}