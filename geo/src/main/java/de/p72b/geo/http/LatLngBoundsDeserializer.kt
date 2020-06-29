package de.p72b.geo.http

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.*
import java.lang.reflect.Type

class LatLngBoundsDeserializer : JsonDeserializer<LatLngBounds> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LatLngBounds {
        val southwest = toLatLng(json.asJsonObject.get("southwest").asJsonObject)
        val northeast = toLatLng(json.asJsonObject.get("northeast").asJsonObject)
        return LatLngBounds(southwest, northeast)
    }

    private fun toLatLng(jsonObject: JsonObject): LatLng {
        return LatLng(jsonObject.get("lat").asDouble, jsonObject.get("lng").asDouble)
    }
}