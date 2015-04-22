package com.espressif.iot.esptouch;

import android.content.Context;

import com.espressif.iot.esptouch.task.__EsptouchTask;


public class EsptouchTask implements IEsptouchTask {

	public __EsptouchTask _mEsptouchTask;

	/**
	 * Constructor of EsptouchTask
	 * 
	 * @param apSsid
	 *            the Ap's ssid
	 * @param apPassword
	 *            the Ap's password
	 * @param context
	 * 			  the Context of the Application
	 */
	public EsptouchTask(String apSsid, String apPassword, Context context) {
		_mEsptouchTask = new __EsptouchTask(apSsid, apPassword, context);
	}

	@Override
	public void interrupt() {
		_mEsptouchTask.interrupt();
	}

	@Override
	public boolean execute() throws RuntimeException {
		return _mEsptouchTask.execute();
	}

	@Override
	public IEsptouchResult executeForResult() throws RuntimeException {
		return _mEsptouchTask.executeForResult();
	}

}
