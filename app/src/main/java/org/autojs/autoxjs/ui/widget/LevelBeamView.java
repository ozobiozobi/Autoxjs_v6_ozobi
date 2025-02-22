package org.autojs.autoxjs.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.stardust.view.accessibility.NodeInfo;

import org.autojs.autoxjs.R;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LevelBeamView extends View {

    private static final String TAG = "LevelBeamView";

    private static final int[] colors = {
            0xff1abc9c,
            0xff3498db,
            0xffe67e22,
            0xff8e44ad,
            0xfff1c40f,
            0xff2ecc71,
    };

    private int mLevel;

//    private int mPaddingLeft, mPaddingRight;
    private int mLinesWidth;
//    private int mLinesOffset;
    private Paint mBoxPaint;
    private Paint mCorePaint;
    // Added by Ozobi - 2024/11/02 >
    private Paint mLevelTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private Boolean mClickable;
    public static int levelInfoTextColor = Color.BLACK;
    public static NodeInfo selectedNode = null;
    private Paint selectedPaint;
    private NodeInfo curNodeInfo;
    // <

    public LevelBeamView(Context context) {
        super(context);
        init();
    }

    public LevelBeamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LevelBeamView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setLevel(int level) {
        mLevel = level;
        requestLayout();
    }
    // Added by Ozobi - 2024/11/02 >
    public void setNodeClickable(Boolean clickable){
        mClickable = clickable;
        requestLayout();
    }
    public void setSelectedPaintColor(int color){
        selectedPaint.setColor(color);
        requestLayout();
    }
    public void setCurNodeInfo(NodeInfo nodeInfo){
        curNodeInfo = nodeInfo;
        if(curNodeInfo == selectedNode){
            selectedPaint.setColor(0xff49C949);
        }else{
            selectedPaint.setColor(0);
        }
        requestLayout();
    }
    // <
    private void init() {
        setWillNotDraw(false);
        mLinesWidth = (int) getResources().getDimension(R.dimen.level_beam_view_line_width);
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0xff2d2d2d);
        mBoxPaint.setStyle(Paint.Style.FILL);
        mCorePaint = new Paint();
        mCorePaint.setColor(Color.GREEN);
        mCorePaint.setStyle(Paint.Style.FILL);
        // Added by Ozobi - 2024/11/02 >
        mLevelTextPaint = new Paint();
        Rect textBounds = new Rect();
        mLevelTextPaint.setTextSize(48f);
        mLevelTextPaint.setColor(levelInfoTextColor);
        mLevelTextPaint.getTextBounds("000",0,"000".length(), textBounds);
        mLevelTextPaint.setTextAlign(Paint.Align.LEFT);
        selectedPaint = new Paint();
        selectedPaint.setStyle(Paint.Style.FILL);
        mTextWidth = (float) textBounds.width();
        mTextHeight = (float) textBounds.height();
        mClickable = false;
        // <
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mLevel * mLinesWidth + (int) mTextWidth;// Modified by Ozobi - 2024/11/02 > added textWidth, 将线条数量改为与 level 相同: -1
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // Added by Ozobi - 2024/11/02 >
        canvas.drawRect(0, 0, mTextWidth + mLevel * mLinesWidth, getHeight(), selectedPaint);
        String levelText = mLevel+"";
        canvas.drawText(levelText,4, mTextHeight + 18f, mLevelTextPaint);
        if(mClickable){
            float left = 8f;
            float top = mTextHeight + 32f;
            float right = mTextWidth/2 + left;
            float bottom = top + mTextWidth/2;
            float width = mTextWidth/10;
            canvas.drawRect(left,top,right,bottom, mBoxPaint);
            canvas.drawRect(left + width,top + width,right - width, bottom - width, mCorePaint);
        }
    }

    private int getColorForLevel(int level) {
        return colors[level % colors.length];
    }

}
