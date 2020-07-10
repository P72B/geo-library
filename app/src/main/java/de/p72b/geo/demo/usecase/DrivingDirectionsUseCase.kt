package de.p72b.geo.demo.usecase

import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.GeoService
import de.p72b.geo.demo.util.GoogleGeoService
import de.p72b.geo.demo.util.OsrmGeoService
import de.p72b.geo.google.DirectionsRoute
import io.reactivex.Single

class OsrmDrivingDirectionsUseCase(geoService: OsrmGeoService) :
    DrivingDirectionsUseCase(geoService)

class GoogleDrivingDirectionsUseCase(geoService: GoogleGeoService) :
    DrivingDirectionsUseCase(geoService)

abstract class DrivingDirectionsUseCase(private val geoService: GeoService) {
    operator fun invoke(
        origin: LatLng,
        destination: LatLng,
        allowedAccuracyOriginInMeters: Int = 0,
        allowedAccuracyDestinationInMeters: Int = 0
    ): Single<DirectionsRoute> {
        return geoService.drivingRoute(
            origin,
            destination,
            allowedAccuracyOriginInMeters,
            allowedAccuracyDestinationInMeters
        )
    }
}