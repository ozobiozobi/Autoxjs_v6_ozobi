package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.stardust.autojs.core.ui.inflater.util.Colors;

/**
 * Created by Stardust on 2017/5/15.
 */

public class JsEditText extends androidx.appcompat.widget.AppCompatEditText {
    public JsEditText(Context context) {
        super(context);
    }

    public JsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String text() {
        return getText().toString();
    }

    public void text(CharSequence text) {
        setText(text);
    }

    public void setTint(String value){
        setBackgroundTintList(Colors.parseFocusColorStateList(this,value));
    }

}
