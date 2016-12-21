package com.espressif.iot.esptouch;

import java.util.List;

public interface IEsptouchTask {

	/**
	 * set the esptouch listener, when one device is connected to the Ap, it will be called back
	 * @param esptouchListener when one device is connected to the Ap, it will be called back
	 */
	void setEsptouchListener(IEsptouchListener esptouchListener);
	
	/**
	 * Interrupt the Esptouch Task when User tap back or close the Application.
	 */
	void interrupt();

	/**
	 * Note: !!!Don't call the task at UI Main Thread or RuntimeException will
	 * be thrown Execute the Esptouch Task and return the result
	 * 
	 * Smart Config v2.4 support the API
	 * 
	 * @return the IEsptouchResult
	 * @throws RuntimeException
	 */
	IEsptouchResult executeForResult() throws RuntimeException;
	
	/**
	 * Note: !!!Don't call the task at UI Main Thread or RuntimeException will
	 * be thrown Execute the Esptouch Task and return the result
	 * 
	 * Smart Config v2.4 support the API
	 * 
	 * It will be blocked until the client receive result count >= expectTaskResultCount.
	 * If it fail, it will return one fail result will be returned in the list.
	 * If it is cancelled while executing,
	 *     if it has received some results, all of them will be returned in the list.
	 *     if it hasn't received any results, one cancel result will be returned in the list.                                
	 * 
	 * @param expectTaskResultCount
	 *            the expect result count(if expectTaskResultCount <= 0,
	 *            expectTaskResultCount = Integer.MAX_VALUE)
	 * @return the list of IEsptouchResult
	 * @throws RuntimeException
	 */
	List<IEsptouchResult> executeForResults(int expectTaskResultCount) throws RuntimeException;
	
	/**
	 * check whether the task is cancelled by user
	 * 
	 * @return whether the task is cancelled by user
	 */
	boolean isCancelled();
}
