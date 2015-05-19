package com.espressif.iot.esptouch.task;

import com.espressif.iot.esptouch.IEsptouchResult;

/**
 * IEsptouchTask defined the task of esptouch should offer. INTERVAL here means
 * the milliseconds of interval of the step. REPEAT here means the repeat times
 * of the step.
 * 
 * @author afunx
 * 
 */
public interface __IEsptouchTask {

	static final long INTERVAL_GUIDE_CODE_MILLISECOND = 8;

	static final long INTERVAL_DATA_CODE_MILLISECOND = 8;

	static final long TIMEOUT_MILLISECOND_GUIDE_CODE = 2000;
	
	static final long TIMEOUT_MILLISECOND_DATA_CODE = 4000;

	static final long TIMEOUT_MILLISECOND_TOTAL_CODE = TIMEOUT_MILLISECOND_GUIDE_CODE + TIMEOUT_MILLISECOND_DATA_CODE;
	/*
	 * TOTAL_REPEAT_TIME means execute how many circle times
	 */
	static final int TOTAL_REPEAT_TIME = 1;

	/*
	 * WAIT_UDP_RESPONSE_MILLISECOND means just wait the device send udp
	 * broadcast response, but don't send udp broadcast at the sametime
	 */
	static final int WAIT_UDP_RESPONSE_MILLISECOND = 10 * 1000;

	/**
	 * the len of the Esptouch result 1st byte is the total length of ssid and
	 * password, the other 6 bytes are the device's bssid
	 */
	static final int ESP_TOUCH_RESULT_ONE_LEN = 1;

	static final int ESP_TOUCH_RESULT_MAC_LEN = 6;

	static final int ESP_TOUCH_RESULT_IP_LEN = 4;

	static final int ESP_TOUCH_RESULT_TOTAL_LEN = ESP_TOUCH_RESULT_ONE_LEN
			+ ESP_TOUCH_RESULT_MAC_LEN + ESP_TOUCH_RESULT_IP_LEN;

	/**
	 * The port which device will send broadcast when it configured suc
	 */
	static final int PORT_LISTENING = 18266;

	/**
	 * Time between the device receive the Ap's ssid,password and the device
	 * send broadcast
	 */
	static final int TIME_MILLISECOND_DEVICE_SEND_BROADCAST = 4000;

	/**
	 * The timeout for Esptouch wait the device sending broadcast
	 */
	static final int WAIT_TIMEOUT_MILLISECOND = 48 * 1000;

	/**
	 * The threshold number how many UDP broadcast received when we think the
	 * device is configured suc
	 */
	static final int THRESHOLD_ESPTOUCH_SUC_BROADCAST_COUNT = 1;

	/**
	 * The broadcast host name
	 */
	static final String TARGET_HOSTNAME = "255.255.255.255";

	/**
	 * The target port
	 */
	static final int TARGET_PORT = 7001;

	/**
	 * Interrupt the Esptouch Task when User tap back or close the Application.
	 */
	void interrupt();

	/**
	 * Note: !!!Don't call the task at UI Main Thread or RuntimeException will
	 * be thrown Execute the Esptouch Task and return the result
	 * 
	 * @return the IEsptouchResult
	 * @throws RuntimeException
	 */
	IEsptouchResult executeForResult() throws RuntimeException;

	/**
	 * Turn on or off the log.
	 */
	static final boolean DEBUG = true;

	boolean isCancelled();
}
