package com.espressif.iot.esptouch2.provision;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.espressif.iot.esptouch2.BuildConfig;

import java.io.Closeable;

public interface IEspProvisioner extends Closeable {
    String ESPTOUCH_VERSION = BuildConfig.VERSION_NAME;

    int DEVICE_PORT = 7001;
    int[] APP_PORTS = {18266, 28266, 38266, 48266};

    void startSync(@Nullable EspSyncListener listener);

    void stopSync();

    boolean isSyncing();

    void startProvisioning(@NonNull EspProvisioningRequest request, @Nullable EspProvisioningListener listener);

    void stopProvisioning();

    boolean isProvisioning();

}
