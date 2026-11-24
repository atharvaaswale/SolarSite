package com.unreal.solarsite.data.repository

import com.unreal.solarsite.data.dao.SitesDao
import com.unreal.solarsite.data.dao.UpsertSiteRequest
import com.unreal.solarsite.data.remote.ApiService

class SitesRepository(private val api: ApiService) {
    suspend fun getSites(): Result<List<SitesDao.Data>> = runCatching {
        val res = api.getSites()
        val body = res.body()
        if(res.isSuccessful && res.body() != null && body!!.success) body.data
        else error("Failed to load sites (${res.code()})")
    }

    suspend fun getRecentSites(limit: Int = 5): Result<List<SitesDao.Data>> = runCatching {
        val response = api.getRecentSites(limit = limit)
        val body = response.body()
        if (!response.isSuccessful || body == null || !body.success) {
            throw Exception("Failed to fetch recent sites (${response.code()})")
        }
        body.data ?: emptyList()
    }

        suspend fun getSiteById(id: String): Result<SitesDao.Data> = runCatching {
        val response = api.getSiteById(id)
        val body = response.body()
        if (!response.isSuccessful || body == null || !body.success || body.data == null) {
            throw Exception(body?.message ?: "Failed to load site (${response.code()})")
        }
        body.data
    }

    suspend fun createSite(request: UpsertSiteRequest): Result<SitesDao.Data> = runCatching {
        val response = api.createSite(request)
        val body = response.body()
        if (!response.isSuccessful || body == null || !body.success || body.data == null) {
            throw Exception(body?.message ?: "Failed to create site (${response.code()})")
        }
        body.data
    }

    suspend fun updateSite(id: String, request: UpsertSiteRequest): Result<SitesDao.Data> = runCatching {
        val response = api.updateSite(id, request)
        val body = response.body()
        if (!response.isSuccessful || body == null || !body.success || body.data == null) {
            throw Exception(body?.message ?: "Failed to update site (${response.code()})")
        }
        body.data
    }

    suspend fun deleteSite(id: String): Result<Unit> = runCatching {
        val response = api.deleteSite(id)
        val body = response.body()
        if (!response.isSuccessful || body == null || !body.success) {
            throw Exception(body?.message ?: "Failed to delete site (${response.code()})")
        }
    }
}