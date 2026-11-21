package com.unreal.solarsite.data.repository

import com.unreal.solarsite.data.dao.SitesDao
import com.unreal.solarsite.data.remote.ApiService

class SitesRepository(private val api: ApiService) {
    suspend fun getSites(): Result<List<SitesDao.Data>> = runCatching {
        val res = api.getSites()
        val body = res.body()
        if(res.isSuccessful && res.body() != null && body!!.success) body.data
        else error("Failed to load sites (${res.code()})")
    }
}