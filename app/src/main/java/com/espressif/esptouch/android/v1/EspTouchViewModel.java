package com.espressif.esptouch.android.v1;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

class EspTouchViewModel {
    TextView apSsidTV;
    TextView apBssidTV;
    EditText apPasswordEdit;
    EditText deviceCountEdit;
    RadioGroup packageModeGroup;
    TextView messageView;
    Button confirmBtn;

    String ssid;
    byte[] ssidBytes;
    String bssid;

    CharSequence message;

    boolean confirmEnable;

    void invalidateAll() {
        apSsidTV.setText(ssid);
        apBssidTV.setText(bssid);
        messageView.setText(message);
        confirmBtn.setEnabled(confirmEnable);
    }
}
