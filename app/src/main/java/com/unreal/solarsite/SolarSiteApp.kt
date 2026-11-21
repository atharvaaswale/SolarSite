package com.unreal.solarsite

import android.app.Application
import com.unreal.solarsite.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SolarSiteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SolarSiteApp)
            modules(appModule)
        }
    }
}