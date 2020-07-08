package de.p72b.geo.demo.showcase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.p72b.geo.demo.BR
import de.p72b.geo.demo.R
import de.p72b.geo.demo.databinding.ActivityMainBinding
import de.p72b.geo.demo.util.ConverterHelper
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
        viewModel.origin.observe(this, Observer { input ->
            ConverterHelper.stringToLatLng(input).let { latLng ->
                if (this::originMarker.isInitialized) {
                    if (isSameLoctaion(originMarker.position, latLng)) return@Observer
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
        viewModel.destination.observe(this, Observer { input ->
            ConverterHelper.stringToLatLng(input).let { latLng ->
                if (this::destinationMarker.isInitialized) {
                    if (isSameLoctaion(destinationMarker.position, latLng)) return@Observer
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

    private fun isSameLoctaion(latLng1: LatLng, latLng2: LatLng): Boolean {
        return latLng1.latitude == latLng2.latitude && latLng1.longitude == latLng2.longitude
    }
}