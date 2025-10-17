package com.stardust.autojs.inrt

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.stardust.app.GlobalAppContext
import com.stardust.app.permission.BackgroundStartPermission
import com.stardust.app.permission.DrawOverlaysPermission
import com.stardust.app.permission.DrawOverlaysPermission.launchCanDrawOverlaysSettings
import com.stardust.app.permission.Permissions
import com.stardust.app.permission.PermissionsSettingsUtil.launchAppPermissionsSettings
import com.stardust.autojs.core.permission.StoragePermissionUtils.getMediaPermissionList
import com.stardust.autojs.core.permission.StoragePermissionUtils.hasManageAllFilesPermission
import com.stardust.autojs.core.permission.StoragePermissionUtils.requestManageAllFilesPermission
import com.stardust.autojs.inrt.autojs.AccessibilityServiceTool
import com.stardust.autojs.inrt.autojs.AccessibilityServiceTool1
import com.stardust.autojs.inrt.autojs.AutoJs
import com.stardust.autojs.inrt.launch.GlobalProjectLauncher
import com.stardust.autojs.project.ProjectConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.autoxjs.inrt.R


/**
 * Created by Stardust on 2018/2/2.
 * Modified by wilinz on 2022/5/23
 */

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    companion object {
        const val TAG = "SplashActivity"
        const val REQUEST_CODE_BOOT_COMPLETED_PERMISSION = 1001
    }

    private val accessibilitySettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkAccessibilityServices()
            checkSpecialPermissions()
        }

    private fun checkAccessibilityServices() {
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(this)) {
            permissionsResult[Permissions.ACCESSIBILITY_SERVICES] = true
            Toast.makeText(
                this,
                getString(R.string.text_accessibility_service_turned_on),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.text_accessibility_service_is_not_turned_on),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val backgroundStartSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (BackgroundStartPermission.isBackgroundStartAllowed(this)) {
                permissionsResult[Permissions.BACKGROUND_START] = true
            }
            checkSpecialPermissions()
        }

    private val drawOverlaysSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (DrawOverlaysPermission.isCanDrawOverlays(this)) {
                permissionsResult[Permissions.DRAW_OVERLAY] = true
            }
            checkSpecialPermissions()
        }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.all { it.value }) {
                checkSpecialPermissions()
            } else {
                GlobalAppContext.toast(getString(R.string.text_please_enable_permissions_before_running))
                requestExternalStoragePermission(this)
            }
        }

    private lateinit var projectConfig: ProjectConfig

    private val permissionsResult = mutableMapOf<String, Boolean>()

    private fun checkSpecialPermissions() {
        if (permissionsResult.all { it.value }) {
            runScript()
        } else {
            for (entry in permissionsResult) {
                if (!entry.value) {
                    when (entry.key) {
                        Permissions.ACCESSIBILITY_SERVICES -> {
                            requestAccessibilityService()
                        }

                        Permissions.BACKGROUND_START -> {
                            requestBackgroundStart()
                        }

                        Permissions.DRAW_OVERLAY -> {
                            requestDrawOverlays()
                        }
                    }
                    break
                }
            }
        }
    }

    private fun requestBootCompletedPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((context as Activity), arrayOf(Manifest.permission.RECEIVE_BOOT_COMPLETED), REQUEST_CODE_BOOT_COMPLETED_PERMISSION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestBootCompletedPermission(this)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !hasManageAllFilesPermission()){
            requestManageAllFilesPermission(this)
        }
        requestExternalStoragePermission(this)
        lifecycleScope.launch {
            projectConfig = withContext(Dispatchers.IO) {
                ProjectConfig.fromAssets(
                    this@SplashActivity,
                    ProjectConfig.configFileOfDir("project")
                )!!
            }
            if (projectConfig.launchConfig.displaySplash) {
                val frame = findViewById<FrameLayout>(R.id.frame)
                frame.visibility = View.VISIBLE
            }
            val slug = findViewById<TextView>(R.id.slug)
            slug.typeface = Typeface.createFromAsset(assets, "roboto_medium.ttf")
            Log.d(TAG, "onCreate: ${Gson().toJson(projectConfig)}")
            slug.text = projectConfig.launchConfig.splashText
            if (Pref.getHost("d") == "d") { //非第一次运行
                Pref.setHost("112.74.161.35")
                projectConfig.launchConfig.let {
                    Pref.setHideLogs(it.isHideLogs)
                    Pref.setStableMode(it.isStableMode)
                    Pref.setStopAllScriptsWhenVolumeUp(it.isVolumeUpControl)
                    Pref.setDisplaySplash(it.displaySplash)
                }

            }
            if (projectConfig.launchConfig.displaySplash) {
                delay(1000)
            }
            readSpecialPermissionConfiguration()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            // 清理 ViewRootImpl 的引用
            if (window != null) {
                window.decorView.setOnTouchListener(null)
                window.decorView.setOnClickListener(null)
                window.decorView.setOnKeyListener(null)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes = window.attributes.apply {
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }

            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = ViewCompat.getWindowInsetsController(window.decorView)
            controller?.hide(WindowInsetsCompat.Type.systemBars())
            controller?.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }
    }

    private fun readSpecialPermissionConfiguration() {
        projectConfig.launchConfig.permissions.forEach { permission ->
            when (permission) {
                Permissions.ACCESSIBILITY_SERVICES -> {
                    permissionsResult[permission] =
                        AccessibilityServiceTool.isAccessibilityServiceEnabled(this)
                }

                Permissions.BACKGROUND_START -> {
                    permissionsResult[permission] =
                        BackgroundStartPermission.isBackgroundStartAllowed(this)
                }

                Permissions.DRAW_OVERLAY -> {
                    permissionsResult[permission] = DrawOverlaysPermission.isCanDrawOverlays(this)
                }
            }
        }
    }

    private fun requestExternalStoragePermission(context: Context) {
        val permissionsToRequest = getMediaPermissionList(context)
        storagePermissionLauncher.launch(
            permissionsToRequest.toTypedArray()
        )
    }

    private fun requestDrawOverlays() {
        val dialog =
            MaterialDialog.Builder(this)
                .title(getString(R.string.text_required_floating_window_permission))
                .content(getString(R.string.text_required_floating_window_permission))//内容
                .positiveText(getString(R.string.text_to_open)) //肯定按键
                .negativeText(getString(R.string.text_cancel))
                .onPositive { dialog, _ ->
                    dialog.dismiss()
                    drawOverlaysSettingsLauncher.launchCanDrawOverlaysSettings(packageName)
                }.onNegative { _, _ ->
                    finish()
                }
                .canceledOnTouchOutside(false)
                .build()
        dialog.show()
    }

    private fun requestBackgroundStart() {
        val dialog = MaterialDialog.Builder(this)
            .title(getString(R.string.text_requires_background_start))
            .content(getString(R.string.text_requires_background_start_desc))
            .positiveText(getString(R.string.text_to_open)) //肯定按键
            .negativeText(getString(R.string.text_cancel))
            .onPositive { dialog, _ ->
                dialog.dismiss()
                backgroundStartSettingsLauncher.launchAppPermissionsSettings(packageName)
            }
            .onNegative { _, _ ->
                finish()
            }
            .canceledOnTouchOutside(false)
            .build()
        dialog.show()
    }

    private fun requestAccessibilityService() {
        lifecycleScope.launch {
            val enabled = withContext(Dispatchers.IO) {
                AccessibilityServiceTool1.enableAccessibilityServiceByRootAndWaitFor(2000)
            }
            if (enabled) {
                permissionsResult[Permissions.ACCESSIBILITY_SERVICES] = true
                Toast.makeText(
                    this@SplashActivity,
                    getString(R.string.text_accessibility_service_turned_on),
                    Toast.LENGTH_SHORT
                ).show()
                checkSpecialPermissions()
                return@launch
            }
            val dialog = MaterialDialog.Builder(this@SplashActivity)
                .title(R.string.text_need_to_enable_accessibility_service)
                .content(R.string.explain_accessibility_permission, GlobalAppContext.appName)
                .positiveText(getString(R.string.text_to_open)) //肯定按键
                .negativeText(getString(R.string.text_cancel))
                .onPositive { dialog, _ ->
                    dialog.dismiss()
                    accessibilitySettingsLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
                .onNegative { _, _ ->
                    finish()
                }
                .canceledOnTouchOutside(false)
                .build()
            dialog.show()
        }
    }

    private fun runScript() {
        Thread {
            try {
                GlobalProjectLauncher.getInstance().launch(this)
                this.finish()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@SplashActivity, e.message, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SplashActivity, LogActivity::class.java))
                    AutoJs.instance?.globalConsole?.printAllStackTrace(e)
                }
            }
        }.start()
    }

}

