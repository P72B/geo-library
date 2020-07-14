package de.p72b.geo.util

import java.util.*

abstract class UnitLocale {

    abstract fun fromMeters(meters: Long): String

    companion object {
        private const val LIBERIA_ISO2 = "LR"
        private const val LIBERIA_ISO3 = "LBR"
        private const val USA_ISO2 = "US"
        private const val USA_ISO3 = "USA"
        private const val MYANMAR_ISO2 = "MM"
        private const val MYANMAR_ISO3 = "MMR"

        private val Imperial = object : UnitLocale() {
            override fun fromMeters(meters: Long): String {
                val FEET_IN_MILE: Long = 5280
                val FEET_THRESHOLD: Long = 525 //170 Meters threshold
                val FEET_IN_HUNDRED_YARDS: Long = 300
                val FEET_IN_METER = 3.28084
                val FEET_IN_YARD: Long = 3
                val FEET_UNIT = "feet"
                val YARD_UNIT = "yards"
                val MILE_UNIT = "miles"
                val feet: Double = (meters * FEET_IN_METER)
                return when {
                    feet < FEET_IN_HUNDRED_YARDS -> format("%.0f $FEET_UNIT", feet)
                    feet < FEET_IN_MILE - FEET_THRESHOLD -> format("%.0f $YARD_UNIT", feet / FEET_IN_YARD)
                    feet > FEET_IN_MILE - FEET_THRESHOLD && feet < FEET_IN_MILE + FEET_THRESHOLD -> "1 mile"
                    else -> format("%.1f $MILE_UNIT", feet / FEET_IN_MILE)
                }
            }
        }

        private val Metric = object : UnitLocale() {
            override fun fromMeters(meters: Long): String {
                val mts: Double = meters.toDouble()
                val KM_UNIT = "km"
                val METER_UNIT = "m"

                return when {
                    mts > 1000.0 -> String.format("%.1f $KM_UNIT", (mts / 1000))
                    else -> String.format("%.0f $METER_UNIT", mts)
                }
            }
        }

        private fun format(text: String?, vararg arguments: Any?): String {
            return String.format(Locale.getDefault(), text!!, *arguments)
        }

        val default: UnitLocale
            get() = getFrom(Locale.getDefault())

        fun getFrom(locale: Locale): UnitLocale {
            return when (locale.country.toUpperCase(locale)) {
                USA_ISO2, USA_ISO3,
                LIBERIA_ISO2, LIBERIA_ISO3,
                MYANMAR_ISO2, MYANMAR_ISO3 -> Imperial
                else -> Metric
            }
        }
    }
}