package com.hd.presentation.main

sealed class MainNavigation {
    data class ShowPermissionsActivity(val doNotAskVisibility: Int) : MainNavigation()
}