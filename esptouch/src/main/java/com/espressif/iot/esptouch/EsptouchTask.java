package com.espressif.iot.esptouch;

import android.content.Context;
import android.text.TextUtils;

import com.espressif.iot.esptouch.protocol.TouchData;
import com.espressif.iot.esptouch.task.EsptouchTaskParameter;
import com.espressif.iot.esptouch.task.__EsptouchTask;
import com.espressif.iot.esptouch.util.EspAES;
import com.espressif.iot.esptouch.util.EspNetUtil;

import java.util.List;

public class EsptouchTask implements IEsptouchTask {
    private __EsptouchTask _mEsptouchTask;
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

    public EsptouchTask(byte[] apSsid, byte[] apBssid, byte[] apPassword, Context context) {
        this(apSsid, apBssid, apPassword, null, context);
    }

    /**
     * Constructor of EsptouchTask
     *
     * @param apSsid     the Ap's ssid
     * @param apBssid    the Ap's bssid
     * @param apPassword the Ap's password
     * @param espAES     AES secret key
     * @param context    the Context of the Application
     */
    private EsptouchTask(String apSsid, String apBssid, String apPassword, EspAES espAES, Context context) {
        if (TextUtils.isEmpty(apSsid)) {
            throw new NullPointerException("SSID can't be empty");
        }
        if (TextUtils.isEmpty(apBssid)) {
            throw new NullPointerException("BSSID can't be empty");
        }
        if (apPassword == null) {
            apPassword = "";
        }
        TouchData ssid = new TouchData(apSsid);
        TouchData bssid = new TouchData(EspNetUtil.parseBssid2bytes(apBssid));
        TouchData password = new TouchData(apPassword);
        init(context, ssid, bssid, password, espAES);
    }

    private EsptouchTask(byte[] apSsid, byte[] apBssid, byte[] apPassword, EspAES espAES, Context context) {
        if (apSsid == null || apSsid.length == 0) {
            throw new NullPointerException("SSID can't be empty");
        }
        if (apBssid == null || apBssid.length == 0) {
            throw new NullPointerException("BSSID can't be empty");
        }
        if (apPassword == null) {
            apPassword = new byte[0];
        }
        TouchData ssid = new TouchData(apSsid);
        TouchData bssid = new TouchData(apBssid);
        TouchData password = new TouchData(apPassword);
        init(context, ssid, bssid, password, espAES);
    }

    private void init(Context context, TouchData ssid, TouchData bssid, TouchData password, EspAES aes) {
        _mParameter = new EsptouchTaskParameter();
        _mEsptouchTask = new __EsptouchTask(context, ssid, bssid, password, aes, _mParameter, true);
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

    @Override
    public void setPackageBroadcast(boolean broadcast) {
        _mParameter.setBroadcast(broadcast);
    }
}
