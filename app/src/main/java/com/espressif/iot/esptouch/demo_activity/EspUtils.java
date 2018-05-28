package com.espressif.iot.esptouch.demo_activity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EspUtils {
    public static byte[] getOriginalSsidBytes(WifiInfo info) {
        try {
            Method method = info.getClass().getMethod("getWifiSsid");
            method.setAccessible(true);
            Object wifiSsid = method.invoke(info);
            method = wifiSsid.getClass().getMethod("getOctets");
            method.setAccessible(true);
            return (byte[]) method.invoke(wifiSsid);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
