package com.stardust;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;


public class ServiceMessenger extends Service {
    private final Messenger messenger = new Messenger(new IncomingHandler());
    public static HashMap<String,Messenger> mClientMap = new HashMap<>();

    public static int SEND_TO_DEVPLUGIN = 8;

    private static class IncomingHandler extends Handler{
        public void handleMessage(@NonNull Message msg){
            // 处理消息
            
            Bundle data = msg.getData();
            String id = data.getString("id");
            if(id != null){
                if(mClientMap.get(id) == null){
                    mClientMap.put(data.getString("id"),msg.replyTo);
                } else{
                    
                }
            }
        }
    }
    public static void sendMessageToClient(String clientId,int what, Bundle data) {
//        if (mClient != null) {
        try {
            Messenger client = mClientMap.get(clientId);
            if(client != null){
                Message msg = Message.obtain();
                msg.what = what;
                msg.setData(data);
                client.send(msg);
            }else{
                
            }

        } catch (RemoteException e) {
            // 客户端已断开连接
            Log.e("ozobiLog", "Failed to send message to client", e);
        }
//        }else{

//        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        
        return messenger.getBinder();
    }
}
