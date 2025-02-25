package org.autojs.autoxjs.ui.main.drawer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.PreferenceManager
import coil.compose.rememberAsyncImagePainter
import com.stardust.app.DialogUtils
import com.stardust.app.GlobalAppContext
import com.stardust.app.isOpPermissionGranted
import com.stardust.app.permission.DrawOverlaysPermission
import com.stardust.app.permission.DrawOverlaysPermission.launchCanDrawOverlaysSettings
import com.stardust.app.permission.PermissionsSettingsUtil
import com.stardust.autojs.core.ozobi.shizuku.OzobiShizuku
import com.stardust.autojs.core.ozobi.voiceassistant.OzobiAssistInteractionService
import com.stardust.autojs.runtime.DeviceAdminReceiverMsg
import com.stardust.enhancedfloaty.FloatyService
import com.stardust.notification.NotificationListenerService
import com.stardust.toast
import com.stardust.util.ClipboardUtil
import com.stardust.util.IntentUtil
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
import org.autojs.autoxjs.R
import org.autojs.autoxjs.autojs.AutoJs
import org.autojs.autoxjs.devplugin.DevPlugin
import org.autojs.autoxjs.external.foreground.ForegroundService
import org.autojs.autoxjs.tool.AccessibilityServiceTool
import org.autojs.autoxjs.tool.WifiTool
import org.autojs.autoxjs.ui.build.MyTextField
import org.autojs.autoxjs.ui.common.OperationDialogBuilder
import org.autojs.autoxjs.ui.compose.theme.AutoXJsTheme
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

private var alwaysTryToConnectState = false
private var isFirstTime = true
private lateinit var devicePolicyManager: DevicePolicyManager
private lateinit var componentName: ComponentName
private val ozobiShizuku = OzobiShizuku()
private const val ozobiLogTag = "ozobiLog"
private const val ozobiSubfix = "_ozobi"
//
@Composable
fun DrawerPage() {
    val context = LocalContext.current
    
    
    if(isFirstTime){
        
        isFirstTime = false
    }else{
        
        startUpCheck()
    }
    // <
    
    devicePolicyManager = com.stardust.autojs.runtime.DevicePolicyManager.devicePolicyManager
    componentName = com.stardust.autojs.runtime.DevicePolicyManager.componentName
    // <
    rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.autojs_logo1),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                )
            }
            SwitchClassifyTittle(text = stringResource(id = R.string.text_service))
//            ShizukuSwitch()
//            DeviceManagerSwitch()
//            VoiceAssistantSwitch()
            AccessibilityServiceSwitch()
            StableModeSwitch()
            NotificationUsageRightSwitch()
            ForegroundServiceSwitch()
            UsageStatsPermissionSwitch()

            SwitchClassifyTittle(text = stringResource(id = R.string.text_script_record))
            FloatingWindowSwitch()
            VolumeDownControlSwitch()


            SwitchClassifyTittle("连接")
            ConnectComputerSwitch()
            AlwaysTryToConnect()
            USBDebugSwitch()
            
            SwitchClassifyTittle("布局分析")
            layoutInsWaitForCaptureSwitch()
            layoutInsDelayCaptureSwitch()
            layoutInsScreenshotSwitch()
            layoutInsRefreshSwitch()
            // <
//            nightModeSwitch()
            showModificationDetailsButton()
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
}

@Composable
private fun SwitchClassifyTittle(text:String){
    var color = Color.Black
    if(isNightMode()){
        color = Color.White
    }
    Text(text= text, style = TextStyle(color= color, fontStyle = FontStyle.Italic), fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp,15.dp,0.dp,0.dp), fontSize = 16.sp)
}
@Composable
fun isNightMode():Boolean{
//    val context = LocalContext.current
//    return PreferenceManager.getDefaultSharedPreferences(context)

//    return isSystemInDarkTheme()
    return (LocalContext.current.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}
fun isNightModeNormal(context: Context?):Boolean{
    if(context == null){
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
        Text(text =stringResource(R.string.text_app_download_link)+ozobiSubfix)
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
        Text(text = stringResource(R.string.text_project_link) + ozobiSubfix)
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
            MyIcon(imageVector = Icons.Default.Settings, contentDescription = null,nightMode=isNightMode())
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
            MyIcon(imageVector = Icons.Default.ExitToApp, contentDescription = null,nightMode=isNightMode())
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
    AutoJs.getInstance().scriptEngineService.stopAll()
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
                contentDescription = null,nightMode=isNightMode()
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
                        
                        if(alwaysTryToConnectState){
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
                null,nightMode=isNightMode()
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
private fun ShizukuSwitch(){
    val context = LocalContext.current
    var isShizukuActive by remember {
        mutableStateOf(ozobiShizuku.checkPermission())
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            isShizukuActive = ozobiShizuku.checkPermission()
        }
    )
//    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit, block = {

    })
    SwitchItem(
        icon = {
            MyIcon(
                Icons.Default.Warning,
                contentDescription = null,nightMode=isNightMode()
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.text_Shizuku
                ),
                modifier = Modifier
                    .background(Color(0x33df73ff))
            )
        },
        checked = isShizukuActive
    ) {
        if(ozobiShizuku.checkPermission()){
            // 提示已激活
            Toast.makeText(context,"Shizuku 已激活",Toast.LENGTH_SHORT).show()
        }else{
            // 打开shizuku
            val packageName = "moe.shizuku.privileged.api"
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                launcher.launch(intent)
            } else {
                Toast.makeText(context, "Shizuku 未安装", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
@Composable
private fun VoiceAssistantSwitch(){
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
                contentDescription = null,nightMode=isNightMode()
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
        if(!OzobiAssistInteractionService.active){
            // 打开数字助理设置页面
            val intent = Intent( Settings.ACTION_VOICE_INPUT_SETTINGS )
            context.startActivity(intent)
            Toast.makeText(context,"请选择 Autox.js v6_ozobi",Toast.LENGTH_SHORT).show()
        }else{
            // 关闭数字助理服务?
            val intent = Intent( Settings.ACTION_VOICE_INPUT_SETTINGS )
            context.startActivity(intent)
        }
    }
}
// <


@Composable
private fun DeviceManagerSwitch(){
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
                contentDescription = null,nightMode=isNightMode()
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
suspend fun checkDeviceManagerStatus(interval:Long, count:Int,callBack:()->Unit){
    
    var countLeft = count
    val initStatus = devicePolicyManager.isAdminActive(componentName)
    while (countLeft > 0){
        if(interval > 0){
            delay(interval)
        }else{
            delay(500L)
        }
        countLeft--
        if(initStatus != devicePolicyManager.isAdminActive(componentName)){
            
            callBack()
            return
        }
    }
    
    return
}
// <


@Composable
private fun AlwaysTryToConnect(){
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
                null,nightMode=isNightMode()
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
            }else{
                
                alwaysTryToConnectState = false
            }
        }
    )
}

@OptIn(DelicateCoroutinesApi::class)
fun checkConnectState(context: Context){
    val curCheckingStatus = PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(context.getString(R.string.key_cur_check_connection_status), false)
    if(curCheckingStatus){
        
        return
    }
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(context.getString(R.string.key_cur_check_connection_status), true)
        .apply()
    GlobalScope.launch{
        delay(5000L)
        
        while (true){
            if(alwaysTryToConnectState){
                
                if(!DevPlugin.isActive){
                    val host = Pref.getServerAddressOrDefault(WifiTool.getRouterIp(context))
                    DevPlugin.connect(getUrl(host))
                }
            }else{
                
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
fun startUpCheck(){
    DevPlugin.isFirstTime = true
    
    val context = LocalContext.current
    val host by remember {
        mutableStateOf(Pref.getServerAddressOrDefault(WifiTool.getRouterIp(context)))
    }
    val connected by remember {
        mutableStateOf(DevPlugin.isActive)
    }
    val scope = rememberCoroutineScope()
    scope.launch{
        if(!connected){
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
    GlobalScope.launch { DevPlugin.connect(url) }
}

private fun getUrl(host: String): String {
    var url1 = host
    if (!url1.matches(Regex("^(ws|wss)://.*"))) {
        url1 = "ws://${url1}"
    }
    if (!url1.matches(Regex("^.+://.+?:.+$"))) {
        url1 += ":${DevPlugin.SERVER_PORT}"
    }
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
                null,nightMode=isNightMode()
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
                null,nightMode=isNightMode()
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
                null,nightMode=isNightMode()
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
                Icons.Default.Settings,
                null,nightMode=isNightMode()
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
                Icons.Default.Settings,
                contentDescription = null,nightMode=isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.text_foreground_service)) },
        checked = isOpenForegroundServices,
        onCheckedChange = {
            if (it) {
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
                null,nightMode=isNightMode()
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
                contentDescription = null,nightMode=isNightMode()
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
    var editor by remember { mutableStateOf(Pref.getEditor()) }
    SwitchItem(
        icon = {
            MyIcon(
                Icons.Default.Edit,
                contentDescription = null,nightMode=isNightMode()
            )
        },
        text = { Text(text = "启用新编辑器") },
        checked = editor,
        onCheckedChange = { isChecked ->
            editor = isChecked
            Pref.setEditor(isChecked)
        }
    )
    SwitchItem(
        icon = {
            MyIcon(
                Icons.Default.Settings,
                contentDescription = null,nightMode=isNightMode()
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
private fun layoutInsScreenshotSwitch() {
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
                modifier = Modifier.size(24.dp),nightMode=isNightMode()
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
private fun layoutInsRefreshSwitch() {
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
                modifier = Modifier.size(24.dp),nightMode=isNightMode()
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
private fun layoutInsWaitForCaptureSwitch() {
    val context = LocalContext.current
    var isCaptureScreenshot by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_isWaitFor_capture), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_timed_task),
                contentDescription = null,
                modifier = Modifier.size(24.dp),nightMode=isNightMode()
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
private fun layoutInsDelayCaptureSwitch() {
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
                modifier = Modifier.size(24.dp),nightMode=isNightMode()
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
// <

@Composable
private fun nightModeSwitch() {
    val context = LocalContext.current
    var isCaptureScreenshot by remember {
        val default = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getString(R.string.ozobi_key_isNightMode), false)
        mutableStateOf(default)
    }
    SwitchItem(
        icon = {
            MyIcon(
                painterResource(id = R.drawable.ic_night_mode),
                contentDescription = null,
                modifier = Modifier.size(24.dp),nightMode=isNightMode()
            )
        },
        text = { Text(text = stringResource(id = R.string.ozobi_text_isNightMode)) },
        checked = isCaptureScreenshot,
        onCheckedChange = {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(context.getString(R.string.ozobi_key_isNightMode), it)
                .apply()
            isCaptureScreenshot = it
        }
    )
}

@Composable
fun showModificationDetailsButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    TextButton(onClick = {
        scope.launch{
            detailsDialog(context)
        }
    }) {
        Text(text = stringResource(id = R.string.ozobi_modification_content)+ozobiSubfix)
    }
}

fun detailsDialog(context: Context){
    val detailsDialog = OperationDialogBuilder(context)
        .item(
            R.id.qq_communication_group,
            R.drawable.ic_group_black_48dp,
            "QQ交流群: "+context.resources.getString(R.string.qq_communication_group)
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6588->6589 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化: 还是布局层次分析页面\n"+
                    "-就, 好看了一点吧(也可能是我谦虚了\n\n"+
            "修复(658): 布局层次分析页面\n"+
                    "-显示选中不唯一\n"+
                    "-返回无法关闭页面\n\n"+
            "添加: 布局层次分析页面:\n"+
                    "-施法按钮\n"+
                    "\t\t数数？为什么不用法术(@-@)\n"+
                    "-给当前选中节点周围添加标记\n"+
                    "\t\t没有火眼金睛? 不要紧, 我来助你\n"+
                    "-切换是否可以折叠(化bug为功能:D)\n"+
                    "-显示描述和文本\n"+
                    "-标记当前选中节点的兄弟\n"+
                    "-标记当前选中节点的孩子\n"+
                    "-标记当前选中节点的所有直系长辈(大概就这个意思-.-)\n"+
                    "-布局分析, 为所欲为QwQ"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6587->6588 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化: 夜间模式\n\n"+
            "优化: 布局层次分析页面\n"+
                    "-修复展开后不可收起\n"+
                    "-隐藏按钮可拖动\n\n"+
            "修复(6587): 布局分析相关 bug\n\n"+
            "更改(658): app抽屉页面使用随机彩色图标\n\n"+
            "修复(6587): app布局分析刷新显示不全\n"+
                    "-一般用不到刷新, 除非画面发生变动之后捕获结果没有改变\n" +
                    "-(刷新会比等待捕获多花 2-3 倍的时间)\n\n"+
            "添加: app布局分析等待捕获、延迟捕获开关\n"+
                    "-布局分析, 随心所欲(~.-\n\n"+
            "添加: 截图是否返回新的对象\n"+
                    "-let img1 = images.captureScreen(true)\n"+
                    "-let img2 = images.captureScreen(true)\n"+
                    "-即使一直使用同一张缓存图像(屏幕没有发生变化), img1 和 img2 都不会是同一个对象\n"+
                    "-反之如果不加参数 true, img1 === img2"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6586->6587 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "添加: 获取屏幕实时宽高\n"+
                    "-let curW = device.getCurWidth()\n"+
                    "-let curH = device.getCurHeight()\n"+
                    "-let size = device.getCurScreenSize()\n"+
                    "-size.x == curW\n"+
                    "-size.y == curH\n\n"+
            "添加: 获取当前屏幕方向\n"+
                    "-let ori = getCurOrientation()\n"+
                    "-竖屏: 1  横屏: 2\n\n"+
            "添加: app布局分析刷新开关\n" +
                    "-有些情况刷新会出问题(比如某音极速版啥的)，可以关掉刷新，点开悬浮窗后，自己看情况等上一段时间再点分析\n\n"+
            "添加: 通过 setClip 复制的文本会发送到 vscode 的输出\n"+
                    "-例如: app布局分析复制控件属性/生成代码后点击复制\n"+
                    "\t\t脚本使用 setClip\n"+
                    "-(长按手动复制不会触发)\n\n"+
            "优化(658): 减少 app 悬浮窗点击响应时长(慢不了一点\n\n"+
            "更改: app 抽屉页面\n\n"+
            "添加: 将 adbConnect、termux、adbIMEShellCommand、sendTermuxIntent 添加到全局\n\n"+
            "添加: viewUtils\n"+
                    "-let v = viewUtils.findParentById(view,id)\n"+
                    "-let sp = viewUtils.pxToSp(px)\n"+
                    "-let px = viewUtils.dpToPx(dp)\n"+
                    "-let dp = viewUtils.pxToDp(px)\n"+
                    "-let px = viewUtils.spToPx(sp)\n\n"+
            "添加: 获取[raw]悬浮窗 contentView\n"+
                    "-let fw = floaty.window(<frame id=\"content\"></frame>)\n"+
                    "-let contentView = fw.getContentView()\n"+
                    "-contentView === fw.content"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6585->6586 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化: 启动 app 自动连接不显示 toast\n\n"+
            "升级: SDK35、gradle-8.7、AGP-8.6.0\n\n"+
            "添加: 获取状态栏高度(px)\n"+
                    "-let h = getStatusBarHeight()\n\n"+
            "添加: 布局分析截图开关\n\n"+
            "添加: 获取当前存在的本地存储 名称[路径] 数组\n"+
                    "-let arr = storages.getExisting([returnPath])"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6584->6585 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
//            "修复(6582): 布局分析影响脚本截图服务\n\n"+
            "添加: 跟踪堆栈行号打印\n"+
                    "-traceLog(\"嘿嘿\"[,path(输出到文件)])\n"+
                    "-(让 bug 无处可藏>_>)\n\n"+
            "添加: 时间戳格式化\n"+
                    "-let ts = Date.now();\n"+
                    "-let fm = dateFormat(ts[,format])\n"+
                    "-format: 时间格式, 默认为 \"yyyy-MM-dd HH:mm:ss.SSS\"\n\n"+
            "添加: 设置 http 代理(options)\n"+
                    "-设置代理: http.get(url, {proxyHost:\"192.168.1.10\", proxyPort:7890})\n"+
                    "-身份认证: {userName:\"Ozobi\", password:"+context.resources.getString(R.string.qq_communication_group)+"}\n\n"+
            "添加: 设置 http 尝试次数、单次尝试超时时间(options)\n"+
                    "-例如: http.get(url, {maxTry:3, timeout: 5000})\n"+
                    "-一共尝试 3 次(默认3), 每次 5s (默认10s)超时\n\n"+
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
            "<=== 6583->6584 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "修复(658):某些设备 RootAutomator 不生效\n\n"+
//            "修复(6583):找不到方法 runtime.adbConnect(string, number)\n\n"+
//            "修复(6583):布局分析时反复申请投影权限\n\n"+
            "添加: Adb输入法\n"+
                    "-let command = runtime.adbIMEShellCommand.inputText(\"嘿嘿\")\n"+
                    "-执行命令: adb shell + command\n"+
                    "-将输出文本 嘿嘿 到当前光标所在位置(需要先启用然后设置为当前输入法)\n\n"+
                    "-enableAdbIME() 启用adb输入法\n"+
                    "-setAdbIME() 设置adb输入法为当前输入法\n"+
                    "-resetIME() 重置输入法\n"+
                    "-clearAllText() 清除所有文本\n"+
                    "-inputTextB64(text) 如果inputText没用试试这个\n"+
                    "-inputKey(keyCode) 输入按键\n"+
                    "-inputCombKey(metaKey, keyCode) 组合键\n"+
                    "-inputCombKey(metaKey[], keyCode) 多meta组合键\n\n"+
                    "-meta 键对照:\n"+
                    "-SHIFT == 1\n" +
                    "-SHIFT_LEFT == 64\n" +
                    "-SHIFT_RIGHT == 128\n" +
                    "-CTRL == 4096\n" +
                    "-CTRL_LEFT == 8192\n" +
                    "-CTRL_RIGHT == 16384\n" +
                    "-ALT == 2\n" +
                    "-ALT_LEFT == 16\n" +
                    "-ALT_RIGHT == 32\n"+
                    "-输入组合键: ctrl+shift+v:\n"+
                    "-adb shell + runtime.adbIMEShellCommand.inputCombKey([4096,1], 50)\n\n"+
                    "-调用 termux\n"+
                    "-安装 termux(版本需0.95以上)\n"+
                    "-编辑 ~/.termux/termux.properties 文件, 将 allow-external-apps=true 前面的注释#去掉, 保存退出\n"+
                    "-安装 adb 工具\n"+
                    "-pkg update\n"+
                    "-pkg install android-tools\n"+
                    "-adb连接手机后授权 autoxjs(打包后的应用也需要授权)\n"+
                    "-(如果有)手机需要开启 USB调试(安全设置)\n"+
                    "-adb shell pm grant 包名 com.termux.permission.RUN_COMMAND\n"+
                    "-调用: runtime.termux(\"adb shell input keyevent 3\") 返回桌面\n"+
                    "-这里默认后台执行, 若想使用自己构建的 intent 可以使用 runtime.sendTermuxIntent(intent)"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 6582->6583 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
        "添加: 远程AdbShell(好像不支持远程配对, 手机需要设置 adb 监听端口)\n"+
                    "-let adbShell = runtime.adbConnect(host,port)连接设备\n"+
                    "-adbShell.exec(\"ls /\") 执行命令\n"+
                    "-adbShell.close() 断开连接\n"+
                    "-adbShell.connection.getHost() 获取当前连接主机名\n"+
                    "-adbShell.connection.getPost() 获取当前连接端口\n\n"+
            "修改: 将悬浮窗位置改为以屏幕左上角为原点(终于可以指哪打哪了\n"+
            ">_<)\n\n"+
//            "修复(6582): 脚本请求截图权限后再进行布局分析时打不开悬浮窗\n\n"+
            "增强: 使用相对路径显示本地图片\n"+
                    "-<img src=\"./pic.png\" />\n"+
                    "-./ 等于 file://当前引擎的工作目录/"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_edit_black_48dp,
            "<=== 658->6582 ===>"
        )
        .item(
            R.id.modification_detail,
            R.drawable.ic_ali_log,
            "优化?: 现在可以从 vscode 的插件选择运行项目, vscode打开项目新建一个 project.json 文件,里面有{}就可以, 再将主脚本文件命名为 main.js 即可\n\n"+
                "修复(658): 老版编辑器长按删除崩溃\n\n"+
                 "添加: 添加 v2本地、在线文档\n\n"+
                "app功能\n" +
                "\t\t添加连上为止\n" +
                "\t\t软件启动时会尝试连接电脑一次\n" +
                "\t\t打开之后会一直尝试连接电脑，直到连上为止，除非手动关闭\n" +
                "\t\t被动和主动断开连接电脑，都会触发一直尝试连接，除非手动关闭(可能还是有bug, 某些情况会连接多次\n\n"+
                "app布局分析\n" +
                "\t\t每次分析都会刷新页面节点信息，下拉状态栏可打断刷新，同时会大概率丢失页面节点信息\n" +
                "\t\t添加延迟选项。选择其中一个选项之后会延迟相应的时间之后进行布局分析，等待期间无法再次打开布局分析对话框。\n" +
                "\t\t添加显示上次节点信息选项。可重新分析上一次刷新的节点信息\n\n"+
                "app布局范围分析\n" +
                "\t\t根据控件属性使用不同的颜色\n" +
                "\t\t绿色：可点击\n" +
                "\t\t紫色：有描述\n" +
                "\t\t紫红色：有文本\n" +
                "\t\t白色：上面三个都没有\n" +
                "\t\t同一控件显示颜色优先级顺序同上\n" +
                "\t\t如果两个控件bounds重叠，子控件的颜色会盖住父控件的\n\n"+
                "app布局层次分析\n" +
                "\t\t将控件的 depth、是否可点击、是否有描述、是否有文本 显示在外面\n" +
                "\t\t添加展开按钮(展开当前选中的控件的全部孩子控件)\n"+
                "\t\t添加转到布局范围按钮\n"+
                "这个层次分析页面还有待改进\n\n"+
                "app布局分析属性\n" +
                "\t\t将控件的常用属性（个人认为）往前排\n\n"+
                "代码布局分析\n" +
                "\t\t给 UiSelector.find() 添加刷新参数\n" +
                "\t\t例如：text('嘿嘿').find(true)\n" +
                "\t\t将会先刷新页面节点信息，然后再返回刷新后的寻找结果\n"+
                "\t\t？怎么知道有用呢？可以拿某手国际版来开刀，试试刷新和不刷新的区别"
        )
        .title("魔改内容")
        .build()
    DialogUtils.showDialog(detailsDialog)
    val qq = context.resources.getString(R.string.qq_communication_group)
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