package de.p72b.geo.demo.util

import de.p72b.geo.GeoService

class OsrmGeoService(
    baseUrl: String,
    channel: String
) : GeoService(
    baseUrl,
    channel
)

class GoogleGeoService(
    baseUrl: String,
    channel: String,
    key: String
) : GeoService(
    baseUrl,
    channel,
    key
)