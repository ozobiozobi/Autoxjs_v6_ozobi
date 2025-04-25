package com.stardust.autojs.core.ui.inflater.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import com.stardust.app.GlobalAppContext;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Colors {

    public static int parse(Context context, String color) {
        Resources resources = context.getResources();
        if (color.startsWith("@color/")) {
            return resources.getColor(resources.getIdentifier(color.substring("@color/".length()), "color", GlobalAppContext.getAutojsPackageName()));
        }
        if (color.startsWith("@android:color/")) {
            return Color.parseColor(color.substring(15));
        }
        return Color.parseColor(color);
    }

    public static int parse(View view, String color) {
        return parse(view.getContext(), color);
    }

    public static ColorStateList parseColorStateList(Context context, String value) {
        Resources resources = context.getResources();
        String[] valueArr = new String[]{value, value};
        int[] colorArr = new int[]{0, 0};
        if (value.contains("|")) {
            valueArr[0] = value.substring(0, value.indexOf("|"));
            valueArr[1] = value.substring(value.indexOf("|") + 1);
        }
        for (int index = 0; index < valueArr.length; index++) {
            if (valueArr[index].contains("@")) {
                if (value.startsWith("@color/")) {
                    colorArr[index] = resources.getColor(resources.getIdentifier(valueArr[index].substring("@color/".length()), "color", GlobalAppContext.getAutojsPackageName()));
                }
                if (value.startsWith("@android:color/")) {
                    colorArr[index] = Color.parseColor(valueArr[index].substring(15));
                }
            } else {
                colorArr[index] = Color.parseColor(valueArr[index]);
            }
        }
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // 关闭状态
                        new int[]{android.R.attr.state_checked}, // 开启状态
                },
                new int[]{
                        colorArr[0], // 关闭状态的颜色
                        colorArr[1], // 开启状态的颜色
                }
        );
    }

    public static ColorStateList parseColorStateList(View view, String color) {
        return parseColorStateList(view.getContext(), color);
    }
}
