package de.p72b.geo

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object Geo {

    @Volatile
    lateinit var appContext: Context

    fun setContext(context: Context) {
        appContext = context
    }
}