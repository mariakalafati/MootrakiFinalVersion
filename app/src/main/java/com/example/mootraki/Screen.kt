package com.example.mootraki

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Charts : Screen("charts")
    object Breathing : Screen("breathing")
    object Affirmations : Screen("affirms")
}
