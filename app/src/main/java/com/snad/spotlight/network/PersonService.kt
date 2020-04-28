package com.snad.spotlight.network

import com.snad.spotlight.network.models.Person
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonService {
    @GET("person/{person_id}")
    suspend fun getPerson(
        @Path("person_id") id: Int,
        @Query("append_to_response") appendToResponse: String
    ): Person
}