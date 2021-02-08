package com.espressif.iot.esptouch2.provision;

import java.net.InetAddress;

public class EspProvisioningResult {
    public final InetAddress address;
    public final String bssid;

    EspProvisioningResult(InetAddress address, String bssid) {
        this.address = address;
        this.bssid = bssid;
    }
}
