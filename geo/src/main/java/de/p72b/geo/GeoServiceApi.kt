package de.p72b.geo

import de.p72b.geo.google.ResolvedAddress
import de.p72b.geo.google.ResolvedAutocomplete
import de.p72b.geo.google.ResolvedBounds
import de.p72b.geo.google.ResolvedDirections
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoServiceApi {

    companion object {
        const val ENDPOINT_GEO_DIRECTIONS = "directions/json"
        const val ENDPOINT_GEO_GEOCODE = "geocode/json"
        const val ENDPOINT_GEO_AUTOCOMPLETE = "autocomplete"
    }

    @GET(ENDPOINT_GEO_DIRECTIONS)
    fun distance(
        @Query("origin") origin: String, @Query("destination") destination: String,
        @Query("mode") mode: String
    ): Single<ResolvedDirections>

    @GET(ENDPOINT_GEO_GEOCODE)
    fun reverseGeocodeLocation(
        @Query("latlng") latLng: String,
        @Query("language") language: String
    ): Single<ResolvedAddress>

    @GET(ENDPOINT_GEO_GEOCODE)
    fun geocodeAddress(
        @Query("address") address: String,
        @Query("latlng") latLng: String? = null
    ): Single<ResolvedAddress>

    @GET(ENDPOINT_GEO_GEOCODE)
    fun getBoundsById(@Query("place_id") placeId: String): Single<ResolvedBounds>

    @GET(ENDPOINT_GEO_GEOCODE)
    fun getBoundsByComponents(@Query("components") components: String): Single<ResolvedBounds>

    @GET(ENDPOINT_GEO_AUTOCOMPLETE)
    fun autocompleteInput(
        @Query("input") query: String,
        @Query("latlng") latLng: String?,
        @Query("country") country: String?
    ): Single<ResolvedAutocomplete>
}