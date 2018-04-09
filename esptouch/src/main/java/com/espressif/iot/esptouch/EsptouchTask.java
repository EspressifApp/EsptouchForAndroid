package com.espressif.iot.esptouch;

import android.content.Context;

import com.espressif.iot.esptouch.task.EsptouchTaskParameter;
import com.espressif.iot.esptouch.task.__EsptouchTask;
import com.espressif.iot.esptouch.util.EspAES;

import java.util.List;

public class EsptouchTask implements IEsptouchTask {

    public __EsptouchTask _mEsptouchTask;
    private EsptouchTaskParameter _mParameter;

    /**
     * Constructor of EsptouchTask
     *
     * @param apSsid     the Ap's ssid
     * @param apBssid    the Ap's bssid
     * @param apPassword the Ap's password
     * @param context    the Context of the Application
     */
    public EsptouchTask(String apSsid, String apBssid, String apPassword, Context context) {
        this(apSsid, apBssid, apPassword, null, context);
    }

    /**
     * Constructor of EsptouchTask
     *
     * @param apSsid     the Ap's ssid
     * @param apBssid    the Ap's bssid
     * @param apPassword the Ap's password
     * @param espAES     AES secret key and iv
     * @param context    the Context of the Application
     */
    public EsptouchTask(String apSsid, String apBssid, String apPassword, EspAES espAES, Context context) {
        _mParameter = new EsptouchTaskParameter();
        _mEsptouchTask = new __EsptouchTask(apSsid, apBssid, apPassword, espAES,
                context, _mParameter, true);
    }

    public EsptouchTask(String apSsid, String apBssid, String apPassword, EspAES espAES, Context context, String udpHost) {
        _mParameter = new EsptouchTaskParameter();
        _mEsptouchTask = new __EsptouchTask(apSsid, apBssid, apPassword, espAES,
                context, _mParameter, true);
    }

    /**
     * @deprecated Use the new {{@link #EsptouchTask(String, String, String, EspAES, Context)} API
     */
    public EsptouchTask(String apSsid, String apBssid, String apPassword,
                        boolean isSsidHidden, Context context) {
        this(apSsid, apBssid, apPassword, null, context);
    }

    /**
     * Constructor of EsptouchTask
     *
     * @param apSsid             the Ap's ssid
     * @param apBssid            the Ap's bssid
     * @param apPassword         the Ap's password
     * @param timeoutMillisecond (it should be >= 15000+6000) millisecond of total timeout
     * @param context            the Context of the Application
     */
    public EsptouchTask(String apSsid, String apBssid, String apPassword, EspAES espAES, int timeoutMillisecond, Context context) {
        _mParameter = new EsptouchTaskParameter();
        _mParameter.setWaitUdpTotalMillisecond(timeoutMillisecond);
        _mEsptouchTask = new __EsptouchTask(apSsid, apBssid, apPassword, espAES,
                context, _mParameter, true);
    }

    /**
     * @deprecated Use the new {{@link #EsptouchTask(String, String, String, EspAES, int, Context)} API
     */
    public EsptouchTask(String apSsid, String apBssid, String apPassword, boolean isSsidHidden, int timeoutMillisecond, Context context) {
        this(apSsid, apBssid, apPassword, null, context);
    }

    @Override
    public void interrupt() {
        _mEsptouchTask.interrupt();
    }

    @Override
    public IEsptouchResult executeForResult() throws RuntimeException {
        return _mEsptouchTask.executeForResult();
    }

    @Override
    public boolean isCancelled() {
        return _mEsptouchTask.isCancelled();
    }

    @Override
    public List<IEsptouchResult> executeForResults(int expectTaskResultCount)
            throws RuntimeException {
        if (expectTaskResultCount <= 0) {
            expectTaskResultCount = Integer.MAX_VALUE;
        }
        return _mEsptouchTask.executeForResults(expectTaskResultCount);
    }

    @Override
    public void setEsptouchListener(IEsptouchListener esptouchListener) {
        _mEsptouchTask.setEsptouchListener(esptouchListener);
    }
}
