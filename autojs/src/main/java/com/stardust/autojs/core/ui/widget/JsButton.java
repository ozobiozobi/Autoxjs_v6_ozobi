package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.stardust.autojs.core.ui.inflater.util.Drawables;

/**
 * Created by Stardust on 2017/5/15.
 */

//@SuppressLint("AppCompatCustomView")
public class JsButton extends androidx.appcompat.widget.AppCompatButton {
    private final Drawables mDrawables = new Drawables();
    public JsButton(Context context) {
        super(context);
    }

    public JsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String text() {
        return getText().toString();
    }

    public void text(CharSequence text) {
        setText(text);
    }

    public void setBackgroundGradient(String value){
        setBackgroundDrawable(mDrawables.parseGradientDrawable(this.getContext(), value));
    }
}
