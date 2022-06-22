package com.espressif.iot.esptouch2.provision;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class EspProvisioningParams {
    private static final String TAG = "ProvisionParams";
    private static final boolean DEBUG = true;

    private static final int VERSION = 0;

    private static final int SEQUENCE_FIRST = -1;

    private static final byte[] EMPTY_DATA = new byte[0];

    private final List<byte[]> mDataPacketList;

    private int mAppPortMark;
    private byte[] mPassword;
    private byte[] mSsid;
    private byte[] mReservedData;

    private boolean mWillEncrypt;
    private byte[] mAesKey;

    private boolean mPasswordEncode;
    private boolean mReservedEncode;
    private boolean mSsidEncode;

    private byte[] mHead;

    EspProvisioningParams(EspProvisioningRequest request, int portMark) {
        mDataPacketList = new ArrayList<>();
        this.mAppPortMark = portMark;

        parse(request);
        generate();
    }

    List<byte[]> getPacketList() {
        return new ArrayList<>(mDataPacketList);
    }

    private byte[] randomBytes(Random random, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; ++i) {
            result[i] = (byte) random.nextInt(127);
        }
        return result;
    }

    private boolean checkCharEncode(byte[] data) {
        for (byte b : data) {
            if (b < 0) {
                return true;
            }
        }
        return false;
    }

    private void parse(EspProvisioningRequest request) {
        TouchCRC crcCalc = new TouchCRC();

        boolean isIPv4 = request.address instanceof Inet4Address;
        mPassword = request.password == null ? EMPTY_DATA : request.password;
        mSsid = request.ssid == null ? EMPTY_DATA : request.ssid;
        mReservedData = request.reservedData == null ? EMPTY_DATA : request.reservedData;

        mWillEncrypt = request.aesKey != null && (mPassword.length > 0 || mReservedData.length > 0);
        mAesKey = mWillEncrypt ? request.aesKey : EMPTY_DATA;

        mPasswordEncode = checkCharEncode(mPassword);
        mReservedEncode = checkCharEncode(mReservedData);
        mSsidEncode = checkCharEncode(mSsid);

        int ssidInfo = mSsid.length | (mSsidEncode ? 0b10000000 : 0);
        int pwdInfo = mPassword.length | (mPasswordEncode ? 0b10000000 : 0);
        int reservedInfo = mReservedData.length | (mReservedEncode ? 0b10000000 : 0);
        crcCalc.reset();
        crcCalc.update(request.bssid);
        int bssidCrc = (int) (crcCalc.getValue() & 0xff);
        int flag = (isIPv4 ? 1 : 0) // bit0: ipv4 or ipv6
                | (mWillEncrypt ? 0b01_0 : 0) // bit1 bit2: crypt
                | ((mAppPortMark & 0b11) << 3) // bit3 bit4: app port
                | ((VERSION & 0b11) << 6); // bit6 bit7: version
        mHead = new byte[]{
                (byte) ssidInfo,
                (byte) pwdInfo,
                (byte) reservedInfo,
                (byte) bssidCrc,
                (byte) flag,
                0 // head crc
        };
        crcCalc.reset();
        crcCalc.update(mHead, 0, 5);
        int headerCrc = (int) (crcCalc.getValue() & 0xff);
        mHead[5] = (byte) headerCrc;
    }

    private void setTotalSequenceSize(int totalSequenceSize) {
        byte[] packet = TouchPacketUtils.getSequenceSizePacket(totalSequenceSize);
        mDataPacketList.set(1, packet);
        mDataPacketList.set(3, packet);
    }

    private void addDataFor6Bytes(byte[] buf, int sequence, int seqCrc, boolean tailIsCrc) {
        if (DEBUG) {
            Log.w(TAG, "buf=" + Arrays.toString(buf) + " , seq=" + sequence + " , seqCrc=" + seqCrc +
                    " , tailIsCrc=" + tailIsCrc);
        }
        if (sequence == SEQUENCE_FIRST) {
            byte[] syncPacket = TouchPacketUtils.getSyncPacket();
            byte[] sequenceSizePacket = new byte[0];
            mDataPacketList.add(syncPacket);
            mDataPacketList.add(sequenceSizePacket);
            mDataPacketList.add(syncPacket);
            mDataPacketList.add(sequenceSizePacket);
        } else {
            byte[] sequencePacket = TouchPacketUtils.getSequencePacket(sequence);
            mDataPacketList.add(sequencePacket);
            mDataPacketList.add(sequencePacket);
            mDataPacketList.add(sequencePacket);
        }

        int bitCount = tailIsCrc ? 7 : 8;
        for (int i = 0; i < bitCount; ++i) {
            int data = (buf[5] >> i & 1)
                    | ((buf[4] >> i & 1) << 1)
                    | ((buf[3] >> i & 1) << 2)
                    | ((buf[2] >> i & 1) << 3)
                    | ((buf[1] >> i & 1) << 4)
                    | ((buf[0] >> i & 1) << 5);

            byte[] dataPacket = TouchPacketUtils.getDataPacket(data, i);
            mDataPacketList.add(dataPacket);
        }

        if (tailIsCrc) {
            byte[] seqCrcPacket = TouchPacketUtils.getDataPacket(seqCrc, 7);
            mDataPacketList.add(seqCrcPacket);
        }
    }

    private void generate() {
        Random random = new Random();
        TouchCRC crcCalc = new TouchCRC();

        int padding;

        byte[] password;
        byte[] passwordPadding;
        int passwordPaddingFactor;
        boolean passwordEncode;

        byte[] reservedData;
        byte[] reservedPadding;
        int reservedPaddingFactor;
        boolean reservedEncode;

        byte[] ssid;
        byte[] ssidPadding;
        int ssidPaddingFactor;
        boolean ssidEncode;

        if (mWillEncrypt) {
            byte[] willEncryptData = new byte[mPassword.length + mReservedData.length];
            System.arraycopy(mPassword, 0, willEncryptData, 0, mPassword.length);
            System.arraycopy(mReservedData, 0, willEncryptData, mPassword.length, mReservedData.length);
            TouchAES aes = new TouchAES(mAesKey);
            byte[] encryptedData = aes.encrypt(willEncryptData);
            password = encryptedData;
            passwordEncode = true;
            passwordPaddingFactor = 5;
            passwordPadding = EMPTY_DATA;
            padding = passwordPaddingFactor - encryptedData.length % passwordPaddingFactor;
            if (padding < passwordPaddingFactor) {
                passwordPadding = randomBytes(random, padding);
            }

            reservedData = EMPTY_DATA;
            reservedPadding = EMPTY_DATA;
            reservedPaddingFactor = -1;
            reservedEncode = false;
        } else if (!mPasswordEncode && !mReservedEncode) {
            byte[] nonEncodeData = new byte[mPassword.length + mReservedData.length];
            System.arraycopy(mPassword, 0, nonEncodeData, 0, mPassword.length);
            System.arraycopy(mReservedData, 0, nonEncodeData, mPassword.length, mReservedData.length);
            password = nonEncodeData;
            passwordEncode = false;
            passwordPaddingFactor = 6;
            passwordPadding = EMPTY_DATA;
            padding = passwordPaddingFactor - nonEncodeData.length % passwordPaddingFactor;
            if (padding < passwordPaddingFactor) {
                passwordPadding = randomBytes(random, padding);
            }

            reservedData = EMPTY_DATA;
            reservedPadding = EMPTY_DATA;
            reservedPaddingFactor = -1;
            reservedEncode = false;
        } else {
            password = mPassword;
            passwordEncode = mPasswordEncode;
            passwordPadding = EMPTY_DATA;
            passwordPaddingFactor = passwordEncode ? 5 : 6;
            padding = passwordPaddingFactor - password.length % passwordPaddingFactor;
            if (padding < passwordPaddingFactor) {
                passwordPadding = randomBytes(random, padding);
            }

            reservedData = mReservedData;
            reservedEncode = mReservedEncode;
            reservedPadding = EMPTY_DATA;
            reservedPaddingFactor = reservedEncode ? 5 : 6;
            padding = reservedPaddingFactor - reservedData.length % reservedPaddingFactor;
            if (padding < reservedPaddingFactor) {
                reservedPadding = randomBytes(random, padding);
            }
        }

        ssid = mSsid;
        ssidEncode = mSsidEncode;
        ssidPaddingFactor = ssidEncode ? 5 : 6;
        ssidPadding = EMPTY_DATA;
        padding = ssidPaddingFactor - ssid.length % ssidPaddingFactor;
        if (padding < ssidPaddingFactor) {
            ssidPadding = randomBytes(random, padding);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(mHead, 0, mHead.length);
        os.write(password, 0, password.length);
        os.write(passwordPadding, 0, passwordPadding.length);
        os.write(reservedData, 0, reservedData.length);
        os.write(reservedPadding, 0, reservedPadding.length);
        os.write(ssid, 0, ssid.length);
        os.write(ssidPadding, 0, ssidPadding.length);

        int reservedBeginPosition = mHead.length + password.length + passwordPadding.length;
        int ssidBeginPosition = reservedBeginPosition + reservedData.length + reservedPadding.length;
        int offset = 0;
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        int sequence = SEQUENCE_FIRST;
        int count = 0;
        while (is.available() > 0) {
            int expectLength;
            boolean tailIsCrc;
            if (sequence < SEQUENCE_FIRST + 1) {
                // First packet
                tailIsCrc = false;
                expectLength = 6;
            } else {
                if (offset < reservedBeginPosition) {
                    // Password data
                    tailIsCrc = !passwordEncode;
                    expectLength = passwordPaddingFactor;
                } else if (offset < ssidBeginPosition) {
                    // Reserved data
                    tailIsCrc = !reservedEncode;
                    expectLength = reservedPaddingFactor;
                } else {
                    // SSID data
                    tailIsCrc = !ssidEncode;
                    expectLength = ssidPaddingFactor;
                }
            }
            byte[] buf = new byte[6];
            int read = is.read(buf, 0, expectLength);
            if (read == -1) {
                break;
            }
            offset += read;

            crcCalc.reset();
            crcCalc.update(buf, 0, read);
            int seqCrc = (int) crcCalc.getValue();
            if (expectLength < buf.length) {
                buf[buf.length - 1] = (byte) seqCrc;
            }
            addDataFor6Bytes(buf, sequence, seqCrc, tailIsCrc);
            ++sequence;
            ++count;
        } // end while

        setTotalSequenceSize(count);
    }
}
