package com.espressif.iot.esptouch.task;

public class EsptouchTaskParameter implements IEsptouchTaskParameter {

	private long mIntervalGuideCodeMillisecond = 8;

	@Override
	public long getIntervalGuideCodeMillisecond() {
		return mIntervalGuideCodeMillisecond;
	}

	private long mIntervalDataCodeMillisecond = 8;

	@Override
	public long getIntervalDataCodeMillisecond() {
		return mIntervalDataCodeMillisecond;
	}

	private long mTimeoutGuideCodeMillisecond = 2000;

	@Override
	public long getTimeoutGuideCodeMillisecond() {
		return mTimeoutGuideCodeMillisecond;
	}

	private long mTimeoutDataCodeMillisecond = 4000;

	@Override
	public long getTimeoutDataCodeMillisecond() {
		return mTimeoutDataCodeMillisecond;
	}

	private long mTimeoutTotalCodeMillisecond = 2000 + 4000;

	@Override
	public long getTimeoutTotalCodeMillisecond() {
		return mTimeoutTotalCodeMillisecond;
	}

	private int mTotalRepeatTime = 1;
	
	@Override
	public int getTotalRepeatTime() {
		return mTotalRepeatTime;
	}

	private int mEsptouchResultOneLen = 1;
	
	@Override
	public int getEsptouchResultOneLen() {
		return mEsptouchResultOneLen;
	}

	private int mEsptouchResultMacLen = 6;
	
	@Override
	public int getEsptouchResultMacLen() {
		return mEsptouchResultMacLen;
	}

	private int mEsptouchResultIpLen = 4;
	
	@Override
	public int getEsptouchResultIpLen() {
		return mEsptouchResultIpLen;
	}

	private int mEsptouchResultTotalLen = 1 + 6 + 4;
	
	@Override
	public int getEsptouchResultTotalLen() {
		return mEsptouchResultTotalLen;
	}

	private int mPortListening = 18266;
	
	@Override
	public int getPortListening() {
		return mPortListening;
	}

	private String mTargetHostname = "255.255.255.255";
	
	@Override
	public String getTargetHostname() {
		return mTargetHostname;
	}

	private int mTargetPort = 7001;
	
	@Override
	public int getTargetPort() {
		return mTargetPort;
	}

	private int mWaitUdpReceivingMilliseond = 10000;
	
	@Override
	public int getWaitUdpReceivingMillisecond() {
		return mWaitUdpReceivingMilliseond;
	}

	private int mWaitUdpSendingMillisecond = 48000;
	
	@Override
	public int getWaitUdpSendingMillisecond() {
		return mWaitUdpSendingMillisecond;
	}

	@Override
	public int getWaitUdpTotalMillisecond() {
		return mWaitUdpReceivingMilliseond + mWaitUdpSendingMillisecond;
	}

	private int mThresholdSucBroadcastCount = 1;
	
	@Override
	public int getThresholdSucBroadcastCount() {
		return mThresholdSucBroadcastCount;
	}

	@Override
	public void setWaitUdpTotalMillisecond(int waitUdpTotalMillisecond) {
		mWaitUdpSendingMillisecond = waitUdpTotalMillisecond
				- mWaitUdpReceivingMilliseond;
	}

}
