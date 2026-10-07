package com.unreal.solarsite.presentation.navigation

import androidx.annotation.DrawableRes
import com.unreal.solarsite.R

enum class TopLevelDestination(
    val route: String,
    val label: String,
    @DrawableRes val iconRes: Int
) {
    HOME("home", "Home", R.drawable.ic_home),
    SITES("sites", "Sites", R.drawable.ic_nav_sites),
    SYNC("sync", "Sync", R.drawable.ic_nav_sync)
}

// Inner screens (routes)
object AppDestinations {
    const val SITE_FORM = "site_form?siteId={siteId}"
    const val POLYGON_MAP = "polygon_map"

    fun createSiteFormRoute(siteId: String? = null): String {
        return if (siteId != null) "site_form?siteId=$siteId" else "site_form"
    }
}