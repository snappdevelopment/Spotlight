package com.snad.spotlight.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Credits(
    val cast: List<CastMember>,
    val crew: List<CrewMember>
)

@JsonClass(generateAdapter = true)
class CastMember(
    val cast_id: Int,
    val character: String,
    val credit_id: String,
    val gender: Int?,
    val id: Int,
    val name: String,
    val order: Int,
    val profile_path: String?
)

@JsonClass(generateAdapter = true)
class CrewMember(
    val credit_id: String,
    val department: String,
    val gender: Int?,
    val id: Int,
    val job: String,
    val name: String,
    val profile_path: String?
)