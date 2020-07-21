package de.p72b.geo.demo

import de.p72b.geo.demo.showcase.MainViewModel
import de.p72b.geo.demo.usecase.OsrmDrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.GoogleDrivingDirectionsUseCase
import de.p72b.geo.demo.usecase.GoogleWalkingDirectionsUseCase
import de.p72b.geo.demo.usecase.OsrmWalkingDirectionsUseCase
import de.p72b.geo.demo.util.GoogleGeoService
import de.p72b.geo.demo.util.OsrmGeoService
import de.p72b.geo.demo.util.SecuredConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object DependencyGraph {

    private const val GOOGLE_API_BASE_URL = "https://maps.googleapis.com/maps/api/"
    private const val GEO_SERVICE_BASE_URL = "https://p72b.de/geo/"

    private val appModule = module {

        single {
            OsrmGeoService(
                baseUrl = GEO_SERVICE_BASE_URL,
                channel = BuildConfig.APPLICATION_ID
            )
        }

        single {
            GoogleGeoService(
                baseUrl = GOOGLE_API_BASE_URL,
                channel = BuildConfig.APPLICATION_ID,
                key = SecuredConstants.GOOGLE_MAPS_WEB_API_KEY
            )
        }

        factory {
            OsrmDrivingDirectionsUseCase(
                geoService = get()
            )
        }

        factory {
            OsrmWalkingDirectionsUseCase(
                geoService = get()
            )
        }

        factory {
            GoogleDrivingDirectionsUseCase(
                geoService = get()
            )
        }

        factory {
            GoogleWalkingDirectionsUseCase(
                geoService = get()
            )
        }

        viewModel {
            MainViewModel(
                networkThread = Schedulers.io(),
                mainThread = AndroidSchedulers.mainThread(),
                osrmDrivingDirectionsUseCase = get(),
                osrmWalkingDirectionsUseCase = get(),
                googleDrivingDirectionsUseCase = get(),
                googleWalkingDirectionsUseCase = get()
            )
        }
    }

    fun get() = mutableListOf(
        appModule
    )
}