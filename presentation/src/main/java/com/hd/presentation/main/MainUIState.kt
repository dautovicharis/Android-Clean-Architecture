package com.hd.presentation.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class MainUIState(
    @StringRes open val text: Int,
    @DrawableRes open val icon: Int
) {
    data class AskUser(
        override val text: Int,
        override val icon: Int
    ) : MainUIState(text, icon)

    data class Granted(
        override val text: Int,
        override val icon: Int
    ) : MainUIState(text, icon)

    data class MissingPermissions(
        override val text: Int,
        override val icon: Int
    ) : MainUIState(text, icon)
}