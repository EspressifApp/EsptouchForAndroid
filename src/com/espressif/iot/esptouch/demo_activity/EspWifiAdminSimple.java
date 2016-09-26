package com.espressif.iot.esptouch.demo_activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class EspWifiAdminSimple {

	private final Context mContext;

	
	public EspWifiAdminSimple(Context context) {
		mContext = context;
	}

	public String getWifiConnectedSsid() {
		WifiInfo mWifiInfo = getConnectionInfo();
		String ssid = null;
		if (mWifiInfo != null && isWifiConnected()) {
			int len = mWifiInfo.getSSID().length();
			if (mWifiInfo.getSSID().startsWith("\"")
					&& mWifiInfo.getSSID().endsWith("\"")) {
				ssid = mWifiInfo.getSSID().substring(1, len - 1);
			} else {
				ssid = mWifiInfo.getSSID();
			}

		}
		return ssid;
	}
	
	public String getWifiConnectedSsidAscii(String ssid) {
		final long timeout = 100;
		final long interval = 20;
		String ssidAscii = ssid;

		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.startScan();

		boolean isBreak = false;
		long start = System.currentTimeMillis();
		do {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ignore) {
				isBreak = true;
				break;
			}
			List<ScanResult> scanResults = wifiManager.getScanResults();
			for (ScanResult scanResult : scanResults) {
				if (scanResult.SSID != null && scanResult.SSID.equals(ssid)) {
					isBreak = true;
					try {
						Field wifiSsidfield = ScanResult.class
								.getDeclaredField("wifiSsid");
						wifiSsidfield.setAccessible(true);
						Class<?> wifiSsidClass = wifiSsidfield.getType();
						Object wifiSsid = wifiSsidfield.get(scanResult);
						Method method = wifiSsidClass
								.getDeclaredMethod("getOctets");
						byte[] bytes = (byte[]) method.invoke(wifiSsid);
						ssidAscii = new String(bytes, "ISO-8859-1");
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		} while (System.currentTimeMillis() - start < timeout && !isBreak);

		return ssidAscii;
	}
	
	public String getWifiConnectedBssid() {
		WifiInfo mWifiInfo = getConnectionInfo();
		String bssid = null;
		if (mWifiInfo != null && isWifiConnected()) {
			bssid = mWifiInfo.getBSSID();
		}
		return bssid;
	}

	// get the wifi info which is "connected" in wifi-setting
	private WifiInfo getConnectionInfo() {
		WifiManager mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return wifiInfo;
	}

	private boolean isWifiConnected() {
		NetworkInfo mWiFiNetworkInfo = getWifiNetworkInfo();
		boolean isWifiConnected = false;
		if (mWiFiNetworkInfo != null) {
			isWifiConnected = mWiFiNetworkInfo.isConnected();
		}
		return isWifiConnected;
	}

	private NetworkInfo getWifiNetworkInfo() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWiFiNetworkInfo;
	}
}
