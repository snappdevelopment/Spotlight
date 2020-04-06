package com.snad.spotlight.persistence

import com.snad.spotlight.network.models.Movie
import com.snad.spotlight.persistence.models.LibraryMovie
import java.util.*

fun Movie.toLibraryMovie(
    addedAt: Calendar? = null,
    updatedAt: Calendar? = null,
    hasBeenWatched: Boolean = false
): LibraryMovie {

    val genres = this.genres.joinToString(separator = ", ", limit = 2, truncated = "") { genre ->
        genre.name
    }.dropLastWhile { character ->
        character == ' ' || character == ','
    }

    val trailer = this.videos.videos.firstOrNull { video ->
        video.type == "Trailer" && video.site == "YouTube"
    }

    return LibraryMovie(
        added_at = addedAt,
        updated_at = updatedAt,
        has_been_watched = hasBeenWatched,
        adult = this.adult,
        backdrop_path = this.backdrop_path,
        backdrops = this.images.backdrops,
        budget = this.budget,
        cast = this.credits.cast,
        genres = genres,
        id = this.id,
        imdb_id = this.imdb_id,
        overview = this.overview,
        popularity = this.popularity,
        poster_path = this.poster_path,
        release_date = this.release_date,
        revenue = this.revenue,
        reviews = this.reviews.reviews,
        runtime = this.runtime,
        tagline = this.tagline,
        title = this.title,
        trailer = trailer?.key,
        video = this.video,
        vote_average = this.vote_average,
        vote_count = this.vote_count
    )
}