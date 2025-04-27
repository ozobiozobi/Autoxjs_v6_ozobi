package com.stardust.autojs.core.ui.inflater.inflaters;

import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.widget.JsButton;

import java.util.Map;

/**
 * Created by ozobi - 2025/04/23
 */

public class JsButtonInflater extends BaseViewInflater<JsButton> {
    private final Drawables mDrawable = new Drawables();

    public JsButtonInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(JsButton view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "text":
                view.setText(value);
                break;
            case "gradient":
                view.setBackgroundDrawable(mDrawable.parseGradientDrawable(view.getContext(), value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<JsButton> getCreator() {
        return (context, attrs) -> {
            JsButton jsButton = new JsButton(context);
            String text = attrs.remove("android:text");
            if (text != null) {
                jsButton.setText(text);
            }
            String gradient = attrs.remove("android:gradient");
            if (gradient != null) {
                jsButton.setBackgroundDrawable(mDrawable.parseGradientDrawable(context, gradient));
            }else{
                jsButton.setBackgroundDrawable(mDrawable.parseGradientDrawable(context, "shape=rect"));
            }
            return jsButton;
        };
    }
}
