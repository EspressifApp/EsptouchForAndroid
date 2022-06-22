package com.espressif.iot.esptouch2.provision;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EspProvisioner implements IEspProvisioner {
    private final EspProvisionerImpl mDelegate;

    public EspProvisioner(Context context) {
        mDelegate = new EspProvisionerImpl(context);
    }

    @Override
    public boolean isSyncing() {
        return mDelegate.isSyncing();
    }

    @Override
    public void startSync(@Nullable EspSyncListener listener) {
        mDelegate.startSync(listener);
    }

    @Override
    public void stopSync() {
        mDelegate.stopSync();
    }

    @Override
    public boolean isProvisioning() {
        return mDelegate.isProvisioning();
    }

    @Override
    public void startProvisioning(@NonNull EspProvisioningRequest request, @Nullable EspProvisioningListener listener) {
        mDelegate.startProvisioning(request, listener);
    }

    @Override
    public void stopProvisioning() {
        mDelegate.stopProvisioning();
    }

    @Override
    public void close() {
        mDelegate.close();
    }
}
