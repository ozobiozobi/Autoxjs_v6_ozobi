package org.autojs.autoxjs.ui.main.drawer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.PreferenceManager
import coil.compose.rememberAsyncImagePainter
import com.stardust.app.DialogUtils
import com.stardust.app.GlobalAppContext
import com.stardust.app.isOpPermissionGranted
import com.stardust.app.permission.DrawOverlaysPermission
import com.stardust.app.permission.DrawOverlaysPermission.launchCanDrawOverlaysSettings
import com.stardust.app.permission.PermissionsSettingsUtil
import com.ozobi.shizuku.OzobiShizuku
import com.ozobi.voiceassistant.OzobiAssistInteractionService
import com.stardust.autojs.runtime.DeviceAdminReceiverMsg
import com.stardust.enhancedfloaty.FloatyService
import com.stardust.notification.NotificationListenerService
import com.stardust.toast
import com.stardust.util.ClipboardUtil
import com.stardust.util.IntentUtil
import com.stardust.util.NetworkUtils.getWifiIPv4
import com.stardust.util.Ozobi
import com.stardust.view.accessibility.AccessibilityService
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import io.noties.markwon.Markwon
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.autoxjs.Pref
import org.autojs.autoxjs.PrefManager
import org.autojs.autoxjs.R
import org.autojs.autoxjs.autojs.AutoJs
import org.autojs.autoxjs.devplugin.DevPlugin
import org.autojs.autoxjs.external.foreground.ForegroundService
import org.autojs.autoxjs.network.ozobi.DocsServiceAddress
import org.autojs.autoxjs.network.ozobi.KtorDocsService
import org.autojs.autoxjs.ozobi.ui.LoopingVerticalMove
import org.autojs.autoxjs.tool.AccessibilityServiceTool
import org.autojs.autoxjs.tool.WifiTool
import org.autojs.autoxjs.ui.build.MyTextField
import org.autojs.autoxjs.ui.common.OperationDialogBuilder
import org.autojs.autoxjs.ui.compose.theme.AutoXJsTheme
import org.autojs.autoxjs.ui.compose.util.getFitRandomColor
import org.autojs.autoxjs.ui.compose.widget.MyAlertDialog1
import org.autojs.autoxjs.ui.compose.widget.MyIcon
import org.autojs.autoxjs.ui.compose.widget.MySwitch
import org.autojs.autoxjs.ui.floating.FloatyWindowManger
import org.autojs.autoxjs.ui.settings.SettingsActivity
import org.joda.time.DateTimeZone
import org.joda.time.Instant


private const val TAG = "DrawerPage"
private const val URL_DEV_PLUGIN = "https://github.com/aiselp/Auto.js-VSCode-Extension"

//private const val PROJECT_ADDRESS = "https://github.com/aiselp/AutoX"
private const val PROJECT_ADDRESS = "https://github.com/ozobiozobi/Autoxjs_v6_ozobi"

//private const val DOWNLOAD_ADDRESS = "https://github.com/aiselp/AutoX/releases"
private const val DOWNLOAD_ADDRESS = "https://github.com/ozobiozobi/Autoxjs_v6_ozobi/releases"
private const val FEEDBACK_ADDRESS = "https://github.com/aiselp/AutoX/issues"
private const val DONATION_PAGE_ADDRESS =
    "https://ozobiozobi.github.io/Autox_ozobi_Docs/doc/overview.html"
private const val V1_DOC_COMMUNITY_ADDRESS = "http://bmxwzsq.kesug.com/v1"
private const val OZOBI_SUBFIX = "_ozobi"
private const val MODIFICATION_SINCE = "2024-10-01"

private var alwaysTryToConnectState = false
private var isFirstTime = true
private lateinit var devicePolicyManager: DevicePolicyManager
private lateinit var componentName: ComponentName
private val modification_since_timestamp =
    Ozobi.dateTimeToTimestamp(MODIFICATION_SINCE, "yyyy-MM-dd")

//
@Composable
fun DrawerPage() {
    val context = LocalContext.current
    if (isFirstTime) {
        isFirstTime = false
    } else {
        startUpCheck()
    }
    devicePolicyManager = com.stardust.autojs.runtime.DevicePolicyManager.devicePolicyManager
    componentName = com.stardust.autojs.runtime.DevicePolicyManager.componentName
    rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )
        Box(Modifier.weight(1f)) {
            Column(Modifier.padding(top = 100.dp)) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
                    SwitchClassifyTittle("权限")
                    ShizukuSwitch()
                    NotificationUsageRightSwitch()
                    UsageStatsPermissionSwitch()
//            DeviceManagerSwitch()
//            VoiceAssistantSwitch()
//            StableModeSwitch()
                    SwitchClassifyTittle(text = stringResource(id = R.string.text_service))
                    AccessibilityServiceSwitch()
                    ForegroundServiceSwitch()
                    DocsServiceSwitch()

                    SwitchClassifyTittle("连接")
                    ConnectComputerSwitch()
                    AlwaysTryToConnect()
                    USBDebugSwitch()

                    SwitchClassifyTittle("功能")
                    FloatingWindowSwitch()
                    VolumeDownControlSwitch()
                    EditFloatySwitch()

                    SetDoneCaptureNotify()
                    LayoutInsWaitForCaptureSwitch()
                    LayoutInsDelayCaptureSwitch()
                    LayoutInsScreenshotSwitch()
                    LayoutInsRefreshSwitch()
                    LayoutInsSelectWindowSwitch()

                    // <
//            nightModeSwitch()
                    showModificationDetailsButton()
                    DonationPage(context)
                    CommunityWebsite(context)
                    ProjectAddress(context)
                    DownloadLink(context)


                    SwitchTimedTaskScheduler()
                    AppDetailsSettings(context)
                }
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(AutoXJsTheme.colors.divider)
                )
                BottomButtons()
                Spacer(
                    modifier = Modifier
                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }
            LoopingVerticalMove(
                modifier = Modifier.fillMaxWidth(),
                targetValue = 30f,
                duration = 2300
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.autojs_logo1),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                )
            }
        }

    }
}

@Composable
fun CommunityWebsite(context: Context) {
    TextButton(onClick = {
        IntentUtil.browse(
            context,
            V1_DOC_COMMUNITY_ADDRESS
        )
    }) {
        Text(text = stringResource(R.string.bmx_text_community_website))
    }
}

@Composable
fun DonationDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    imageResId: Int,
    contentText: String,
    linkText: String,
    linkUrl: String
) {
    val randomColor = Color(getFitRandomColor(isNightMode()))
    if (showDialog) {
        var timeDifference =
            calculateTimeDifference(modification_since_timestamp, System.currentTimeMillis())
        var elapseString by remember { mutableStateOf(timeDifference.toString()) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(10000)
                timeDifference = calculateTimeDifference(
                    modification_since_timestamp,
                    System.currentTimeMillis()
                )
                elapseString = timeDifference.toString()
            }
        }
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorResource(R.color.background)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$MODIFICATION_SINCE ~ 能走多远，且看诸位",
                        style = TextStyle(color = randomColor)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = elapseString,
                        style = TextStyle(color = randomColor)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    // 显示图片
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Dialog Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // 显示文字
                    Text(
                        text = contentText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 点击跳转的链接文字
                    val context = LocalContext.current
                    Text(
                        text = linkText,
                        color = colorResource(R.color.colorPrimary),
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                                context.startActivity(intent)
                            }
                    )
                }
            }
        }
    }
}

data class TimeDifference(val years: Int, val days: Int) {
    override fun toString(): String {
        return "$years 年 $days 天"
    }
}

fun calculateTimeDifference(from: Long, to: Long): TimeDifference {
    val difference = to - from
    val days = difference / 24 / 60 / 60 / 1000
    val years = days / 365

    return TimeDifference(
        years = years.toInt(),
        days = (days % 365).toInt()
    )
}

@Composable
fun ShowDonationDialog(onDismiss: () -> Unit) {
    var showDialog by remember { mutableStateOf(true) }
    DonationDialog(
        showDialog = showDialog,
        onDismissRequest = {
            onDismiss()
            showDialog = false
        },
        imageResId = R.drawable.qrcode_924401464,
        contentText = "为魔改充电(需要加入QQ群)\n备注可以指定充电的开发者、版本或功能\n(没有备注则默认充电时的最新版)",
        linkText = "github 船员名单",
        linkUrl = DONATION_PAGE_ADDRESS
    )
}

@Composable
fun DonationPage(context: Context) {
    var showContent by remember { mutableStateOf(PrefManager.isVersionChanged(context)) }
    TextButton(onClick = {
        showContent = true
    }) {
        Text(text = stringResource(R.string.ozobi_text_donation_page))
    }
    if (showContent) {
        ShowDonationDialog(onDismiss = { showContent = false })
    }
}

@Composable
private fun SwitchClassifyTittle(text: String) {
    var color = Color.Black
    if (isNightMode()) {
        color = Color.White
    }
    Text(
        text = text,
        style = TextStyle(color = color, fontStyle = FontStyle.Italic),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 0.dp),
        fontSize = 16.sp
    )
}

@Composable
fun isNightMode(): Boolean {
//    val context = LocalContext.current
//    return PreferenceManager.getDefaultSharedPreferences(context)

//    return isSystemInDarkTheme()
    return (LocalContext.current.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

fun isNightModeNormal(context: Context?): Boolean {
    if (context == null) {
        return false
    }
    return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

@Composable
private fun AppDetailsSettings(context: Context) {
    TextButton(onClick = {
        context.startActivity(PermissionsSettingsUtil.getAppDetailSettingIntent(context.packageName))
    }) {
        Text(text = stringResource(R.string.text_app_detail_settings))
    }
}

@Composable
private fun Feedback(context: Context) {
    TextButton(onClick = {
        IntentUtil.browse(
            context,
            FEEDBACK_ADDRESS
        )
    }) {
        Text(text = stringResource(R.string.text_issue_report))
    }
}

@Composable
private fun DownloadLink(context: Context) {
    TextButton(onClick = {
        IntentUtil.browse(
            context,
            DOWNLOAD_ADDRESS
        )
    }) {
        Text(text = stringResource(R.string.text_app_download_link) + OZOBI_SUBFIX)
    }
}

@Composable
private fun ProjectAddress(context: Context) {
    TextButton(onClick = {
        IntentUtil.browse(
            context,
            PROJECT_ADDRESS
        )
    }) {
        Text(text = stringResource(R.string.text_project_link) + OZOBI_SUBFIX)
    }
}

@Composable
private fun CheckForUpdate(model: DrawerViewModel = viewModel()) {
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var enabled by rememberSaveable {
        mutableStateOf(true)
    }
    model.githubReleaseInfo

    TextButton(
        enabled = enabled,
        onClick = {
            enabled = false
            model.checkUpdate(
                onUpdate = {
                    showDialog = true
                },
                onComplete = {
                    enabled = true
                },
            )
        }
    ) {
        Text(text = stringResource(R.string.text_check_for_updates))
    }
    if (showDialog && model.githubReleaseInfo != null) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(
                        id = R.string.text_new_version2,
                        model.githubReleaseInfo!!.name
                    )
                )
            },
            text = {
                val date = rememberSaveable {
                    Instant.parse(model.githubReleaseInfo!!.createdAt)
                        .toDateTime(DateTimeZone.getDefault())
                        .toString("yyyy-MM-dd HH:mm:ss")
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = stringResource(id = R.string.text_release_date, date))
                    AndroidView(
                        factory = { context ->
                            TextView(context).apply {
                                val content =
                                    model.githubReleaseInfo!!.body.trim().replace("\r\n", "\n")
                                        .replace("\n", "  \n")
                                val markdwon = Markwon.builder(context).build()
                                markdwon.setMarkdown(this, content)
                            }
                        },
                        update = {

                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text(text = stringResource(id = R.string.text_cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    model.downloadApk()
                }) {
                    Text(text = stringResource(id = R.string.text_download))
                }
            })
    }
}

@Composable
private fun BottomButtons() {
    val context = LocalContext.current
    var lastBackPressedTime = remember {
        0L
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            modifier = Modifier.weight(1f),
            onClick = {
                context.startActivity(
                    Intent(
                        context,
                        SettingsActivity::class.java
                    )
                )
            },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onBackground)
        ) {
            MyIcon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                nightMode = isNightMode()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.text_setting))
        }
        TextButton(
            modifier = Modifier.weight(1f), onClick = {
                val currentTime = System.currentTimeMillis()
                val interval = currentTime - lastBackPressedTime
                if (interval > 2000) {
                    lastBackPressedTime = currentTime
                    Toast.makeText(
                        context,
                        context.getString(R.string.text_press_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                } else exitCompletely(context)
            },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onBackground)
        ) {
            MyIcon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                nightMode = isNightMode()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.text_exit))
        }
    }
}

fun exitCompletely(context: Context) {
    if (context is Activity) context.finish()
    FloatyWindowManger.hideCircularMenu()
    ForegroundService.stop(context)
    context.stopService(Intent(context, FloatyService::class.java))
    AutoJs.getInstance().scriptEngineService.get()?.stopAll()
}

@Composable
fun USBDebugSwitch() {
    val context = LocalContext.current
    var enable by remember {
        mutableStateOf(DevPlugin.isUSBDebugServiceActive)
    }
    val scope = rememberCoroutineScope()
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_debug),
                contentDescription = null, nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_open_usb_debug)) },
        checked = enable,
        onCheckedChange = {
            if (it) {
                scope.launch {
                    try {
                        DevPlugin.startUSBDebug()
                        enable = true
                    } catch (e: Exception) {
                        enable = false
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.text_start_service_failed,
                                e.localizedMessage
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                scope.launch {
                    DevPlugin.stopUSBDebug()
                    enable = false
                }
            }
        }
    )
}

@Composable
private fun ConnectComputerSwitch() {
    val context = LocalContext.current
    var enable by remember {
        mutableStateOf(DevPlugin.isActive)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val scanCodeLauncher =
        rememberLauncherForActivityResult(contract = ScanQRCode(), onResult = { result ->
            when (result) {
                is QRResult.QRSuccess -> {
                    val url = result.content.rawValue
                    if (url.matches(Regex("^(ws://|wss://).+$"))) {
                        Pref.saveServerAddress(url)
                        getUrl(url)
                        connectServer(url)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.text_unsupported_qr_code),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                QRResult.QRUserCanceled -> {}
                QRResult.QRMissingPermission -> {}
                is QRResult.QRError -> {}
            }
        })
    LaunchedEffect(key1 = Unit, block = {
        DevPlugin.connectState.collect {
            withContext(Dispatchers.Main) {
                when (it.state) {
                    DevPlugin.State.CONNECTED -> enable = true
                    DevPlugin.State.DISCONNECTED -> {
                        enable = false

                        if (alwaysTryToConnectState) {
                            checkConnectState(context)
                        }
                        // <
                    }
                }
            }
        }
    })
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_debug),
                null, nightMode = isNightMode()
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = if (!enable) R.string.text_connect_computer
                    else R.string.text_connected_to_computer
                )
            )
        },
        checked = enable,
        onCheckedChange = {
            if (it) {
                showDialog = true
            } else {
                scope.launch { DevPlugin.close() }
            }
        }
    )
    if (showDialog) {
        ConnectComputerDialog(
            onDismissRequest = { showDialog = false },
            onScanCode = { scanCodeLauncher.launch(null) }
        )
    }

}

@Composable
private fun ShizukuSwitch() {
    val context = LocalContext.current
    var isShizukuActive by remember {
        mutableStateOf(OzobiShizuku().checkPermission())
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 用户返回应用时触发的操作
                isShizukuActive = OzobiShizuku().checkPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(R.drawable.ic_shizuku_thick),
                contentDescription = null, nightMode = isNightMode(),
                modifier = Modifier.size(24.dp)
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.text_Shizuku
                ) + "(目前只是开关)"
            )
        },
        checked = isShizukuActive
    ) {
        if (isShizukuActive) {
            OzobiShizuku.openShizuku(context)
        } else {
            OzobiShizuku.requestPermision(context, 1)
        }
    }
}

@Composable
private fun VoiceAssistantSwitch() {
    val context = LocalContext.current
    var isVoiceAssistantActive by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.key_voiceAssistant), false)
        mutableStateOf(default)
    }
//    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit, block = {
        isVoiceAssistantActive = OzobiAssistInteractionService.active
    })
    SwitchItem(
        icon = {
            MyIcon(
                Icons.Default.Build,
                contentDescription = null, nightMode = isNightMode()
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.text_voiceAssistant
                ),
                modifier = Modifier
                    .background(Color(0x33df73ff))
            )
        },
        checked = isVoiceAssistantActive
    ) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(context.getString(R.string.key_voiceAssistant), it)
            .apply()
        if (!OzobiAssistInteractionService.active) {
            // 打开数字助理设置页面
            val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
            context.startActivity(intent)
            Toast.makeText(context, "请选择 Autox.js v6_ozobi", Toast.LENGTH_SHORT).show()
        } else {
            // 关闭数字助理服务?
            val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
            context.startActivity(intent)
        }
    }
}
// <


@Composable
private fun DeviceManagerSwitch() {
    val context = LocalContext.current
    var isDeviceManagerActive by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.key_device_manager), false)
        mutableStateOf(default)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit, block = {
        isDeviceManagerActive = devicePolicyManager.isAdminActive(componentName)
    })
    DeviceAdminReceiverMsg.isEnabled = isDeviceManagerActive
    SwitchItem(
        icon = {
            MyIcon(
                Icons.Default.Warning,
                contentDescription = null, nightMode = isNightMode()
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.text_device_manager
                ),
                modifier = Modifier
                    .background(Color(0x33df73ff))
            )
        },
        checked = isDeviceManagerActive
    ) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(context.getString(R.string.key_device_manager), it)
            .apply()
        if (it) {

            scope.launch {
                checkDeviceManagerStatus(1000L, 120) {
                    isDeviceManagerActive = devicePolicyManager.isAdminActive(componentName)
                    DeviceAdminReceiverMsg.isEnabled = isDeviceManagerActive
                    return@checkDeviceManagerStatus
                }
            }
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            context.startActivity(intent)
        } else {

            devicePolicyManager.removeActiveAdmin(componentName)
            isDeviceManagerActive = false
            DeviceAdminReceiverMsg.isEnabled = false
        }
    }
}

suspend fun checkDeviceManagerStatus(interval: Long, count: Int, callBack: () -> Unit) {

    var countLeft = count
    val initStatus = devicePolicyManager.isAdminActive(componentName)
    while (countLeft > 0) {
        if (interval > 0) {
            delay(interval)
        } else {
            delay(500L)
        }
        countLeft--
        if (initStatus != devicePolicyManager.isAdminActive(componentName)) {

            callBack()
            return
        }
    }

    return
}
// <


@Composable
private fun AlwaysTryToConnect() {
    val context = LocalContext.current
    var enable by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.key_keep_trying), false)
        mutableStateOf(default)
    }
    alwaysTryToConnectState = enable
    checkConnectState(context)
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_cutover),
                null, nightMode = isNightMode()
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.text_always_try_to_connect
                )
            )
        },
        checked = enable,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.key_keep_trying), it)
                .apply()
            enable = it
            if (it) {

                alwaysTryToConnectState = true
            } else {

                alwaysTryToConnectState = false
            }
        }
    )
}

@OptIn(DelicateCoroutinesApi::class)
fun checkConnectState(context: Context) {
    val curCheckingStatus = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(context.getString(R.string.key_cur_check_connection_status), false)
    if (curCheckingStatus) {

        return
    }
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(context.getString(R.string.key_cur_check_connection_status), true)
        .apply()
    GlobalScope.launch {
        delay(5000L)

        while (true) {
            if (alwaysTryToConnectState) {

                if (!DevPlugin.isActive) {
                    val host = Pref.getServerAddressOrDefault(WifiTool.getRouterIp(context))
                    DevPlugin.connect(getUrl(host))
                }
            } else {

                break
            }
            delay(15000L)
        }
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(context.getString(R.string.key_cur_check_connection_status), false)
            .apply()
    }
}


@Composable
fun startUpCheck() {
    DevPlugin.isFirstTime = true

    val context = LocalContext.current
    val host by remember {
        mutableStateOf(Pref.getServerAddressOrDefault(WifiTool.getRouterIp(context)))
    }
    val connected by remember {
        mutableStateOf(DevPlugin.isActive)
    }
    val scope = rememberCoroutineScope()
    scope.launch {
        if (!connected) {
            DevPlugin.connect(getUrl(host))
        }
    }
}

@Composable
private fun ConnectComputerDialog(
    onDismissRequest: () -> Unit,
    onScanCode: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { onDismissRequest() }) {
        var host by remember {
            mutableStateOf(Pref.getServerAddressOrDefault(WifiTool.getRouterIp(context)))
        }
        Surface(shape = RoundedCornerShape(4.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text(text = stringResource(id = R.string.text_server_address))
                MyTextField(
                    value = host,
                    onValueChange = { host = it },
                    modifier = Modifier.padding(vertical = 16.dp),
                    placeholder = {
                        Text(text = host)
                    }
                )
                Row(Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                            IntentUtil.browse(context, URL_DEV_PLUGIN)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.text_help))
                    }
                    TextButton(
                        onClick = {
                            onDismissRequest()
                            onScanCode()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.text_scan_qr))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        onDismissRequest()
                        Pref.saveServerAddress(host)
                        connectServer(getUrl(host))
                    }) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }

    }
}

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("HardwareIds")
private fun connectServer(
    url: String,
) {
    GlobalScope.launch {
        DevPlugin.connect(url)
    }
}

private fun getUrl(host: String): String {
    var url1 = host
    var isHost = true
    if (!url1.matches(Regex("^(ws|wss)://.*"))) {
        url1 = "ws://${url1}"
    } else {
        isHost = false
    }
    if (!url1.matches(Regex("^.+://.+?:.+$"))) {
        url1 += ":${DevPlugin.SERVER_PORT}"
    } else {
        isHost = false
    }
    if (isHost) {
        DevPlugin.serverAddress = host
    } else {
        var okHost = host
        if (host.indexOf("//") != -1) {
            okHost = okHost.substring(okHost.indexOf("//") + 2)
        }
        if (okHost.indexOf(":") != -1) {
            okHost = okHost.substring(0, okHost.indexOf(":"))
        }
        DevPlugin.serverAddress = okHost
    }
    Log.d("ozobiLog", "DevPlugin.serverAddress: " + DevPlugin.serverAddress)
    return url1
}

@Composable
private fun AutoBackupSwitch() {
    val context = LocalContext.current
    var enable by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.key_auto_backup), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_backup),
                null, nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_auto_backup)) },
        checked = enable,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.key_auto_backup), it)
                .apply()
            enable = it
        }
    )
}

@Composable
private fun VolumeDownControlSwitch() {
    val context = LocalContext.current
    var enable by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.key_use_volume_control_record), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_sound_waves),
                null, nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_volume_down_control)) },
        checked = enable,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.key_use_volume_control_record), it)
                .apply()
            enable = it
        }
    )
}

@Composable
private fun FloatingWindowSwitch() {
    val context = LocalContext.current

    var isFloatingWindowShowing by remember {
        mutableStateOf(FloatyWindowManger.isCircularMenuShowing())
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (DrawOverlaysPermission.isCanDrawOverlays(context)) FloatyWindowManger.showCircularMenu()
            isFloatingWindowShowing = FloatyWindowManger.isCircularMenuShowing()
        }
    )
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_overlay),
                null, nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_floating_window)) },
        checked = isFloatingWindowShowing,
        onCheckedChange = {
            if (isFloatingWindowShowing) {
                FloatyWindowManger.hideCircularMenu()
            } else {
                if (DrawOverlaysPermission.isCanDrawOverlays(context)) FloatyWindowManger.showCircularMenu()
                else launcher.launchCanDrawOverlaysSettings(context.packageName)
            }
            isFloatingWindowShowing = FloatyWindowManger.isCircularMenuShowing()
            Pref.setFloatingMenuShown(isFloatingWindowShowing)
        }
    )
}

@Composable
private fun UsageStatsPermissionSwitch() {
    val context = LocalContext.current
    var enabled by remember {
        mutableStateOf(context.isOpPermissionGranted(AppOpsManager.OPSTR_GET_USAGE_STATS))
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            enabled = context.isOpPermissionGranted(AppOpsManager.OPSTR_GET_USAGE_STATS)
        }
    )
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(R.drawable.ic_chrome_reader_mode_black_48dp),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
                nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_usage_stats_permission)) },
        checked = enabled,
        onCheckedChange = {
            showDialog = true
        }
    )
    if (showDialog) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.text_usage_stats_permission)) },
            onDismissRequest = { showDialog = false },
            text = {
                Text(
                    text = stringResource(
                        R.string.description_usage_stats_permission
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    launcher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }) {
                    Text(text = stringResource(id = R.string.text_go_to_setting))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(id = R.string.text_cancel))
                }
            },
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun hasNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun ForegroundServiceSwitch() {
    val context = LocalContext.current
    var isOpenForegroundServices by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.key_foreground_servie), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(R.drawable.ic_info_black_48dp), modifier = Modifier.size(24.dp),
                contentDescription = null, nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_foreground_service)) },
        checked = isOpenForegroundServices,
        onCheckedChange = {
            if (it) {
                if (!hasNotificationPermission(context)) {
                    Toast.makeText(context, "请打开通知权限", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                }
                ForegroundService.start(context)
            } else {
                ForegroundService.stop(context)
            }
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.key_foreground_servie), it)
                .apply()
            isOpenForegroundServices = it
        }
    )
}

@Composable
private fun NotificationUsageRightSwitch() {
    val context = LocalContext.current
    var isNotificationListenerEnable by remember {
        mutableStateOf(notificationListenerEnable())
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            isNotificationListenerEnable = notificationListenerEnable()
        }
    )
    SwitchItem(
        icon = {
            MyIcon(
                Icons.Default.Notifications,
                null, nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_notification_permission)) },
        checked = isNotificationListenerEnable,
        onCheckedChange = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                launcher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            } else isNotificationListenerEnable = it
        }
    )
}

private fun notificationListenerEnable(): Boolean = NotificationListenerService.instance != null


@Composable
private fun StableModeSwitch() {
    val context = LocalContext.current
    var showDialog by remember {
        mutableStateOf(false)
    }
    var isStableMode by remember {
        val default = Pref.isStableModeEnabled()
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painter = painterResource(id = R.drawable.ic_triangle),
                contentDescription = null, nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_stable_mode)) },
        checked = isStableMode,
        onCheckedChange = {
            if (it) showDialog = true
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.key_stable_mode), it)
                .apply()
            isStableMode = it
        }
    )
    if (showDialog) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.text_stable_mode)) },
            onDismissRequest = { showDialog = false },
            text = {
                Text(
                    text = stringResource(
                        R.string.description_stable_mode
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }
}

@Composable
private fun AccessibilityServiceSwitch() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDialog by remember {
        mutableStateOf(false)
    }
    var isAccessibilityServiceEnabled by remember {
        mutableStateOf(AccessibilityServiceTool.isAccessibilityServiceEnabled(context))
    }
    val accessibilitySettingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (AccessibilityServiceTool.isAccessibilityServiceEnabled(context)) {
                isAccessibilityServiceEnabled = true
            } else {
                isAccessibilityServiceEnabled = false
                Toast.makeText(
                    context,
                    R.string.text_accessibility_service_is_not_enable,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
//    var editor by remember { mutableStateOf(Pref.getEditor()) }
//    SwitchItem(
//        icon = {
//            MyIcon(
//                Icons.Default.Edit,
//                contentDescription = null,nightMode=isNightMode()
//            )
//        },
//        text = { Text(text = "启用新编辑器") },
//        checked = editor,
//        onCheckedChange = { isChecked ->
//            editor = isChecked
//            Pref.setEditor(isChecked)
//        }
//    )
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(R.drawable.ic_accessibility_black_48dp),
                modifier = Modifier.size(24.dp),
                contentDescription = null,
                nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_accessibility_service)) },
        checked = isAccessibilityServiceEnabled,
        onCheckedChange = {
            if (!isAccessibilityServiceEnabled) {
                if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                    scope.launch {
                        val enabled = withContext(Dispatchers.IO) {
                            AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(2000)
                        }
                        if (enabled) isAccessibilityServiceEnabled = true
                        else showDialog = true
                    }
                } else showDialog = true
            } else {
                isAccessibilityServiceEnabled = !AccessibilityService.disable()
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.text_need_to_enable_accessibility_service)) },
            onDismissRequest = { showDialog = false },
            text = {
                Text(
                    text = stringResource(
                        R.string.explain_accessibility_permission2,
                        GlobalAppContext.appName
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    accessibilitySettingsLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }) {
                    Text(text = stringResource(id = R.string.text_go_to_open))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(id = R.string.text_cancel))
                }
            },
        )
    }
}

@Composable
fun SwitchItem(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            icon()
        }
        Box(modifier = Modifier.weight(1f)) {
            text()
        }
        MySwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SwitchTimedTaskScheduler() {
    var isShowDialog by rememberSaveable {
        mutableStateOf(false)
    }
    TextButton(onClick = { isShowDialog = true }) {
        Text(text = stringResource(id = R.string.text_switch_timed_task_scheduler))
    }
    if (isShowDialog) {
        TimedTaskSchedulerDialog(onDismissRequest = { isShowDialog = false })
    }
}


@Composable
private fun LayoutInsScreenshotSwitch() {
    val context = LocalContext.current
    var isCaptureScreenshot by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_isCapture_Screenshot), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_photo_camera_black_48dp),
                contentDescription = null,
                modifier = Modifier.size(24.dp), nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_isCapture_Screenshot)) },
        checked = isCaptureScreenshot,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_isCapture_Screenshot), it)
                .apply()
            isCaptureScreenshot = it
        }
    )
}
// <

@Composable
private fun LayoutInsRefreshSwitch() {
    val context = LocalContext.current
    var isCaptureScreenshot by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_isCapture_refresh), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_refresh_black_48dp),
                contentDescription = null,
                modifier = Modifier.size(24.dp), nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_isCapture_refresh)) },
        checked = isCaptureScreenshot,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_isCapture_refresh), it)
                .apply()
            isCaptureScreenshot = it
        }
    )
}

@Composable
private fun LayoutInsWaitForCaptureSwitch() {
    val context = LocalContext.current
    var isCaptureScreenshot by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_isWaitFor_capture), true)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_timed_task),
                contentDescription = null,
                modifier = Modifier.size(24.dp), nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_isWaitFor_capture)) },
        checked = isCaptureScreenshot,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_isWaitFor_capture), it)
                .apply()
            isCaptureScreenshot = it
        }
    )
}

// <
@Composable
private fun SetDoneCaptureNotify() {
    var isShowDialog by remember { mutableStateOf(false) }
    var color = Color.Black
    if (isNightMode()) {
        color = Color.White
    }
    TextButton(
        onClick = {
            isShowDialog = !isShowDialog
        }
    ) {
        Text(
            text = "布局分析(点击设置)",
            style = TextStyle(color = color, fontStyle = FontStyle.Italic),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 0.dp),
            fontSize = 16.sp
        )
    }
    if (isShowDialog) {
        showSetDoneCaptureNotifyDialog { isShowDialog = false }
    }
}

@Composable
private fun showSetDoneCaptureNotifyDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(true) }
    var isVibrate by remember {
        mutableStateOf(
            PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.ozobi_key_doneCaptureVibrate), true)
        )
    }
    var isPlaySound by remember {
        mutableStateOf(
            PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.ozobi_key_doneCapturePlaySound), false)
        )
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                showDialog = false
            },
            title = { Text(text = "捕获完成之后") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isVibrate = !isVibrate
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isVibrate,
                            onCheckedChange = {
                                isVibrate = it
                            }
                        )
                        Text(
                            text = "振动",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isPlaySound = !isPlaySound
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isPlaySound,
                            onCheckedChange = {
                                isPlaySound = it
                            }
                        )
                        Text(
                            text = "提示音",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDismiss()
                    PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putBoolean(
                            context.getString(R.string.ozobi_key_doneCaptureVibrate),
                            isVibrate
                        )
                        .apply()
                    PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putBoolean(
                            context.getString(R.string.ozobi_key_doneCapturePlaySound),
                            isPlaySound
                        )
                        .apply()
                }) {
                    Text(text = "确认")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    onDismiss()
                }) {
                    Text(text = "取消")
                }
            }
        )
    }
}

@Composable
private fun LayoutInsDelayCaptureSwitch() {
    val context = LocalContext.current

    var isCaptureScreenshot by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_isDelay_capture), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_timer_black_48dp),
                contentDescription = null,
                modifier = Modifier.size(24.dp), nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_isDelay_capture)) },
        checked = isCaptureScreenshot,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_isDelay_capture), it)
                .apply()
            isCaptureScreenshot = it
        }
    )
}

@Composable
private fun LayoutInsSelectWindowSwitch() {
    val context = LocalContext.current
    var isSelectWindow by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_isSelect_window), true)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_window),
                contentDescription = null,
                modifier = Modifier.size(24.dp), nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_isSelect_window)) },
        checked = isSelectWindow,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_isSelect_window), it)
                .apply()
            isSelectWindow = it
        }
    )
}

@Composable
private fun DocsServiceSwitch() {
    val context = LocalContext.current
    var isDocsServiceRunning by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_docs_service), false)
        mutableStateOf(default)
    }
    DocsServiceAddress.ip = getWifiIPv4(context)
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_ali_log),
                contentDescription = null,
                modifier = Modifier.size(24.dp), nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_docs_service)) },
        checked = isDocsServiceRunning,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_docs_service), it)
                .apply()
            isDocsServiceRunning = it
            if (it) {
                val startIntent = Intent(context, KtorDocsService::class.java)
                context.startService(startIntent)
                val detailsDialog = OperationDialogBuilder(context)
                    .item(
                        R.id.docs_service_address,
                        R.drawable.ic_web_black_48dp,
                        DocsServiceAddress.ip + ":" + DocsServiceAddress.port
                    )
                    .title("文档服务已开启")
                    .build()
                DialogUtils.showDialog(detailsDialog)
                ClipboardUtil.setClip(
                    context,
                    DocsServiceAddress.ip + ":" + DocsServiceAddress.port
                )
            } else {
                val startIntent = Intent(context, KtorDocsService::class.java)
                context.stopService(startIntent)
                val detailsDialog = OperationDialogBuilder(context)
                    .title("文档服务已关闭")
                    .build()
                DialogUtils.showDialog(detailsDialog)
            }
        }
    )
}

@Composable
private fun EditFloatySwitch() {
    val context = LocalContext.current
    var showFloaty by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_show_edit_floaty), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_featured_video_black_48dp),
                contentDescription = null,
                modifier = Modifier.size(24.dp), nightMode = isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_edit_floaty)) },
        checked = showFloaty,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_show_edit_floaty), it)
                .apply()
            showFloaty = it
        }
    )
}

@Composable
fun showModificationDetailsButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    TextButton(onClick = {
        scope.launch {
            detailsDialog(context)
        }
    }) {
        Text(text = stringResource(id = R.string.ozobi_modification_content) + OZOBI_SUBFIX)
    }
}

fun detailsDialog(context: Context) {
    val detailsDialog = OperationDialogBuilder(context)
        .item(
            R.id.qq_communication_group,
            R.drawable.ic_group_black_48dp,
            "QQ交流群2: " + context.resources.getString(R.string.qq_communication_group_2)
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65822 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复: 脚本运行结束后资源没有回收导致的内存泄露\n\n"+
            "添加: 脚本文件卡片创建快捷方式选项(有些手机可能不起作用, 快捷方式也可以通过安卓小部件创建)\n\n"+
            "修改: 通过 runtime.loadDex 或 runtime.loadJar 加载dex或包时返回 DexClassLoader\n" +
                    "~ let dexClassLoader = runtime.loadDex(\"./test.dex\")\n\n" +
            "修复: ppocrv5 内存泄露\n\n"+
            "修复: 模拟器编辑代码 ctrl + s 会使 app 崩溃"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65821 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复: 排序改变之后无法操作正确的卡片\n\n"+
            "添加: 创建项目选项, 项目文件夹打包和运行按钮\n\n"+
            "修改: 主页不再将文件和文件夹分成两个列表\n\n"+
            "添加: ppocrv5(只有 autox app 可用, 通用的还没弄好), 具体使用看示例脚本\n\n"+
            "修复: 某些情况主页搜索会使app崩溃\n\n"+
            "添加: 任务卡片长按操作\n\n"+
            "修复: 运行中的脚本路径太长导致关闭按钮被挤出屏幕\n\n"+
            "修复: 非脚本文件重命名按钮显示不完整\n\n"+
            "添加: Storage 实例方法: getAll、getAllKeys、getPref"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65820 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复: 名称降序排序闪退\n\n" +
                    "添加: 管理页面排序\n\n" +
                    "添加: 管理页面文件卡片显示上次修改时间和大小\n\n" +
                    "添加: 管理页面刷新按钮\n\n" +
                    "修复: 脚本在 app 关闭页面时结束运行无法移除运行记录\n\n" +
                    "修改: 搜索时忽略大小写\n\n" +
                    "修复: 搜索时无法操作正确的文件\n\n" +
                    "修改: http\n\n" +
                    "重写: 使用 Compose 重写 app 首页和管理页面\n\n" +
                    "修复: 打包后无障碍服务判断问题"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65819 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复: 脚本退出时触发两次 onExit\n\n" +
                    "调整: 抽屉页面和脚本例表控件按钮\n\n" +
                    "添加: 代码编辑器编辑菜单(另存为)\n\n" +
                    "修复: 某些设备 RootAutomator 滑动无效\n\n" +
                    "修复: 打包后每次打开都会跳转到所有文件访问权限页面\n\n" +
                    "修复: 打包前后 autojs 版本不一致"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65819 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复: switch、button 控件设置字体颜色不生效\n\n" +
                    "添加: termux 执行参数: options( outputPath、callback、runBackground、top、sessionAction、clean、checkGap、checkCount)\n\n" +
                    "添加: 全局方法 getTermuxCommandIntent、stringArray\n\n" +
                    "添加: termux 示例代码\n\n" +
                    "添加: app 代码编辑器悬浮窗开关\n\n" +
                    "优化: termux 执行命令(zryyoung)\n\n" +
                    "修复: switch 控件不显示文本\n\n" +
                    "修复: 通过 app 代码编辑器悬浮窗运行时 cwd 不是脚本所在路径\n\n" +
                    "高版本 bug 太多，sdk 改回 28\n\n" +
                    "增强: 解决微信控件混乱问题\n" +
                    "~ 如果还是不行的话，估计是环境异常了"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65817 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复: app 前台服务无法使用\n\n" +
                    "修复: 打包后权限判断问题\n\n" +
                    "添加: 通知权限\n\n" +
                    "添加: 打包后授予全部文件访问权限\n\n" +
                    "修复: 安卓 15 存储权限问题\n\n" +
                    "添加: app 编辑脚本时的控制悬浮窗(zryyoung)"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65816 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修改(658): 悬浮窗停止脚本(与app一样)\n\n" +
                    "添加: 设置 input 色调\n" +
                    "~ <input id=\"input\" tint=\"#ff0000|#00ff00\" />\n" +
                    "~ ui.input.setTint(\"#00ff00|#ff0000\")\n\n" +
                    "添加: 设置 checkbox 色调\n" +
                    "~ <checkbox id=\"checkbox\" tint=\"#ff0000|#00ff00\" />\n" +
                    "~ ui.checkbox.setTint(\"#00ff00|#ff0000\")\n\n" +
                    "修改: checkbox 控件为 androidx 控件\n\n" +
                    "添加: 设置 button 渐变背景\n" +
                    "> <button id=\"btn\" w=\"88\" h=\"88\" gradient=\"shape=oval|colors=#ff00ff,#584EF0|ori=bottom_top|type=linear\">\n" +
                    "> ui.btn.setBackgroundGradient(\"shape=rect|corner=88\");\n" +
                    "> 参数：shape: rect(方形-默认)、line(线)、ring(圆环)、oval(椭圆)\n" +
                    "> colors: 渐变颜色数组\n" +
                    "> ori: 渐变方向 top_bottom、bottom_top、left_right、right_left、tl_br、br_tl、tr_bl、bl_tr\n" +
                    "> type: 渐变类型 linear(线性-默认) radial(辐射) sweep(扫描)\n" +
                    "> center: 渐变中心 0.5,0.5  默认(x:0.5, y:0.5)\n" +
                    "> corner: 圆角 默认16\n" +
                    "> 有些可能不符合预期，暂时不深入研究 *.*\n\n" +
                    "添加: 设置 radio 色调\n" +
                    "> <radio id=\"radio\" tint=\"#ff0000|#00ff00\" />\n" +
                    "> ui.radio.setTint(\"#00ff00|#ff0000\")\n" +
                    "> 注：未选中|选中 （只有一个颜色则一样）\n\n" +
                    "修改：button、input、spinner、radio、text、toolbar 控件为 androidx 的控件"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65815 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复: 安卓 13 之后闪退或打不开app\n\n" +
                    "添加: JsSwitch 开关控件\n" +
                    "<switch id=\"switch\"></switch>\n" +
                    "// 以下用 xxx 代替 thumb(滑块) 或 track(轨道)\n" +
                    "// 色调: xxxTint=\"#ff00ff\" | xxxTint=\"#cfcfcf|#ff00ff\"\n" +
                    "// 大小|形状: xxxShape=\"168|88\" | xxxShape=\"168|88,88,36,36\"\n" +
                    "// 注: \"宽[高](dp) | (圆角半径)左上水平,左上垂直, 右上水平,右上垂直, 右下水平,右下垂直, 左下水平,左下垂直\"\n" +
                    "// 背景: xxxBg=\"file:///sdcard/logo.png\"\n" +
                    "------\n" +
                    "let Switch = ui.switch;\n" +
                    "Switch.setThumbTint(\"#ff00ff\");// 设置滑块色调\n" +
                    "Switch.setTrackTint(\"#ff00ff\");// 设置轨道色调\n" +
                    "Switch.setThumbShape(\"168|88\");// 设置滑块大小形状\n" +
                    "Switch.setTrackShape(\"168|88\");// 设置轨道大小形状\n" +
                    "Switch.setThumbBackground(\"file:///sdcard/logo.png\");// 设置滑块背景\n" +
                    "Switch.setTrackBackground(\"file:///sdcard/logo.png\");// 设置轨道背景\n" +
                    "// 如果需要设置多项, 推荐的顺序为: bg -> shape -> tint \n" +
                    "// 若出现不符合预期效果, 那应该是冲突了\n\n" +
                    "添加: 布局分析窗口选择开关\n\n" +
                    "添加: 布局分析窗口选择(开启延迟捕获无法使用)\n\n" +
                    "添加: MQTT(来自前人的智慧)"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65814 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复(一半): 打包后无法安装\n" +
                    "> 偶尔可能出现无法直接安装，自己用MT管理器签名即可\n\n" +
                    "修复: 申请截图权限失败\n\n" +
                    "添加: 授予管理所有文件权限\n\n" +
                    "升级: 将 targetSdk 改为 35(安卓 15)\n" +
                    "> 有可能会出现一些未知的 bug\n\n" +
                    "修复(魔改): looper 初始化之前创建 AdbIME 对象导致报错闪退\n\n" +
                    "修复(658): 多选对话框无法使用"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65813 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修改(658): 无障碍服务类名\n\n" +
                    "添加: 一些编辑器提示栏符号\n\n" +
                    "添加: 编辑器编辑菜单粘贴\n\n" +
                    "修复(魔改): 两个内存泄露\n\n" +
                    "修改: app 文档服务和 v1 本地文档改为新版 v1 文档\n\n" +
                    "添加: 悬浮窗保持屏幕常亮\n" +
                    "> floaty.keepScreenOn()\n" +
                    "> (之后创建的<第一个>悬浮窗将会使屏幕保持常亮)\n\n" +
                    "添加: 设置布局分析捕获完成提示"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65812 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "(L.)添加(vscode插件): goScoper\n" +
                    "https://github.com/ozobiozobi/Auto.js-VSCode-Extension/releases\n\n" +
                    "修复(尽力局): app 无法停止脚本\n" +
                    "> 这应该是最后一次修这个bug了，如果还是不行的话，只能靠你们自己的代码解决了(循环适当地休息一下)\n" +
                    "> (脚本是一个线程，只能通过 thread.interrupt() 优雅地结束)\n\n" +
                    "修复(658): 悬浮窗点击输入无法弹出输入法\n\n" +
                    "添加: app 开机自启(需要后台弹出界面[自启动]权限)\n\n" +
                    "添加: 打包后开机自启(需要后台弹出界面[自启动]权限)\n\n" +
                    "(BMX)更新: v1 文档 ui 控件使用方法"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65811 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "添加: 时间转时间戳\n" +
                    "> let ts = dateToTimestamp(dateStr, pattern)\n" +
                    "> dateStr: 时间字符串(2025-01-20)\n" +
                    "> pattern: 时间字符串对应的模式(yyyy-MM-dd)\n\n" +
                    "添加: v1在线文档、社区(由 BMX 提供)\n\n" +
                    "修复(65811): app 停止脚本后打开日志页面返回闪退\n\n" +
                    "添加: 魔改充电\n\n" +
                    "修复(65810): app 无法停止脚本(这回应该没问题了)\n\n" +
                    "添加: Shizuku\n" +
                    "> 开关(哈哈)"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 65810 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化: 布局层次分析页面\n" +
                    "> 修复 compose 无法触发重组\n" +
                    "> 调整按钮大小和位置\n" +
                    "> 将标记箭头改为方框，并在拖动时跟随\n" +
                    "> 在隐显按钮和当前选中节点边界之间添加连接线\n\n" +
                    "移除(658): 新版编辑器\n\n" +
                    "修复(658): app 无法停止脚本(好像可以秒停@.@)\n\n" +
                    "添加: networkUtils\n" +
                    "> networkUtils.isWifiAvailable()\n" +
                    "> networkUtils.getWifiIPv4()\n" +
                    "> networkUtils.getIPList()\n\n" +
                    "添加: 文档服务\n" +
                    "> vscode, 启动!\n" +
                    "> 什么, 文档404了?\n" +
                    "> 没事, 还有后背隐藏能源"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6589 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化: 还是布局层次分析页面\n" +
                    "> 就, 好看了一点吧(也可能是我谦虚了\n\n" +
                    "修复(658): 布局层次分析页面\n" +
                    "> 显示选中不唯一\n" +
                    "> 返回无法关闭页面\n\n" +
                    "添加: 布局层次分析页面:\n" +
                    "> 施法按钮\n" +
                    "\t\t数数？为什么不用法术(@-@)\n" +
                    "> 给当前选中节点周围添加标记\n" +
                    "\t\t没有火眼金睛? 不要紧, 我来助你\n" +
                    "> 切换是否可以折叠(化bug为功能:D)\n" +
                    "> 显示描述和文本\n" +
                    "> 标记当前选中节点的兄弟\n" +
                    "> 标记当前选中节点的孩子\n" +
                    "> 标记当前选中节点的所有直系长辈(大概就这个意思-.-)\n" +
                    "> 布局分析, 为所欲为QwQ"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6588 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化: 夜间模式\n\n" +
                    "优化: 布局层次分析页面\n" +
                    "> 修复展开后不可收起\n" +
                    "> 隐藏按钮可拖动\n\n" +
                    "修复(6587): 布局分析相关 bug\n\n" +
                    "更改(658): app抽屉页面使用随机彩色图标\n\n" +
                    "修复(6587): app布局分析刷新显示不全\n" +
                    "> 一般用不到刷新, 除非画面发生变动之后捕获结果没有改变\n" +
                    "> (刷新会比等待捕获多花 2-3 倍的时间)\n\n" +
                    "添加: app布局分析等待捕获、延迟捕获开关\n" +
                    "> 布局分析, 随心所欲(~.-\n\n" +
                    "添加: 截图是否返回新的对象\n" +
                    "> let img1 = images.captureScreen(true)\n" +
                    "> let img2 = images.captureScreen(true)\n" +
                    "> 即使一直使用同一张缓存图像(屏幕没有发生变化), img1 和 img2 都不会是同一个对象\n" +
                    "> 反之如果不加参数 true, img1 === img2"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6587 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "添加: 获取屏幕实时宽高\n" +
                    "> let curW = device.getCurWidth()\n" +
                    "> let curH = device.getCurHeight()\n" +
                    "> let size = device.getCurScreenSize()\n" +
                    "> size.x == curW\n" +
                    "> size.y == curH\n\n" +
                    "添加: 获取当前屏幕方向\n" +
                    "> let ori = getCurOrientation()\n" +
                    "> 竖屏: 1  横屏: 2\n\n" +
                    "添加: app布局分析刷新开关\n" +
                    "> 有些情况刷新会出问题(比如某音极速版啥的)，可以关掉刷新，点开悬浮窗后，自己看情况等上一段时间再点分析\n\n" +
                    "添加: 通过 setClip 复制的文本会发送到 vscode 的输出\n" +
                    "> 例如: app布局分析复制控件属性/生成代码后点击复制\n" +
                    "\t\t脚本使用 setClip\n" +
                    "> (长按手动复制不会触发)\n\n" +
                    "优化(658): 减少 app 悬浮窗点击响应时长(慢不了一点\n\n" +
                    "更改: app 抽屉页面\n\n" +
                    "添加: 将 adbConnect、termux、adbIMEShellCommand、sendTermuxIntent 添加到全局\n\n" +
                    "添加: viewUtils\n" +
                    "> let v = viewUtils.findParentById(view,id)\n" +
                    "> let sp = viewUtils.pxToSp(px)\n" +
                    "> let px = viewUtils.dpToPx(dp)\n" +
                    "> let dp = viewUtils.pxToDp(px)\n" +
                    "> let px = viewUtils.spToPx(sp)\n\n" +
                    "添加: 获取[raw]悬浮窗 contentView\n" +
                    "> let fw = floaty.window(<frame id=\"content\"> </frame>)\n" +
                    "> let contentView = fw.getContentView()\n" +
                    "> contentView === fw.content"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6586 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化: 启动 app 自动连接不显示 toast\n\n" +
                    "升级: SDK35、gradle-8.7、AGP-8.6.0\n\n" +
                    "添加: 获取状态栏高度(px)\n" +
                    "> let h = getStatusBarHeight()\n\n" +
                    "添加: 布局分析截图开关\n\n" +
                    "添加: 获取当前存在的本地存储 名称[路径] 数组\n" +
                    "> let arr = storages.getExisting([returnPath])"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6585 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
//            "修复(6582): 布局分析影响脚本截图服务\n\n"+
            "添加: 跟踪堆栈行号打印\n" +
                    "> traceLog(\"嘿嘿\"[,path(输出到文件)])\n" +
                    "> (让 bug 无处可藏>_>)\n\n" +
                    "添加: 时间戳格式化\n" +
                    "> let ts = Date.now();\n" +
                    "> let fm = dateFormat([ts, format])\n" +
                    "> ts: 时间戳, 默认为当前时间戳\n" +
                    "> format: 时间格式, 默认为 \"yyyy-MM-dd HH:mm:ss.SSS\"\n\n" +
                    "添加: 设置 http 代理(options)\n" +
                    "> 设置代理: http.get(url, {proxyHost:\"192.168.1.10\", proxyPort:7890})\n" +
                    "> 身份认证: {userName:\"Ozobi\", password:" + context.resources.getString(R.string.qq_communication_group_2) + "}\n\n" +
                    "添加: 设置 http 尝试次数、单次尝试超时时间(options)\n" +
                    "> 例如: http.get(url, {maxTry:3, timeout: 5000})\n" +
                    "> 一共尝试 3 次(默认3), 每次 5s (默认10s)超时\n\n" +
                    "修改:将布局层次分析页面的彩色线条数量改为与 depth 相等"
//            "优化: 布局分析不显示异常截图(宽高异常/全黑截图)"
//            "添加: 生成 sendevent 命令(touch)\n"+ // 好像没什么用 -_-
//            "注: SELinux 需要是宽松模式或关闭状态\n"+
//            "let sec = runtime.sendeventCommand\n"+
//            "let commandList = sec.touchDown(x,y[,id])\n"+
//            "commandList.forEach(command=>{\n"+
//            "\t\tadb shell /dev/input/eventX + command})\n"+
//            "按下 x, y (eventX:各不相同，终端执行 adb shell getevent，然后随便滑一下屏幕即可确认。一个 commandList 为一套完整的命令)\n"+
//            "sec.touchDown(x,y[,id])\n"+
//            "sec.touchMove(x,y[,id])\n"+
//            "sec.touchUp(id)\n"+
//            "设置屏幕宽高: 使用 sec.setScreenMetrics(width, height)"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6584 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复(658):某些设备 RootAutomator 不生效\n\n" +
//            "修复(6583):找不到方法 runtime.adbConnect(string, number)\n\n"+
//            "修复(6583):布局分析时反复申请投影权限\n\n"+
                    "添加: Adb输入法\n" +
                    "> let command = runtime.adbIMEShellCommand.inputText(\"嘿嘿\")\n" +
                    "> 执行命令: adb shell + command\n" +
                    "> 将输出文本 嘿嘿 到当前光标所在位置(需要先启用然后设置为当前输入法)\n\n" +
                    "> enableAdbIME() 启用adb输入法\n" +
                    "> setAdbIME() 设置adb输入法为当前输入法\n" +
                    "> resetIME() 重置输入法\n" +
                    "> clearAllText() 清除所有文本\n" +
                    "> inputTextB64(text) 如果inputText没用试试这个\n" +
                    "> inputKey(keyCode) 输入按键\n" +
                    "> inputCombKey(metaKey, keyCode) 组合键\n" +
                    "> inputCombKey(metaKey[], keyCode) 多meta组合键\n\n" +
                    "> meta 键对照:\n" +
                    "> SHIFT == 1\n" +
                    "> SHIFT_LEFT == 64\n" +
                    "> SHIFT_RIGHT == 128\n" +
                    "> CTRL == 4096\n" +
                    "> CTRL_LEFT == 8192\n" +
                    "> CTRL_RIGHT == 16384\n" +
                    "> ALT == 2\n" +
                    "> ALT_LEFT == 16\n" +
                    "> ALT_RIGHT == 32\n" +
                    "> 输入组合键: ctrl+shift+v:\n" +
                    "> adb shell + runtime.adbIMEShellCommand.inputCombKey([4096,1], 50)\n\n" +
                    "> 调用 termux\n" +
                    "> 安装 termux(版本需0.95以上)\n" +
                    "> 编辑 ~/.termux/termux.properties 文件, 将 allow-external-apps=true 前面的注释#去掉, 保存退出\n" +
                    "> 安装 adb 工具\n" +
                    "> pkg update\n" +
                    "> pkg install android-tools\n" +
                    "> adb连接手机后授权 autoxjs(打包后的应用也需要授权)\n" +
                    "> (如果有)手机需要开启 USB调试(安全设置)\n" +
                    "> adb shell pm grant 包名 com.termux.permission.RUN_COMMAND\n" +
                    "> 调用: runtime.termux(\"adb shell input keyevent 3\") 返回桌面\n" +
                    "> 这里默认后台执行, 若想使用自己构建的 intent 可以使用 runtime.sendTermuxIntent(intent)"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6583 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "添加: 远程AdbShell(好像不支持远程配对, 手机需要设置 adb 监听端口)\n" +
                    "> let adbShell = runtime.adbConnect(host,port)连接设备\n" +
                    "> adbShell.exec(\"ls /\") 执行命令\n" +
                    "> adbShell.close() 断开连接\n" +
                    "> adbShell.connection.getHost() 获取当前连接主机名\n" +
                    "> adbShell.connection.getPost() 获取当前连接端口\n\n" +
                    "修改: 将悬浮窗位置改为以屏幕左上角为原点(终于可以指哪打哪了\n" +
                    "> _<)\n\n" +
//            "修复(6582): 脚本请求截图权限后再进行布局分析时打不开悬浮窗\n\n"+
                    "增强: 使用相对路径显示本地图片\n" +
                    "> <img src=\"./pic.png\" />\n" +
                    "> ./ 等于 file://当前引擎的工作目录/"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6582 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化?: 现在可以从 vscode 的插件选择运行项目, vscode打开项目新建一个 project.json 文件,里面有{}就可以, 再将主脚本文件命名为 main.js 即可\n\n" +
                    "修复(658): 老版编辑器长按删除崩溃\n\n" +
                    "添加: 添加 v2本地、在线文档\n\n" +
                    "app功能\n" +
                    "\t\t添加连上为止\n" +
                    "\t\t软件启动时会尝试连接电脑一次\n" +
                    "\t\t打开之后会一直尝试连接电脑，直到连上为止，除非手动关闭\n" +
                    "\t\t被动和主动断开连接电脑，都会触发一直尝试连接，除非手动关闭(可能还是有bug, 某些情况会连接多次\n\n" +
                    "app布局分析\n" +
                    "\t\t每次分析都会刷新页面节点信息，下拉状态栏可打断刷新，同时会大概率丢失页面节点信息\n" +
                    "\t\t添加延迟选项。选择其中一个选项之后会延迟相应的时间之后进行布局分析，等待期间无法再次打开布局分析对话框。\n" +
                    "\t\t添加显示上次节点信息选项。可重新分析上一次刷新的节点信息\n\n" +
                    "app布局范围分析\n" +
                    "\t\t根据控件属性使用不同的颜色\n" +
                    "\t\t绿色：可点击\n" +
                    "\t\t紫色：有描述\n" +
                    "\t\t紫红色：有文本\n" +
                    "\t\t白色：上面三个都没有\n" +
                    "\t\t同一控件显示颜色优先级顺序同上\n" +
                    "\t\t如果两个控件bounds重叠，子控件的颜色会盖住父控件的\n\n" +
                    "app布局层次分析\n" +
                    "\t\t将控件的 depth、是否可点击、是否有描述、是否有文本 显示在外面\n" +
                    "\t\t添加展开按钮(展开当前选中的控件的全部孩子控件)\n" +
                    "\t\t添加转到布局范围按钮\n" +
                    "这个层次分析页面还有待改进\n\n" +
                    "app布局分析属性\n" +
                    "\t\t将控件的常用属性（个人认为）往前排\n\n" +
                    "代码布局分析\n" +
                    "\t\t给 UiSelector.find() 添加刷新参数\n" +
                    "\t\t例如：text('嘿嘿').find(true)\n" +
                    "\t\t将会先刷新页面节点信息，然后再返回刷新后的寻找结果\n" +
                    "\t\t？怎么知道有用呢？可以拿某手国际版来开刀，试试刷新和不刷新的区别"
        )
        .title("魔改内容")
        .build()
    DialogUtils.showDialog(detailsDialog)
    val qq = context.resources.getString(R.string.qq_communication_group_2)
    ClipboardUtil.setClip(context, qq)
    Toast.makeText(context, R.string.text_qq_already_copy_to_clip, Toast.LENGTH_SHORT).show()
}

// <
@Composable
fun TimedTaskSchedulerDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var selected by rememberSaveable {
        mutableStateOf(Pref.getTaskManager())
    }
    MyAlertDialog1(
        onDismissRequest = onDismissRequest,
        onConfirmClick = {
            onDismissRequest()
            Pref.setTaskManager(selected)
            toast(context, R.string.text_set_successfully)
        },
        title = { Text(text = stringResource(id = R.string.text_switch_timed_task_scheduler)) },
        text = {
            Column {
                Spacer(modifier = Modifier.size(16.dp))
                Column() {
                    for (i in 0 until 3) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selected = i }) {
                            RadioButton(selected = selected == i, onClick = { selected = i })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (i) {
                                    0 -> stringResource(id = R.string.text_work_manager)
                                    1 -> stringResource(id = R.string.text_android_job)
                                    else -> stringResource(id = R.string.text_alarm_manager)
                                }
                            )
                        }
                    }
                }
            }

        }
    )
}