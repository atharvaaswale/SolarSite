package com.unreal.solarsite.data.remote

import com.unreal.solarsite.data.dao.BasicApiResponse
import com.unreal.solarsite.data.dao.SingleSiteResponse
import com.unreal.solarsite.data.dao.SitesDao
import com.unreal.solarsite.data.dao.UpsertSiteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/api/sites")
    suspend fun getSites(): Response<SitesDao>

    @GET("/api/sites/{id}")
    suspend fun getSiteById(@Path("id") id: String): Response<SingleSiteResponse>

    @POST("/api/sites")
    suspend fun createSite(@Body request: UpsertSiteRequest): Response<SingleSiteResponse>

    @PUT("/api/sites/{id}")
    suspend fun updateSite(@Path("id") id: String, @Body request: UpsertSiteRequest): Response<SingleSiteResponse>

    @DELETE("api/sites/{id}")
    suspend fun deleteSite(
        @Path("id") id: String
    ): Response<BasicApiResponse>
}