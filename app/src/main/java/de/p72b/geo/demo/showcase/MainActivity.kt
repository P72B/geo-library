package de.p72b.geo.demo.showcase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import de.p72b.geo.demo.BR
import de.p72b.geo.demo.R
import de.p72b.geo.demo.databinding.ActivityMainBinding
import de.p72b.geo.demo.util.ConverterHelper
import de.p72b.geo.util.GeoUtils
import org.koin.androidx.viewmodel.ext.android.getViewModel


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    companion object {
        private const val TAG_ORIGIN_MARKER = "origin"
        private const val TAG_DESTINATION_MARKER = "destination"
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap
    private lateinit var originMarker: Marker
    private lateinit var destinationMarker: Marker
    private lateinit var route: Polyline
    private lateinit var googleRoute: Polyline
    private lateinit var originBoxPolygon: Polygon
    private lateinit var destinationBoxPolygon: Polygon
    private var shouldAutoZoom = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = getViewModel()
        lifecycle.addObserver(viewModel)
        binding.setVariable(BR.viewmodel, viewModel)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerDragListener(this)
        observeOrigin()
        observeDestination()
        observeRoute()
        observeGoogleRoute()
        observeCacheBoxSize()
        zoomToBounds()
    }

    override fun onMarkerDragEnd(marker: Marker?) {
        marker?.let {
            when (it.tag) {
                TAG_ORIGIN_MARKER -> {
                    shouldAutoZoom = false
                    viewModel.origin.value = ConverterHelper.latLngToString(it.position)
                }
                TAG_DESTINATION_MARKER -> {
                    shouldAutoZoom = false
                    viewModel.destination.value = ConverterHelper.latLngToString(it.position)
                }
            }
        }
    }

    override fun onMarkerDragStart(marker: Marker?) {
        // ignore
    }

    override fun onMarkerDrag(marker: Marker?) {
        // ignore
    }

    private fun zoomToBounds() {
        if (shouldAutoZoom) {
            val builder = LatLngBounds.Builder()
            builder.include(ConverterHelper.stringToLatLng(viewModel.origin.value!!))
            builder.include(ConverterHelper.stringToLatLng(viewModel.destination.value!!))
            val width = resources.displayMetrics.widthPixels
            val height = resources.displayMetrics.heightPixels
            val padding = (width * 0.20).toInt() // offset from edges of the map 20% of screen
            val cameraUpdateFactory =
                CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, padding)
            map.moveCamera(cameraUpdateFactory)
        } else {
            shouldAutoZoom = true
        }
    }

    private fun isSameLocation(latLng1: LatLng, latLng2: LatLng): Boolean {
        return latLng1.latitude == latLng2.latitude && latLng1.longitude == latLng2.longitude
    }

    private fun observeOrigin() {
        viewModel.origin.observe(this, Observer { input ->
            ConverterHelper.stringToLatLng(input).let { latLng ->
                if (this::originMarker.isInitialized) {
                    if (isSameLocation(originMarker.position, latLng)) return@Observer
                    originMarker.position = latLng
                    zoomToBounds()
                    return@Observer
                }
                originMarker = map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                )
                originMarker.tag = TAG_ORIGIN_MARKER
            }
        })
    }

    private fun observeDestination() {
        viewModel.destination.observe(this, Observer { input ->
            ConverterHelper.stringToLatLng(input).let { latLng ->
                if (this::destinationMarker.isInitialized) {
                    if (isSameLocation(destinationMarker.position, latLng)) return@Observer
                    destinationMarker.position = latLng
                    zoomToBounds()
                    return@Observer
                }
                destinationMarker = map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                )
                destinationMarker.tag = TAG_DESTINATION_MARKER
            }
        })
    }

    private fun observeRoute() {
        viewModel.osrmRoute.observe(this, Observer { list ->
            if (this::route.isInitialized) {
                route.points = list.second
                tryToDrawCacheBox()
                return@Observer
            }
            val polylineOptions =
                PolylineOptions().color(ContextCompat.getColor(this, R.color.osrm))
            for (item in list.second) {
                polylineOptions.add(item)
            }
            route = map.addPolyline(polylineOptions)
            tryToDrawCacheBox()
        })
    }

    private fun observeGoogleRoute() {
        viewModel.googleRoute.observe(this, Observer { list ->
            if (this::googleRoute.isInitialized) {
                googleRoute.points = list.second
                tryToDrawCacheBox()
                return@Observer
            }
            val polylineOptions =
                PolylineOptions().color(ContextCompat.getColor(this, R.color.google))
            for (item in list.second) {
                polylineOptions.add(item)
            }
            googleRoute = map.addPolyline(polylineOptions)
            tryToDrawCacheBox()
        })
    }

    private fun observeCacheBoxSize() {
        viewModel.boxHitCacheSizeInMeters.observe(this, Observer { value ->
            drawCacheBox(value.toInt())
        })
    }

    private fun tryToDrawCacheBox() {
        viewModel.boxHitCacheSizeInMeters.value?.toInt()?.let {
            drawCacheBox(it)
        }
    }

    private fun drawCacheBox(size: Int) {
        if ((!(this::originMarker.isInitialized) && !(this::destinationMarker.isInitialized)) ||
            size == 0
        ) {
            return
        }
        if (this::destinationBoxPolygon.isInitialized) {
            destinationBoxPolygon.remove()
        }
        if (this::originBoxPolygon.isInitialized) {
            originBoxPolygon.remove()
        }
        val originBox =
            GeoUtils.getBoundsFromLocationWithinDistance(originMarker.position, size)
        val destinationBox =
            GeoUtils.getBoundsFromLocationWithinDistance(destinationMarker.position, size)
        destinationBoxPolygon = map.addPolygon(polygonOptionsFromLatLngBounds(destinationBox))
        originBoxPolygon = map.addPolygon(polygonOptionsFromLatLngBounds(originBox))
    }

    private fun polygonOptionsFromLatLngBounds(latLngBounds: LatLngBounds): PolygonOptions {
        val polygonOptions = PolygonOptions()
        val northwest = LatLng(latLngBounds.southwest.latitude, latLngBounds.northeast.longitude)
        val southeast = LatLng(latLngBounds.northeast.latitude, latLngBounds.southwest.longitude)

        polygonOptions.add(latLngBounds.northeast, northwest)
        polygonOptions.add(northwest, latLngBounds.southwest)
        polygonOptions.add(latLngBounds.southwest, southeast)
        polygonOptions.add(southeast, latLngBounds.northeast)

        return polygonOptions
    }
}