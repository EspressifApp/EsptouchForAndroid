package com.espressif.esptouch.android.v2;

import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.espressif.esptouch.android.EspTouchApp;
import com.espressif.esptouch.android.R;
import com.espressif.esptouch.android.databinding.ActivityProvisionBinding;
import com.espressif.iot.esptouch2.provision.EspProvisioner;
import com.espressif.iot.esptouch2.provision.EspProvisioningListener;
import com.espressif.iot.esptouch2.provision.EspProvisioningRequest;
import com.espressif.iot.esptouch2.provision.EspProvisioningResult;
import com.espressif.iot.esptouch2.provision.TouchNetUtil;

import java.util.ArrayList;
import java.util.List;

public class EspProvisioningActivity extends AppCompatActivity {
    private static final String TAG = EspProvisioningActivity.class.getSimpleName();

    public static final String KEY_PROVISION = "provision";
    public static final String KEY_PROVISION_REQUEST = "provision_request";

    private List<EspProvisioningResult> mStations;
    private StationAdapter mStationAdapter;

    private EspProvisioner mProvisioner;

    private WifiManager mWifiManager;

    private Observer<String> mBroadcastObserver;

    private ActivityProvisionBinding mBinding;

    private boolean mWifiFailed = false;

    private long mTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityProvisionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EspProvisioningRequest request = getIntent().getParcelableExtra(KEY_PROVISION_REQUEST);
        assert request != null;
        mProvisioner = new EspProvisioner(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        mStations = new ArrayList<>();
        mStationAdapter = new StationAdapter();
        recyclerView.setAdapter(mStationAdapter);

        mBinding.stopBtn.setOnClickListener(v -> {
            v.setEnabled(false);
            mProvisioner.stopProvisioning();
        });

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mBroadcastObserver = action -> {
            boolean connected = TouchNetUtil.isWifiConnected(mWifiManager);
            if (!connected && mProvisioner.isProvisioning()) {
                mWifiFailed = true;
                mBinding.messageView.setText(getString(R.string.esptouch2_provisioning_wifi_disconnect));
                mProvisioner.stopProvisioning();
            }
        };
        EspTouchApp.getInstance().observeBroadcastForever(mBroadcastObserver);

        mTime = System.currentTimeMillis();
        mProvisioner.startProvisioning(request, new ProvisionListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EspTouchApp.getInstance().removeBroadcastObserver(mBroadcastObserver);
        mProvisioner.stopProvisioning();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mProvisioner.stopProvisioning();
        mProvisioner.close();
    }

    private static class StationHolder extends RecyclerView.ViewHolder {
        TextView text1;
        TextView text2;

        StationHolder(@NonNull View itemView) {
            super(itemView);

            text1 = itemView.findViewById(android.R.id.text1);
            text1.setTextColor(Color.BLACK);
            text2 = itemView.findViewById(android.R.id.text2);
            text2.setTextColor(Color.BLACK);
        }
    }

    private class StationAdapter extends RecyclerView.Adapter<StationHolder> {

        @NonNull
        @Override
        public StationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getApplicationContext()).inflate(android.R.layout.simple_list_item_2,
                    parent, false);
            return new StationHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull StationHolder holder, int position) {
            EspProvisioningResult station = mStations.get(position);

            holder.text1.setText(getString(R.string.esptouch2_provisioning_result_bssid, station.bssid));
            holder.text2.setText(getString(R.string.esptouch2_provisioning_result_address,
                    station.address.getHostAddress()));
        }

        @Override
        public int getItemCount() {
            return mStations.size();
        }
    }


    private class ProvisionListener implements EspProvisioningListener {
        @Override
        public void onStart() {
            Log.d(TAG, "ProvisionListener onStart: ");
        }

        @Override
        public void onResponse(EspProvisioningResult result) {
            String mac = result.bssid;
            String host = result.address.getHostAddress();
            Log.d(TAG, "ProvisionListener onResponse: " + mac + " " + host);
            runOnUiThread(() -> {
                mStations.add(result);
                mStationAdapter.notifyItemInserted(mStations.size() - 1);
            });
        }

        @Override
        public void onStop() {
            Log.d(TAG, "ProvisionListener onStop: ");
            runOnUiThread(() -> {
                if (!mWifiFailed && mStations.isEmpty()) {
                    mBinding.messageView.setText(R.string.esptouch2_provisioning_result_none);
                }
                mBinding.stopBtn.setEnabled(false);
                mBinding.progressView.setVisibility(View.GONE);
            });
            mTime = System.currentTimeMillis() - mTime;
            Log.e(TAG, "Provisioning task cost " + mTime);
        }

        @Override
        public void onError(Exception e) {
            Log.w(TAG, "ProvisionListener onError: ", e);
            mProvisioner.stopProvisioning();
            runOnUiThread(() -> {
                String message = getString(R.string.esptouch2_provisioning_result_exception,
                        e.getLocalizedMessage());
                mBinding.messageView.setText(message);
            });
        }
    }
}
