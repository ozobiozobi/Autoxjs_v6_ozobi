package com.stardust.autojs.core.ozobi.remoteadb;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.cgutman.adblib.AdbCrypto;
import com.stardust.autojs.core.ozobi.remoteadb.console.ConsoleBuffer;
import com.stardust.autojs.core.ozobi.remoteadb.devconn.DeviceConnection;
import com.stardust.autojs.core.ozobi.remoteadb.devconn.DeviceConnectionListener;
import com.stardust.autojs.core.ozobi.remoteadb.service.ShellService;

import java.io.IOException;
import java.util.Date;

public class AdbShell implements DeviceConnectionListener  {

    private String hostName;
    private int port;
    public static DeviceConnection connection;
    private Intent service;
    private ShellService.ShellServiceBinder binder;
    public static String adbShellResult = null;
//    private StringBuilder commandBuffer = new StringBuilder();
//    private SpinnerDialog connectWaiting;
    private final String logTag = "ozobiLog";
    public static String shellResult = null;
    public static volatile boolean isDone = false;

    public AdbShell(Context context,String h,int p){
        hostName = h;
        port = p;
        service = new Intent(context, ShellService.class);
        context.bindService(service, serviceConn, Service.BIND_AUTO_CREATE);
    }

    public String exec(String text){
        if(connection != null){
            shellResult = null;
            isDone = false;
            connection.queueCommand(text+"\n");
            long startTime = new Date().getTime();
            while (!isDone){
                if(new Date().getTime() - startTime > 30000){
                    break;
                }
            }

            return shellResult;
        }
        return "connection is null";
    }

    public void close() throws IOException {
        connection.close();
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            binder = (ShellService.ShellServiceBinder)arg1;
            if (connection != null) {
                binder.removeListener(connection, AdbShell.this);
            }
            try {
                connection = AdbShell.this.connectOrLookupConnection(hostName, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            binder = null;
        }
    };

    public DeviceConnection startConnection(String host, int port) throws IOException {
        if(connection != null){
            close();
        }
//        /* Display the connection progress spinner */
//        connectWaiting = SpinnerDialog.displayDialog(this, "Connecting to "+hostName+":"+port,
//                "Please make sure the target device has network ADB enabled.\n\n"+
//                        "You may need to accept a prompt on the target device if you are connecting "+
//                        "to it for the first time from this device.", true);

        /* Create the connection object */
        DeviceConnection conn = binder.createConnection(host, port);

        /* Add this activity as a connection listener */
        binder.addListener(conn, this);

        /* Begin the async connection process */
        conn.startConnect();
        
        return conn;
    }

    private DeviceConnection connectOrLookupConnection(String host, int port) throws IOException {
        DeviceConnection conn = binder.findConnection(host, port);
        if (conn == null) {
            /* No existing connection, so start the connection process */
            conn = startConnection(host, port);
        }
        else {
            /* Add ourselves as a new listener of this connection */
            binder.addListener(conn, this);
        }
        return conn;
    }

    @Override
    public void notifyConnectionEstablished(DeviceConnection devConn) {
        
    }

    @Override
    public void notifyConnectionFailed(DeviceConnection devConn, Exception e) {
        
    }

    @Override
    public void notifyStreamFailed(DeviceConnection devConn, Exception e) {
        
    }

    @Override
    public void notifyStreamClosed(DeviceConnection devConn) {
        
    }

    @Override
    public AdbCrypto loadAdbCrypto(DeviceConnection devConn) {
        
        return null;
    }

    @Override
    public boolean canReceiveData() {
        
        return false;
    }

    @Override
    public void receivedData(DeviceConnection devConn, byte[] data, int offset, int length) {
        
    }

    @Override
    public boolean isConsole() {
        
        return false;
    }

    @Override
    public void consoleUpdated(DeviceConnection devConn, ConsoleBuffer console) {
        
    }
}
