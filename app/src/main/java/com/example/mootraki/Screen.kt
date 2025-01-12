package com.example.mootraki

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Charts : Screen("charts")
    object Affirmations : Screen("affirmations")
}