package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.stardust.autojs.core.ui.inflater.util.Colors;

/**
 * Created by ozobi - 2025/04/21
 */
public class JsCheckBox extends androidx.appcompat.widget.AppCompatCheckBox{
    public JsCheckBox(@NonNull Context context) {
        super(context);
    }
    public JsCheckBox(@NonNull Context context, @NonNull AttributeSet attrs){
        super(context, attrs);
    }
    public JsCheckBox(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }
    public void setTint(String value){
        setButtonTintList(Colors.parseCheckColorStateList(this.getContext(),value));
    }
}
