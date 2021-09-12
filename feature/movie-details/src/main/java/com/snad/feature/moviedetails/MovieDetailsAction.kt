package com.snad.feature.moviedetails

internal sealed class MovieDetailsAction

internal data class LoadMovie(val id: Int): MovieDetailsAction()
internal object CtaClicked: MovieDetailsAction()
internal object WatchedClicked: MovieDetailsAction()