package com.espressif.iot.esptouch;

public interface IEsptouchResult {
	boolean isSuc();
	String getBssid();
	boolean isCancelled();
}
