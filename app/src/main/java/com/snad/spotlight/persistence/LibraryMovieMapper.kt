package com.snad.spotlight.persistence

import com.snad.spotlight.network.models.Movie
import com.snad.spotlight.persistence.models.LibraryMovie
import java.util.Calendar

fun Movie.toLibraryMovie(
    addedAt: Calendar = Calendar.getInstance(),
    hasBeenWatched: Boolean = false
): LibraryMovie {
    val genres = this.genres.joinToString(separator = ",", limit = 3, truncated = "") {genre ->
        genre.name
    }
    return LibraryMovie(
        added_at = addedAt,
        has_been_watched = hasBeenWatched,
        adult = this.adult,
        backdrop_path = this.backdrop_path,
        budget = this.budget,
        genres = genres,
        id = this.id,
        imdb_id = this.imdb_id,
        overview = this.overview,
        popularity = this.popularity,
        poster_path = this.poster_path,
        release_date = this.release_date,
        revenue = this.revenue,
        runtime = this.runtime,
        tagline = this.tagline,
        title = this.title,
        video = this.video,
        vote_average = this.vote_average,
        vote_count = this.vote_count)
}