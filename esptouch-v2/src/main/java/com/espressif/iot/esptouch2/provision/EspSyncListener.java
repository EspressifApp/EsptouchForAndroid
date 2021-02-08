package com.espressif.iot.esptouch2.provision;

public interface EspSyncListener {
    void onStart();

    void onStop();

    void onError(Exception e);
}
