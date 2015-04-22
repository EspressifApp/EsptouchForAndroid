package com.espressif.iot.esptouch;

public interface IEsptouchTask {
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
	 * Smart Config v1.1 support the API
	 * 
	 * @return the IEsptouchResult
	 * @throws RuntimeException
	 */
	IEsptouchResult executeForResult() throws RuntimeException;
	
}
