package com.espressif.iot.esptouch2.provision;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class EspProvisionerImpl implements IEspProvisioner {
    private static final String TAG = EspProvisionerImpl.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final long SYNC_PKG_INTERVAL = 100;
    private static final long DATA_PKG_INTERVAL = 15;
    private static final long DATA_PKG_INTERVAL2 = 100;

    private static final int DATA_PKG_TIMEOUT_SEND = 90_000;
    private static final int DATA_PKG_TIMEOUT_RECEIVE = DATA_PKG_TIMEOUT_SEND + 2_000;

    private InetAddress mBroadcastAddress;
    private final ExecutorService mExecutorService;
    private final WifiManager.MulticastLock mMulticastLock;

    private volatile DatagramSocket mSyncSocket;
    private volatile DatagramSocket mProvisionSocket;

    private volatile boolean mProvisioning = false;
    private volatile boolean mSyncing = false;
    private volatile boolean mClosed = false;

    private Future<?> mSyncFuture;
    private Future<?> mProvisionReceiveFuture;
    private Future<?> mProvisionPostFuture;
    private final Set<String> mResponseMacs = new HashSet<>();

    EspProvisionerImpl(Context context) {
        checkPermissions(context);
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wm != null;
        try {
            mBroadcastAddress = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mMulticastLock = wm.createMulticastLock("EspTouchV2");
        mMulticastLock.setReferenceCounted(false);
        mMulticastLock.acquire();
        mExecutorService = Executors.newCachedThreadPool();
    }

    private void checkPermissions(Context context) {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE);
        for (String permission : permissions) {
            int check = ContextCompat.checkSelfPermission(context, permission);
            if (check != PackageManager.PERMISSION_GRANTED) {
                throw new TouchPermissionException(String.format("Permission %s is denied", permission));
            }
        }
    }

    @Override
    public synchronized void close() {
        mClosed = true;

        stopProvisioning();
        stopSync();

        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        mMulticastLock.release();
    }

    @Override
    public boolean isSyncing() {
        return mSyncing;
    }

    @Override
    public boolean isProvisioning() {
        return mProvisioning;
    }

    @Override
    public synchronized void startSync(EspSyncListener listener) {
        if (mClosed) {
            throw new IllegalStateException("The provisioner has closed");
        }

        if (mProvisioning) {
            if (listener != null) {
                listener.onError((new IllegalStateException("startSync Error, Provision task is running")));
            }
            return;
        }

        if (mSyncing) {
            if (listener != null) {
                listener.onError(new IllegalStateException("startSync Error, Sync task is running"));
            }
            return;
        }

        while (true) {
            try {
                mSyncSocket = new DatagramSocket();
                break;
            } catch (IOException e) {
                Log.w(TAG, "startSync: create Socket failed, try again");
            }
        }

        mSyncing = true;
        if (listener != null) {
            mExecutorService.submit(listener::onStart);
        }
        mSyncFuture = mExecutorService.submit(new SyncRunnable(listener));
        Log.i(TAG, "startSync");
    }

    @Override
    public synchronized void stopSync() {
        if (mSyncFuture != null) {
            mSyncFuture.cancel(true);
            mSyncFuture = null;
        }
        if (mSyncSocket != null) {
            mSyncSocket.close();
            mSyncSocket = null;
            Log.i(TAG, "stopSync");
        }
        mSyncing = false;
    }

    @Override
    public synchronized void startProvisioning(@NonNull EspProvisioningRequest request, @Nullable EspProvisioningListener listener) {
        if (mClosed) {
            throw new IllegalStateException("The provisioner has closed");
        }

        if (mSyncing) {
            if (listener != null) {
                listener.onError(new IllegalStateException("startProvision Error, Sync task is running"));
            }
            return;
        }

        if (mProvisioning) {
            if (listener != null) {
                listener.onError(new IllegalStateException("startProvision Error, Provision task is running"));
            }
            return;
        }

        DatagramSocket socket = null;
        int portMark = -1;
        for (int i = 0; i < APP_PORTS.length; ++i) {
            try {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(APP_PORTS[i]));
                socket.setSoTimeout(DATA_PKG_TIMEOUT_RECEIVE);
                portMark = i;
                break;
            } catch (IOException e) {
                Log.w(TAG, "startProvision: bind port " + APP_PORTS[i] + "failed");
            }
        }
        if (socket == null) {
            Log.w(TAG, "Create provision socket failed");
            if (listener != null) {
                listener.onError(new IllegalStateException("Create provision socket failed"));
            }
            return;
        }

        mProvisionSocket = socket;
        mProvisioning = true;
        if (listener != null) {
            mExecutorService.submit(listener::onStart);
        }
        synchronized (mResponseMacs) {
            mResponseMacs.clear();
        }
        mExecutorService.submit(new ProvisionReceiveRunnable(listener));
        mExecutorService.submit(new ProvisionPostRunnable(request, listener, portMark));
        Log.i(TAG, "startProvision");
    }

    @Override
    public synchronized void stopProvisioning() {
        if (mProvisionPostFuture != null) {
            mProvisionPostFuture.cancel(true);
            mProvisionPostFuture = null;
        }
        if (mProvisionReceiveFuture != null) {
            mProvisionReceiveFuture.cancel(true);
            mProvisionReceiveFuture = null;
        }
        if (mProvisionSocket != null) {
            mProvisionSocket.close();
            mProvisionSocket = null;
            Log.i(TAG, "stopProvision");
        }
        synchronized (mResponseMacs) {
            mResponseMacs.clear();
        }
        mProvisioning = false;
    }

    private class ProvisionReceiveRunnable implements Runnable {
        final EspProvisioningListener listener;

        ProvisionReceiveRunnable(EspProvisioningListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            Log.d(TAG, "ProvisionReceiveRunnable: start");
            DatagramPacket packet = new DatagramPacket(new byte[64], 64);
            while (!Thread.currentThread().isInterrupted()) {
                if (mProvisionSocket == null) {
                    break;
                }
                try {
                    mProvisionSocket.receive(packet);
                    if (DEBUG) {
                        byte[] received = Arrays.copyOf(packet.getData(), packet.getLength());
                        Log.i(TAG, "Received UDP: " + Arrays.toString(received));
                    }

                    if (packet.getLength() < 7) {
                        Log.w(TAG, "Invalid EspTouch response");
                        continue;
                    }
                    if (listener != null) {
                        byte[] response = Arrays.copyOf(packet.getData(), packet.getLength());
                        final InetAddress address = packet.getAddress();
                        String bssidStr = String.format("%02x:%02x:%02x:%02x:%02x:%02x",
                                response[1], response[2], response[3], response[4], response[5], response[6]);
                        synchronized (mResponseMacs) {
                            if (!mResponseMacs.contains(bssidStr)) {
                                mResponseMacs.add(bssidStr);
                                final EspProvisioningResult result = new EspProvisioningResult(address, bssidStr);
                                mExecutorService.submit(() -> listener.onResponse(result));
                            }
                        }
                    }
                } catch (IOException | NullPointerException e) {
                    Log.w(TAG, "ProvisionReceiveRunnable: Exception: " + e.getLocalizedMessage());
                    break;
                }
            }
            stopProvisioning();
            Log.d(TAG, "ProvisionReceiveRunnable: end");
        }
    }

    private class ProvisionPostRunnable implements Runnable {
        final EspProvisioningRequest request;
        final EspProvisioningListener listener;
        final int portMark;

        ProvisionPostRunnable(EspProvisioningRequest request, EspProvisioningListener listener, int portMark) {
            this.request = request;
            this.listener = listener;
            this.portMark = portMark;
        }

        @Override
        public void run() {
            Log.d(TAG, "ProvisionPostRunnable: start");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            EspProvisioningParams params = new EspProvisioningParams(request, portMark);
            List<byte[]> packetList = params.getPacketList();
            long start = SystemClock.elapsedRealtime();
            long interval = DATA_PKG_INTERVAL;
            TASK:
            while (!Thread.currentThread().isInterrupted()) {
                for (byte[] data : packetList) {
                    if (mProvisionSocket == null) {
                        break TASK;
                    }
                    DatagramPacket packet = new DatagramPacket(data, data.length, mBroadcastAddress, DEVICE_PORT);
                    try {
                        mProvisionSocket.send(packet);
                    } catch (IOException | NullPointerException e) {
                        Log.w(TAG, "ProvisionPostRunnable: Exception when posting");
                        break TASK;
                    }

                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "ProvisionPostRunnable: InterruptedException one packet");
                        break TASK;
                    }
                }

                long cost = SystemClock.elapsedRealtime() - start;
                if (cost > DATA_PKG_TIMEOUT_SEND) {
                    Log.d(TAG, "ProvisionPostRunnable: timeout");
                    break;
                } else if (cost > DATA_PKG_TIMEOUT_SEND / 2) {
                    interval = DATA_PKG_INTERVAL2;
                }

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Log.w(TAG, "ProvisionPostRunnable: InterruptedException one turn");
                    break;
                }
            }

            stopProvisioning();
            if (listener != null) {
                mExecutorService.submit(listener::onStop);
            }
            Log.d(TAG, "ProvisionPostRunnable: end");
        }
    }

    private class SyncRunnable implements Runnable {
        private EspSyncListener listener;

        SyncRunnable(EspSyncListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            Log.d(TAG, "SyncRunnable: start Sync");
            byte[] syncData = TouchPacketUtils.getSyncPacket();
            DatagramPacket packet = new DatagramPacket(syncData, syncData.length, mBroadcastAddress, DEVICE_PORT);
            while (!Thread.currentThread().isInterrupted()) {
                if (mSyncSocket == null) {
                    break;
                }

                try {
                    mSyncSocket.send(packet);
                } catch (IOException | NullPointerException e) {
                    Log.w(TAG, "SyncRunnable: Exception " + e.getMessage());
                    break;
                }

                try {
                    Thread.sleep(SYNC_PKG_INTERVAL);
                } catch (InterruptedException e) {
                    Log.w(TAG, "SyncRunnable: InterruptedException");
                    break;
                }
            }
            stopSync();
            if (listener != null) {
                mExecutorService.submit(listener::onStop);
            }
            Log.d(TAG, "Sync Future end");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
