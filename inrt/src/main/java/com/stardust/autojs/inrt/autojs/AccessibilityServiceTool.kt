package com.stardust.autojs.inrt.autojs

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import com.google.android.accessibility.selecttospeak.SelectToSpeakService
import com.stardust.app.GlobalAppContext
import com.stardust.autojs.core.util.ProcessShell
import com.stardust.view.accessibility.AccessibilityServiceUtils.isAccessibilityServiceEnabled
import com.stardust.view.accessibility.AccessibilityService
import java.util.Locale

/**
 * Created by Stardust on 2017/7/1.
 */

object AccessibilityServiceTool {

    private const val cmd = "enabled=$(settings get secure enabled_accessibility_services)\n" +
            "pkg=%s\n" +
            "if [[ \$enabled == *\$pkg* ]]\n" +
            "then\n" +
            "echo already_enabled\n" +
            "else\n" +
            "enabled=\$pkg:\$enabled\n" +
            "settings put secure enabled_accessibility_services \$enabled\n" +
            "fi\n" +
            "settings put secure accessibility_enabled 1"

    fun enableAccessibilityServiceByRoot(context: Context, accessibilityService: Class<out AccessibilityService>): Boolean {
        val serviceName = context.packageName + "/" + accessibilityService.name
        return try {
            TextUtils.isEmpty(ProcessShell.execCommand(String.format(Locale.getDefault(), cmd, serviceName), true).error)
        } catch (ignored: Exception) {
            false
        }

    }

    fun enableAccessibilityServiceByRootAndWaitFor(context: Context, timeOut: Long): Boolean {
        if (enableAccessibilityServiceByRoot(context, AccessibilityService::class.java)) {
            AccessibilityService.waitForEnabled(timeOut)
            return true
        }
        return false
    }

    fun goToAccessibilitySetting() {
        GlobalAppContext.get()?.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        return isAccessibilityServiceEnabled(context, SelectToSpeakService::class.java)
    }

}
