package de.p72b.geo.demo.showcase

import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.demo.usecase.DrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.WalkingDirectionsUseCase
import de.p72b.geo.demo.util.BaseViewModel
import de.p72b.geo.google.DirectionsRoute
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel(
    private val mainThread: Scheduler,
    private val drivingDirectionsUseCase: DrivingDirectionsUseCase,
    private val walkingDirectionsUseCase: WalkingDirectionsUseCase
) : BaseViewModel(), LifecycleObserver {

    fun calculateWalkingRoute(origin: LatLng, destination: LatLng) {
        walkingDirectionsUseCase.invoke(origin, destination)
            .observeOn(mainThread)
            .subscribeBy {
                handleDirectionsResult(it)
            }.autoDispose()
    }

    fun calculateDrivingRoute(origin: LatLng, destination: LatLng) {
        drivingDirectionsUseCase.invoke(origin, destination)
            .observeOn(mainThread)
            .subscribeBy {
                handleDirectionsResult(it)
            }.autoDispose()
    }

    private fun handleDirectionsResult(directionsRoute: DirectionsRoute?) {

    }
}