package com.stardust.util;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.stardust.ServiceMessenger;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Stardust on 2017/3/10.
 */

public class ClipboardUtil {

    public static void setClip(Context context, CharSequence text) {
        
        try{
            String format = "HH:mm:ss.SSS";
            Date date = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format); // 定义格式
            String formattedDate = sdf.format(date);
            Bundle data = new Bundle();
            data.putString("setClip","\n[Ozobi:D "+formattedDate+"]\n"+text.toString()+"\n");
            ServiceMessenger.sendMessageToClient("@app",ServiceMessenger.SEND_TO_DEVPLUGIN,data);

        }catch(Exception e){
            Log.e("ozobiLog","ClipboardUtil: setClip: e: "+e);
        }
        // <
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("", text));
    }

    public static CharSequence getClip(Context context) {
        ClipData clip = ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
        return (clip == null || clip.getItemCount() == 0) ? null : clip.getItemAt(0).getText();
    }

    @NonNull
    public static CharSequence getClipOrEmpty(Context context) {
        CharSequence clip = getClip(context);
        if (clip == null) {
            return "";
        }
        return clip;
    }
}
