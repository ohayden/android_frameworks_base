/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.android.internal.R;

// Added WifiInfo and WifiManager for SSID 
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


/**
 * This widget displays the currently connected Wifi SSID name.
 */
public class WifiLabel extends TextView {
    private boolean mAttached;

    String wifiSSID = "";

    public WifiLabel(Context context) {
        this(context, null);
    }

    public WifiLabel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WifiLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        updateWifiSSID(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION );
            filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION );
            filter.addAction(WifiManager.RSSI_CHANGED_ACTION);

            getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) || action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
                updateWifiSSID(context);
            }
        }
    };

    /* updateWifiSSID keeps track of changes made to the SSID and refreshes the text displayed  */
    void updateWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        wifiSSID = wifiInfo.getSSID();
        if(wifiSSID == null) {
            wifiSSID = "";
        }

        setText(wifiSSID);
    }
}

