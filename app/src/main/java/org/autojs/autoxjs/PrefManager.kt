package org.autojs.autoxjs

import android.content.Context

/**
 * Created by ozobi - 2025/03/09
 */

object PrefManager {
    private const val PREF_NAME = "app_prefs"
    private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    private const val APP_VERSION = "AppVersion"

    fun isFirstTimeLaunch(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }

    fun setFirstTimeLaunch(context: Context, isFirstTime: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).apply()
    }

    fun isVersionChanged(context: Context):Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        val savedVersion = sharedPreferences.getInt(APP_VERSION, -1)
        if (currentVersion != savedVersion) {
            editor.putInt(APP_VERSION, currentVersion) // 更新保存的应用版本
            editor.apply()
            return true
        }
        return false
    }
}