package com.stardust.autojs.core.pref

import androidx.preference.PreferenceManager;
import com.stardust.app.GlobalAppContext

object Pref {
    private val preferences = GlobalAppContext.get()
        ?.let { PreferenceManager.getDefaultSharedPreferences(it) }
    val isStableModeEnabled: Boolean
        get() {
            return preferences?.getBoolean("key_stable_mode", false) ?: false
        }

    val isGestureObservingEnabled: Boolean
        get() {
            return preferences?.getBoolean("key_gesture_observing", false) ?: false
        }
}