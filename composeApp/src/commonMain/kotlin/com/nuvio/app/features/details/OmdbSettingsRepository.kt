package com.nuvio.app.features.details

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A user-supplied OMDb key (free at omdbapi.com/apikey.aspx) is optional — it takes priority
 * so power users can draw from their own quota instead of everyone sharing the build's baked-in
 * [ImdbEpisodeRatingsConfig.OMDB_API_KEY], whose free tier is capped at 1,000 requests/day.
 */
object OmdbSettingsRepository {
    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private var hasLoaded = false

    fun ensureLoaded() {
        if (hasLoaded) return
        hasLoaded = true
        _apiKey.value = OmdbSettingsStorage.loadApiKey().orEmpty()
    }

    fun setApiKey(value: String) {
        ensureLoaded()
        val trimmed = value.trim()
        if (_apiKey.value == trimmed) return
        _apiKey.value = trimmed
        OmdbSettingsStorage.saveApiKey(trimmed)
    }

    /** The user's own key when set, otherwise the build's baked-in key. */
    internal fun effectiveApiKey(): String {
        ensureLoaded()
        return _apiKey.value.ifBlank { ImdbEpisodeRatingsConfig.OMDB_API_KEY.trim() }
    }
}
