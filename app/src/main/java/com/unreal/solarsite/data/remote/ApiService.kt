package com.unreal.solarsite.data.remote

import com.unreal.solarsite.data.dao.SitesDao
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/api/sites")
    suspend fun getSites(): Response<SitesDao>
}