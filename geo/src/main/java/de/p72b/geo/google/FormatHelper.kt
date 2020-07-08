package de.p72b.geo.google

internal object FormatHelper {
    fun getFormattedAddress(formattedAddress: String): String {
        val split =
            formattedAddress.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (split.isEmpty()) {
            formattedAddress
        } else {
            split[0]
        }
    }
}