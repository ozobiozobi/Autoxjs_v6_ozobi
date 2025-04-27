package com.stardust.autojs.core.ui.inflater.inflaters;

import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.widget.JsCheckBox;

import java.util.Map;

/**
 * Created by ozobi - 2025/04/21
 */

public class JsCheckBoxInflater extends BaseViewInflater<JsCheckBox> {
    public JsCheckBoxInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(JsCheckBox view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "text":
                view.setText(value);
                break;
            case "textColor":
                view.setTextColor(Colors.parse(view, value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<JsCheckBox> getCreator() {
        return (context, attrs) -> {
            JsCheckBox jsCheckBox = new JsCheckBox(context);
            String tint = attrs.remove("android:tint");
            if(tint != null){
                jsCheckBox.setTint(tint);
            }
            return jsCheckBox;
        };
    }
}
