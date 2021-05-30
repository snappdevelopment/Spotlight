package com.snad.feature.moviedetails.repository

import com.snad.core.persistence.models.CastMember
import com.snad.core.persistence.models.Image
import com.snad.core.persistence.models.LibraryMovie
import com.snad.core.persistence.models.Review
import com.snad.feature.moviedetails.model.Movie
import com.snad.feature.moviedetails.model.Backdrop
import com.snad.feature.moviedetails.model.Review as ApiReview
import com.snad.feature.moviedetails.model.CastMember as ApiCastMember
import java.util.*

internal fun Movie.toLibraryMovie(
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
        backdrops = this.images.backdrops.map { it.toImage() },
        budget = this.budget,
        cast = this.credits.cast.map { it.toCastMember() },
        genres = genres,
        id = this.id,
        imdb_id = this.imdb_id,
        overview = this.overview,
        popularity = this.popularity,
        poster_path = this.poster_path,
        release_date = this.release_date,
        revenue = this.revenue,
        reviews = this.reviews.reviews.map { it.toReview() },
        runtime = this.runtime,
        tagline = this.tagline,
        title = this.title,
        trailer = trailer?.key,
        video = this.video,
        vote_average = this.vote_average,
        vote_count = this.vote_count
    )
}

private fun Backdrop.toImage(): Image {
    return Image(
        aspect_ratio = this.aspect_ratio,
        file_path = this.file_path,
        height = this.height,
        width = this.width,
        iso_639_1 = this.iso_639_1,
        vote_average = this.vote_average,
        vote_count = this.vote_count
    )
}

private fun ApiCastMember.toCastMember(): CastMember {
    return CastMember(
        id = this.id,
        cast_id = this.cast_id,
        credit_id = this.credit_id,
        name = this.name,
        gender = this.gender,
        character = this.character,
        order = this.order,
        profile_path = this.profile_path
    )
}

private fun ApiReview.toReview(): Review {
    return Review(
        id = this.id,
        author = this.author,
        content = this.content,
        url = this.url
    )
}