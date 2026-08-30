package com.nuvio.app.features.details

import platform.Foundation.NSUserDefaults

actual object OmdbEpisodeRatingsStorage {
    private const val payloadKey = "omdb_episode_ratings_cache"

    // Not profile-scoped: IMDb ratings are the same content regardless of which
    // profile is browsing, unlike per-profile preferences elsewhere in this app.
    actual fun loadPayload(): String? =
        NSUserDefaults.standardUserDefaults.stringForKey(payloadKey)

    actual fun savePayload(payload: String) {
        NSUserDefaults.standardUserDefaults.setObject(payload, forKey = payloadKey)
    }
}
