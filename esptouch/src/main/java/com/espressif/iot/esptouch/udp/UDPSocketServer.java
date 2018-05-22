package com.espressif.iot.esptouch.udp;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

public class UDPSocketServer {
    private static final String TAG = "UDPSocketServer";
    private final byte[] buffer;
    private DatagramPacket mReceivePacket;
    private DatagramSocket mServerSocket;
    private Context mContext;
    private WifiManager.MulticastLock mLock;
    private volatile boolean mIsClosed;

    /**
     * Constructor of UDP Socket Server
     *
     * @param port          the Socket Server port
     * @param socketTimeout the socket read timeout
     * @param context       the context of the Application
     */
    public UDPSocketServer(int port, int socketTimeout, Context context) {
        this.mContext = context;
        this.buffer = new byte[64];
        this.mReceivePacket = new DatagramPacket(buffer, 64);
        try {
            this.mServerSocket = new DatagramSocket(null);
            this.mServerSocket.setReuseAddress(true);
            this.mServerSocket.bind(new InetSocketAddress(port));
            this.mServerSocket.setSoTimeout(socketTimeout);
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        }
        this.mIsClosed = false;
        WifiManager manager = (WifiManager) mContext.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        mLock = manager.createMulticastLock("test wifi");
        Log.d(TAG, "mServerSocket is created, socket read timeout: "
                + socketTimeout + ", port: " + port);
    }

    private synchronized void acquireLock() {
        if (mLock != null && !mLock.isHeld()) {
            mLock.acquire();
        }
    }

    private synchronized void releaseLock() {
        if (mLock != null && mLock.isHeld()) {
            try {
                mLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        }
    }

    /**
     * Set the socket timeout in milliseconds
     *
     * @param timeout the timeout in milliseconds or 0 for no timeout.
     * @return true whether the timeout is set suc
     */
    public boolean setSoTimeout(int timeout) {
        try {
            this.mServerSocket.setSoTimeout(timeout);
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Receive one byte from the port and convert it into String
     *
     * @return
     */
    public byte receiveOneByte() {
        Log.d(TAG, "receiveOneByte() entrance");
        try {
            acquireLock();
            mServerSocket.receive(mReceivePacket);
            Log.d(TAG, "receive: " + (mReceivePacket.getData()[0]));
            return mReceivePacket.getData()[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Byte.MIN_VALUE;
    }

    /**
     * Receive specific length bytes from the port and convert it into String
     * 21,24,-2,52,-102,-93,-60
     * 15,18,fe,34,9a,a3,c4
     *
     * @return
     */
    public byte[] receiveSpecLenBytes(int len) {
        Log.d(TAG, "receiveSpecLenBytes() entrance: len = " + len);
        try {
            acquireLock();
            mServerSocket.receive(mReceivePacket);
            byte[] recDatas = Arrays.copyOf(mReceivePacket.getData(), mReceivePacket.getLength());
            Log.d(TAG, "received len : " + recDatas.length);
            for (int i = 0; i < recDatas.length; i++) {
                Log.e(TAG, "recDatas[" + i + "]:" + recDatas[i]);
            }
            Log.e(TAG, "receiveSpecLenBytes: " + new String(recDatas));
            if (recDatas.length != len) {
                Log.w(TAG,
                        "received len is different from specific len, return null");
                return null;
            }
            return recDatas;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void interrupt() {
        Log.i(TAG, "USPSocketServer is interrupt");
        close();
    }

    public synchronized void close() {
        if (!this.mIsClosed) {
            Log.e(TAG, "mServerSocket is closed");
            mServerSocket.close();
            releaseLock();
            this.mIsClosed = true;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
