package com.stardust.autojs.util;

import org.mozilla.javascript.typedarrays.NativeArrayBuffer;

public class ArrayBufferUtil {
    public static byte[] getBytes(NativeArrayBuffer arrayBuffer) {
        return arrayBuffer.getBuffer();
    }
    public static void fromBytes(byte[] byteArray,NativeArrayBuffer arrayBuffer) {
        System.arraycopy(byteArray, 0, arrayBuffer.getBuffer(), 0, byteArray.length);
    }
}
