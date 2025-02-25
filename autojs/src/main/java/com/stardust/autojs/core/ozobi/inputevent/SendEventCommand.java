package com.stardust.autojs.core.ozobi.inputevent;


import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_POSITION_X;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_POSITION_Y;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_SLOT;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_TOUCH_MAJOR;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_TRACKING_ID;
import static com.stardust.autojs.core.inputevent.InputEventCodes.ABS_MT_WIDTH_MAJOR;
import static com.stardust.autojs.core.inputevent.InputEventCodes.BTN_TOUCH;
import static com.stardust.autojs.core.inputevent.InputEventCodes.DOWN;
import static com.stardust.autojs.core.inputevent.InputEventCodes.EV_ABS;
import static com.stardust.autojs.core.inputevent.InputEventCodes.EV_KEY;
import static com.stardust.autojs.core.inputevent.InputEventCodes.EV_SYN;
import static com.stardust.autojs.core.inputevent.InputEventCodes.SYN_REPORT;
import static com.stardust.autojs.core.inputevent.InputEventCodes.UP;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;

import androidx.annotation.Nullable;

import com.stardust.util.ScreenMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SendEventCommand {
    @Nullable
    private ScreenMetrics mScreenMetrics;
    private final int mDefaultId = 0;
    private final AtomicInteger mTracingId = new AtomicInteger(1);
    private final SparseIntArray mSlotIdMap = new SparseIntArray();

    public SendEventCommand(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        setScreenMetrics(displayMetrics.widthPixels,displayMetrics.heightPixels);
    }

    private int scaleX(int x) {
        if (mScreenMetrics == null)
            return x;
        return mScreenMetrics.scaleX(x);
    }
    private int scaleY(int y) {
        if (mScreenMetrics == null)
            return y;
        return mScreenMetrics.scaleY(y);
    }
    public void setScreenMetrics(int width, int height) {
        if (mScreenMetrics == null) {
            mScreenMetrics = new ScreenMetrics();
        }
        mScreenMetrics.setScreenMetrics(width, height);
    }
    
    public List<String> touchDown(int x, int y, int id) {
        if (mSlotIdMap.size() == 0) {
            return touchDown0(x, y, id);
        }
        ArrayList<String> commandList = new ArrayList<>();
        int slotId = mSlotIdMap.size();
        mSlotIdMap.put(id, slotId);
        commandList.add(" "+EV_ABS+" "+ABS_MT_SLOT+" "+slotId);
        commandList.add(" "+EV_ABS+" "+ABS_MT_TRACKING_ID+" "+mTracingId.getAndIncrement());
        commandList.add(" "+EV_ABS+" "+ABS_MT_POSITION_X+" "+scaleX(x));
        commandList.add(" "+EV_ABS+" "+ABS_MT_POSITION_Y+" "+scaleY(y));
        commandList.add(" "+EV_ABS+" "+ABS_MT_TOUCH_MAJOR+" "+5);
        commandList.add(" "+EV_ABS+" "+ABS_MT_WIDTH_MAJOR+" "+5);
        commandList.add(" "+EV_SYN+" "+SYN_REPORT+" "+0);
        return commandList;
    }
    private List<String> touchDown0(int x, int y, int id) {
        mSlotIdMap.put(id, 0);
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add(" "+EV_ABS+" "+ABS_MT_TRACKING_ID+" "+mTracingId.getAndIncrement());
        commandList.add(" "+EV_KEY+" "+BTN_TOUCH+" "+DOWN);
        commandList.add(" "+EV_ABS+" "+ABS_MT_POSITION_X+" "+scaleX(x));
        commandList.add(" "+EV_ABS+" "+ABS_MT_POSITION_Y+" "+scaleY(y));
        commandList.add(" "+EV_ABS+" "+ABS_MT_TOUCH_MAJOR+" "+5);
        commandList.add(" "+EV_ABS+" "+ABS_MT_WIDTH_MAJOR+" "+5);
        commandList.add(" "+EV_SYN+" "+SYN_REPORT+" "+0);
        return commandList;
    }

    public List<String> touchDown(int x, int y) {
        return touchDown(x, y, mDefaultId);
    }

    public List<String> touchUp(int id) {
        int slotId;
        int i = mSlotIdMap.indexOfKey(id);
        if (i < 0) {
            slotId = 0;
        } else {
            slotId = mSlotIdMap.valueAt(i);
            mSlotIdMap.removeAt(i);
        }
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add(" "+EV_ABS+" "+ABS_MT_SLOT+" "+slotId);
        commandList.add(" "+EV_ABS+" "+ABS_MT_TRACKING_ID+" "+0xffffffff);
        if (mSlotIdMap.size() == 0) {
            commandList.add(" "+EV_KEY+" "+BTN_TOUCH+" "+UP);
        }
        commandList.add(" "+EV_SYN+" "+SYN_REPORT+" "+0);
        return commandList;
    }

    public List<String> touchUp() {
        return touchUp(mDefaultId);
    }

    public List<String> touchMove(int x, int y, int id) {
        int slotId = mSlotIdMap.get(id, 0);
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add(" "+EV_ABS+" "+ABS_MT_SLOT+" "+slotId);
        commandList.add(" "+EV_ABS+" "+ABS_MT_TOUCH_MAJOR+" "+5);
        commandList.add(" "+EV_ABS+" "+ABS_MT_POSITION_X+" "+scaleX(x));
        commandList.add(" "+EV_ABS+" "+ABS_MT_POSITION_Y+" "+scaleY(y));
        commandList.add(" "+EV_SYN+" "+SYN_REPORT+" "+0);
        return commandList;
    }

    public List<String> touchMove(int x, int y) {
        return touchMove(x, y, mDefaultId);
    }
}
