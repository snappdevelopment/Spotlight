package com.snad.feature.search

internal sealed class SearchAction

internal data class SearchMovies(val title: String): SearchAction()