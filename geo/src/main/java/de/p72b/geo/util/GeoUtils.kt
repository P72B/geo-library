package de.p72b.geo.util

import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

object GeoUtils {

    private val EARTH_RADIUS = 6378000 // 6378000 Size of the earth radius (in meters)

    fun latLngBounds2GeoJsonPolygon(bounds: LatLngBounds): List<List<Array<Double>>> {
        val polygon = arrayListOf(
            getGeoJsonCoords(
                bounds.northeast.longitude,
                bounds.northeast.latitude
            ),
            getGeoJsonCoords(
                bounds.northeast.longitude,
                bounds.southwest.latitude
            ),
            getGeoJsonCoords(
                bounds.southwest.longitude,
                bounds.southwest.latitude
            ),
            getGeoJsonCoords(
                bounds.southwest.longitude,
                bounds.northeast.latitude
            ),
            getGeoJsonCoords(
                bounds.northeast.longitude,
                bounds.northeast.latitude
            )
        )
        return arrayListOf(polygon)
    }

    fun latLng2GeoJsonPoint(center: LatLng): List<Double> =
        arrayListOf(center.longitude, center.latitude)

    private fun getGeoJsonCoords(longitude: Double, latitude: Double): Array<Double> = arrayOf(
        roundToRequiredAccuracy(longitude),
        roundToRequiredAccuracy(latitude)
    )

    fun roundToRequiredAccuracy(value: Double): Double =
        Math.round(value * 1000000.0).toDouble() / 1000000.0

    fun locationFromLatLng(position: LatLng, withProvider: String = ""): Location {
        return Location(withProvider).apply {
            latitude = position.latitude
            longitude = position.longitude
        }
    }

    /**
     * Calculates the distance of two geo locations.
     *
     * @param pos1 First geo location
     * @param pos2 Second geo location
     * @return The distance between pos1 and pos2 in meters
     */
    fun latLngDistance(pos1: LatLng, pos2: LatLng): Int {
        val loc1 = locationFromLatLng(
            pos1,
            LocationManager.NETWORK_PROVIDER
        )
        val loc2 = locationFromLatLng(
            pos2,
            LocationManager.NETWORK_PROVIDER
        )
        return loc1.distanceTo(loc2).toInt()
    }

    /**
     * Calculates a squared bounding box from a given location within a specified distance.
     *
     * @param location Center point of the bonding box.
     * @param distance Distance in meters of rectangle edge length. Because of square all edge
     * lengths are equal.
     * @return LatLngBounds Bounding box.
     */
    fun getBoundsFromLocationWithinDistance(location: LatLng, distance: Int): LatLngBounds {
        val distanceToBorder = distance / 2

        val mLongitudeD =
            Math.asin(distanceToBorder / (EARTH_RADIUS * Math.cos(Math.PI * location.latitude / 180))) * 180 / Math.PI
        val mLatitudeD =
            Math.asin(distanceToBorder.toDouble() / EARTH_RADIUS.toDouble()) * 180 / Math.PI

        val maxLat = location.latitude + mLatitudeD
        val minLat = location.latitude - mLatitudeD
        val maxLng = location.longitude + mLongitudeD
        val minLng = location.longitude - mLongitudeD

        val southWest = LatLng(minLat, minLng)
        val northEast = LatLng(maxLat, maxLng)
        return LatLngBounds(southWest, northEast)
    }

    fun isPointWithinBounds(bounds: LatLngBounds?, point: LatLng?): Boolean {
        return if (bounds == null || point == null) {
            false
        } else {
            point.longitude >= bounds.southwest.longitude &&
                    point.longitude <= bounds.northeast.longitude &&
                    point.latitude >= bounds.southwest.latitude &&
                    point.latitude <= bounds.northeast.latitude
        }
    }

    fun isBoundWithinBounds(bounds: LatLngBounds?, boundToTest: LatLngBounds?): Boolean {
        return if (bounds == null || boundToTest == null) {
            false
        } else {
            isPointWithinBounds(
                bounds,
                boundToTest.northeast
            ) && isPointWithinBounds(
                bounds,
                boundToTest.southwest
            )
        }
    }

    /**
     * Can extend a given bounding box to a given distance in meters. It returns a LatLngBounds
     * which is bigger or equals(in case of distance equals zero) the given bounding box.
     *
     * @param boundsToExtend           LatLngBounds to be extended.
     * @param distanceToExtendInMeters Distance in meters to enlarge the given bounds.
     * @return Enlarged LatLngBounds by given distance.
     */
    fun getExtendedBound(
        boundsToExtend: LatLngBounds,
        distanceToExtendInMeters: Int
    ): LatLngBounds {
        val topBounds =
            getBoundsFromLocationWithinDistance(
                boundsToExtend.northeast,
                distanceToExtendInMeters
            )
        val bottomBounds =
            getBoundsFromLocationWithinDistance(
                boundsToExtend.southwest,
                distanceToExtendInMeters
            )
        return LatLngBounds(bottomBounds.southwest, topBounds.northeast)
    }
}