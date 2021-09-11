package com.snad.feature.library

import com.snad.core.persistence.models.LibraryMovie

internal sealed class LibraryAction

internal object LoadMovies: LibraryAction()
internal data class DeleteMovie(val libraryMovie: LibraryMovie): LibraryAction()
internal data class UpdateMovie(val libraryMovie: LibraryMovie): LibraryAction()