package com.espressif.iot.esptouch.demo_activity;

import android.net.wifi.WifiInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EspUtils {
    public static byte[] getOriginalSsidBytes(WifiInfo info) {
        try {
            Method method = info.getClass().getMethod("getWifiSsid");
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            Object wifiSsid = method.invoke(info);
            if (wifiSsid == null) {
                return null;
            }
            method = wifiSsid.getClass().getMethod("getOctets");
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return (byte[]) method.invoke(wifiSsid);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
