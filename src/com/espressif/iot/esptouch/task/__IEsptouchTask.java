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
