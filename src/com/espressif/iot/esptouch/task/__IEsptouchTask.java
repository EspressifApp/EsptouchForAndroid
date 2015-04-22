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

	static final long INTERVAL_GUIDE_CODE_MILLISECOND = 10;

	static final long INTERVAL_MAGIC_CODE_MILLISECOND = 10;

	static final long INTERVAL_PREFIX_CODE_MILLISECOND = 10;

	static final long INTERVAL_DATA_CODE_MILLISECOND = 10;
	/*
	 * REPEAT_GUIDE_CODE is INTERFACE for it will be stop by its internal check
	 */
	static final long REPEAT_GUIDE_CODE_TIMES = Long.MAX_VALUE;

	static final long REPEAT_MAGIC_CODE_TIMES = 20;

	static final long REPEAT_PREFIX_CODE_TIMES = 20;
	/*
	 * REPEAT_DATA_CODE is INFINITE for it will be stop by its internal check
	 * thread
	 */
	static final long REPEAT_DATA_CODE_TIMES = Long.MAX_VALUE;

	/*
	 * TIMEOUT_MILLISECOND_GUIDE_CODE is used to chech wehther the GUIDE_CODE sent enough time
	 */
	static final long TIMEOUT_MILLISECOND_GUIDE_CODE = 2000;
	/*
	 * TIMEOUT_MILLISECOND_ONCE is used to check whether the DATA_CODE sent enough time
	 */
	static final long TIMEOUT_MILLISECOND_ONCE = 6000;

	/*
	 * TOTAL_REPEAT_TIME means execute how many circle times
	 */
	static final int TOTAL_REPEAT_TIME = 6;
	
	/*
	 * WAIT_UDP_RESPONSE_MILLISECOND means just wait the device send udp broadcast response,
	 * but don't send udp broadcast at the sametime
	 */
	static final int WAIT_UDP_RESPONSE_MILLISECOND = 12000;

	/**
	 * the len of the Esptouch result
	 * 1st byte is the total length of ssid and password,
	 * the other 6 bytes are the device's bssid
	 */
	static final int ESP_TOUCH_RESULT_LEN = 7;
	
	/**
	 * to support old sdk
	 */
	static final int ESP_TOUCH_RESULT_LEN_ONE = 1;
	
	/**
	 * The port which device will send broadcast when it configured suc
	 */
	static final int PORT_LISTENING = 10000;

	/**
	 * Time between the device receive the Ap's ssid,password and the device send broadcast
	 */
	static final int TIME_MILLISECOND_DEVICE_SEND_BROADCAST = 4000;

	/**
	 * The timeout for Esptouch wait the device sending broadcast
	 */
	static final int WAIT_TIMEOUT_MILLISECOND = (int) (TIMEOUT_MILLISECOND_ONCE * TOTAL_REPEAT_TIME)
			+ TIME_MILLISECOND_DEVICE_SEND_BROADCAST + WAIT_UDP_RESPONSE_MILLISECOND;

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
	 * @return whether the Esptouch Task is executed suc
	 */
	boolean execute() throws RuntimeException;

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
	static final boolean DEBUG = false;

}
