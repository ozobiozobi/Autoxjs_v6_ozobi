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

    public static ColorStateList getColorStateList(int[][] states, int[] colors) {
        return new ColorStateList(
                states,
                colors
        );
    }

    private static int[] getColorArr(Context context, String value) {
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
        return colorArr;
    }

    public static ColorStateList parseCheckColorStateList(Context context, String value) {
        int[] colorArr = getColorArr(context, value);
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        return getColorStateList(states, colorArr);
    }

    public static ColorStateList parseCheckColorStateList(View view, String color) {
        return parseCheckColorStateList(view.getContext(), color);
    }

    public static ColorStateList parseSelectColorStateList(Context context, String value) {
        int[] colorArr = getColorArr(context, value);
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_selected},
                new int[]{android.R.attr.state_selected}
        };
        return getColorStateList(states, colorArr);
    }

    public static ColorStateList parseSelectColorStateList(View view, String color) {
        return parseSelectColorStateList(view.getContext(), color);
    }

    public static ColorStateList parseFocusColorStateList(Context context, String value) {
        int[] colorArr = getColorArr(context, value);
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_focused},
                new int[]{android.R.attr.state_focused}
        };
        return getColorStateList(states, colorArr);
    }

    public static ColorStateList parseFocusColorStateList(View view, String color) {
        return parseFocusColorStateList(view.getContext(), color);
    }
}
