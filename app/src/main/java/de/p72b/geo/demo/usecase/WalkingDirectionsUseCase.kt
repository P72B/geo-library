package de.p72b.geo.demo.usecase

import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.GeoService
import de.p72b.geo.google.DirectionsRoute
import io.reactivex.Single

class WalkingDirectionsUseCase(private val geoService: GeoService) {
    operator fun invoke(origin: LatLng, destination: LatLng): Single<DirectionsRoute> {
        return geoService.pedestrianRoute(origin, destination)
    }
}