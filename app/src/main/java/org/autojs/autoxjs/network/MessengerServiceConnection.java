package org.autojs.autoxjs.network;



import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.stardust.ServiceMessenger;

import org.autojs.autoxjs.devplugin.DevPlugin;

import java.util.Objects;

public class MessengerServiceConnection implements ServiceConnection {
    public static Messenger mMessenger = null;
    public final Handler clientHandler;
    private final Messenger clientMessenger;
    public MessengerServiceConnection(Looper looper){
        this.clientHandler = new Handler(looper){
            @Override
            public void handleMessage(@NonNull Message msg){
                
                if(msg.what == ServiceMessenger.SEND_TO_DEVPLUGIN){
                    Bundle data = msg.getData();
                    String message = Objects.requireNonNull(data.getString("setClip"));

                    DevPlugin.INSTANCE.log(message);
                }
            }
        };
        clientMessenger = new Messenger(clientHandler);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        mMessenger = new Messenger(service);
        Message message = Message.obtain();
        message.what = 1;
        message.replyTo = clientMessenger;
        Bundle data = new Bundle();
        data.putString("id","@app");
        message.setData(data);
        try{
            mMessenger.send(message);
        } catch (RemoteException e) {
            Log.e("ozobiLog","MessengerServiceConnection: onServiceConnected: e: "+e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        
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
