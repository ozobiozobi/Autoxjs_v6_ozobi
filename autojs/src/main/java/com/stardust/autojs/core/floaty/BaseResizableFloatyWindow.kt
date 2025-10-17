package com.stardust.autojs.core.floaty

//import com.stardust.lib.R.layout
import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.allViews
import com.stardust.autojs.R
import com.stardust.autojs.R.layout
import com.stardust.autojs.core.ui.widget.JsEditText
import com.stardust.enhancedfloaty.FloatyService
import com.stardust.enhancedfloaty.FloatyWindow
import com.stardust.enhancedfloaty.ResizableFloaty
import com.stardust.enhancedfloaty.ResizableFloatyWindow
import com.stardust.enhancedfloaty.WindowBridge
import com.stardust.enhancedfloaty.WindowBridge.DefaultImpl
import com.stardust.enhancedfloaty.gesture.DragGesture
import com.stardust.enhancedfloaty.gesture.ResizeGesture
import com.stardust.enhancedfloaty.util.WindowTypeCompat
import kotlinx.coroutines.DelicateCoroutinesApi


@OptIn(DelicateCoroutinesApi::class)
class BaseResizableFloatyWindow(context: Context, viewSupplier: ViewSupplier) : FloatyWindow() {
    interface ViewSupplier {
        fun inflate(context: Context?, parent: ViewGroup?): View
    }
    companion object{
        var keepScreenOn = false
    }
    var rootView: View
        private set
    private var mResizer: View? = null
    private var mMoveCursor: View? = null
    private val mFloaty: MyFloaty
    private var mCloseButton: View? = null
    private val mOffset: Int
    var windowView:ViewGroup

    init {
        mFloaty = MyFloaty(context, viewSupplier)
        mOffset = context.resources.getDimensionPixelSize(R.dimen.floaty_window_offset)
        val layoutParams = createWindowLayoutParams()
        windowView =
            View.inflate(context, layout.ef_floaty_container, null as ViewGroup?) as ViewGroup
        rootView = mFloaty.createView()
        mResizer = mFloaty.getResizerView(rootView)
        mMoveCursor = mFloaty.getMoveCursorView(rootView)
        val params = ViewGroup.LayoutParams(-2, -2)
        windowView.addView(rootView, params)
        windowView.isFocusableInTouchMode = true
        mCloseButton = windowView.findViewById(R.id.close)
        super.setWindowLayoutParams(layoutParams)
        super.setWindowView(windowView)
        super.setWindowManager(context.getSystemService(FloatyService.WINDOW_SERVICE) as WindowManager)
        super.setWindowBridge(super.onCreateWindowBridge(layoutParams))
        
        val windowLayoutParams = windowLayoutParams
        windowLayoutParams.flags = windowLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        if(keepScreenOn){
            windowLayoutParams.flags = windowLayoutParams.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            keepScreenOn = false
            RawWindow.keepScreenOn = false
        }
        rootView.allViews.forEach {
            if(it.javaClass.name == JsEditText::class.java.name){
                it.setOnClickListener{
                    requestWindowFocus()
                }
                it.setOnKeyListener { _, _, event ->
                    if(event.keyCode == KeyEvent.KEYCODE_BACK){
                        disableWindowFocus()
                    }
                    return@setOnKeyListener false
                }
            }
        }
        // <
    }

    @SuppressLint("WrongConstant")
    private fun createWindowLayoutParams(): WindowManager.LayoutParams {
        val layoutParams =
            WindowManager.LayoutParams(-2, -2, WindowTypeCompat.getPhoneWindowType(), 520, -3)
        layoutParams.gravity = 8388659
        return layoutParams
    }
    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        initGesture()
    }

    override fun onCreateWindowLayoutParams(): WindowManager.LayoutParams {
        return super.getWindowLayoutParams()
    }

    override fun onCreateWindowBridge(params: WindowManager.LayoutParams): WindowBridge {
        return object : DefaultImpl(params, windowManager, windowView) {
            override fun getX(): Int {
                return super.getX() + mOffset
            }

            override fun getY(): Int {
                return super.getY() + mOffset
            }

            override fun updatePosition(x: Int, y: Int) {
                super.updatePosition(x - mOffset, y - mOffset)
            }
        }
    }

    fun setOnCloseButtonClickListener(listener: View.OnClickListener?) {
        mCloseButton!!.setOnClickListener(listener)
    }

    private fun initGesture() {
        if (mResizer != null) {
            ResizeGesture.enableResize(mResizer, rootView, this.windowBridge)
        }
        if (mMoveCursor != null) {
            val gesture = DragGesture(
                this.windowBridge,
                mMoveCursor
            )
            gesture.pressedAlpha = 1.0f
        }
    }

    var isAdjustEnabled: Boolean
        get() = mMoveCursor!!.visibility == View.VISIBLE
        set(enabled) {
            if (!enabled) {
                mMoveCursor!!.visibility = View.GONE
                mResizer!!.visibility = View.GONE
                mCloseButton!!.visibility = View.GONE
            } else {
                mMoveCursor!!.visibility = View.VISIBLE
                mResizer!!.visibility = View.VISIBLE
                mCloseButton!!.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(floatyService: FloatyService): View {
        return super.getWindowView()
    }

    fun disableWindowFocus() {
        val windowLayoutParams = windowLayoutParams
        windowLayoutParams.flags =
            windowLayoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        updateWindowLayoutParams(windowLayoutParams)
    }

    fun requestWindowFocus() {
        val windowLayoutParams = windowLayoutParams
        windowLayoutParams.flags =
            windowLayoutParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        updateWindowLayoutParams(windowLayoutParams)
        windowView.requestLayout()
        
    }

    private class MyFloaty(
        mContext: Context,
        mContentViewSupplier: ViewSupplier
    ) : ResizableFloaty {
        private var mRootView: View = View.inflate(mContext, R.layout.floaty_window, null)

        init {
            val container = mRootView.findViewById<FrameLayout>(R.id.container)
            mContentViewSupplier.inflate(mContext, container)
        }

        fun createView(): View {
            return mRootView
        }

        override fun inflateView(
            floatyService: FloatyService,
            resizableFloatyWindow: ResizableFloatyWindow
        ): View {
            return mRootView
        }

        override fun getResizerView(view: View): View? {
            return view.findViewById(R.id.resizer)
        }

        override fun getMoveCursorView(view: View): View? {
            return view.findViewById(R.id.move_cursor)
        }
    }
}
