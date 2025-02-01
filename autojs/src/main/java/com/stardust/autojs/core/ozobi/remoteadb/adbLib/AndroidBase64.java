package com.stardust.autojs.core.ozobi.remoteadb.adbLib;

import com.cgutman.adblib.AdbBase64;
import android.util.Base64;

public class AndroidBase64 implements AdbBase64 {
    @Override
    public String encodeToString(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }
}
