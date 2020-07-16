package de.p72b.geo.google

import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import de.p72b.geo.util.PolylineEncoding

@Keep
data class ResolvedDirections(
    @SerializedName("routes") var list: Array<DirectionsRoute>? = null
)

@Keep
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
                else -> FormatHelper.getFormattedAddress(lastLeg.endAddress!!)
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

@Keep
data class Distance(
    @SerializedName("value") var inMeters: Long = 0
)

@Keep
data class Duration(
    @SerializedName("value") var inSeconds: Long = 0
)

@Keep
data class DirectionsLeg(
    @SerializedName("distance") var distance: Distance? = null,
    @SerializedName("duration") var duration: Duration? = null,
    @SerializedName("end_address") var endAddress: String? = null
)

@Keep
data class EncodedPolyline(
    @SerializedName("points") val points: String
) {

    fun decodePath(): List<LatLng> {
        return PolylineEncoding.decode(points)
    }
}