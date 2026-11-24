package com.unreal.solarsite.presentation.dashboard

import com.unreal.solarsite.data.dao.SitesDao

data class DashboardUiState(
    val isLoading: Boolean = true,
    val recentSites: List<SitesDao.Data> = emptyList(),
    val error: String? = null
)