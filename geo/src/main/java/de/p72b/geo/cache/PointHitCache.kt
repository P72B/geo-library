package de.p72b.geo.cache

import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.google.ResolvedAddress
import de.p72b.geo.google.DirectionsRoute
import de.p72b.geo.util.GeoUtils
import java.util.HashMap

open class PointHitCache(private val maxSize: Int) {
    val map = HashMap<String, Item>()

    fun containsKey(cacheId: String): Boolean {
        return map.containsKey(cacheId)
    }

    fun put(directionsRoute: DirectionsRoute, origin: LatLng, destination: LatLng, mode: String) {
        checkMapLength()
        map[getCacheKey(origin, destination, mode)] =
            Item(directionsRoute, origin, destination, mode)
    }

    fun put(directionsRoute: DirectionsRoute, origin: LatLng, destination: LatLng) {
        checkMapLength()
        map[getCacheKey(origin, destination)] = Item(directionsRoute, origin, destination)
    }

    fun put(addressResponse: ResolvedAddress, latLng: LatLng) {
        checkMapLength()
        map[getCacheKey(latLng)] = Item(addressResponse, latLng)
    }

    private fun checkMapLength() {
        if (map.size == maxSize) {
            map.remove(map.keys.toTypedArray()[0])
        }
    }

    fun get(origin: LatLng, destination: LatLng, mode: String): DirectionsRoute? {
        val key = getCacheKey(origin, destination, mode)
        if (!map.containsKey(key)) {
            return null
        }
        return map[key]!!.cached as DirectionsRoute
    }

    fun get(origin: LatLng, destination: LatLng): DirectionsRoute? {
        val key = getCacheKey(origin, destination)
        if (!map.containsKey(key)) {
            return null
        }
        return map[key]!!.cached as DirectionsRoute
    }

    fun get(latLng: LatLng): ResolvedAddress? {
        val key = getCacheKey(latLng)
        if (!map.containsKey(key)) {
            return null
        }
        return map[key]!!.cached as ResolvedAddress
    }

    private fun getCacheKey(origin: LatLng, destination: LatLng, mode: String): String {
        return getCacheKey(origin) + getCacheKey(destination) + mode
    }

    private fun getCacheKey(origin: LatLng, destination: LatLng): String {
        return getCacheKey(origin) + getCacheKey(destination)
    }

    private fun getCacheKey(location: LatLng): String {
        return GeoUtils.roundToRequiredAccuracy(
            location.latitude
        ).toString() +
                GeoUtils.roundToRequiredAccuracy(
                    location.longitude
                ).toString()
    }

    inner class Item(
        val cached: Any,
        val latLng1: LatLng,
        val latLng2: LatLng? = null,
        val mode: String? = null
    )
}