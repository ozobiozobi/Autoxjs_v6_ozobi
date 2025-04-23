package com.stardust.autojs.core.ui.inflater.inflaters;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.ViewCreator;
import com.stardust.autojs.core.ui.inflater.util.Colors;
import com.stardust.autojs.core.ui.inflater.util.Drawables;
import com.stardust.autojs.core.ui.widget.JsSwitch;

import java.util.Map;

/**
 * Created by ozobi - 2025/04/21
 */

public class SwitchInflater extends BaseViewInflater<JsSwitch> {
    private final Drawables mDrawable = new Drawables();

    public SwitchInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(JsSwitch view, String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        switch (attr) {
            case "thumbTint":
                view.setThumbTintList(Colors.parseColorStateList(view, value));
                break;
            case "trackTint":
                view.setTrackTintList(Colors.parseColorStateList(view, value));
                break;
            default:
                return super.setAttr(view, attr, value, parent, attrs);
        }
        return true;
    }

    @Nullable
    @Override
    public ViewCreator<JsSwitch> getCreator() {
        return (context, attrs) -> {
            JsSwitch jsSwitch = new JsSwitch(context);
            String thumbShape = attrs.remove("android:thumbShape");
            if (thumbShape != null) {
                Drawable thumbDrawable = mDrawable.parseEllipseShapeDrawable(context, thumbShape);
                if(thumbDrawable != null){
                    jsSwitch.setThumbDrawable(thumbDrawable);
                }
            }
            String trackShape = attrs.remove("android:trackShape");
            if (trackShape != null) {
                Drawable trackDrawable = mDrawable.parseEllipseShapeDrawable(context, trackShape);
                if(trackDrawable != null){
                    jsSwitch.setTrackDrawable(trackDrawable);
                }
            }
            String thumbBg = attrs.remove("android:thumbBg");
            if(thumbBg == null){
                thumbBg = attrs.remove("android:thumbBackground");
            }
            if(thumbBg != null){
                jsSwitch.setThumbDrawable(mDrawable.parseDrawable(context, thumbBg));
            }
            String trackBg = attrs.remove("android:trackBg");
            if(trackBg == null){
                trackBg = attrs.remove("android:trackBackground");
            }
            if(trackBg != null){
                jsSwitch.setTrackDrawable(mDrawable.parseDrawable(context, trackBg));
            }
            return jsSwitch;
        };
    }
}
