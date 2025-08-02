package com.stardust.autojs.core.ui.inflater.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.ui.inflater.ImageLoader;
import com.stardust.util.ViewUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Drawables {

    private static final Pattern DATA_PATTERN = Pattern.compile("data:(\\w+/\\w+);base64,(.+)");
    private static final Logger log = LoggerFactory.getLogger(Drawables.class);
    private static ImageLoader sDefaultImageLoader = new DefaultImageLoader();
    private ImageLoader mImageLoader = sDefaultImageLoader;

    public static void setDefaultImageLoader(ImageLoader defaultImageLoader) {
        if (defaultImageLoader == null)
            throw new NullPointerException();
        sDefaultImageLoader = defaultImageLoader;
    }

    public static ImageLoader getDefaultImageLoader() {
        return sDefaultImageLoader;
    }

    public Drawable parse(Context context, String value) {
        Resources resources = context.getResources();
        if (value.startsWith("@color/") || value.startsWith("@android:color/") || value.startsWith("#")) {
            return new ColorDrawable(Colors.parse(context, value));
        }
        if (value.startsWith("?")) {
            return loadAttrResources(context, value);
        }
        if (value.startsWith("file://")) {
            return decodeImage(context, value.substring(7));
        }
        return loadDrawableResources(context, value);
    }

    public StateListDrawable parseStateListDrawable(Context context, String value) {
        // 创建 StateListDrawable
        StateListDrawable stateListDrawable = new StateListDrawable();
        String[] valueArr = new String[]{value, value};
        Drawable[] drawableArr = new Drawable[2];
        if (value.contains("|")) {
            valueArr[0] = value.substring(0, value.indexOf("|"));
            valueArr[1] = value.substring(value.indexOf("|") + 1);
        }
        for (int index = 0; index < valueArr.length; index++) {
            if (valueArr[index].startsWith("@color/") || valueArr[index].startsWith("@android:color/") || valueArr[index].startsWith("#")) {
                drawableArr[index] = new ColorDrawable(Colors.parse(context, valueArr[index]));
            } else if (valueArr[index].startsWith("?")) {
                drawableArr[index] = loadAttrResources(context, valueArr[index]);
            } else if (valueArr[index].startsWith("file://")) {
                // 创建 ScaleDrawable
                ScaleDrawable scaleDrawable = new ScaleDrawable(parse(context, valueArr[index]), Gravity.CENTER, 1.0f, 1.0f);
                // 设置缩放级别 0: 不缩放  10000: 完全缩放
                scaleDrawable.setLevel(10000);
                // 加载图片
                drawableArr[index] = scaleDrawable;
            }
        }
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, drawableArr[1]);
        stateListDrawable.addState(new int[]{}, drawableArr[0]);
        return stateListDrawable;
    }

    public Drawable parseDrawable(Context context, String value) {
        if (value.startsWith("@color/") || value.startsWith("@android:color/") || value.startsWith("#")) {
            return new ColorDrawable(Colors.parse(context, value));
        } else if (value.startsWith("?")) {
            return loadAttrResources(context, value);
        } else if (value.startsWith("file://")) {
            // 创建 ScaleDrawable
            ScaleDrawable scaleDrawable = new ScaleDrawable(parse(context, value), Gravity.CENTER, 1.0f, 1.0f);
            // 设置缩放级别 0: 不缩放  10000: 完全缩放
            scaleDrawable.setLevel(10000);
            // 加载图片
            return scaleDrawable;
        }
        return new ColorDrawable(Colors.parse(context, value));
    }

    public int toDp(Context context, String value) {
        if (value.isEmpty()) {
            return -1;
        }
        if (value.endsWith("px")) {
            return ViewUtils.pxToDp(context, Integer.parseInt(value.substring(0, value.length() - 2)));
        } else if (value.endsWith("sp")) {
            float px = ViewUtils.spToPx(context, Integer.parseInt(value.substring(0, value.length() - 2)));
            return ViewUtils.pxToDp(context, (int) px);
        } else if (value.endsWith("dp")) {
            return Integer.parseInt(value.substring(0, value.length() - 2));
        }
        return Integer.parseInt(value);
    }

    public Drawable parseCircleShapeDrawable(Context context, String value) {
        ShapeDrawable thumbDrawable = new ShapeDrawable(new OvalShape());
        String[] valueArr = new String[]{value, value};
        int[] sizeArr = new int[2];
        if (value.contains("|")) {
            valueArr[0] = value.substring(0, value.indexOf("|"));
            valueArr[1] = value.substring(value.indexOf("|") + 1);
        }
        for (int index = 0; index < valueArr.length; index++) {
            sizeArr[index] = toDp(context, valueArr[index]);
        }
        thumbDrawable.setIntrinsicWidth(sizeArr[0]); // 设置宽度
        thumbDrawable.setIntrinsicHeight(sizeArr[1]); // 设置高度
        return thumbDrawable;
    }

    public Drawable parseGradientDrawable(Context context, String value) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        String[] valueArr = value.split("\\|");
        float corner = 16f;
        int shape = GradientDrawable.RECTANGLE;
        List<Integer> colorList = new ArrayList<>();
        float centerX = 0.5f;
        float centerY = 0.5f;
        GradientDrawable.Orientation ori = GradientDrawable.Orientation.TOP_BOTTOM;
        int type = GradientDrawable.LINEAR_GRADIENT;
        for (String v : valueArr) {
            String trim = v.trim();
            String key = trim.substring(0, trim.indexOf("="));
            String val = trim.substring(trim.lastIndexOf("=") + 1);
            switch (key) {
                case "shape":
                    if (val.startsWith("oval")) {
                        shape = GradientDrawable.OVAL;
                    } else if (val.startsWith("ring")) {
                        shape = GradientDrawable.RING;
                    } else if (val.startsWith("line")) {
                        shape = GradientDrawable.LINE;
                    }
                    break;
                case "color":
                case "colors":
                    String[] colorStrArr = val.split(",");
                    for (String s : colorStrArr) {
                        if (!s.isEmpty()) {
                            colorList.add(Colors.parse(context, s));
                        }
                    }
                    break;
                case "corner":
                    corner = (float) ViewUtils.dpToPx(context, toDp(context, val));
                    break;
                case "center":
                    centerX = Float.parseFloat(val.substring(0, val.indexOf(",")));
                    centerY = Float.parseFloat(val.substring(val.indexOf(",") + 1));
                    break;
                case "type":
                    if (val.startsWith("radial")) {
                        type = GradientDrawable.RADIAL_GRADIENT;
                    } else if (val.startsWith("sweep")) {
                        type = GradientDrawable.SWEEP_GRADIENT;
                    }
                    break;
                case "ori":
                    switch (val){
                        case "top_bottom":
                            ori = GradientDrawable.Orientation.TOP_BOTTOM;
                            break;
                        case "bottom_top":
                            ori = GradientDrawable.Orientation.BOTTOM_TOP;
                            break;
                        case "left_right":
                            ori = GradientDrawable.Orientation.LEFT_RIGHT;
                            break;
                        case "right_left":
                            ori = GradientDrawable.Orientation.RIGHT_LEFT;
                            break;
                        case "tl_br":
                            ori = GradientDrawable.Orientation.TL_BR;
                            break;
                        case "br_tl":
                            ori = GradientDrawable.Orientation.BR_TL;
                            break;
                        case "tr_bl":
                            ori = GradientDrawable.Orientation.TR_BL;
                            break;
                        case "bl_tr":
                            ori = GradientDrawable.Orientation.BL_TR;
                            break;
                    }
                    break;
            }
        }
        gradientDrawable.setShape(shape);
        gradientDrawable.setCornerRadius(corner);
        if (colorList.isEmpty()) {
            colorList.add(Colors.parse(context, "#46BDFF"));
            colorList.add(Colors.parse(context, "#4C9AFF"));
        }
        int[] colorArr = new int[colorList.size()];
        for (int i = 0; i < colorArr.length; i++) {
            colorArr[i] = colorList.get(i);
        }
        gradientDrawable.setColors(colorArr);
        gradientDrawable.setOrientation(ori);
        gradientDrawable.setGradientType(type);
        gradientDrawable.setGradientCenter(centerX, centerY);
        return gradientDrawable;
    }

    public Drawable parseEllipseShapeDrawable(Context context, String value) {
        int[] sizeArr = new int[]{0, 0};
        String sizeStr = value;
        String outerRadiiStr = "10000";
        if (value.contains("|")) {
            sizeStr = value.substring(0, value.indexOf("|"));
            outerRadiiStr = value.substring(value.indexOf("|") + 1);
        }
        String[] sizeSplitArr = sizeStr.split(",");
        sizeArr[0] = toDp(context, sizeSplitArr[0]);
        String[] outStrArr = outerRadiiStr.split(",");
        int[] outIntArr = new int[outStrArr.length];
        for (int index = 0; index < outStrArr.length; index++) {
            if (!outStrArr[index].isEmpty()) {
                outIntArr[index] = toDp(context, outStrArr[index]);
            }
        }
        float[] outerRadii = new float[8];
        for (int index = 0; index < outerRadii.length; index++) {
            if (outIntArr.length <= 1) {
                outerRadii[index] = outIntArr[0];
            } else if (outIntArr.length < 8) {
                outerRadii[index] = outIntArr[(index + 1) / 2];
                outerRadii[index + 1] = outIntArr[(index + 1) / 2];
                index++;
            } else {
                outerRadii[index] = outIntArr[index];
            }
        }
        ShapeDrawable trackDrawable = new ShapeDrawable(new RoundRectShape(outerRadii, null, null));
        if (sizeArr[0] > 0) {
            trackDrawable.setIntrinsicWidth(sizeArr[0]);
            trackDrawable.setIntrinsicHeight(sizeArr[0]);
        }
        return trackDrawable;
    }

    public Drawable loadDrawableResources(Context context, String value) {
        int resId = context.getResources().getIdentifier(value, "drawable",
                GlobalAppContext.getAutojsPackageName());
        if (resId == 0)
            throw new Resources.NotFoundException("drawable not found: " + value);
        return ContextCompat.getDrawable(context, resId);
    }

    public Drawable loadAttrResources(Context context, String value) {
        int[] attr = {context.getResources().getIdentifier(value.substring(1), "attr",
                GlobalAppContext.getAutojsPackageName())};
        TypedArray ta = context.obtainStyledAttributes(attr);
        Drawable drawable = ta.getDrawable(0 /* index */);
        ta.recycle();
        return drawable;
    }

    public Drawable decodeImage(String path) {
        return new BitmapDrawable(BitmapFactory.decodeFile(path));
    }

    public Drawable decodeImage(Context context, String path) {
        return new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(path));
    }

    public Drawable parse(View view, String name) {
        return parse(view.getContext(), name);
    }

    public void loadInto(ImageView view, Uri uri) {
        mImageLoader.loadInto(view, uri);
    }

    public void loadIntoBackground(View view, Uri uri) {
        mImageLoader.loadIntoBackground(view, uri);
    }

    public <V extends ImageView> void setupWithImage(V view, String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            loadInto(view, Uri.parse(value));
        } else if (value.startsWith("data:")) {
            loadDataInto(view, value);
        } else {
            view.setImageDrawable(parse(view, value));
        }
    }

    private void loadDataInto(ImageView view, String data) {
        Bitmap bitmap = loadBase64Data(data);
        view.setImageBitmap(bitmap);
    }

    public static Bitmap loadBase64Data(String data) {
        Matcher matcher = DATA_PATTERN.matcher(data);
        String base64;
        if (!matcher.matches() || matcher.groupCount() != 2) {
            base64 = data;
        } else {
            String mimeType = matcher.group(1);
            base64 = matcher.group(2);
        }
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void setupWithViewBackground(View view, String value) {
        if (value.startsWith("http://") || value.startsWith("https://")) {
            loadIntoBackground(view, Uri.parse(value));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(parse(view, value));
            } else {
                view.setBackgroundDrawable(parse(view, value));
            }
        }
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private static class DefaultImageLoader implements ImageLoader {

        @Override
        public void loadInto(final ImageView view, Uri uri) {
            load(view, uri, view::setImageDrawable);
        }

        @Override
        public void loadIntoBackground(final View view, Uri uri) {
            load(view, uri, view::setBackground);
        }

        @Override
        public Drawable load(View view, Uri uri) {
            try {
                URL url = new URL(uri.toString());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return new BitmapDrawable(view.getResources(), bmp);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void load(View view, Uri uri, final DrawableCallback callback) {
            load(view, uri, (BitmapCallback) bitmap -> callback.onLoaded(new BitmapDrawable(view.getResources(), bitmap)));
        }

        @Override
        public void load(final View view, final Uri uri, final BitmapCallback callback) {
            new Thread(() -> {
                try {
                    URL url = new URL(uri.toString());
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    view.post(() -> callback.onLoaded(bmp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

