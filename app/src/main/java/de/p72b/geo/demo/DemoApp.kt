package de.p72b.geo.demo

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DemoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DemoApp)
            modules(DependencyGraph.get())
        }
    }
}