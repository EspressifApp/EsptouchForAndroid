package com.espressif.iot.esptouch2.provision;

public final class TouchPacketUtils {
    public static byte[] getSyncPacket() {
        return new byte[1048];
    }

    public static byte[] getSequenceSizePacket(int size) {
        return new byte[1072 + size - 1];
    }

    public static byte[] getSequencePacket(int sequence) {
        return new byte[128 + sequence];
    }

    public static byte[] getDataPacket(int data, int index) {
        return new byte[(index << 7) | (1 << 6) | data];
    }
}
