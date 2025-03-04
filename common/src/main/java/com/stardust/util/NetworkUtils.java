package com.stardust.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Stardust on 2017/4/9.
 */

public class NetworkUtils {

    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static String getWifiIPv4(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        }
        return "0.0.0.0"; // 默认返回无效地址
    }

    public static List<List<String>> getIPList(Context context) {
        List<List<String>> all = new ArrayList<>();
        List<String> ipv4List = new ArrayList<>();
        List<String> ipv6List = new ArrayList<>();

        try {
            // 获取所有网络接口
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                // 获取网络接口的 IP 地址列表
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        // 检查是否为 IPv6 地址且不是本地的
                        String sAddr = Objects.requireNonNull(addr.getHostAddress());
                        if(!sAddr.contains("%")){
                            if (sAddr.contains(":")) {
                                ipv6List.add(sAddr);
                            }else{
                                ipv4List.add(sAddr);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        all.add(ipv4List);
        all.add(ipv6List);
        return all;
    }

    private static String intToIp(int ipAddress) {
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
    }
}
