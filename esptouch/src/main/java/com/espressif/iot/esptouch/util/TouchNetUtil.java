package com.espressif.iot.esptouch.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TouchNetUtil {

    /**
     * get the local ip address by Android System
     *
     * @param context the context
     * @return the local ip addr allocated by Ap
     */
    public static InetAddress getLocalInetAddress(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        assert wm != null;
        WifiInfo wifiInfo = wm.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        byte[] addressBytes = new byte[]{
                (byte) (ipAddress & 0xff),
                (byte) (ipAddress >> 8 & 0xff),
                (byte) (ipAddress >> 16 & 0xff),
                (byte) (ipAddress >> 24 & 0xff)
        };
        InetAddress localInetAddr = null;
        try {
            localInetAddr = InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return localInetAddr;
    }

    /**
     * parse InetAddress
     *
     * @param inetAddrBytes
     * @return
     */
    public static InetAddress parseInetAddr(byte[] inetAddrBytes, int offset, int count) {
        InetAddress inetAddress = null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append((inetAddrBytes[offset + i] & 0xff));
            if (i != count - 1) {
                sb.append('.');
            }
        }
        try {
            inetAddress = InetAddress.getByName(sb.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress;
    }

    /**
     * parse bssid
     *
     * @param bssid the bssid like aa:bb:cc:dd:ee:ff
     * @return byte converted from bssid
     */
    public static byte[] parseBssid2bytes(String bssid) {
        String[] bssidSplits = bssid.split(":");
        byte[] result = new byte[bssidSplits.length];
        for (int i = 0; i < bssidSplits.length; i++) {
            result[i] = (byte) Integer.parseInt(bssidSplits[i], 16);
        }
        return result;
    }
}
