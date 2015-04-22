package com.espressif.iot.esptouch.task;

public interface IEsptouchGenerator {
	/**
	 * Get guide code by the format of byte[][]
	 * @return guide code by the format of byte[][]
	 */
	byte[][] getGCBytes2();
	/**
	 * Get magic code by the format of byte[][]
	 * @return magic code by the format of byte[][] 
	 */
	byte[][] getMCBytes2();
	/**
	 * Get prefix code by the format of byte[][]
	 * @return prefix code by the format of byte[][]
	 */
	byte[][] getPCBytes2();
	/**
	 * Get data code by the format of byte[][]
	 * @return data code by the format of byte[][]
	 */
	byte[][] getDCBytes2();
}
