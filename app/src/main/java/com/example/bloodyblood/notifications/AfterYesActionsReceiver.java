package com.example.bloodyblood.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.enums.NotificationIds;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.ZoneId;

public class AfterYesActionsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.cancelNotification(context, NotificationIds.MAIN_NOTIFICATION);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int duration = Integer.parseInt(prefs.getString(StringConstants.DURATION_KEY, "3"));
        int period = Integer.parseInt(prefs.getString(StringConstants.PERIOD_KEY, "31"));
        boolean exactDayEnabled = prefs.getBoolean(StringConstants.EXACT_DAY_ENABLED, false);
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);

        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, !isStart).apply();

        if (exactDayEnabled) {
            //todo howlong notification
        } else {
            if (isStart) {
                NotificationUtils.setMainNotification(
                        context,
                        true,
                        LocalDate.now().plusDays(period).atStartOfDay(ZoneId.systemDefault()));
                NotificationUtils.setEndNotification(
                        context,
                        LocalDate.now().plusDays(duration).atStartOfDay(ZoneId.systemDefault()));
                //todo save start history
            } else {
                //todo set end history
            }
        }
    }
}
