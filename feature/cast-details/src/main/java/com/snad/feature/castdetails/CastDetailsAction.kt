package com.snad.feature.castdetails

internal sealed class CastDetailsAction

internal data class LoadCastDetails(val id: Int): CastDetailsAction()