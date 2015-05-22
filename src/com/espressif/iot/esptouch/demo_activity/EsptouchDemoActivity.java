package com.espressif.iot.esptouch.demo_activity;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.task.__IEsptouchTask;
import com.espressif.iot_esptouch_demo.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class EsptouchDemoActivity extends Activity implements OnClickListener {

	private static final String TAG = "EsptouchDemoActivity";

	private TextView mTvApSsid;

	private EditText mEdtApPassword;

	private Button mBtnConfirm;
	
	private Switch mSwitchIsSsidHidden;

	private EspWifiAdminSimple mWifiAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.esptouch_demo_activity);

		mWifiAdmin = new EspWifiAdminSimple(this);
		mTvApSsid = (TextView) findViewById(R.id.tvApSssidConnected);
		mEdtApPassword = (EditText) findViewById(R.id.edtApPassword);
		mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
		mSwitchIsSsidHidden = (Switch) findViewById(R.id.switchIsSsidHidden);
		mBtnConfirm.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// display the connected ap's ssid
		String apSsid = mWifiAdmin.getWifiConnectedSsid();
		if (apSsid != null) {
			mTvApSsid.setText(apSsid);
		} else {
			mTvApSsid.setText("");
		}
		// check whether the wifi is connected
		boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
		mBtnConfirm.setEnabled(!isApSsidEmpty);
	}

	@Override
	public void onClick(View v) {

		if (v == mBtnConfirm) {
			String apSsid = mTvApSsid.getText().toString();
			String apPassword = mEdtApPassword.getText().toString();
			String apBssid = mWifiAdmin.getWifiConnectedBssid();
			Boolean isSsidHidden = mSwitchIsSsidHidden.isChecked();
			String isSsidHiddenStr = "NO";
			if (isSsidHidden) 
			{
				isSsidHiddenStr = "YES";
			}
			if (__IEsptouchTask.DEBUG) {
				Log.d(TAG, "mBtnConfirm is clicked, mEdtApSsid = " + apSsid
						+ ", " + " mEdtApPassword = " + apPassword);
			}
			new EsptouchAsyncTask2().execute(apSsid, apBssid, apPassword, isSsidHiddenStr);
		}
	}

	private class EsptouchAsyncTask2 extends AsyncTask<String, Void, IEsptouchResult> {

		private ProgressDialog mProgressDialog;

		private IEsptouchTask mEsptouchTask;

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(EsptouchDemoActivity.this);
			mProgressDialog
					.setMessage("Esptouch is configuring, please wait for a moment...");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (__IEsptouchTask.DEBUG) {
						Log.i(TAG, "progress dialog is canceled");
					}
					if (mEsptouchTask != null) {
						mEsptouchTask.interrupt();
					}
				}
			});
			mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
					"Waiting...", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			mProgressDialog.show();
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setEnabled(false);
		}

		@Override
		protected IEsptouchResult doInBackground(String... params) {
			String apSsid = params[0];
			String apBssid = params[1];
			String apPassword = params[2];
			String isSsidHiddenStr = params[3];
			boolean isSsidHidden = false;
			if(isSsidHiddenStr.equals("YES"))
			{
				isSsidHidden = true;
			}
			mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, isSsidHidden, EsptouchDemoActivity.this);
			IEsptouchResult result = mEsptouchTask.executeForResult();
			return result;
		}

		@Override
		protected void onPostExecute(IEsptouchResult result) {
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setEnabled(true);
			mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
					"Confirm");
			// it is unnecessary at the moment, add here just to show how to use isCancelled()
			if (!result.isCancelled()) {
				if (result.isSuc()) {
					mProgressDialog.setMessage("Esptouch success, bssid = "
							+ result.getBssid() + ",InetAddress = "
							+ result.getInetAddress().getHostAddress());
				} else {
					mProgressDialog.setMessage("Esptouch fail");
				}
			}
		}
	}
}
