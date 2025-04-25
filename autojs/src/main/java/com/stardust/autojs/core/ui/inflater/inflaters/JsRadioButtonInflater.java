package com.stardust.autojs.core.ui.inflater.inflaters;

import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.widget.JsRadioButton;

import java.util.Map;

/**
 * Created by ozobi - 2025/04/21
 */

public class JsRadioButtonInflater extends BaseViewInflater<JsRadioButton> {
    public JsRadioButtonInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(JsRadioButton view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "text":
                view.setText(value);
                break;
            case "textColor":
                view.setTextColor(Colors.parse(view, value));
                break;
            case "tint":
                view.setButtonTintList(Colors.parseColorStateList(view, value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<JsRadioButton> getCreator() {
        return (context, attrs) -> {
            JsRadioButton jsRadioButton = new JsRadioButton(context);
            String text = attrs.remove("android:text");
            if(text != null){
                jsRadioButton.setText(text);
            }
            String tint = attrs.remove("android:tint");
            if(tint != null){
                jsRadioButton.setTint(tint);
            }
            String textColor = attrs.remove("android:textColor");
            if(textColor != null){
                jsRadioButton.setTextColor(Colors.parse(context, textColor));
            }
            return jsRadioButton;
        };
    }
}
