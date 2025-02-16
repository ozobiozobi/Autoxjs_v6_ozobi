package com.stardust.view.accessibility

import android.content.Context
import android.graphics.Rect
import android.preference.PreferenceManager
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import android.util.DisplayMetrics
import android.view.WindowManager
import com.stardust.automator.R

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
    // Added by ozobi - 2024/11/04 >
    private var isRefresh = true
    private var isDoneCapture = false
    private var width = 0
    private var height = 0
    // <

    interface CaptureAvailableListener {
        fun onCaptureAvailable(capture: NodeInfo?)
    }

    /*
    * Added by ozobi - 2024/10/06
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

    // Added by ozobi - 2024/11/04 >
    fun getIsDoneCapture():Boolean{
        return isDoneCapture
    }
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
    // <

    fun captureCurrentWindow(): Int {
        val service = AccessibilityService.instance
        if (service == null) {
            Log.d(LOG_TAG, "captureCurrentWindow: service = null")
            capture = null
            return -1
        }

        val root = getRootInActiveWindow(service)
        if (root == null) {
            Log.d(LOG_TAG, "captureCurrentWindow: root = null")
            capture = null
            return -1
        }
        /*
        * Added by ozobi - 2024/10/06
        * */
        if(width == 0 || height == 0){
            getScreenDimensions()
        }
        isDoneCapture = false
        var nodeCount = 0
        if(isRefresh){
            nodeCount = refreshChildList(root)
        }
        /**/
        mExecutor.execute {
            isDumping = true
            capture = NodeInfo.capture(mContext, root)
            isDumping = false
            // Added by ozobi - 2024/11/04 >
            isDoneCapture = true
            // <
            for (l in mCaptureAvailableListeners) {
                l.onCaptureAvailable(capture)
            }
        }
        return nodeCount
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
    // Modified by ozobi - 2024/11/04 >
    //    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)// ozobi: 使用 Android studio 的建议
    private fun refreshChildList(root: AccessibilityNodeInfo?): Int {
        if (root == null)
            return 0
        var n = 0
        n++
//        if(isNodeOnScreen(root)){
        root.refresh()
        val childCount = root.childCount
        for (i in 0 until childCount) {
            n += refreshChildList(root.getChild(i))
        }
//        }
        return n
    }
    // <

    fun clearCapture() {
        capture = null
    }

    companion object {

        private val LOG_TAG = LayoutInspector::class.java.simpleName
    }
}
