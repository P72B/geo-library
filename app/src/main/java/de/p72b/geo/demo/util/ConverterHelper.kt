package de.p72b.geo.demo.util

import com.google.android.gms.maps.model.LatLng
import de.p72b.geo.util.GeoUtils

object ConverterHelper {


    fun stringToLatLng(value: String): LatLng {
        val components = value.split(",")
        return LatLng(
            GeoUtils.roundToRequiredAccuracy(components[0].toDouble()),
            GeoUtils.roundToRequiredAccuracy(components[1].toDouble())
        )
    }

    fun latLngToString(latLng: LatLng): String {
        return "${GeoUtils.roundToRequiredAccuracy(latLng.latitude)}," +
                "${GeoUtils.roundToRequiredAccuracy(latLng.longitude)}"
    }
}