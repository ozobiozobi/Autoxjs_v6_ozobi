package com.stardust.autojs.core.ui

import android.content.Context
import android.content.res.Configuration

fun isNightMode(context: Context): Boolean {
    val configuration = context.resources.configuration
    return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}