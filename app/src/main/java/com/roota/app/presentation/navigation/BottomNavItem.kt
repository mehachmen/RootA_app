package com.roota.app.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.roota.app.R

enum class BottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int
) {
    Projects("projects", R.string.nav_projects, R.drawable.ic_nav_projects),
    Progress("global_progress", R.string.nav_progress, R.drawable.ic_nav_progress),
    Settings("settings", R.string.nav_settings, R.drawable.ic_nav_projects) // settings uses material icon in composable
}

val mainTabRoutes = setOf("projects", "global_progress", "settings")

fun shouldShowBottomBar(route: String?): Boolean {
    if (route == null) return false
    return route in mainTabRoutes
}
