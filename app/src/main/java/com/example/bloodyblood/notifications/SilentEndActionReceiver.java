package com.example.bloodyblood.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.DateUtils;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SilentEndActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, true).apply();

        DateUtils.saveHistory(prefs, LocalDate.now(), false);
    }
}
