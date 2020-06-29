package de.p72b.geo.cache

import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.google.DirectionsRoute
import de.p72b.geo.util.GeoUtils

class BoxHitCache(maxSize: Int) : PointHitCache(maxSize) {

    fun get(
        origin: LatLng,
        destination: LatLng,
        allowedAccuracyOrigin: Int,
        allowedAccuracyDestination: Int
    ):
            DirectionsRoute? {
        val originBox =
            GeoUtils.getBoundsFromLocationWithinDistance(
                origin,
                allowedAccuracyOrigin
            )
        val destinationBox =
            GeoUtils.getBoundsFromLocationWithinDistance(
                destination,
                allowedAccuracyDestination
            )

        for (cacheEntry in map.entries) {
            val isOriginInside =
                GeoUtils.isPointWithinBounds(
                    originBox,
                    cacheEntry.value.latLng1
                )
            val isDestinationInside =
                GeoUtils.isPointWithinBounds(
                    destinationBox,
                    cacheEntry.value.latLng2
                )

            if (isOriginInside && isDestinationInside) {
                return cacheEntry.value.cached as DirectionsRoute
            }
        }
        return null // nothing found
    }
}