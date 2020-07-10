package de.p72b.geo.demo.usecase

import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.GeoService
import de.p72b.geo.demo.util.GoogleGeoService
import de.p72b.geo.demo.util.OsrmGeoService
import de.p72b.geo.google.DirectionsRoute
import io.reactivex.Single

class OsrmWalkingDirectionsUseCase(geoService: OsrmGeoService) :
    WalkingDirectionsUseCase(geoService)

class GoogleWalkingDirectionsUseCase(geoService: GoogleGeoService) :
    WalkingDirectionsUseCase(geoService)

class OsrmDrivingDirectionsUseCase(geoService: OsrmGeoService) :
    DrivingDirectionsUseCase(geoService)

class GoogleDrivingDirectionsUseCase(geoService: GoogleGeoService) :
    DrivingDirectionsUseCase(geoService)

abstract class WalkingDirectionsUseCase(private val geoService: GeoService) : DirectionsUseCase {
    override operator fun invoke(
        origin: LatLng,
        destination: LatLng,
        allowedAccuracyOriginInMeters: Int,
        allowedAccuracyDestinationInMeters: Int
    ): Single<DirectionsRoute> {
        return geoService.pedestrianRoute(
            origin,
            destination,
            allowedAccuracyOriginInMeters,
            allowedAccuracyDestinationInMeters
        )
    }
}

abstract class DrivingDirectionsUseCase(private val geoService: GeoService) : DirectionsUseCase {
    override operator fun invoke(
        origin: LatLng,
        destination: LatLng,
        allowedAccuracyOriginInMeters: Int,
        allowedAccuracyDestinationInMeters: Int
    ): Single<DirectionsRoute> {
        return geoService.drivingRoute(
            origin,
            destination,
            allowedAccuracyOriginInMeters,
            allowedAccuracyDestinationInMeters
        )
    }
}

interface DirectionsUseCase {
    fun invoke(
        origin: LatLng,
        destination: LatLng,
        allowedAccuracyOriginInMeters: Int = 0,
        allowedAccuracyDestinationInMeters: Int = 0
    ): Single<DirectionsRoute>
}