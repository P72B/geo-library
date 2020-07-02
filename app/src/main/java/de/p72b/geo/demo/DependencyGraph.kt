package de.p72b.geo.demo

import de.p72b.geo.GeoService
import de.p72b.geo.demo.showcase.MainViewModel
import de.p72b.geo.demo.usecase.DrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.WalkingDirectionsUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object DependencyGraph {

    private const val GEO_SERVICE_BASE_URL = "https://maps.googleapis.com/maps/api/"

    private val appModule = module {

        single {
            GeoService(
                baseUrl = GEO_SERVICE_BASE_URL,
                channelId = BuildConfig.APPLICATION_ID
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