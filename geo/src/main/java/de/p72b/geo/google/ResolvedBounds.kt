package de.p72b.geo.google

import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.annotations.SerializedName

data class ResolvedBounds(
    @SerializedName("results") private val list: List<Bound>? = null
) {

    val bounds: LatLngBounds?
        get() = list!![0].geometry!!.mBounds

    val viewport: LatLngBounds?
        get() = list!![0].geometry!!.mViewport

}

data class Bound(
    @SerializedName("geometry") var geometry: BoundsGeometry? = null
)

data class BoundsGeometry(
    @SerializedName("bounds") var mBounds: LatLngBounds? = null,
    @SerializedName("viewport") var mViewport: LatLngBounds? = null
)