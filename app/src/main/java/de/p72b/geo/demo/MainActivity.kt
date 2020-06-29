package de.p72b.geo.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.p72b.geo.GeoService

class MainActivity : AppCompatActivity() {

    private lateinit var geoService: GeoService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        geoService = GeoService()
    }
}