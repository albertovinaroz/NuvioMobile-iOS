package com.nuvio.app.features.details

import co.touchlab.kermit.Logger
import com.nuvio.app.features.addons.httpGetText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Replaces TMDB's per-episode vote average with IMDb's own rating, via OMDb's bulk
 * per-season endpoint (`?i=ttXXXX&Season=N`) — one request per season instead of one per
 * episode, matched to how [com.nuvio.app.features.tmdb.TmdbMetadataService] already fetches
 * TMDB season details in parallel.
 */
internal object OmdbEpisodeRatingsService {
    private val log = Logger.withTag("OmdbEpisodeRatings")
    private val json = Json { ignoreUnknownKeys = true }
    private val imdbIdRegex = Regex("tt\\d+")
    private val seasonCache = mutableMapOf<String, Map<Int, Double>>()

    val hasApiKey: Boolean
        get() = ImdbEpisodeRatingsConfig.OMDB_API_KEY.isNotBlank()

    fun extractImdbId(vararg candidates: String?): String? =
        candidates.firstNotNullOfOrNull { candidate ->
            candidate?.let(imdbIdRegex::find)?.value
        }

    /** Returns (season, episode) -> IMDb rating, only for episodes OMDb actually rated. */
    suspend fun fetchRatings(
        imdbId: String,
        seasonNumbers: List<Int>,
    ): Map<Pair<Int, Int>, Double> = withContext(Dispatchers.Default) {
        val apiKey = ImdbEpisodeRatingsConfig.OMDB_API_KEY.trim()
        if (apiKey.isBlank()) return@withContext emptyMap()

        val normalizedSeasons = seasonNumbers.distinct().sorted()
        if (normalizedSeasons.isEmpty()) return@withContext emptyMap()

        val perSeason = coroutineScope {
            normalizedSeasons.map { season ->
                async { season to fetchSeason(imdbId = imdbId, season = season, apiKey = apiKey) }
            }.awaitAll()
        }

        perSeason.flatMap { (season, episodeRatings) ->
            episodeRatings.map { (episode, rating) -> (season to episode) to rating }
        }.toMap()
    }

    private suspend fun fetchSeason(
        imdbId: String,
        season: Int,
        apiKey: String,
    ): Map<Int, Double> {
        val cacheKey = "$imdbId:$season"
        seasonCache[cacheKey]?.let { return it }

        val url = "https://www.omdbapi.com/?i=$imdbId&Season=$season&apikey=$apiKey"
        val response = runCatching {
            json.decodeFromString<OmdbSeasonResponse>(httpGetText(url))
        }.onFailure { error ->
            log.w { "OMDb season request failed for $imdbId season $season: ${error.message}" }
        }.getOrNull() ?: return emptyMap()

        if (!response.response.equals("True", ignoreCase = true)) return emptyMap()

        val ratings = response.episodes.orEmpty()
            .mapNotNull { episode ->
                val episodeNumber = episode.episode?.toIntOrNull() ?: return@mapNotNull null
                val rating = episode.imdbRating
                    ?.takeIf { it.isNotBlank() && !it.equals("N/A", ignoreCase = true) }
                    ?.toDoubleOrNull()
                    ?.takeIf { it > 0.0 }
                    ?: return@mapNotNull null
                episodeNumber to rating
            }
            .toMap()

        if (ratings.isNotEmpty()) {
            seasonCache[cacheKey] = ratings
        }
        return ratings
    }
}

@Serializable
private data class OmdbSeasonResponse(
    @SerialName("Response") val response: String? = null,
    @SerialName("Episodes") val episodes: List<OmdbEpisodeEntry>? = null,
)

@Serializable
private data class OmdbEpisodeEntry(
    @SerialName("Episode") val episode: String? = null,
    @SerialName("imdbRating") val imdbRating: String? = null,
)
