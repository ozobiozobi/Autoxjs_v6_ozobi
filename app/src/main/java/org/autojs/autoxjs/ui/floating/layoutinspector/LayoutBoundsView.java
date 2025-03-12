package org.autojs.autoxjs.ui.floating.layoutinspector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.core.ozobi.capture.ScreenCapture;
import com.stardust.util.Ozobi;
import com.stardust.util.ViewUtil;
import com.stardust.view.accessibility.NodeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LayoutBoundsView extends View {

    private static final int COLOR_SHADOW = 0x6a000000;
//    private static final int COLOR_SHADOW = 0x6d000000;
    private NodeInfo mRootNode;
    private NodeInfo mTouchedNode;
    private Paint mBoundsPaint;
    private Paint mFillingPaint;
    private OnNodeInfoSelectListener mOnNodeInfoSelectListener;

    private int mTouchedNodeBoundsColor = 0xFF00B6FF;

    private int mNormalNodeBoundsColor = Color.WHITE;
    private boolean isAuth = false;

    private Rect mTouchedNodeBounds;

    private int[] mBoundsInScreen;
    protected int mStatusBarHeight;

    public LayoutBoundsView(Context context) {
        super(context);
        init();
    }

    public LayoutBoundsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LayoutBoundsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LayoutBoundsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    public void setOnNodeInfoSelectListener(OnNodeInfoSelectListener onNodeInfoSelectListener) {
        mOnNodeInfoSelectListener = onNodeInfoSelectListener;
    }

    public void setRootNode(NodeInfo rootNode) {
        mRootNode = rootNode;
        mTouchedNode = null;
    }


    private void init() {
        isAuth = Ozobi.authenticate(getContext());
        mBoundsPaint = new Paint();
        mBoundsPaint.setStyle(Paint.Style.STROKE);
        mFillingPaint = new Paint();
        mFillingPaint.setStyle(Paint.Style.FILL);
        mFillingPaint.setColor(COLOR_SHADOW);
        setWillNotDraw(false);
        mStatusBarHeight = ViewUtil.getStatusBarHeight(getContext());
        mBoundsPaint.setColor(mNormalNodeBoundsColor);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        
        if(getContext().getPackageName().contains("ozobi")){
            if(ScreenCapture.Companion.isCurImgBitmapValid() && ScreenCapture.Companion.getCurImgBitmap() != null){
                if(getWidth() == ScreenCapture.Companion.getCurImgBitmap().getHeight() || getHeight() == ScreenCapture.Companion.getCurImgBitmap().getWidth()){
                    
                }else{
                    canvas.drawBitmap(ScreenCapture.Companion.getCurImgBitmap(),0f,-mStatusBarHeight,null);
                }
            }
        }
        //
        super.onDraw(canvas);
        if (mBoundsInScreen == null) {
            mBoundsInScreen = new int[4];
            getLocationOnScreen(mBoundsInScreen);
            mStatusBarHeight = mBoundsInScreen[1];
        }
        if (mTouchedNode != null) {
            canvas.save();
            if (mTouchedNodeBounds == null) {
                mTouchedNodeBounds = new Rect(mTouchedNode.getBoundsInScreen());
                mTouchedNodeBounds.offset(0, -mStatusBarHeight);
            }
            canvas.clipRect(mTouchedNodeBounds, Region.Op.DIFFERENCE);
        }
        // Before modified:
//        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mFillingPaint);
        /*
         * Modified by Ozobi - 2024/10/07
         * ->使用 Android Studio 的建议
         * */
        canvas.drawRect(0, 0, getWidth(), getHeight(), mFillingPaint);
        /**/
        if (mTouchedNode != null) {
            canvas.restore();
        }
        mBoundsPaint.setColor(mNormalNodeBoundsColor);
        draw(canvas, mRootNode);
        if (mTouchedNode != null) {
            mBoundsPaint.setColor(mTouchedNodeBoundsColor);
            drawRect(canvas, mTouchedNode.getBoundsInScreen(), mStatusBarHeight, mBoundsPaint);
        }
    }

    public Paint getBoundsPaint() {
        return mBoundsPaint;
    }

    public Paint getFillingPaint() {
        return mFillingPaint;
    }

    public void setTouchedNodeBoundsColor(int touchedNodeBoundsColor) {
        mTouchedNodeBoundsColor = touchedNodeBoundsColor;
    }

    public void setNormalNodeBoundsColor(int normalNodeBoundsColor) {
        mNormalNodeBoundsColor = normalNodeBoundsColor;
    }

    /*
     * Modified by Ozobi - 2024/11/03
     * */
    private void draw(Canvas canvas, NodeInfo node) {
        if (node == null)
            return;
        Rect bounds = node.getBoundsInScreen();
        if (bounds.right > -5 && bounds.bottom > -5 && bounds.left < bounds.right && bounds.top < bounds.bottom) {
            /*
            * Added by Ozobi - 2024/10/07
            * */
            if(isAuth){
                if(node.getClickable()){
                    mBoundsPaint.setColor(Color.GREEN);
                }else if(node.getDesc() != null ) {
                    mBoundsPaint.setColor(0xFF9900FF);
                }else if(!node.getText().isEmpty()){
                    mBoundsPaint.setColor(0xFFFF0099);
                }else{
                    mBoundsPaint.setColor(Color.WHITE);
                }
            }else{
                mBoundsPaint.setColor(Color.GREEN);
            }
            /**/
            drawRect(canvas, bounds, mStatusBarHeight, mBoundsPaint);
       }
       for (NodeInfo child : node.getChildren()) {
           draw(canvas, child);
       }
    }

    public static void drawRect(Canvas canvas, Rect rect, int statusBarHeight, Paint paint) {
        Rect offsetRect = new Rect(rect);
        offsetRect.offset(0, -statusBarHeight);
        canvas.drawRect(offsetRect, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRootNode != null) {
            setSelectedNode(findNodeAt(mRootNode, (int) event.getRawX(), (int) event.getRawY()));
        }
        if (event.getAction() == MotionEvent.ACTION_UP && mTouchedNode != null) {
            onNodeInfoClick(mTouchedNode);
            return true;
        }
        return super.onTouchEvent(event);

    }

    private void onNodeInfoClick(NodeInfo nodeInfo) {
        if (mOnNodeInfoSelectListener != null) {
            mOnNodeInfoSelectListener.onNodeSelect(nodeInfo);
        }
    }

    private NodeInfo findNodeAt(NodeInfo node, int x, int y) {
        ArrayList<NodeInfo> list = new ArrayList<>();
        findNodeAt(node, x, y, list);
        if (list.isEmpty()) {
            return null;
        }
        /*
         * Modefied by Ozobi - 2024/10/07
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Collections.min(list, Comparator.comparingInt(o -> o.getBoundsInScreen().width() * o.getBoundsInScreen().height()));
        } else {
            return null;
        }
        /**/
        // Before modified:
//        return Collections.min(list, (o1, o2) ->
//                o1.getBoundsInScreen().width() * o1.getBoundsInScreen().height() -
//                        o2.getBoundsInScreen().width() * o2.getBoundsInScreen().height());

    }


    private void findNodeAt(NodeInfo node, int x, int y, List<NodeInfo> list) {
        for (NodeInfo child : node.getChildren()) {
            if (child != null && child.getBoundsInScreen().contains(x, y)) {
                list.add(child);
                findNodeAt(child, x, y, list);
            }
        }
    }

    public void setSelectedNode(NodeInfo selectedNode) {
        mTouchedNode = selectedNode;
        mTouchedNodeBounds = null;
        invalidate();
    }

    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }
}
