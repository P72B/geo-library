package de.p72b.geo

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.GsonBuilder
import de.p72b.geo.cache.BoxHitCache
import de.p72b.geo.google.DirectionsRoute
import de.p72b.geo.google.ResolvedDirections
import de.p72b.geo.http.LatLngBoundsDeserializer
import de.p72b.geo.http.ParamInterceptor
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class GeoService(
    baseUrl: String,
    channel: String? = null,
    key: String? = null,
    networkInterceptorList: List<Interceptor>? = ArrayList(),
    interceptorList: List<Interceptor>? = ArrayList(),
    converterFactoryList: List<Converter.Factory>? = ArrayList(),
    itemCacheSize: Int = 60,
    okHttpCacheSizeInMegaByte: Long = 10L * 1_024L * 1_024L // 10 MB
) {

    private val directionsCache = BoxHitCache(itemCacheSize)
    private val locationsCache = BoxHitCache(itemCacheSize)
    private val api: GeoServiceApi by lazy {
        val client = OkHttpClient.Builder()
        try {
            val cache = Cache(
                Geo.appContext.cacheDir,
                okHttpCacheSizeInMegaByte
            )
            client.cache(cache)
        } catch (e: Exception) {
            // nothing to do here so far
        }
        channel?.let {
            client.addInterceptor(
                ParamInterceptor(
                    "channel", channel
                )
            )
        }
        key?.let {
            client.addInterceptor(
                ParamInterceptor(
                    "key", it
                )
            )
        }
        networkInterceptorList?.let {
            for (item in it) {
                client.addNetworkInterceptor(item)
            }
        }
        interceptorList?.let {
            for (item in it) {
                client.addInterceptor(item)
            }
        }

        val gson = GsonBuilder().registerTypeAdapter(
            LatLngBounds::class.java,
            LatLngBoundsDeserializer()
        ).create()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client.build())
        converterFactoryList?.let {
            for (item in it) {
                retrofit.addConverterFactory(item)
            }
        }

        retrofit.build().create(GeoServiceApi::class.java)
    }


    fun pedestrianRoute(
        origin: LatLng,
        destination: LatLng,
        allowedAccuracyOriginInMeters: Int = 0,
        allowedAccuracyDestinationInMeters: Int = 0
    ): Single<DirectionsRoute> {
        return routeBy(
            origin,
            destination,
            "walking",
            allowedAccuracyOriginInMeters,
            allowedAccuracyDestinationInMeters
        )
    }

    fun drivingRoute(
        origin: LatLng,
        destination: LatLng,
        allowedAccuracyOriginInMeters: Int = 0,
        allowedAccuracyDestinationInMeters: Int = 0
    ): Single<DirectionsRoute> {
        return routeBy(
            origin,
            destination,
            "driving",
            allowedAccuracyOriginInMeters,
            allowedAccuracyDestinationInMeters
        )
    }

    private fun routeBy(
        origin: LatLng,
        destination: LatLng,
        mode: String,
        allowedAccuracyOriginInMeters: Int = 0,
        allowedAccuracyDestinationInMeters: Int = 0
    ): Single<DirectionsRoute> {
        val cached = directionsCache.get(
            origin,
            destination,
            mode,
            allowedAccuracyOriginInMeters,
            allowedAccuracyDestinationInMeters
        )
        return if (cached != null) {
            Single.just(cached)
        } else distanceCall(origin, destination, mode)
            .map { response ->
                var result = DirectionsRoute()
                response.list?.let { routes ->
                    if (routes.isNotEmpty()) {
                        val route = routes[0] // we assume the first route is the best shipped route
                        directionsCache.put(route, origin, destination, mode)
                        result = route
                    }
                }
                result
            }
    }

    private fun distanceCall(
        origin: LatLng,
        destination: LatLng,
        mode: String
    ): Single<ResolvedDirections> {
        return api.distance(
            getLatLngApiRepresentation(origin.latitude, origin.longitude),
            getLatLngApiRepresentation(destination.latitude, destination.longitude),
            mode
        )
    }

    private fun getLatLngApiRepresentation(latitude: Double, longitude: Double): String {
        return "$latitude,$longitude"
    }
}