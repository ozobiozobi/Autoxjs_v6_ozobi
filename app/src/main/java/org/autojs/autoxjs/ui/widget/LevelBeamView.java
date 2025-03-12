package org.autojs.autoxjs.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.stardust.util.ViewUtils;
import com.stardust.view.accessibility.NodeInfo;

import org.autojs.autoxjs.R;

/**
 * Created by Stardust on 2017/3/10.
 */

public class LevelBeamView extends View {

    private static final String TAG = "LevelBeamView";

//    private static final int[] colors = {
//            0xff1abc9c,
//            0xff3498db,
//            0xffe67e22,
//            0xff8e44ad,
//            0xfff1c40f,
//            0xff2ecc71,
//    };

    private int mLevel = 0;

//    private int mPaddingLeft, mPaddingRight;
    private int mLinesWidth;
//    private int mLinesOffset;
    private Paint mClickableBoxPaint;
    private Paint mClickableCorePaint;
    
    private Paint mLevelTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private Boolean mClickable = false;
    private Boolean mIsBrother = false;
    public static int levelInfoTextColor = Color.BLACK;
    public static NodeInfo selectedNode = null;
    private Paint selectedPaint;
    private RectF selectedRectf = null;
    private final float oneSp = ViewUtils.spToPx(getContext(),1f);
    private float roundRadius = 16f;
    private int viewHeight = 0;
    private RectF clickableBoxRectF = null;
    private RectF clickableCoreRectF = null;
    public static Paint brotherPaint = new Paint();
    private RectF brotherBoxRectF = null;
    public static boolean nightMode = false;
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

    public void setAttr(int level, boolean isClickable, boolean isSelected, boolean isParent,boolean isChild, boolean isBrother) {
        mLevel = level;
        mClickable = isClickable;
        mIsBrother = isBrother;
        viewHeight = getHeight();
        selectedRectf = new RectF(0f,0f,mTextWidth + mLevel * mLinesWidth, viewHeight);
        if(isClickable){
            mClickableBoxPaint = new Paint();
            mClickableBoxPaint.setStyle(Paint.Style.FILL);
            mClickableCorePaint = new Paint();
            mClickableCorePaint.setColor(Color.GREEN);
            mClickableCorePaint.setStyle(Paint.Style.FILL);
            float left = 3*oneSp;
            float top = viewHeight - left - mTextWidth/2;
            float right = mTextWidth/2 + left;
            float bottom = top + mTextWidth/2;
            clickableBoxRectF = new RectF(left,top,right, bottom);
            float width = mTextWidth/10;
            clickableCoreRectF = new RectF(left+width,top+width,right-width, bottom-width);
            if(nightMode){
                mClickableBoxPaint.setColor(0xff574343);
            }else {
                mClickableBoxPaint.setColor(0xff2E2020);
            }
        }
        if(nightMode){
            if(isSelected){
                selectedPaint.setColor(0xff73D973);
            }else if(isChild){
                selectedPaint.setColor(0xdd72739C);
            }else if(isParent){
                selectedPaint.setColor(0xdd8E5D9C);
            }else{
                selectedPaint.setColor(Color.BLACK);
            }
        }else{
            if(isSelected){
                selectedPaint.setColor(0xff73D973);
            }else if(isChild){
                selectedPaint.setColor(0xddC6CDFF);
            }else if(isParent){
                selectedPaint.setColor(0xddE998FF);
            }else{
                selectedPaint.setColor(Color.WHITE);
            }
        }
        if(isBrother){
            brotherPaint.setColor(0xff73D973);
            float right = mTextWidth + mLevel * mLinesWidth;
            brotherBoxRectF = new RectF(right - 10*oneSp,3*oneSp,right - 4*oneSp,viewHeight-3*oneSp);
        }
        requestLayout();
    }
    // <
    private void init() {
        setWillNotDraw(false);
        mLinesWidth = (int) getResources().getDimension(R.dimen.level_beam_view_line_width);
        
        mLevelTextPaint = new Paint();
        Rect textBounds = new Rect();
        mLevelTextPaint.setTextSize(18 * oneSp);
        mLevelTextPaint.setColor(levelInfoTextColor);
        mLevelTextPaint.getTextBounds("000",0,"000".length(), textBounds);
        mLevelTextPaint.setTextAlign(Paint.Align.LEFT);
        selectedPaint = new Paint();
        selectedPaint.setStyle(Paint.Style.FILL);
        brotherPaint.setStyle(Paint.Style.FILL);
        if(nightMode){
            selectedPaint.setColor(Color.WHITE);
        }else{
            selectedPaint.setColor(Color.BLACK);
        }
        selectedRectf = new RectF(0f,0f,mTextWidth + mLevel * mLinesWidth, viewHeight);
        mTextWidth = (float) textBounds.width();
        mTextHeight = (float) textBounds.height();
        mClickable = false;
        roundRadius = 5 * oneSp;
        // <
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mLevel * mLinesWidth + (int) mTextWidth;
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.drawRoundRect(selectedRectf, roundRadius, roundRadius, selectedPaint);
        String levelText = mLevel+"";
        canvas.drawText(levelText,4, mTextHeight + 4*oneSp, mLevelTextPaint);
        if(mClickable){
            canvas.drawRoundRect(clickableBoxRectF,mTextWidth/16,mTextWidth/16, mClickableBoxPaint);
            canvas.drawRoundRect(clickableCoreRectF,mTextWidth/20,mTextWidth/20, mClickableCorePaint);
        }
        if(mIsBrother){
            canvas.drawRoundRect(brotherBoxRectF, 3*oneSp, 3*oneSp, brotherPaint);
        }
    }

//    private int getColorForLevel(int level) {
//        return colors[level % colors.length];
//    }

}
