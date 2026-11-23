package com.unreal.solarsite.di

import com.unreal.solarsite.data.remote.ApiClient
import com.unreal.solarsite.data.repository.SitesRepository
import com.unreal.solarsite.presentation.SitesViewModel
import com.unreal.solarsite.presentation.boundary.PolygonBoundaryViewModel
import com.unreal.solarsite.presentation.siteform.SiteFormViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { ApiClient.apiService }
    singleOf(::SitesRepository)
    viewModelOf(::SitesViewModel)
    viewModelOf(::SiteFormViewModel)
    viewModelOf(::PolygonBoundaryViewModel)
}