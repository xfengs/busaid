package com.connect;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class GetIpAddress {

    public static String IP;
    public static String IP2;
    public static String gateway;

    public static int PORT;

    public static String getIP() {
        return IP;
    }

    public static String getIP2() {
        return IP2;
    }

    public static String gateway() {
        return gateway;
    }

    //public static int getPort(){
    //    return PORT;
    //}
    public static void getLocalIpAddress() { //ServerSocket serverSocket

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    String mIP = inetAddress.getHostAddress().substring(0, 3);
                    if (mIP.equals("192")) {
                        IP = inetAddress.getHostAddress();    //获取本地IP
                        //PORT = serverSocket.getLocalPort();    //获取本地的PORT
                        Log.e("IP", "" + IP);
                        // Log.e("PORT",""+PORT);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public static String getIPAddress(Context ctx) {
        WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        IP2 = String.valueOf(dhcpInfo.ipAddress);
        gateway = String.valueOf(dhcpInfo.gateway);
        System.out.print("xfeng ip" +" Wifi info----->"+IP2);
        System.out.print("xfeng gateway "+ gateway);

        return Formatter.formatIpAddress(dhcpInfo.ipAddress);
    }
}
