package com.stardust.view.accessibility

import android.content.Context
import android.graphics.Rect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

/**
 * Created by Stardust on 2017/3/10.
 */
class LayoutInspector(private val mContext: Context) {
    @Volatile
    var capture: NodeInfo? = null
        private set

    @Volatile
    var isDumping = false
        private set
    private val mExecutor = Executors.newSingleThreadExecutor()
    private val mCaptureAvailableListeners = CopyOnWriteArrayList<CaptureAvailableListener>()
    
    private var width = 0
    private var height = 0
    private val mVibrator: Vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    // <

    interface CaptureAvailableListener {
        fun onCaptureAvailable(capture: NodeInfo?)
    }

    /*
    * Added by Ozobi - 2024/10/06
    * 获取屏幕宽高
    * */
    private fun getScreenDimensions(): Array<Int> {
        val windowManager: WindowManager =
            mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels
        return arrayOf(width,height)
    }
    /**/

    
    private fun isNodeOnScreen(nodeInfo:AccessibilityNodeInfo):Boolean{
        if(width == 0 || height == 0){
            return true
        }
        val bounds = Rect()
        nodeInfo.getBoundsInScreen(bounds)
        if(bounds.right < -100 || bounds.bottom < -100){
            return false
        }
        if(bounds.left > width + 100 || bounds.top > height + 100){
            return false
        }
        return true
    }
    fun setRefresh(boolean: Boolean){
        isRefresh = boolean
    }
    fun isAvailable():AccessibilityNodeInfo?{
        val service = AccessibilityService.instance
        if (service == null) {
            Log.d(LOG_TAG, "captureCurrentWindow: service = null")
            capture = null
            return null
        }

        val root = getRootInActiveWindow(service)
        if (root == null) {
            Log.d(LOG_TAG, "captureCurrentWindow: root = null")
            capture = null
            return null
        }

        return root
    }
    // <

    fun captureCurrentWindow() :Boolean{
        val root = isAvailable()
        if(isAvailable() == null){
            return false
        }
        /*
        * Added by Ozobi - 2024/10/06
        * */
        if(width == 0 || height == 0){
            getScreenDimensions()
        }
        NodeInfo.isRefresh = isRefresh
        if(isRefresh){
            refreshChildList(root)
        }
        /**/
        mExecutor.execute {
            isDumping = true
            capture = root?.let { NodeInfo.capture(mContext, it) }
            isDumping = false
            for (l in mCaptureAvailableListeners) {
                l.onCaptureAvailable(capture)
            }
//            Thread {
//                Looper.prepare()
//                mVibrator.vibrate(90)
//                Looper.loop()
//            }.start()
        }
        return true
    }

    fun addCaptureAvailableListener(l: CaptureAvailableListener) {
        mCaptureAvailableListeners.add(l)
    }

    fun removeCaptureAvailableListener(l: CaptureAvailableListener): Boolean {
        return mCaptureAvailableListeners.remove(l)
    }

    private fun getRootInActiveWindow(service: AccessibilityService): AccessibilityNodeInfo? {
        return service.rootInActiveWindow ?: return service.fastRootInActiveWindow()

    }
    
    
    private fun refreshChildList(root: AccessibilityNodeInfo?) {
        if (root == null)
            return
//        if(isNodeOnScreen(root)){
        root.refresh()
        val childCount = root.childCount
        for (i in 0 until childCount) {
            refreshChildList(root.getChild(i))
        }
//        }
        return
    }
    // <

    fun clearCapture() {
        capture = null
    }

    companion object {
        
        var isRefresh = true
        // <
        private val LOG_TAG = LayoutInspector::class.java.simpleName
    }
}
