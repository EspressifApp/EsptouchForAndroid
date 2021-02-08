package com.espressif.esptouch.android.v2;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;

import com.espressif.esptouch.android.EspTouchActivityAbs;
import com.espressif.esptouch.android.EspTouchApp;
import com.espressif.esptouch.android.R;
import com.espressif.esptouch.android.databinding.ActivityEsptouch2Binding;
import com.espressif.iot.esptouch2.provision.EspProvisioner;
import com.espressif.iot.esptouch2.provision.EspProvisioningRequest;
import com.espressif.iot.esptouch2.provision.EspSyncListener;
import com.espressif.iot.esptouch2.provision.IEspProvisioner;
import com.espressif.iot.esptouch2.provision.TouchNetUtil;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class EspTouch2Activity extends EspTouchActivityAbs {
    private static final String TAG = EspTouch2Activity.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 0x01;
    private static final int REQUEST_PROVISIONING = 0x02;

    private EspProvisioner mProvisioner;

    private ActivityEsptouch2Binding mBinding;

    private InetAddress mAddress;
    private String mSsid;
    private byte[] mSsidBytes;
    private String mBssid;
    private CharSequence mMessage;
    private int mMessageVisible;
    private int mControlVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityEsptouch2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.controlGroup.setVisibility(View.INVISIBLE);
        mBinding.confirmBtn.setOnClickListener(v -> {
            EspProvisioningRequest request = genRequest();
            if (request == null) {
                return;
            }
            if (mProvisioner != null) {
                mProvisioner.close();
            }
            Intent intent = new Intent(EspTouch2Activity.this, EspProvisioningActivity.class);
            intent.putExtra(EspProvisioningActivity.KEY_PROVISION_REQUEST, request);
            startActivityForResult(intent, REQUEST_PROVISIONING);
            mBinding.confirmBtn.setEnabled(false);
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        EspTouchApp.getInstance().observeBroadcast(this, action -> check());

        check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mProvisioner = new EspProvisioner(getApplicationContext());
        SyncListener syncListener = new SyncListener(mProvisioner);
        mProvisioner.startSync(syncListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mProvisioner != null) {
            mProvisioner.stopSync();
            mProvisioner.close();
            mProvisioner = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            check();
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_PROVISIONING) {
            mBinding.confirmBtn.setEnabled(true);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected String getEspTouchVersion() {
        return getString(R.string.esptouch2_about_version, IEspProvisioner.ESPTOUCH_VERSION);
    }

    private boolean checkState() {
        StateResult stateResult = checkPermission();
        if (!stateResult.permissionGranted) {
            mMessage = stateResult.message;
            mBinding.messageView.setOnClickListener(v -> {
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(EspTouch2Activity.this, permissions, REQUEST_PERMISSION);
            });
            return false;
        }

        stateResult = checkLocation();
        if (stateResult.locationRequirement) {
            mMessage = stateResult.message;
            mBinding.messageView.setOnClickListener(null);
            return false;
        }

        stateResult = checkWifi();
        mSsid = stateResult.ssid;
        mSsidBytes = stateResult.ssidBytes;
        mBssid = stateResult.bssid;
        mMessage = stateResult.message;
        mAddress = stateResult.address;
        return stateResult.wifiConnected && !stateResult.is5G;
    }

    private byte[] getBssidBytes() {
        return mBssid == null ? null : TouchNetUtil.convertBssid2Bytes(mBssid);
    }

    private void invalidateAll() {
        mBinding.controlGroup.setVisibility(mControlVisible);
        mBinding.apSsidText.setText(mSsid);
        mBinding.apBssidText.setText(mBssid);
        mBinding.ipText.setText(mAddress == null ? "" : mAddress.getHostAddress());
        mBinding.messageView.setText(mMessage);
        mBinding.messageView.setVisibility(mMessageVisible);
    }

    private void check() {
        if (checkState()) {
            mControlVisible = View.VISIBLE;
            mMessageVisible = View.GONE;
        } else {
            mControlVisible = View.GONE;
            mMessageVisible = View.VISIBLE;

            if (mProvisioner != null) {
                if (mProvisioner.isSyncing()) {
                    mProvisioner.stopSync();
                }
                if (mProvisioner.isProvisioning()) {
                    mProvisioner.stopProvisioning();
                }
            }
        }
        invalidateAll();
    }

    private EspProvisioningRequest genRequest() {
        mBinding.aesKeyEdit.setError(null);
        mBinding.customDataEdit.setError(null);

        CharSequence aesKeyChars = mBinding.aesKeyEdit.getText();
        byte[] aesKey = null;
        if (!TextUtils.isEmpty(aesKeyChars)) {
            aesKey = aesKeyChars.toString().getBytes();
        }
        if (aesKey != null && aesKey.length != 16) {
            mBinding.aesKeyEdit.setError(getString(R.string.esptouch2_aes_key_error));
            return null;
        }

        CharSequence customDataChars = mBinding.customDataEdit.getText();
        byte[] customData = null;
        if (!TextUtils.isEmpty(customDataChars)) {
            customData = customDataChars.toString().getBytes();
        }
        if (customData != null && customDataChars.length() > 127) {
            mBinding.customDataEdit.setError(getString(R.string.esptouch2_custom_data_error));
            return null;
        }

        CharSequence password = mBinding.apPasswordEdit.getText();
        return new EspProvisioningRequest.Builder(getApplicationContext())
                .setSSID(mSsidBytes)
                .setBSSID(getBssidBytes())
                .setPassword(password == null ? null : password.toString().getBytes())
                .setAESKey(aesKey)
                .setReservedData(customData)
                .build();
    }

    private static class SyncListener implements EspSyncListener {
        private WeakReference<EspProvisioner> provisioner;

        SyncListener(EspProvisioner provisioner) {
            this.provisioner = new WeakReference<>(provisioner);
        }

        @Override
        public void onStart() {
            Log.d(TAG, "SyncListener onStart");
        }

        @Override
        public void onStop() {
            Log.d(TAG, "SyncListener onStop");
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
            EspProvisioner provisioner = this.provisioner.get();
            if (provisioner != null) {
                provisioner.stopSync();
            }
        }
    }
}
