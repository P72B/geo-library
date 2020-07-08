package de.p72b.geo.demo

import de.p72b.geo.GeoService
import de.p72b.geo.demo.showcase.MainViewModel
import de.p72b.geo.demo.usecase.DrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.WalkingDirectionsUseCase
import de.p72b.geo.util.SecuredConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object DependencyGraph {

    private const val GOOGLE_API_BASE_URL = "https://maps.googleapis.com/maps/api/"
    private const val GEO_SERVICE_BASE_URL = "http://192.168.1.26:5000/geo/"

    private val appModule = module {

        single {
            GeoService(
                baseUrl = GEO_SERVICE_BASE_URL,
                channel = BuildConfig.APPLICATION_ID,
                key = SecuredConstants.GOOGLE_MAPS_WEB_API_KEY
            )
        }

        factory {
            DrivingDirectionsUseCase(
                geoService = get()
            )
        }

        factory {
            WalkingDirectionsUseCase(
                geoService = get()
            )
        }

        viewModel {
            MainViewModel(
                networkThread = Schedulers.io(),
                mainThread = AndroidSchedulers.mainThread(),
                drivingDirectionsUseCase = get(),
                walkingDirectionsUseCase = get()
            )
        }
    }

    fun get() = mutableListOf(
        appModule
    )
}