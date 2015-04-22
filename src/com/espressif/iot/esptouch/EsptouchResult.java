package com.espressif.iot.esptouch;

public class EsptouchResult implements IEsptouchResult {

	private final boolean mIsSuc;
	private String mBssid;

	public EsptouchResult(boolean isSuc, String bssid) {
		this.mIsSuc = isSuc;
		this.mBssid = bssid;
	}

	@Override
	public boolean isSuc() {
		return this.mIsSuc;
	}

	@Override
	public String getBssid() {
		return this.mBssid;
	}

}
