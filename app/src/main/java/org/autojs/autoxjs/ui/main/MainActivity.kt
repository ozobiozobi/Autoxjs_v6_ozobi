package org.autojs.autoxjs.ui.main

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DrawerState
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.preference.PreferenceManager
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.leinardi.android.speeddial.compose.FabWithLabel
import com.stardust.app.GlobalAppContext.post
import com.stardust.app.permission.DrawOverlaysPermission
import com.stardust.autojs.core.permission.StoragePermissionUtils
import com.stardust.autojs.core.permission.StoragePermissionUtils.getMediaPermissionList
import com.stardust.autojs.execution.ScriptExecution
import com.stardust.autojs.execution.ScriptExecutionListener
import com.stardust.autojs.execution.SimpleScriptExecutionListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.autojs.autoxjs.Pref
import org.autojs.autoxjs.R
import org.autojs.autoxjs.autojs.AutoJs
import org.autojs.autoxjs.external.foreground.ForegroundService
import org.autojs.autoxjs.network.MessengerServiceConnection
import org.autojs.autoxjs.network.ozobi.KtorDocsService
import org.autojs.autoxjs.network.ozobi.KtorDocsService.Companion.copyFileFromAssets
import org.autojs.autoxjs.ozobi.dialogs.NewFileDialog
import org.autojs.autoxjs.storage.database.ModelChange
import org.autojs.autoxjs.timing.IntentTask
import org.autojs.autoxjs.timing.TimedTask
import org.autojs.autoxjs.timing.TimedTaskManager.intentTaskChanges
import org.autojs.autoxjs.timing.TimedTaskManager.timeTaskChanges
import org.autojs.autoxjs.timing.TimedTaskScheduler
import org.autojs.autoxjs.ui.build.ProjectConfigActivity
import org.autojs.autoxjs.ui.build.ProjectConfigActivity_
import org.autojs.autoxjs.ui.compose.theme.AutoXJsTheme
import org.autojs.autoxjs.ui.compose.widget.SearchBox2
import org.autojs.autoxjs.ui.floating.FloatyWindowManger
import org.autojs.autoxjs.ui.main.fileitem.FileItem
import org.autojs.autoxjs.ui.main.MainActivity.Companion.beforeFilterFileList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.beforeFilterFolderList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curDisplayPath
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curFilterFileList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.curFilterFolderList
import org.autojs.autoxjs.ui.main.MainActivity.Companion.isSearching
import org.autojs.autoxjs.ui.main.MainActivity.Companion.lastOperationFilePath
import org.autojs.autoxjs.ui.main.components.DocumentPageMenuButton
import org.autojs.autoxjs.ui.main.components.LogButton
import org.autojs.autoxjs.ui.main.drawer.DrawerPage
import org.autojs.autoxjs.ui.main.fileitem.getFileItems
import org.autojs.autoxjs.ui.main.task.Task.PendingTask
import org.autojs.autoxjs.ui.main.task.Task.RunningTask
import java.io.File


data class BottomNavigationItem(val icon: Int, val label: String)

class MainActivity : ComponentActivity() {

    companion object {
        @JvmStatic
        fun getIntent(context: Context) = Intent(context, MainActivity::class.java)

        var curDisplayPath by mutableStateOf("")
        var multiSelect by mutableStateOf(false)
        val curSelectedFileMap = mutableStateMapOf<String, FileItem>()
        val curSelectedFolderMap = mutableStateMapOf<String, FileItem>()
        val environmentPath: String = Environment.getExternalStorageDirectory().path
        val lastOperationFilePath = mutableStateOf("")
        val curScriptFilePath = mutableStateOf(File(Pref.getScriptDirPath()).absolutePath)
        val curRunningTaskList = mutableStateListOf<RunningTask>()
        val curPendingTaskList = mutableStateListOf<PendingTask>()
        val curPathScrollStateMap = mutableStateMapOf(
            curDisplayPath to LazyListState()
        )
        val curFilterFolderList = mutableStateListOf<FileItem>()
        val curFilterFileList = mutableStateListOf<FileItem>()
        val beforeFilterFolderList = mutableStateListOf<FileItem>()
        val beforeFilterFileList = mutableStateListOf<FileItem>()
        val isSearching = mutableStateOf(false)

        fun addCurRunningTask(task: RunningTask) {
            val index = indexOfExecution(task.scriptExecution)
            if (index == -1) {
                curRunningTaskList.add(task)
            }
        }

        fun removeCurRunningTask(execution: ScriptExecution) {
            val index = indexOfExecution(execution)
            if (index != -1) {
                curRunningTaskList.removeAt(index)
            }
        }

        fun indexOfExecution(execution: ScriptExecution): Int {
            for (i in curRunningTaskList.indices) {
                if (curRunningTaskList[i].scriptExecution == execution) {
                    return i
                }
            }
            return -1
        }

        fun addPendingTask(task: Any) {
            when (task) {
                is TimedTask -> curPendingTaskList.add(PendingTask(task))
                is IntentTask -> curPendingTaskList.add(PendingTask(task))
            }
        }

        fun removePendingTask(data: Any) {
            val index = indexOfTask(data)
            if (index != -1) {
                curPendingTaskList.removeAt(index)
            }
        }

        fun indexOfTask(data: Any): Int {
            for (i in curPendingTaskList.indices) {
                if (curPendingTaskList[i].taskEquals(data)) {
                    return i
                }
            }
            return -1
        }
    }

    //    private val scriptListFragment by lazy { ScriptListFragment() }
//    private val taskManagerFragment by lazy { TaskManagerFragmentKt() }
//    private lateinit var webViewFragment : EditorAppManager
    private var lastBackPressedTime = 0L
    private var drawerState: DrawerState? = null

    //    private val viewPager: ViewPager2 by lazy { ViewPager2(this) }
    private var scope: CoroutineScope? = null
    private lateinit var serviceConnection: MessengerServiceConnection

    private var mTimedTaskChangeDisposable: Disposable? = null
    private var mIntentTaskChangeDisposable: Disposable? = null

    private val explorerViewModel: ExplorerViewModel by viewModels()

    private var docsWebView: WebView? = null

    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(ExperimentalPermissionsApi::class, DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 检查是否拥有管理所有文件的权限
        if (!StoragePermissionUtils.hasManageAllFilesPermission()) {
            // 如果没有权限，跳转到授权页面
            StoragePermissionUtils.requestManageAllFilesPermission(this)
        }

        if (Pref.isForegroundServiceEnabled()) ForegroundService.start(this)
        else ForegroundService.stop(this)

        if (Pref.isFloatingMenuShown() && !FloatyWindowManger.isCircularMenuShowing()) {
            if (DrawOverlaysPermission.isCanDrawOverlays(this)) FloatyWindowManger.showCircularMenu()
            else Pref.setFloatingMenuShown(false)
        }
        serviceConnection = MessengerServiceConnection(Looper.getMainLooper())
        val intent = Intent("com.stardust.autojs.messengerAction")
        intent.setPackage(this.packageName)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        KtorDocsService.getDocs(this)
        val isDocsServiceRunning =
            isServiceRunning(this, "org.autojs.autoxjs.network.ozobi.KtorDocsService")
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putBoolean(
                applicationContext.getString(R.string.ozobi_key_docs_service),
                isDocsServiceRunning
            )
            .apply()

        AutoJs.getInstance().scriptEngineService.get()?.registerGlobalScriptExecutionListener(
            mScriptExecutionListener
        )
        mTimedTaskChangeDisposable = timeTaskChanges
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onTimedTaskChange)
        mIntentTaskChangeDisposable = intentTaskChanges
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onIntentTaskChange)

        copySampleScriptsToScriptPath()

        refreshCurPendingTaskList()
        refreshCurRunningTaskList()

        initExplorerViewModel(explorerViewModel)

        setContent {
            scope = rememberCoroutineScope()
            AutoXJsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val permission = rememberExternalStoragePermissionsState(LocalContext.current) {
//                        if (it) {
//                            scriptListFragment.explorerView.onRefresh()
//                        }
                    }
                    LaunchedEffect(key1 = Unit, block = {
                        permission.launchMultiplePermissionRequest()
                    })
                    MainPage(
                        explorerViewModel,
                        getDocsWebView = { docsWebView },
//                        docsWebView,
//                        activity = this,
//                        scriptListFragment = scriptListFragment,
//                        taskManagerFragment = taskManagerFragment,
//                        webViewFragment = webViewFragment,
                        onDrawerState = {
                            this.drawerState = it
                        },
                        onInitDocsWebView = {
                            docsWebView = it
                        }
//                        viewPager = viewPager
                    )
                }
            }
        }
        GlobalScope.launch {
            delay(1000L)
            var tryCount = 0
            while (Pref.getScriptDirPath().isNullOrEmpty()) {
                tryCount++
                delay(1000L)
            }
            curDisplayPath = Pref.getScriptDirPath()
            val scriptFile = File(curDisplayPath)
            curDisplayPath = scriptFile.absolutePath
        }
    }

    private fun copySampleScriptsToScriptPath() {
        copyFileFromAssets(
            this.assets,
            "sample",
            curScriptFilePath.value + "/" + this.getString(R.string.text_sample)
        )
    }

    private fun initExplorerViewModel(viewModel: ExplorerViewModel) {
        viewModel.updateCurDisplayPath(Pref.getScriptDirPath())
        viewModel.updateCurSortBy(Pref.getExplorerCurSortBy())
        viewModel.updateIsDesSort(Pref.getExplorerIsDesSort())
    }

    private fun onIntentTaskChange(taskChange: ModelChange<IntentTask>) {
        if (taskChange.action == ModelChange.INSERT) {
            addPendingTask(taskChange.data)
        } else if (taskChange.action == ModelChange.DELETE) {
            removePendingTask(taskChange.data)
        } else if (taskChange.action == ModelChange.UPDATE) {
            val index = indexOfTask(taskChange.data)
            if (index != -1) {
                curPendingTaskList[index] = PendingTask(taskChange.data)
            }
        }
    }

    private fun onTimedTaskChange(taskChange: ModelChange<TimedTask>) {
        if (taskChange.action == ModelChange.INSERT) {
            addPendingTask(taskChange.data)
        } else if (taskChange.action == ModelChange.DELETE) {
            removePendingTask(taskChange.data)
        } else if (taskChange.action == ModelChange.UPDATE) {
            val index = indexOfTask(taskChange.data)
            if (index != -1) {
                curPendingTaskList[index] = PendingTask(taskChange.data)
            }
        }
    }

    private val mScriptExecutionListener: ScriptExecutionListener =
        object : SimpleScriptExecutionListener() {
            override fun onStart(execution: ScriptExecution) {
                val task = RunningTask(execution)
                addCurRunningTask(task)
            }

            override fun onSuccess(execution: ScriptExecution, result: Any?) {
                onFinish(execution)
            }

            override fun onException(execution: ScriptExecution, e: Throwable) {
                onFinish(execution)
            }

            private fun onFinish(execution: ScriptExecution) {
                post(Runnable {
                    removeCurRunningTask(execution)
                })
            }
        }

    private fun isServiceRunning(context: Context, serviceName: String): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceName == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        curScriptFilePath.value = File(Pref.getScriptDirPath()).absolutePath
        TimedTaskScheduler.ensureCheckTaskWorks(application)
        refreshExplorerList(
            curDisplayPath,
            onDisPlayPathChange = { curDisplayPath = it },
            onBeforeRefreshPathChange = { explorerViewModel.updateCurDisplayPath(it) })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        AutoJs.getInstance().scriptEngineService.get()?.unregisterGlobalScriptExecutionListener(
            mScriptExecutionListener
        )
        mTimedTaskChangeDisposable!!.dispose()
        mIntentTaskChangeDisposable!!.dispose()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerState?.isOpen == true) {
            scope?.launch { drawerState?.close() }
            return
        }
//        if (viewPager.currentItem == 0 && scriptListFragment.onBackPressed()) {
//            return
//        }
        back()
    }

    private fun back() {
        if (isSearching.value) {
            isSearching.value = false
            return
        }
        if (multiSelect) {
            multiSelect = false
            curSelectedFileMap.clear()
            curSelectedFolderMap.clear()
            return
        }
        if (curDisplayPath.contains("$environmentPath/")) {
            lastOperationFilePath.value = curDisplayPath
            curPathScrollStateMap.remove(curDisplayPath)
            curDisplayPath = curDisplayPath.substring(0, curDisplayPath.lastIndexOf("/"))
            clearSelectedMap()
        } else {
            val currentTime = System.currentTimeMillis()
            val interval = currentTime - lastBackPressedTime
            if (interval > 2000) {
                lastBackPressedTime = currentTime
                Toast.makeText(
                    this,
                    getString(R.string.text_press_again_to_exit),
                    Toast.LENGTH_SHORT
                ).show()
            } else super.onBackPressed()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun hasNotificationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainPage(
    viewModel: ExplorerViewModel,
    getDocsWebView: () -> WebView?,
//    activity: FragmentActivity,
//    scriptListFragment: ScriptListFragment,
//    taskManagerFragment: TaskManagerFragmentKt,
//    webViewFragment: EditorAppManager,
    onDrawerState: (DrawerState) -> Unit,
    onInitDocsWebView: (WebView) -> Unit
//    viewPager: ViewPager2
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    onDrawerState(scaffoldState.drawerState)
    val scope = rememberCoroutineScope()

    val bottomBarItems = remember {
        getBottomItems(context)
    }
//    var currentPage by remember {
//        mutableStateOf(0)
//    }

    val pageState = rememberPagerState()

    SetSystemUI(scaffoldState)

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        topBar = {
            Surface(elevation = 4.dp, color = MaterialTheme.colors.primarySurface) {
                Column() {
                    Spacer(
                        modifier = Modifier
                            .windowInsetsTopHeight(WindowInsets.statusBars)
                    )
                    TopBar(
                        viewModel,
                        getDocsWebView = getDocsWebView,
//                        docsWebView,
                        currentPage = pageState.currentPage,
                        requestOpenDrawer = {
                            scope.launch { scaffoldState.drawerState.open() }
                        },
//                        onSearch = { keyword ->
////                            scriptListFragment.explorerView.setFilter { it.name.contains(keyword) }
//                        },
//                        scriptListFragment = scriptListFragment,
//                        webViewFragment = webViewFragment
                    )
                }
            }
        },
        bottomBar = {
            Surface(elevation = 4.dp, color = MaterialTheme.colors.surface) {
                Column {
                    BottomBar(bottomBarItems, pageState, onSelectedChange = {
                        scope.launch {
//                            pageState.animateScrollToPage(it)
                            pageState.scrollToPage(it)
                        }
                    })
                    Spacer(
                        modifier = Modifier
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    )
                }
            }
        },
        drawerContent = {
            DrawerPage()
        },

        ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HorizontalPager(
                state = pageState,
                count = 3,
                userScrollEnabled = false,
                modifier = Modifier
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> HomePage(viewModel)
                    1 -> TaskManagePage()
                    2 -> DocsPage(onInitDocsWebView)
                }
            }
        }
//        AndroidView(
//            modifier = Modifier.padding(it),
//            factory = {
//                viewPager.apply {
//                    fillMaxSize()
//                    adapter = ViewPager2Adapter(
//                        activity,
//                        scriptListFragment,
//                        taskManagerFragment,
//                        webViewFragment
//                    )
//                    isUserInputEnabled = false
//                    ViewCompat.setNestedScrollingEnabled(this, true)
//                }
//            },
//            update = { viewPager0 ->
//                viewPager0.currentItem = currentPage
//            }
//        )
    }
}


fun showExternalStoragePermissionToast(context: Context) {
    Toast.makeText(
        context,
        context.getString(R.string.text_please_enable_external_storage),
        Toast.LENGTH_SHORT
    ).show()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberExternalStoragePermissionsState(
    context: Context,
    onPermissionsResult: (allAllow: Boolean) -> Unit
) =
    rememberMultiplePermissionsState(
        permissions = getMediaPermissionList(context),
        onPermissionsResult = { map ->
            onPermissionsResult(map.all { it.value })
        })

@Composable
private fun SetSystemUI(scaffoldState: ScaffoldState) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons =
        if (MaterialTheme.colors.isLight) {
            scaffoldState.drawerState.isOpen || scaffoldState.drawerState.isAnimationRunning
        } else false

    val navigationUseDarkIcons = MaterialTheme.colors.isLight
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
        systemUiController.setNavigationBarColor(
            Color.Transparent,
            darkIcons = navigationUseDarkIcons
        )
    }
}

private fun getBottomItems(context: Context) = mutableStateListOf(
    BottomNavigationItem(
        R.drawable.ic_home,
        context.getString(R.string.text_home)
    ),
    BottomNavigationItem(
        R.drawable.ic_manage,
        context.getString(R.string.text_management)
    ),
    BottomNavigationItem(
        R.drawable.ic_web,
        context.getString(R.string.text_document)
    )
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomBar(
    items: List<BottomNavigationItem>,
    currentSelected: PagerState,
    onSelectedChange: (Int) -> Unit
) {
    val context = LocalContext.current
    BottomNavigation(elevation = 0.dp, backgroundColor = MaterialTheme.colors.background) {
        items.forEachIndexed { index, item ->
            val selected = currentSelected.currentPage == index
            val color = if (selected) MaterialTheme.colors.primary else Color.Gray
            BottomNavigationItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onSelectedChange(index)
                    }
                    if (selected && index == 0) {
                        val scriptFile = File(Pref.getScriptDirPath())
                        if (curDisplayPath != scriptFile.absolutePath) {
                            curDisplayPath = scriptFile.absolutePath
                            Toast.makeText(context, context.getString(R.string.text_back_to_script_path), Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = color
                    )
                },
                label = {
                    Text(text = item.label, color = color)
                }
            )
        }
    }
}

fun refreshCurFilterList() {
    beforeFilterFolderList.clear()
    beforeFilterFileList.clear()
    curFilterFolderList.clear()
    curFilterFileList.clear()
    getFileItems(curDisplayPath) {
        return@getFileItems it.isDirectory
    }.forEach { folder ->
        beforeFilterFolderList.add(folder)
        curFilterFolderList.add(folder)
    }
    getFileItems(curDisplayPath) {
        return@getFileItems it.isFile
    }.forEach { file ->
        beforeFilterFileList.add(file)
        curFilterFileList.add(file)
    }
}

fun filterCurDisplayPathList(key: String) {
    try {
        if (key.isEmpty()) {
            beforeFilterFolderList.forEach {
                curFilterFolderList.add(it)
            }
            beforeFilterFileList.forEach {
                curFilterFileList.add(it)
            }
        } else {
            val keyRegex = ".*$key.*".toRegex(RegexOption.IGNORE_CASE)
            beforeFilterFolderList.forEach {
                if (keyRegex.matches(it.name)) {
                    curFilterFolderList.add(it)
                }
            }
            beforeFilterFileList.forEach {
                if (keyRegex.matches(it.name)) {
                    curFilterFileList.add(it)
                }
            }
        }
    } catch (e: Exception) {
        Log.e("MainActivity", e.toString())
    }
}

@Composable
private fun TopBar(
    viewModel: ExplorerViewModel,
    getDocsWebView: () -> WebView?,
//    docsWebView:WebView?,
    currentPage: Int,
    requestOpenDrawer: () -> Unit,
//    onSearch: (String) -> Unit,
//    scriptListFragment: ScriptListFragment,
//    webViewFragment: EditorAppManager,
) {
    val context = LocalContext.current
    var keyword by remember {
        mutableStateOf("")
    }
    TopAppBar(elevation = 0.dp) {
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.high,
        ) {
            if (!isSearching.value) {
                IconButton(onClick = requestOpenDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(id = R.string.text_menu),
                    )
                }

                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.app_name)
                    )
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    IconButton(onClick = {
//                        context.startActivity(Intent(context, EditActivity::class.java))
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.Edit,
//                            contentDescription = "editor"
//                        )
//                    }
//                }
                if (currentPage == 0) {
                    IconButton(onClick = {
                        keyword = ""
                        isSearching.value = true
                        refreshCurFilterList()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.text_search)
                        )
                    }
                }
            } else {
                IconButton(onClick = {
                    isSearching.value = false
//                    onSearch("")
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.text_exit_search)
                    )
                }
                SearchBox2(
                    value = keyword,
                    onValueChange = {
                        keyword = it
                        curFilterFolderList.clear()
                        curFilterFileList.clear()
                        filterCurDisplayPathList(it)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = stringResource(id = R.string.text_search)) },
//                    keyboardActions = KeyboardActions(onSearch = {
////                        onSearch(keyword)
//                    })
                )
                if (keyword.isNotEmpty()) {
                    IconButton(onClick = { keyword = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                }
            }
            LogButton()
            when (currentPage) {
                0 -> {
                    var expanded by remember {
                        mutableStateOf(false)
                    }
                    Box() {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.desc_more)
                            )
                        }
                        TopAppBarMenu(
                            viewModel = viewModel,
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
//                            scriptListFragment = scriptListFragment
                        )
                    }
                }

                1 -> {
                    IconButton(onClick = {
                        refreshCurRunningTaskList()
                        refreshCurPendingTaskList()
                        Toast.makeText(
                            context,
                            context.getString(R.string.text_refresh),
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.desc_more)
                        )
                    }
                }

                2 -> {
                    DocumentPageMenuButton {
                        getDocsWebView()
                    }
                }
            }

        }
    }
}


@Composable
fun TopAppBarMenu(
    viewModel: ExplorerViewModel,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    offset: DpOffset = DpOffset.Zero
) {
    val context = LocalContext.current
    val showNewFileDialog = remember { mutableStateOf(false) }
    val showNewFolderDialog = remember { mutableStateOf(false) }
    val showNewProjectDialog = remember { mutableStateOf(false) }
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest, offset = offset) {
        DropdownMenuItem(onClick = {
            showNewProjectDialog.value = true
        }) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_project2),
                    contentDescription = stringResource(R.string.text_project),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.primary
                )
                Spacer(Modifier.size(6.dp))
                Text(stringResource(R.string.text_project))
            }
        }
        DropdownMenuItem(onClick = {
            showNewFolderDialog.value = true
        }) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_folder_black_48dp),
                    contentDescription = stringResource(R.string.text_folder),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.primary
                )
                Spacer(Modifier.size(6.dp))
                Text(stringResource(R.string.text_folder))
            }
        }
        DropdownMenuItem(onClick = {
            showNewFileDialog.value = true
        }) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_floating_action_menu_file),
                    contentDescription = stringResource(R.string.text_file),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colors.secondary
                )
                Spacer(Modifier.size(6.dp))
                Text(stringResource(R.string.text_file))
            }
        }
    }
    if (showNewFileDialog.value) {
        NewFileDialog(curDisplayPath, "", "file", onDismiss = {
            showNewFileDialog.value = false
            onDismissRequest()
        }) { path ->
            val newFile = File(path)
            newFile.createNewFile()
            lastOperationFilePath.value = newFile.absolutePath
            showNewFileDialog.value = false
            onDismissRequest()
            refreshExplorerList(
                curDisplayPath,
                onDisPlayPathChange = { curDisplayPath = it },
                onBeforeRefreshPathChange = { viewModel.updateCurDisplayPath(it) })
        }
    }
    if (showNewFolderDialog.value) {
        NewFileDialog(curDisplayPath, "", "dir", onDismiss = {
            showNewFolderDialog.value = false
            onDismissRequest()
        }) { path ->
            val newFolder = File(path)
            newFolder.mkdirs()
            lastOperationFilePath.value = newFolder.absolutePath
            showNewFolderDialog.value = false
            onDismissRequest()
            refreshExplorerList(
                curDisplayPath,
                onDisPlayPathChange = { curDisplayPath = it },
                onBeforeRefreshPathChange = { viewModel.updateCurDisplayPath(it) })
        }
    }
    if (showNewProjectDialog.value) {
        showNewProjectDialog.value = false
        onDismissRequest()
        ProjectConfigActivity_.intent(context)
            .extra(
                ProjectConfigActivity.EXTRA_PARENT_DIRECTORY,
                curDisplayPath
            )
            .extra(ProjectConfigActivity.EXTRA_NEW_PROJECT, true)
            .start()
    }
}

