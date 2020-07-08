package de.p72b.geo.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class ResolvedAddress(
    @SerializedName("results") val list: List<Address>? = null
) {

    companion object {
        val LOCALITY = "locality"
        val COUNTRY = "country"
    }
}

data class Address(
    @SerializedName("formatted_address") private val formattedAddress: String? = null,
    @SerializedName("geometry") private val geometry: Geometry? = null,
    @SerializedName("address_components") private val addressComponents: List<AddressComponent>? = null
) {

    val getFormattedAddress: String
        get() = FormatHelper.getFormattedAddress(formattedAddress!!)
    val getFullAddress: String?
        get() = formattedAddress
    val latLng: LatLng
        get() = geometry!!.location!!.latLng

    fun findBy(typeToFind: String): String? {
        return addressComponents?.let {
            for (component in addressComponents) {
                val types = component.types ?: continue
                for (type in types) {
                    if (typeToFind == type) {
                        return component.longName
                    }
                }
            }
            null
        }
    }
}

data class Geometry(
    @SerializedName("location") val location: Location? = null
)

data class Location(
    @SerializedName("lat") private val mLat: String? = null,
    @SerializedName("lng") private val mLng: String? = null
) {

    val latLng: LatLng
        get() = LatLng(java.lang.Double.parseDouble(mLat!!), java.lang.Double.parseDouble(mLng!!))
}

data class AddressComponent(
    @SerializedName("long_name") val longName: String? = null,
    @SerializedName("short_name") val shortName: String? = null,
    @SerializedName("types") val types: List<String>? = null
) {
    companion object {
        val POSTAL_CODE = "postal_code"
        val POSTAL_COUNTRY = "country"
        val POSTAL_LOCALITY = "locality"
        val POSTAL_ROUTE = "route"
        val POSTAL_STREET_NUMBER = "street_number"
    }
}