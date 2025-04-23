package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
/**
 * Created by ozobi - 2025/04/21
 */
public class JsSwitch extends androidx.appcompat.widget.SwitchCompat{
    private final Drawables mDrawable = new Drawables();
    public JsSwitch(@NonNull Context context) {
        super(context);
    }
    public JsSwitch(@NonNull Context context, @NonNull AttributeSet attrs){
        super(context, attrs);
    }
    public JsSwitch(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }
    public void setThumbTint(String value){
        setThumbTintList(Colors.parseColorStateList(this, value));
    }
    public void setTrackTint(String value){
        setTrackTintList(Colors.parseColorStateList(this, value));
    }
    public void setThumbShape(String value){
        setThumbDrawable(mDrawable.parseEllipseShapeDrawable(this.getContext(), value));
    }
    public void setTrackShape(String value){
        setTrackDrawable(mDrawable.parseEllipseShapeDrawable(this.getContext(), value));
    }
    public void setThumbBackground(String value){
        setThumbDrawable(mDrawable.parseDrawable(this.getContext(), value));
    }
    public void setTrackBackground(String value){
        setTrackDrawable(mDrawable.parseDrawable(this.getContext(), value));
    }
}
