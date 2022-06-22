package com.espressif.iot.esptouch2.provision;

public interface EspProvisioningListener {
    void onStart();

    void onResponse(EspProvisioningResult result);

    void onStop();

    void onError(Exception e);
}
