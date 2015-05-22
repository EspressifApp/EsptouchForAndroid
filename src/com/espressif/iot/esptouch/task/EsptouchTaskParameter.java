package com.espressif.iot.esptouch.task;

public class EsptouchTaskParameter implements IEsptouchTaskParameter {

	private long mIntervalGuideCodeMillisecond;
	private long mIntervalDataCodeMillisecond;
	private long mTimeoutGuideCodeMillisecond;
	private long mTimeoutDataCodeMillisecond;
	private int mTotalRepeatTime;
	private int mEsptouchResultOneLen;
	private int mEsptouchResultMacLen;
	private int mEsptouchResultIpLen;
	private int mEsptouchResultTotalLen;
	private int mPortListening;
	private String mTargetHostname;
	private int mTargetPort;
	private int mWaitUdpReceivingMilliseond;
	private int mWaitUdpSendingMillisecond;
	private int mThresholdSucBroadcastCount;

	public EsptouchTaskParameter() {
		mIntervalGuideCodeMillisecond = 10;
		mIntervalDataCodeMillisecond = 10;
		mTimeoutGuideCodeMillisecond = 2000;
		mTimeoutDataCodeMillisecond = 4000;
		mTotalRepeatTime = 1;
		mEsptouchResultOneLen = 1;
		mEsptouchResultMacLen = 6;
		mEsptouchResultIpLen = 4;
		mEsptouchResultTotalLen = 1 + 6 + 4;
		mPortListening = 18266;
		mTargetHostname = "255.255.255.255";
		mTargetPort = 7001;
		mWaitUdpReceivingMilliseond = 10000;
		mWaitUdpSendingMillisecond = 48000;
		mThresholdSucBroadcastCount = 1;
	}

	@Override
	public long getIntervalGuideCodeMillisecond() {
		return mIntervalGuideCodeMillisecond;
	}

	@Override
	public long getIntervalDataCodeMillisecond() {
		return mIntervalDataCodeMillisecond;
	}

	@Override
	public long getTimeoutGuideCodeMillisecond() {
		return mTimeoutGuideCodeMillisecond;
	}

	@Override
	public long getTimeoutDataCodeMillisecond() {
		return mTimeoutDataCodeMillisecond;
	}

	@Override
	public long getTimeoutTotalCodeMillisecond() {
		return mTimeoutGuideCodeMillisecond + mTimeoutDataCodeMillisecond;
	}

	@Override
	public int getTotalRepeatTime() {
		return mTotalRepeatTime;
	}

	@Override
	public int getEsptouchResultOneLen() {
		return mEsptouchResultOneLen;
	}

	@Override
	public int getEsptouchResultMacLen() {
		return mEsptouchResultMacLen;
	}

	@Override
	public int getEsptouchResultIpLen() {
		return mEsptouchResultIpLen;
	}

	@Override
	public int getEsptouchResultTotalLen() {
		return mEsptouchResultTotalLen;
	}

	@Override
	public int getPortListening() {
		return mPortListening;
	}

	@Override
	public String getTargetHostname() {
		return mTargetHostname;
	}

	@Override
	public int getTargetPort() {
		return mTargetPort;
	}

	@Override
	public int getWaitUdpReceivingMillisecond() {
		return mWaitUdpReceivingMilliseond;
	}

	@Override
	public int getWaitUdpSendingMillisecond() {
		return mWaitUdpSendingMillisecond;
	}

	@Override
	public int getWaitUdpTotalMillisecond() {
		return mWaitUdpReceivingMilliseond + mWaitUdpSendingMillisecond;
	}

	@Override
	public int getThresholdSucBroadcastCount() {
		return mThresholdSucBroadcastCount;
	}

	@Override
	public void setWaitUdpTotalMillisecond(int waitUdpTotalMillisecond) {
		if (waitUdpTotalMillisecond < mWaitUdpReceivingMilliseond
				+ getTimeoutTotalCodeMillisecond()) {
			// if it happen, even one turn about sending udp broadcast can't be
			// completed
			throw new IllegalArgumentException(
					"waitUdpTotalMillisecod is invalid, "
							+ "it is less than mWaitUdpReceivingMilliseond + getTimeoutTotalCodeMillisecond()");
		}
		mWaitUdpSendingMillisecond = waitUdpTotalMillisecond
				- mWaitUdpReceivingMilliseond;
	}

}
