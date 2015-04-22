package com.espressif.iot.esptouch.task;

import com.espressif.iot.esptouch.EsptouchResult;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.protocol.EsptouchGenerator;
import com.espressif.iot.esptouch.udp.UDPSocketClient;
import com.espressif.iot.esptouch.udp.UDPSocketServer;
import com.espressif.iot.esptouch.util.ByteUtil;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

public class __EsptouchTask implements __IEsptouchTask {

	private static final String TAG = "EsptouchTask";

	private volatile IEsptouchResult mEsptouchResult;
	private volatile boolean mIsSuc = false;
	private volatile boolean mIsInterrupt = false;
	private volatile boolean mIsExecuted = false;
	private final UDPSocketClient mSocketClient;
	private final UDPSocketServer mSocketServer;
	private final String mApSsid;
	private final String mApPassword;

	public __EsptouchTask(String apSsid, String apPassword, Context context) {
		if (TextUtils.isEmpty(apSsid)) {
			throw new IllegalArgumentException(
					"the apSsid should be null or empty");
		}
		if (apPassword == null) {
			apPassword = "";
		}
		mApSsid = apSsid;
		mApPassword = apPassword;
		mSocketClient = new UDPSocketClient();
		mSocketServer = new UDPSocketServer(PORT_LISTENING,
				WAIT_TIMEOUT_MILLISECOND, context);
	}

	private synchronized void __interrupt() {
		if (!mIsInterrupt) {
			mIsInterrupt = true;
			mSocketClient.interrupt();
			mSocketServer.interrupt();
			// interrupt the current Thread which is used to wait for udp response 
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void interrupt() {
		if (__IEsptouchTask.DEBUG) {
			Log.d(TAG, "interrupt()");
		}
		__interrupt();
	}

	private void __listenAsyn(final int expectDataLen) {
		new Thread() {
			public void run() {
				if (__IEsptouchTask.DEBUG) {
					Log.d(TAG, "__listenAsyn() start");
				}
				long startTimestamp = System.currentTimeMillis();
				byte expectOneByte = (byte) (mApSsid.length() + mApPassword
						.length());
				byte receiveOneByte = -1;
				byte[] receiveBytes = null;
				int correctBroadcastCount = 0;
				while (correctBroadcastCount < THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT) {
					if (expectDataLen == ESP_TOUCH_RESULT_LEN_ONE) {
						receiveOneByte = mSocketServer.receiveOneByte();
					} else {
						receiveBytes = mSocketServer
								.receiveSpecLenBytes(expectDataLen);
						if (receiveBytes != null) {
							receiveOneByte = receiveBytes[0];
						}
					}
					if (receiveOneByte == expectOneByte) {
						correctBroadcastCount++;
						if (__IEsptouchTask.DEBUG) {
							Log.i(TAG, "receive " + correctBroadcastCount
									+ " correct broadcast");
						}
						// change the socket's timeout
						long consume = System.currentTimeMillis()
								- startTimestamp;
						int timeout = (int) (WAIT_TIMEOUT_MILLISECOND - consume);
						if (timeout < 0) {
							if (__IEsptouchTask.DEBUG) {
								Log.i(TAG, "esptouch timeout");
							}
							break;
						} else {
							if (__IEsptouchTask.DEBUG) {
								Log.i(TAG, "mSocketServer's new timeout is "
										+ timeout + " milliseconds");
							}
							mSocketServer.setSoTimeout(timeout);
						}
						if (correctBroadcastCount == THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT) {
							if (__IEsptouchTask.DEBUG) {
								Log.i(TAG, "receive enough correct broadcast");
							}
							if (receiveBytes != null) {
								String mBssid = ByteUtil.parseBssid(
										receiveBytes, ESP_TOUCH_RESULT_LEN_ONE,
										ESP_TOUCH_RESULT_LEN - ESP_TOUCH_RESULT_LEN_ONE);
								mEsptouchResult = new EsptouchResult(true,
										mBssid);
							}
							mIsSuc = true;
							break;
						}
					}
					else if (expectDataLen == ESP_TOUCH_RESULT_LEN_ONE
							&& receiveOneByte == Byte.MIN_VALUE) {
						if (__IEsptouchTask.DEBUG) {
							Log.i(TAG, "esptouch timeout 2");
						}
						break;
					} else if (expectDataLen == ESP_TOUCH_RESULT_LEN
							&& receiveBytes == null) {
						if (__IEsptouchTask.DEBUG) {
							Log.i(TAG, "esptouch timeout 3");
						}
						break;
					}
					else {
						if (__IEsptouchTask.DEBUG) {
							Log.i(TAG, "receive rubbish message, just ignore");
						}
					}
				}
				__EsptouchTask.this.__interrupt();
				if (__IEsptouchTask.DEBUG) {
					Log.i(TAG, "esptouch finished");
				}
				if (__IEsptouchTask.DEBUG) {
					Log.d(TAG, "__listenAsyn() finish");
				}
			}
		}.start();
	}

	private boolean __execute(IEsptouchGenerator generator) {
		
		long startTime = System.currentTimeMillis();

		// send guide code
		for (int i = 0; mIsInterrupt != true && i < REPEAT_GUIDE_CODE_TIMES; i++) {
			if (__IEsptouchTask.DEBUG) {
				Log.d(TAG, "send guide code " + i + " time");
			}
			mSocketClient.sendData(generator.getGCBytes2(), TARGET_HOSTNAME,
					TARGET_PORT, INTERVAL_GUIDE_CODE_MILLISECOND);
			// check whether it is timeout
			if (System.currentTimeMillis() - startTime > TIMEOUT_MILLISECOND_GUIDE_CODE) {
				if (__IEsptouchTask.DEBUG) {
					Log.d(TAG, "send guide code enough time");
				}
				break;
			}
		}

		// send magic code
		for (int i = 0; mIsInterrupt != true && i < REPEAT_MAGIC_CODE_TIMES; i++) {
			if (__IEsptouchTask.DEBUG) {
				Log.d(TAG, "send magic code " + i + " time");
			}
			mSocketClient.sendData(generator.getMCBytes2(), TARGET_HOSTNAME,
					TARGET_PORT, INTERVAL_MAGIC_CODE_MILLISECOND);
		}

		// send prefix code
		for (int i = 0; mIsInterrupt != true && i < REPEAT_PREFIX_CODE_TIMES; i++) {
			if (__IEsptouchTask.DEBUG) {
				Log.d(TAG, "send prefix code " + i + " time");
			}
			mSocketClient.sendData(generator.getPCBytes2(), TARGET_HOSTNAME,
					TARGET_PORT, INTERVAL_PREFIX_CODE_MILLISECOND);
		}

		// send data code
		for (int i = 0; mIsInterrupt != true && i < REPEAT_DATA_CODE_TIMES; i++) {
			// it must be interrupted when mSocketServer receiving the feedback
			// from the device or timeout
			if (__IEsptouchTask.DEBUG) {
				Log.d(TAG, "send data code " + i + " time");
			}
			mSocketClient.sendData(generator.getDCBytes2(), TARGET_HOSTNAME,
					TARGET_PORT, INTERVAL_PREFIX_CODE_MILLISECOND);
			// check whether it is timeout
			if (System.currentTimeMillis() - startTime > TIMEOUT_MILLISECOND_ONCE) {
				if (__IEsptouchTask.DEBUG) {
					Log.d(TAG, "send data code enough time");
				}
				break;
			}
		}
		if (__IEsptouchTask.DEBUG) {
			Log.i(TAG, "__execute() finished, the result is " + mIsSuc);
		}
		return mIsSuc;
	}

	private void __checkTaskValid() {
		// !!!NOTE: the esptouch task could be executed only once
		if (this.mIsExecuted) {
			throw new IllegalStateException(
					"the Esptouch task could be executed only once");
		}
		this.mIsExecuted = true;
	}
	
	@Override
	public boolean execute() throws RuntimeException {
		
		__checkTaskValid();
		
		if (__IEsptouchTask.DEBUG) {
			Log.d(TAG, "execute()");
		}
		if (Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException(
					"Don't call the esptouch Task at Main(UI) thread directly.");
		}
		// generator the esptouch byte[][] to be transformed, which will cost
		// some time(maybe a bit much)
		IEsptouchGenerator generator = new EsptouchGenerator(mApSsid,
				mApPassword);
		// listen the esptouch result asyn
		__listenAsyn(ESP_TOUCH_RESULT_LEN_ONE);
		boolean isSuc = false;
		for (int i = 0; i < TOTAL_REPEAT_TIME; i++) {
			isSuc = __execute(generator);
			if (isSuc) {
				return mIsSuc;
			}
		}
		// wait the udp response without sending udp broadcast
		try {
			Thread.sleep(WAIT_UDP_RESPONSE_MILLISECOND);
		} catch (InterruptedException e) {
			// receive the udp broadcast or the user interrupt the task
			if (this.mIsSuc)
			{
				return mIsSuc;
			}
			else
			{
				this.__interrupt();
				return false;
			}
		}
		this.__interrupt();
		return false;
	}

	@Override
	public IEsptouchResult executeForResult() throws RuntimeException {
		
		__checkTaskValid();
		
		if (__IEsptouchTask.DEBUG) {
			Log.d(TAG, "execute()");
		}
		if (Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException(
					"Don't call the esptouch Task at Main(UI) thread directly.");
		}
		// generator the esptouch byte[][] to be transformed, which will cost
		// some time(maybe a bit much)
		IEsptouchGenerator generator = new EsptouchGenerator(mApSsid,
				mApPassword);
		// listen the esptouch result asyn
		__listenAsyn(ESP_TOUCH_RESULT_LEN);
		IEsptouchResult esptouchResultFail = new EsptouchResult(false, null);
		boolean isSuc = false;
		for (int i = 0; i < TOTAL_REPEAT_TIME; i++) {
			isSuc = __execute(generator);
			if (isSuc) {
				return mEsptouchResult;
			}
		}
		// wait the udp response without sending udp broadcast
		try {
			Thread.sleep(WAIT_UDP_RESPONSE_MILLISECOND);
		} catch (InterruptedException e) {
			// receive the udp broadcast or the user interrupt the task
			if (this.mIsSuc)
			{
				return mEsptouchResult;
			}
			else
			{
				this.__interrupt();
				return esptouchResultFail;
			}
		}
		this.__interrupt();
		return esptouchResultFail;
	}

}
