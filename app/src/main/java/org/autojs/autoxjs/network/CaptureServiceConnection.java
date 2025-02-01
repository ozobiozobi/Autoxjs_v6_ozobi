package org.autojs.autoxjs.network;

// Created by ozobi - 2025/01/12

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class CaptureServiceConnection implements ServiceConnection {
    public static Messenger mMessenger = null;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("ozobiLog","CaptureServiceConnection: onServiceConnected");
        mMessenger = new Messenger(service);
        Message message = Message.obtain();
        message.what = 1;
        try{
            mMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("ozobiLog","CaptureServiceConnection: onServiceDisconnected");
    }

//    @Override
//    public void onBindingDied(ComponentName name) {
//        ServiceConnection.super.onBindingDied(name);
//    }
//
//    @Override
//    public void onNullBinding(ComponentName name) {
//        ServiceConnection.super.onNullBinding(name);
//    }
}
