package com.espressif.iot.esptouch2.provision;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EspProvisioningRequest implements Parcelable {
    private static final int SSID_LENGTH_MAX = 32;
    private static final int PASSWORD_LENGTH_MAX = 64;
    private static final int BSSID_LENGTH = 6;
    private static final int AES_KEY_LENGTH = 16;

    public static final int RESERVED_LENGTH_MAX = 64;

    public static final int SECURITY_V1 = 1;
    public static final int SECURITY_V2 = 2;

    public final InetAddress address;

    public final byte[] ssid;
    public final byte[] bssid;
    public final byte[] password;

    public final byte[] reservedData;

    public final byte[] aesKey;
    public final int securityVer;

    private EspProvisioningRequest(InetAddress address, byte[] ssid, byte[] bssid, byte[] password,
                                   byte[] reservedData, byte[] aesKey, int securityVer) {
        this.address = address;
        this.ssid = ssid;
        this.bssid = bssid;
        this.password = password;
        this.reservedData = reservedData;
        this.aesKey = aesKey;
        this.securityVer = securityVer;
    }

    private EspProvisioningRequest(Parcel in) {
        address = (InetAddress) in.readSerializable();
        ssid = in.createByteArray();
        bssid = in.createByteArray();
        password = in.createByteArray();
        reservedData = in.createByteArray();
        aesKey = in.createByteArray();
        securityVer = in.readInt();
    }

    public static final Creator<EspProvisioningRequest> CREATOR = new Creator<EspProvisioningRequest>() {
        @Override
        public EspProvisioningRequest createFromParcel(Parcel in) {
            return new EspProvisioningRequest(in);
        }

        @Override
        public EspProvisioningRequest[] newArray(int size) {
            return new EspProvisioningRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(address);
        dest.writeByteArray(ssid);
        dest.writeByteArray(bssid);
        dest.writeByteArray(password);
        dest.writeByteArray(reservedData);
        dest.writeByteArray(aesKey);
        dest.writeInt(securityVer);
    }

    public static class Builder {
        private InetAddress address;

        private byte[] ssid = null;
        private byte[] bssid = null;
        private byte[] password = null;

        private byte[] reservedData;

        private byte[] aesKey;
        private int secretVer = SECURITY_V1;

        private Context mContext;

        public Builder(Context context) {
            mContext = context.getApplicationContext();
        }

        public Builder setSSID(@Nullable byte[] ssid) {
            if (ssid != null && ssid.length > SSID_LENGTH_MAX) {
                throw new IllegalArgumentException("SSID length is greater than 32");
            }
            this.ssid = ssid;
            return this;
        }

        public Builder setBSSID(@NonNull byte[] bssid) {
            if (bssid.length != BSSID_LENGTH) {
                throw new IllegalArgumentException("Invalid BSSID data");
            }
            this.bssid = bssid;
            return this;
        }

        public Builder setPassword(@Nullable byte[] password) {
            if (password != null && password.length > PASSWORD_LENGTH_MAX) {
                throw new IllegalArgumentException("Password length is greater than 64");
            }

            this.password = password;
            return this;
        }

        public Builder setReservedData(@Nullable byte[] data) {
            if (data != null && data.length > RESERVED_LENGTH_MAX) {
                throw new IllegalArgumentException("ReservedData length is greater than 64");
            }

            this.reservedData = data;
            return this;
        }

        public Builder setAESKey(@Nullable byte[] aesKey) {
            if (aesKey != null && aesKey.length != AES_KEY_LENGTH) {
                throw new IllegalArgumentException("AES Key must be null or 16 bytes");
            }

            this.aesKey = aesKey;
            return this;
        }

        public Builder setSecurityVer(int securityVer) {
            switch (securityVer) {
                case SECURITY_V1:
                case SECURITY_V2:
                    break;
                default:
                    throw new IllegalArgumentException(("Security ver is illegal"));
            }
            this.secretVer = securityVer;
            return this;
        }

        public EspProvisioningRequest build() {
            WifiManager wm = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert wm != null;
            WifiInfo wifiInfo = wm.getConnectionInfo();
            if (ssid == null || ssid.length == 0) {
                if (wifiInfo.getHiddenSSID()) {
                    ssid = TouchNetUtil.getRawSsidBytes(wifiInfo);
                    if (ssid == null) {
                        ssid = TouchNetUtil.getSsidString(wifiInfo).getBytes();
                    }
                }
            }
            if (bssid == null) {
                bssid = TouchNetUtil.convertBssid2Bytes(wifiInfo.getBSSID());
            }
            if (address == null) {
                if (wifiInfo.getIpAddress() != 0) {
                    address = TouchNetUtil.getAddress(wifiInfo.getIpAddress());
                }
            }
            if (address == null) {
                address = TouchNetUtil.getIPv4Address();
            }
            if (address == null) {
                address = TouchNetUtil.getIPv6Address();
            }
            if (address == null) {
                try {
                    address = InetAddress.getByName("255.255.255.255");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

            return new EspProvisioningRequest(address, ssid, bssid, password, reservedData, aesKey, secretVer);
        }
    }
}
