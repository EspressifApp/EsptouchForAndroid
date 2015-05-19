package com.espressif.iot.esptouch.task;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import com.espressif.iot.esptouch.EsptouchResult;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.protocol.EsptouchGenerator;
import com.espressif.iot.esptouch.udp.UDPSocketClient;
import com.espressif.iot.esptouch.udp.UDPSocketServer;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.EspNetUtil;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

public class __EsptouchTask implements __IEsptouchTask {

	private static final String TAG = "EsptouchTask";

	private volatile EsptouchResult mEsptouchResult;
	private volatile boolean mIsSuc = false;
	private volatile boolean mIsInterrupt = false;
	private volatile boolean mIsExecuted = false;
	private final UDPSocketClient mSocketClient;
	private final UDPSocketServer mSocketServer;
	private final String mApSsid;
	private final String mApPassword;
	private final Context mContext;
	private AtomicBoolean mIsCancelled;

	public __EsptouchTask(String apSsid, String apPassword, Context context) {
		if (TextUtils.isEmpty(apSsid)) {
			throw new IllegalArgumentException(
					"the apSsid should be null or empty");
		}
		if (apPassword == null) {
			apPassword = "";
		}
		mContext = context;
		mApSsid = apSsid;
		mApPassword = apPassword;
		mIsCancelled = new AtomicBoolean(false);
		mSocketClient = new UDPSocketClient();
		mSocketServer = new UDPSocketServer(PORT_LISTENING,
				WAIT_TIMEOUT_MILLISECOND + WAIT_UDP_RESPONSE_MILLISECOND,
				context);
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
		mIsCancelled.set(true);
		__interrupt();
	}

	private void __listenAsyn(final int expectDataLen) {
		new Thread() {
			public void run() {
				if (__IEsptouchTask.DEBUG) {
					Log.d(TAG, "__listenAsyn() start");
				}
				long startTimestamp = System.currentTimeMillis();
				byte[] apSsidAndPassword = ByteUtil.getBytesByString(mApSsid
						+ mApPassword);
				byte expectOneByte = (byte) (apSsidAndPassword.length + 7);
				if (__IEsptouchTask.DEBUG) {
					Log.i(TAG, "expectOneByte: " + (0 + expectOneByte));
				}
				byte receiveOneByte = -1;
				byte[] receiveBytes = null;
				int correctBroadcastCount = 0;
				while (correctBroadcastCount < THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT) {
					receiveBytes = mSocketServer
							.receiveSpecLenBytes(expectDataLen);
					if (receiveBytes != null) {
						receiveOneByte = receiveBytes[0];
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
							if (correctBroadcastCount == THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT) {
								if (__IEsptouchTask.DEBUG) {
									Log.i(TAG,
											"receive enough correct broadcast");
								}
								if (receiveBytes != null) {
									String mBssid = ByteUtil.parseBssid(
											receiveBytes,
											ESP_TOUCH_RESULT_ONE_LEN,
											ESP_TOUCH_RESULT_MAC_LEN);
									InetAddress inetAddress = EspNetUtil
											.parseInetAddr(
													receiveBytes,
													ESP_TOUCH_RESULT_ONE_LEN
															+ ESP_TOUCH_RESULT_MAC_LEN,
													ESP_TOUCH_RESULT_IP_LEN);
									mEsptouchResult = new EsptouchResult(true,
											mBssid, inetAddress);
								}
								mIsSuc = true;
								break;
							}
						}
					}
					else if (expectDataLen == ESP_TOUCH_RESULT_TOTAL_LEN
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
		long currentTime = startTime;
		long lastTime = currentTime - TIMEOUT_MILLISECOND_TOTAL_CODE;

		byte[][] gcBytes2 = generator.getGCBytes2();
		byte[][] dcBytes2 = generator.getDCBytes2();
		Log.e(TAG, "gcBytes2 start:");
		for(int i=0;i<gcBytes2.length;i++)
		{
			Log.e(TAG, 0+gcBytes2[i].length + "");
		}
		Log.e(TAG, "gcBytes2 end:");
		Log.e(TAG, "dcBytes2 start:");
		Log.e(TAG, "dcBytes2 end:");
		int index = 0;
		final int one_data_len = 3;
		while (!mIsInterrupt) {
			if (currentTime - lastTime >= TIMEOUT_MILLISECOND_TOTAL_CODE) {
				if (__IEsptouchTask.DEBUG) {
					Log.d(TAG, "send gc code ");
				}
				// send guide code
				while (!mIsInterrupt
						&& System.currentTimeMillis() - currentTime < TIMEOUT_MILLISECOND_GUIDE_CODE) {
					mSocketClient.sendData(gcBytes2, TARGET_HOSTNAME,
							TARGET_PORT, INTERVAL_GUIDE_CODE_MILLISECOND);
					// check whether the udp is send enough time
					if (System.currentTimeMillis() - startTime > WAIT_TIMEOUT_MILLISECOND) {
						break;
					}
				}
				lastTime = currentTime;
			} else {
				mSocketClient.sendData(dcBytes2, index, one_data_len,
						TARGET_HOSTNAME, TARGET_PORT,
						INTERVAL_DATA_CODE_MILLISECOND);
			}
			currentTime = System.currentTimeMillis();
			// check whether the udp is send enough time
			if (currentTime - startTime > WAIT_TIMEOUT_MILLISECOND) {
				break;
			}
			index = (index + one_data_len) % dcBytes2.length;
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
	public IEsptouchResult executeForResult() throws RuntimeException {

		__checkTaskValid();

		if (__IEsptouchTask.DEBUG) {
			Log.d(TAG, "execute()");
		}
		if (Looper.myLooper() == Looper.getMainLooper()) {
			throw new RuntimeException(
					"Don't call the esptouch Task at Main(UI) thread directly.");
		}
		InetAddress localInetAddress = EspNetUtil.getLocalInetAddress(mContext);
		if (__IEsptouchTask.DEBUG) {
			Log.i(TAG, "localInetAddress: " + localInetAddress);
		}
		// generator the esptouch byte[][] to be transformed, which will cost
		// some time(maybe a bit much)
		IEsptouchGenerator generator = new EsptouchGenerator(mApSsid,
				mApPassword, localInetAddress);
		// listen the esptouch result asyn
		__listenAsyn(ESP_TOUCH_RESULT_TOTAL_LEN);
		EsptouchResult esptouchResultFail = new EsptouchResult(false, null,
				null);
		boolean isSuc = false;
		for (int i = 0; i < TOTAL_REPEAT_TIME; i++) {
			isSuc = __execute(generator);
			if (isSuc) {
				mEsptouchResult.setIsCancelled(mIsCancelled.get());
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
				esptouchResultFail.setIsCancelled(mIsCancelled.get());
				return esptouchResultFail;
			}
		}
		this.__interrupt();
		esptouchResultFail.setIsCancelled(mIsCancelled.get());
		return esptouchResultFail;
		
	}

	@Override
	public boolean isCancelled() {
		return this.mIsCancelled.get();
	}

}
