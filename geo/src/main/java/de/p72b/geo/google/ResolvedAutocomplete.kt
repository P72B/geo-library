package de.p72b.geo.google

import com.google.gson.annotations.SerializedName

data class ResolvedAutocomplete(
    @SerializedName("predictions") val predictions: List<Prediction>? = null,
    @SerializedName("status") private val status: String? = null
)

data class Prediction(
    @SerializedName("description") val description: String? = null,
    @SerializedName("id") private val id: String? = null,
    @SerializedName("place_id") private val placeId: String? = null,
    @SerializedName("reference") private val reference: String? = null,
    @SerializedName("types") val types: List<String>? = null,
    @SerializedName("geometry") val geometry: Geometry? = null
)