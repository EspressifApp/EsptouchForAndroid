package com.espressif.iot.esptouch;

import java.util.concurrent.atomic.AtomicBoolean;

public class EsptouchResult implements IEsptouchResult {

	private final boolean mIsSuc;
	private String mBssid;
	private AtomicBoolean mIsCancelled;

	public EsptouchResult(boolean isSuc, String bssid) {
		this.mIsSuc = isSuc;
		this.mBssid = bssid;
		this.mIsCancelled = new AtomicBoolean(false);
	}

	@Override
	public boolean isSuc() {
		return this.mIsSuc;
	}

	@Override
	public String getBssid() {
		return this.mBssid;
	}

	@Override
	public boolean isCancelled() {
		return mIsCancelled.get();
	}
	
	public void setIsCancelled(boolean isCancelled){
		this.mIsCancelled.set(isCancelled);
	}

}
