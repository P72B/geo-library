package de.p72b.geo.demo.showcase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import de.p72b.geo.demo.BR
import de.p72b.geo.demo.R
import de.p72b.geo.demo.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: GoogleMap

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
    }
}