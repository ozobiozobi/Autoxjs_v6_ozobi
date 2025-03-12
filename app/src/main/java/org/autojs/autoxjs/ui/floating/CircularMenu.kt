package org.autojs.autoxjs.ui.floating

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.text.TextUtils
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.afollestad.materialdialogs.MaterialDialog
import com.makeramen.roundedimageview.RoundedImageView
import com.stardust.app.DialogUtils
import com.stardust.autojs.core.ozobi.capture.ScreenCapture
import com.stardust.autojs.core.record.Recorder
import com.stardust.autojs.runtime.api.Images
import com.stardust.enhancedfloaty.FloatyService
import com.stardust.enhancedfloaty.FloatyWindow
import com.stardust.util.ClipboardUtil
import com.stardust.util.Ozobi
import com.stardust.view.accessibility.AccessibilityService.Companion.instance
import com.stardust.view.accessibility.LayoutInspector.CaptureAvailableListener
import com.stardust.view.accessibility.NodeInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.autoxjs.Pref
import org.autojs.autoxjs.R
import org.autojs.autoxjs.autojs.AutoJs
import org.autojs.autoxjs.autojs.record.GlobalActionRecorder
import org.autojs.autoxjs.model.explorer.ExplorerDirPage
import org.autojs.autoxjs.model.explorer.Explorers
import org.autojs.autoxjs.model.script.Scripts.run
import org.autojs.autoxjs.theme.dialog.ThemeColorMaterialDialogBuilder
import org.autojs.autoxjs.tool.AccessibilityServiceTool
import org.autojs.autoxjs.tool.RootTool
import org.autojs.autoxjs.ui.common.NotAskAgainDialog
import org.autojs.autoxjs.ui.common.OperationDialogBuilder
import org.autojs.autoxjs.ui.explorer.ExplorerViewKt
import org.autojs.autoxjs.ui.floating.layoutinspector.LayoutBoundsFloatyWindow
import org.autojs.autoxjs.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow
import org.autojs.autoxjs.ui.main.MainActivity
import org.greenrobot.eventbus.EventBus
import org.jdeferred.Deferred
import org.jdeferred.impl.DeferredObject


/**
 * Created by Stardust on 2017/10/18.
 */
@SuppressLint("NonConstantResourceId")
class CircularMenu(context: Context?) : Recorder.OnStateChangedListener, CaptureAvailableListener {
    class StateChangeEvent(val currentState: Int, val previousState: Int)

    private var mWindow: CircularMenuWindow? = null
    private var mState = 0
    private var mActionViewIcon: RoundedImageView? = null
    private val mContext: Context = ContextThemeWrapper(context, R.style.AppTheme)
    private val mRecorder: GlobalActionRecorder
    private var mSettingsDialog: MaterialDialog? = null
    private var mLayoutInspectDialog: MaterialDialog? = null
    // Added by ibozo - 2024/11/02 >
    private var mLastLayoutInspectDialog: MaterialDialog? = null
    private var mCaptureDelayDialog: MaterialDialog? = null
    private val mVibrator:Vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private var mLastCapture:NodeInfo? = null
    private val mLayoutInspector = AutoJs.getInstance().layoutInspector
    private var mIsmCaptureDelayDialogDisappeared = true
    private var isStartCaptureCountDown = false
    private var isStartCapture = false
    private var screenCapture:ScreenCapture = ScreenCapture(mContext)
    private var isCaptureScreenshot = PreferenceManager.getDefaultSharedPreferences(mContext)
        .getBoolean(mContext.getString(R.string.ozobi_key_isCapture_Screenshot), false)
    private var isRefresh = PreferenceManager.getDefaultSharedPreferences(mContext)
        .getBoolean(mContext.getString(R.string.ozobi_key_isCapture_refresh), false)
    private var isWaitForCapture = PreferenceManager.getDefaultSharedPreferences(mContext)
        .getBoolean(mContext.getString(R.string.ozobi_key_isWaitFor_capture), true)
    private var isDelayCapture = PreferenceManager.getDefaultSharedPreferences(mContext)
        .getBoolean(mContext.getString(R.string.ozobi_key_isDelay_capture), false)
    private var captureCostTime = 0L
    private var captureStartTime = 0L
    private var isCapturing = false
    private var available = true
    private var isAuth = false
    // <
    private var mRunningPackage: String? = null
    private var mRunningActivity: String? = null
    private var mCaptureDeferred: Deferred<NodeInfo?, Void, Void>? = null

    @OptIn(DelicateCoroutinesApi::class)
    private fun setupListeners() {
        mWindow?.setOnActionViewTouchListener { v, event ->
             if(event.action == MotionEvent.ACTION_UP){
                 LayoutHierarchyFloatyWindow.firstTagNodeInfo = null
                 LayoutHierarchyFloatyWindow.secondTagNodeInfo = null
                 v.performClick()
                 if (mState == STATE_RECORDING) {
                     stopRecord()
                 } else if (mWindow?.isExpanded == true) {
                    mWindow?.collapse()
                 } else {
                     mWindow?.expand()
                     if(isAuth){
                         isCaptureScreenshot = PreferenceManager.getDefaultSharedPreferences(mContext)
                             .getBoolean(mContext.getString(R.string.ozobi_key_isCapture_Screenshot), false)
                         isRefresh = PreferenceManager.getDefaultSharedPreferences(mContext)
                             .getBoolean(mContext.getString(R.string.ozobi_key_isCapture_refresh), false)
                         isWaitForCapture = PreferenceManager.getDefaultSharedPreferences(mContext)
                             .getBoolean(mContext.getString(R.string.ozobi_key_isWaitFor_capture), true)
                         isDelayCapture = PreferenceManager.getDefaultSharedPreferences(mContext)
                             .getBoolean(mContext.getString(R.string.ozobi_key_isDelay_capture), false)
                         
                         if(isCaptureScreenshot){
                             if(!Images.availale){
                                 screenCapture.stopScreenCapturer()
                                 stopCapture()
                             }
                             GlobalScope.launch {
                                 if(!Images.availale || ScreenCapture.curOrientation != mContext.resources.configuration.orientation){
                                     screenCapture.requestScreenCapture(mContext.resources.configuration.orientation)

                                 }
                             }
                         }
                     }
                     if(isRefresh || isWaitForCapture){
                         NodeInfo.isDoneCapture = false
                     }
                     mLayoutInspector.setRefresh(isRefresh)
                     mCaptureDeferred = DeferredObject()
                     if(!isRefresh && !isStartCapture && !isCapturing && !isDelayCapture){
                         captureStartTime = System.currentTimeMillis()
                         available = mLayoutInspector.captureCurrentWindow()
                         if(available){
                             isCapturing = true
                             checkIsCaptureDone()
                         }
                     }else{
                         if(NodeInfo.isDoneCapture || ((isDelayCapture||isRefresh) && !isStartCapture)){
                             mWindow?.setAlpha(1f)
                         }else{
                             mWindow?.setAlpha(0.7f)
                         }
                     }
                     // <
                 }
            }
            return@setOnActionViewTouchListener true
        }
        
        mWindow?.setOnActionViewClickListener {

        }
    }

    private fun initFloaty() {
        mWindow = CircularMenuWindow(mContext, object : CircularMenuFloaty {
            override fun inflateActionView(
                service: FloatyService,
                window: CircularMenuWindow
            ): View {
                val actionView = View.inflate(service, R.layout.circular_action_view, null)
                mActionViewIcon = actionView.findViewById(R.id.icon)
                return actionView
            }

            override fun inflateMenuItems(
                service: FloatyService,
                window: CircularMenuWindow
            ): CircularActionMenu {
                val menu = View.inflate(
                    ContextThemeWrapper(service, R.style.AppTheme),
                    R.layout.circular_action_menu,
                    null
                ) as CircularActionMenu
                ButterKnife.bind(this@CircularMenu, menu)
                return menu
            }
        })
        mWindow?.setKeepToSideHiddenWidthRadio(0.25f)
        FloatyService.addWindow(mWindow)
    }

    @Optional
    @OnClick(R.id.script_list)
    fun showScriptList() {
        mWindow?.collapse()
        val explorerView = ExplorerViewKt(mContext)
        explorerView.setExplorer(
            Explorers.workspace(),
            ExplorerDirPage.createRoot(Pref.getScriptDirPath())
        )
        explorerView.setDirectorySpanSize(2)
        val dialog = ThemeColorMaterialDialogBuilder(mContext)
            .title(R.string.text_run_script)
            .customView(explorerView, false)
            .positiveText(R.string.text_cancel)
            .build()
        explorerView.setOnItemOperatedListener {
            dialog.dismiss()
        }
        explorerView.setOnItemClickListener { _, item ->
            item?.let { run(item.toScriptFile()) }
        }
        DialogUtils.showDialog(dialog)
    }

    @Optional
    @OnClick(R.id.record)
    fun startRecord() {
        mWindow?.collapse()
        if (!RootTool.isRootAvailable()) {
            DialogUtils.showDialog(NotAskAgainDialog.Builder(mContext, "CircularMenu.root")
                .title(R.string.text_device_not_rooted)
                .content(R.string.prompt_device_not_rooted)
                .neutralText(R.string.text_device_rooted)
                .positiveText(R.string.ok)
                .onNeutral { _, _ -> mRecorder.start() }
                .build())
        } else {
            mRecorder.start()
        }
    }

    private fun setState(state: Int) {
        val previousState = mState
        mState = state
        mActionViewIcon?.setImageResource(if (mState == STATE_RECORDING) R.drawable.ic_ali_record else IC_ACTION_VIEW)
        //  mActionViewIcon.setBackgroundColor(mState == STATE_RECORDING ? mContext.getResources().getColor(R.color.color_red) :
        //        Color.WHITE);
        mActionViewIcon?.setBackgroundResource(if (mState == STATE_RECORDING) R.drawable.circle_red else R.drawable.circle_white)
        val padding =
            mContext.resources.getDimension(if (mState == STATE_RECORDING) R.dimen.padding_circular_menu_recording else R.dimen.padding_circular_menu_normal)
                .toInt()
        mActionViewIcon?.setPadding(padding, padding, padding, padding)
        EventBus.getDefault().post(StateChangeEvent(mState, previousState))
    }

    private fun stopRecord() {
        mRecorder.stop()
    }
    // Added by ibozo - 2024/11/02 >
    private fun playNotificationSound(context: Context) {
        val notificationUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone: Ringtone? = RingtoneManager.getRingtone(context, notificationUri)
        ringtone?.let {
            it.play()
            val handler = Handler()
            handler.postDelayed({
                if (it.isPlaying) {
                    it.stop()
                }
            }, 2000)
        }
    }
    private fun playDoneCapturingSound(context: Context) {
        try{
            val mediaPlayer = MediaPlayer.create(context, R.raw.ozobi_done_capturing_ringtone)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
        }catch(e:Exception){
            
            playNotificationSound(context)
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    @Optional
    fun showDelayCaptureOption(){
        if(isStartCapture){
            Thread {
                Looper.prepare()
                mVibrator.vibrate(50)
                Looper.loop()
            }.start()
            GlobalScope.launch {
                delay(100)
                Thread {
                    Looper.prepare()
                    mVibrator.vibrate(30)
                    Looper.loop()
                }.start()
                withContext(Dispatchers.Main){
                    Toast.makeText(mContext,"正在捕获",Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
//        if(!isRefresh){
//            startRightNow()
//            return
//        }
        mCaptureDelayDialog = OperationDialogBuilder(mContext)
            .item(
                R.id.capture_delay_0s,
                R.drawable.ic_circular_menu_bounds,
                R.string.capture_delay_0s
            )
            .item(
                R.id.capture_delay_2s,
                R.drawable.ic_circular_menu_bounds,
                R.string.capture_delay_2s
            )
            .item(
                R.id.capture_delay_4s,
                R.drawable.ic_circular_menu_bounds,
                R.string.capture_delay_4s
            )
            .item(
                R.id.capture_delay_8s,
                R.drawable.ic_circular_menu_bounds,
                R.string.capture_delay_8s
            )
            .item(
                R.id.capture_use_last_record,
                R.drawable.ic_ali_log,
                R.string.text_show_last_record
            )
            .bindItemClick(this)
            .title(R.string.capture_delay_title)
            .dismissListener {
                mIsmCaptureDelayDialogDisappeared = true
                return@dismissListener
            }
            .build()
        mIsmCaptureDelayDialogDisappeared = false
        DialogUtils.showDialog(mCaptureDelayDialog)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Optional
    @OnClick(R.id.layout_inspect)
    fun captureWindow(){
        mWindow?.collapse()
        if(!available){
            goToAccePage()
            return
        }
        if(mLayoutInspector.isAvailable() != null){
            if(isDelayCapture){
                showDelayCaptureOption()
            }else{
                GlobalScope.launch {
                    inspectLayout()
                }
            }
        }else{
            goToAccePage()
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun checkIsCaptureDone(){
        val start = System.currentTimeMillis()
        mWindow?.setAlpha(0.7f)
        GlobalScope.launch {
            while(!NodeInfo.isDoneCapture && System.currentTimeMillis() - start < 60000){
                delay(100L)

                isCapturing = false
                captureCostTime = System.currentTimeMillis() - captureStartTime
            }
            mWindow?.setAlpha(1f)
        }
    }
    @Optional
    @OnClick(R.id.capture_delay_0s)
    fun startRightNow(){
        delayCapture(200L)
    }
    @Optional
    @OnClick(R.id.capture_delay_2s)
    fun delayTwoSeconds(){
        delayCapture(2000L)
    }
    @Optional
    @OnClick(R.id.capture_delay_4s)
    fun delayFourSeconds(){
        delayCapture(4000L)
    }
    @Optional
    @OnClick(R.id.capture_delay_8s)
    fun delayEightSeconds(){
        delayCapture(8000L)
    }
    @OptIn(DelicateCoroutinesApi::class)
    fun delayCapture(delay:Long){
        if(!isStartCaptureCountDown){
            startCaptureCountDown()
            GlobalScope.launch {
                delay(delay)
                inspectLayout()
            }
        }else{
            GlobalScope.launch {
                withContext(Dispatchers.Main){
                    Toast.makeText(mContext,"倒计时中...",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @OptIn(DelicateCoroutinesApi::class)
    @Optional
    @OnClick(R.id.capture_use_last_record)
    fun showLastRecord(){
        mLastCapture = mLayoutInspector.capture
        GlobalScope.launch {
            withContext(Dispatchers.Main){
                showLastInspectorDialog()
            }
        }
    }
    private fun startCaptureCountDown(){
        isStartCaptureCountDown = true
        mCaptureDelayDialog?.dismiss()
        mCaptureDelayDialog = null
        mWindow?.collapse()
    }
    private fun showLastInspectorDialog(){
        mWindow?.collapse()
        mLastLayoutInspectDialog = OperationDialogBuilder(mContext)
            .item(
                R.id.last_layout_bounds,
                R.drawable.ic_circular_menu_bounds,
                R.string.text_inspect_layout_bounds
            )
            .item(
                R.id.last_layout_hierarchy,
                R.drawable.ic_layout_hierarchy,
                R.string.text_inspect_layout_hierarchy
            )
            .bindItemClick(this)
            .title(R.string.text_last_inspect_layout)
            .build()
        DialogUtils.showDialog(mLastLayoutInspectDialog)
    }

    private fun showInspectorDialog(){
        mLayoutInspectDialog = OperationDialogBuilder(mContext)
            .item(
                R.id.layout_bounds,
                R.drawable.ic_circular_menu_bounds,
                R.string.text_inspect_layout_bounds
            )
            .item(
                R.id.layout_hierarchy,
                R.drawable.ic_layout_hierarchy,
                R.string.text_inspect_layout_hierarchy
            )
            .item(
                R.id.capture_info,
                R.drawable.ic_ali_log,
                "节点数量: ${NodeInfo.nodeCount}\n总耗时: $captureCostTime ms"
            )
            .bindItemClick(this)
            .title(R.string.text_inspect_layout)
            .build()
        DialogUtils.showDialog(mLayoutInspectDialog)
    }
    // <
    // Modified by ibozo - 2024/11/02 >
    //    @OnClick(R.id.layout_inspect)
    //    @Optional
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun inspectLayout() {
        if(isStartCapture){
            Thread {
                Looper.prepare()
                mVibrator.vibrate(90)
                Looper.loop()
            }.start()
            GlobalScope.launch {
                withContext(Dispatchers.Main){
                    Toast.makeText(mContext,"正在捕获",Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        isStartCapture = true
        isStartCaptureCountDown = false
        if(isRefresh || isCaptureScreenshot){
            while (!mIsmCaptureDelayDialogDisappeared || mWindow?.isExpanded == true){
                delay(100L)
                mCaptureDelayDialog?.dismiss()
                mCaptureDelayDialog = null
            }
        }
        if(isCaptureScreenshot && Images.availale){
            GlobalScope.launch {
                
                try{
                    screenCapture.captureScreen(true)

                }catch (e:Exception){
                    
                    ScreenCapture.cleanCurImg()
                    ScreenCapture.cleanCurImgBitmap()
                    Images.availale = false
                }
                // <
            }
        }else{
            ScreenCapture.isDoneVerity = true
            ScreenCapture.cleanCurImg()
            ScreenCapture.cleanCurImgBitmap()
        }
        val isFirstCapture = isRefresh || isCaptureScreenshot || isDelayCapture
        val hasToWait = isFirstCapture || isWaitForCapture
        if(isFirstCapture){
            captureStartTime = System.currentTimeMillis()
            available = mLayoutInspector.captureCurrentWindow()
            if(!available){
                goToAccePage()
                return
            }
        }
        if(isAuth && hasToWait){
            var waitCount = 0
            while(true){
                if((NodeInfo.isDoneCapture && ScreenCapture.isDoneVerity) || waitCount > 600){
                    break
                }
                delay(100)
                waitCount++
            }
        }
        if(isFirstCapture){
            captureCostTime = System.currentTimeMillis() - captureStartTime
        }
        Thread {
            Looper.prepare()
            mVibrator.vibrate(50)
            Looper.loop()
        }.start()
        playDoneCapturingSound(mContext)
        withContext(Dispatchers.Main){
            showInspectorDialog()
        }
        delay(200L)
        stopCapture()
        return
    }
    private fun goToAccePage(){
        stopCapture()
        Toast.makeText(
            mContext,
            R.string.text_no_accessibility_permission_to_capture,
            Toast.LENGTH_SHORT
        ).show()
        AccessibilityServiceTool.goToAccessibilitySetting()
    }
    private fun stopCapture(){
        NodeInfo.isDoneCapture = true
        isStartCapture = false
        isCapturing = false
        isStartCaptureCountDown = false
        mWindow?.setAlpha(1f)
        mWindow?.collapse()
    }
    @Optional
    @OnClick(R.id.last_layout_bounds)
    fun showLastLayoutBounds(){
        inspectLastLayout { rootNode -> rootNode?.let { LayoutBoundsFloatyWindow(it) } }
    }
    @Optional
    @OnClick(R.id.last_layout_hierarchy)
    fun showLastLayoutHierarchy(){
        inspectLastLayout { rootNode -> rootNode?.let { LayoutHierarchyFloatyWindow(it) } }
    }
    private fun inspectLastLayout(windowCreator: (NodeInfo?) -> FloatyWindow?) {
        mCaptureDelayDialog?.dismiss()
        mLastLayoutInspectDialog?.dismiss()
        windowCreator.invoke(mLastCapture)?.let { FloatyService.addWindow(it) }
    }
    // <

    @Optional
    @OnClick(R.id.layout_bounds)
    fun showLayoutBounds() {
        inspectLayout { rootNode -> rootNode?.let { LayoutBoundsFloatyWindow(it) } }
    }
    @Optional
    @OnClick(R.id.layout_hierarchy)
    fun showLayoutHierarchy() {
        inspectLayout { mRootNode -> mRootNode?.let { LayoutHierarchyFloatyWindow(it) } }
    }

    private fun inspectLayout(windowCreator: (NodeInfo?) -> FloatyWindow?) {
        mLayoutInspectDialog?.dismiss()
        mLayoutInspectDialog = null
        if (instance == null) {
            goToAccePage()
            return
        }
        // Modified by ibozo - 2024/11/04 >
//        if(isRefresh){
            windowCreator.invoke(mLayoutInspector.capture)?.let { FloatyService.addWindow(it) }
//        }else{
//            val progress = DialogUtils.showDialog(
//                ThemeColorMaterialDialogBuilder(mContext)
//                    .content(R.string.text_layout_inspector_is_dumping)
//                    .canceledOnTouchOutside(false)
//                    .progress(true, 0)
//                    .build()
//            )
//            mCaptureDeferred?.promise()
//                ?.then({ capture ->
//                    mActionViewIcon?.post {
//                        if (!progress.isCancelled) {
//                            progress.dismiss()
//                            windowCreator.invoke(capture)?.let { FloatyService.addWindow(it) }
//                        }
//                    }
//                }) { mActionViewIcon?.post { progress.dismiss() } }
//        }
    }

    @Optional
    @OnClick(R.id.stop_all_scripts)
    fun stopAllScripts() {
        mWindow?.collapse()
        AutoJs.getInstance().scriptEngineService.stopAllAndToast()
    }

    override fun onCaptureAvailable(capture: NodeInfo?) {
        if (mCaptureDeferred != null && mCaptureDeferred!!.isPending) mCaptureDeferred!!.resolve(
            capture
        )
    }

    @Optional
    @OnClick(R.id.settings)
    fun settings() {
        mWindow?.collapse()
        mRunningPackage = AutoJs.getInstance().infoProvider.getLatestPackageByUsageStatsIfGranted()
        mRunningActivity = AutoJs.getInstance().infoProvider.latestActivity
        mSettingsDialog = OperationDialogBuilder(mContext)
            .item(
                R.id.accessibility_service,
                R.drawable.ic_settings,
                R.string.text_accessibility_settings
            )
            .item(
                R.id.package_name, R.drawable.ic_android_fill,
                mContext.getString(R.string.text_current_package) + mRunningPackage
            )
            .item(
                R.id.class_name, R.drawable.ic_window,
                mContext.getString(R.string.text_current_activity) + mRunningActivity
            )
            .item(
                R.id.open_launcher,
                R.drawable.ic_home_light,
                R.string.text_open_main_activity
            )
            .item(
                R.id.pointer_location,
                R.drawable.ic_coordinate,
                R.string.text_pointer_location
            )
            .item(R.id.exit, R.drawable.ic_close, R.string.text_exit_floating_window)
            .bindItemClick(this)
            .title(R.string.text_more)
            .build()
        DialogUtils.showDialog(mSettingsDialog)
    }

    @Optional
    @OnClick(R.id.accessibility_service)
    fun enableAccessibilityService() {
        dismissSettingsDialog()
        AccessibilityServiceTool.enableAccessibilityService()
    }

    private fun dismissSettingsDialog() {
        mSettingsDialog?.dismiss()
        mSettingsDialog = null
    }

    @Optional
    @OnClick(R.id.package_name)
    fun copyPackageName() {
        dismissSettingsDialog()
        if (TextUtils.isEmpty(mRunningPackage)) return
        ClipboardUtil.setClip(mContext, mRunningPackage)
        Toast.makeText(mContext, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show()
    }

    @Optional
    @OnClick(R.id.class_name)
    fun copyActivityName() {
        dismissSettingsDialog()
        if (TextUtils.isEmpty(mRunningActivity)) return
        ClipboardUtil.setClip(mContext, mRunningActivity)
        Toast.makeText(mContext, R.string.text_already_copy_to_clip, Toast.LENGTH_SHORT).show()
    }

    @Optional
    @OnClick(R.id.open_launcher)
    fun openLauncher() {
        dismissSettingsDialog()
        val intent = Intent(mContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mContext.startActivity(intent)
    }

    @Optional
    @OnClick(R.id.pointer_location)
    fun togglePointerLocation() {
        dismissSettingsDialog()
        RootTool.togglePointerLocation()
    }

    @Optional
    @OnClick(R.id.exit)
    fun close() {
        dismissSettingsDialog()
        try {
            mWindow?.close()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            EventBus.getDefault().post(StateChangeEvent(STATE_CLOSED, mState))
            mState = STATE_CLOSED
        }
        mRecorder.removeOnStateChangedListener(this)
        mLayoutInspector.removeCaptureAvailableListener(this)
    }

    override fun onStart() {
        setState(STATE_RECORDING)
    }

    override fun onStop() {
        setState(STATE_NORMAL)
    }

    override fun onPause() {}
    override fun onResume() {}

    companion object {
        const val STATE_CLOSED = -1
        const val STATE_NORMAL = 0
        const val STATE_RECORDING = 1
        private const val IC_ACTION_VIEW = R.drawable.ic_android_eat_js
    }

    init {
        isAuth = Ozobi.authenticate(mContext)
        initFloaty()
        setupListeners()
        mRecorder = GlobalActionRecorder.getSingleton(context)
        mRecorder.addOnStateChangedListener(this)
        mLayoutInspector.addCaptureAvailableListener(this)
    }
}