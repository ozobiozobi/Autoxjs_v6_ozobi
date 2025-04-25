package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Drawables;

/**
 * Created by Stardust on 2017/5/15.
 */

public class JsRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {
    private final Drawables mDrawables = new Drawables();

    public JsRadioButton(Context context) {
        super(context);
    }

    public JsRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String text() {
        return getText().toString();
    }

    public void text(CharSequence text) {
        setText(text);
    }

    public void setTint(String value) {
        setButtonTintList(Colors.parseColorStateList(this, value));
    }
}
