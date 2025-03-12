package com.stardust.autojs.core.ozobi.adbkeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.util.Base64;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

import com.stardust.autojs.R;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AdbIME extends InputMethodService {
    private final String IME_MESSAGE = "ADB_INPUT_TEXT";
    private final String IME_CHARS = "ADB_INPUT_CHARS";
    private final String IME_KEYCODE = "ADB_INPUT_CODE";
    private final String IME_META_KEYCODE = "ADB_INPUT_MCODE";
    private final String IME_EDITORCODE = "ADB_EDITOR_CODE";
    private final String IME_MESSAGE_B64 = "ADB_INPUT_B64";
    private final String IME_CLEAR_TEXT = "ADB_CLEAR_TEXT";
    private BroadcastReceiver mReceiver = null;

    public static String packageName = null;
    public static String inputText(String text){
        if(text.contains("'")){
            return " am broadcast -a ADB_INPUT_TEXT --es msg \""+text+"\"";
        }
        return " am broadcast -a ADB_INPUT_TEXT --es msg '"+text+"'";
    }
    public static String inputTextB64(String text){
        if(text.contains("'")){
            return " am broadcast -a ADB_INPUT_B64 --es msg `echo -n \""+text+"\" | base64`";
        }
        return " am broadcast -a ADB_INPUT_B64 --es msg `echo -n '"+text+"' | base64`";
    }
    public static String inputKey(int keyCode){
        return " am broadcast -a ADB_INPUT_CODE --ei code " + keyCode;
    }
    public static String inputCombKey(int metaKey,int keyCode){
        return " am broadcast -a ADB_INPUT_TEXT --es mcode " + "'"+metaKey+","+keyCode+"'";
    }
    public static String inputCombKey(int[] metaKey,int keyCode){
        StringBuilder metaString = new StringBuilder();
        for(int meta:metaKey){
            metaString.append(meta).append("+");
        }
        String metaKeyString = metaString.substring(0,metaString.length()-1);
        return " am broadcast -a ADB_INPUT_TEXT --es mcode " + "'"+metaKeyString+","+keyCode+"'";
    }
    public static String clearAllText(){
        return " am broadcast -a ADB_CLEAR_TEXT";
    }
    public static String enableAdbIME(){
        return " ime enable "+packageName+"/com.stardust.autojs.core.Ozobi.adbkeyboard.AdbIME";
    }
    public static String setAdbIME(){
        return " ime set "+packageName+"/com.stardust.autojs.core.Ozobi.adbkeyboard.AdbIME";
    }
    public static String resetIME(){
        return " ime reset";
    }

    @Override
    public View onCreateInputView(){
        View mInputView = getLayoutInflater().inflate(R.layout.adb_keyboard_input_view,null);
        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter(IME_MESSAGE);
            filter.addAction(IME_CHARS);
            filter.addAction(IME_KEYCODE);
            filter.addAction(IME_MESSAGE); // IME_META_KEYCODE // Change IME_MESSAGE to get more values.
            filter.addAction(IME_EDITORCODE);
            filter.addAction(IME_MESSAGE_B64);
            filter.addAction(IME_CLEAR_TEXT);
            mReceiver = new AdbReceiver();
            registerReceiver(mReceiver, filter);
        }

        return mInputView;
    }

    public void onDestroy() {
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    class AdbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            
            if (Objects.equals(intent.getAction(), IME_MESSAGE)) {
                // normal message
                String msg = intent.getStringExtra("msg");
                if (msg != null) {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.commitText(msg, 1);
                }
                // meta codes
                String metaCodes = intent.getStringExtra("mcode"); // Get message.
                if (metaCodes != null) {
                    String[] mcodes = metaCodes.split(","); // Get mcodes in string.
                    int i;
                    InputConnection ic = getCurrentInputConnection();
                    for (i = 0; i < mcodes.length - 1; i = i + 2) {
                        if (ic != null) {
                            KeyEvent ke;
                            if (mcodes[i].contains("+")) { // Check metaState if more than one. Use '+' as delimiter
                                String[] arrCode = mcodes[i].split("\\+"); // Get metaState if more than one.
                                ke = new KeyEvent(
                                        0,
                                        0,
                                        KeyEvent.ACTION_DOWN, // Action code.
                                        Integer.parseInt(mcodes[i + 1]), // Key code.
                                        0, // Repeat. // -1
                                        Integer.parseInt(arrCode[0]) | Integer.parseInt(arrCode[1]), // Flag
                                        0, // The device ID that generated the key event.
                                        0, // Raw device scan code of the event.
                                        KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE, // The flags for this key event.
                                        InputDevice.SOURCE_KEYBOARD // The input source such as SOURCE_KEYBOARD.
                                );
                            } else { // Only one metaState.
                                ke = new KeyEvent(
                                        0,
                                        0,
                                        KeyEvent.ACTION_DOWN, // Action code.
                                        Integer.parseInt(mcodes[i + 1]), // Key code.
                                        0, // Repeat.
                                        Integer.parseInt(mcodes[i]), // Flag
                                        0, // The device ID that generated the key event.
                                        0, // Raw device scan code of the event.
                                        KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE, // The flags for this key event.
                                        InputDevice.SOURCE_KEYBOARD // The input source such as SOURCE_KEYBOARD.
                                );
                            }
                            ic.sendKeyEvent(ke);
                        }
                    }
                }
            }

            if (Objects.equals(intent.getAction(), IME_MESSAGE_B64)) {
                String data = intent.getStringExtra("msg");

                byte[] b64 = Base64.decode(data, Base64.DEFAULT);
                String msg = "NOT SUPPORTED";
                try {
                    msg = new String(b64, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    Log.e("ozobiLog",e.toString());
                }

                InputConnection ic = getCurrentInputConnection();
                if (ic != null)
                    ic.commitText(msg, 1);
            }

            if (Objects.equals(intent.getAction(), IME_CHARS)) {
                int[] chars = intent.getIntArrayExtra("chars");
                if (chars != null) {
                    String msg = new String(chars, 0, chars.length);
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.commitText(msg, 1);
                }
            }

            if (Objects.equals(intent.getAction(), IME_KEYCODE)) {
                int code = intent.getIntExtra("code", -1);
                if (code != -1) {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
                }
            }

            if (Objects.equals(intent.getAction(), IME_EDITORCODE)) {
                int code = intent.getIntExtra("code", -1);
                if (code != -1) {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.performEditorAction(code);
                }
            }

            if (Objects.equals(intent.getAction(), IME_CLEAR_TEXT)) {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    //REF: stackoverflow/33082004 author: Maxime Epain
                    CharSequence curPos = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
                    CharSequence beforePos = ic.getTextBeforeCursor(curPos.length(), 0);
                    CharSequence afterPos = ic.getTextAfterCursor(curPos.length(), 0);
                    assert beforePos != null;
                    assert afterPos != null;
                    ic.deleteSurroundingText(beforePos.length(), afterPos.length());
                }
            }
        }
    }
}
