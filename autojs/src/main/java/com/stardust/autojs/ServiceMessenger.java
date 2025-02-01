package com.stardust.autojs;

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


public class ServiceMessenger extends Service {
    private final Messenger messenger = new Messenger(new IncomingHandler());
    private static Messenger mClient = null;
    private static class IncomingHandler extends Handler{
        public void handleMessage(@NonNull Message msg){
            // 处理消息
            Log.d("ozobiLog","ServiceMessenger: IncomingHandler: handleMessage: msg: "+msg);
            if(msg.what == 9){
                Log.d("ozobiLog","ServiceMessenger: IncomingHandler: handleMessage: 收到消息");
                mClient = msg.replyTo;
            }
        }
    }
    private void sendMessageToClient(int what, String text) {
        if (mClient != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("reply", text);
                Message msg = Message.obtain(null, what);
                msg.setData(bundle);
                mClient.send(msg);
            } catch (RemoteException e) {
                mClient = null; // 客户端已断开连接
                Log.e("ExampleService", "Failed to send message to client", e);
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ozobiLog","ServiceCapture: onBind");
        return messenger.getBinder();
    }
}
