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