package com.nuvio.app.features.details

/** Same single-JSON-payload shape as [com.nuvio.app.features.home.HomeCatalogSettingsStorage]. */
internal expect object OmdbEpisodeRatingsStorage {
    fun loadPayload(): String?
    fun savePayload(payload: String)
}
