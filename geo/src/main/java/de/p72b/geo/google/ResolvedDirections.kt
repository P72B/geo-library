package de.p72b.geo.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import de.p72b.geo.util.GeoUtils
import de.p72b.geo.util.PolylineEncoding

data class ResolvedDirections(
    @SerializedName("routes") var list: Array<DirectionsRoute>? = null
)

data class DirectionsRoute(
    @SerializedName("legs") var list: Array<DirectionsLeg>? = null,
    @SerializedName("overview_polyline") var polyLine: EncodedPolyline? = null
) {

    private var duration: Duration? = null
    private var distance: Distance? = null

    val getDuration: Duration?
        get() {
            if (duration != null) {
                return duration
            }
            sumUpDurationAndDistance()
            return duration
        }

    val getDistance: Distance?
        get() {
            if (distance != null) {
                return distance
            }
            sumUpDurationAndDistance()
            return distance
        }

    val formattedDestinationAddress: String
        get() {
            if (list == null || list!!.isEmpty()) {
                return ""
            }
            val lastLeg = list!![list!!.size - 1]

            return when (lastLeg.endAddress) {
                null -> ""
                else -> GeoUtils.getFormattedAddress(lastLeg.endAddress!!)
            }
        }

    private fun sumUpDurationAndDistance() {
        if (list == null || list!!.isEmpty()) {
            return
        }

        val newDuration = Duration()
        val newDistance = Distance()
        for (leg in list!!) {
            newDuration.inSeconds += leg.duration!!.inSeconds
            newDistance.inMeters += leg.distance!!.inMeters
        }
        duration = newDuration
        distance = newDistance
    }
}

data class Distance(
    @SerializedName("value") var inMeters: Long = 0
)

data class Duration(
    @SerializedName("value") var inSeconds: Long = 0
)

data class DirectionsLeg(
    @SerializedName("distance") var distance: Distance? = null,
    @SerializedName("duration") var duration: Duration? = null,
    @SerializedName("end_address") var endAddress: String? = null
)

data class EncodedPolyline(
    @SerializedName("points") private val points: String
) {

    fun decodePath(): List<LatLng> {
        return PolylineEncoding.decode(points)
    }
}