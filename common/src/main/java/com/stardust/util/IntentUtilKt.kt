package com.stardust.util

import android.content.Context

object IntentUtilKt {
    fun launchQQ(context: Context): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage("com.tencent.mobileqq")
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}