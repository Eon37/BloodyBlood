package com.example.bloodyblood.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.ZoneId;

public class RepeatNotificationSchedule extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);
        int repeatAfter = Integer.parseInt(prefs.getString(StringConstants.REPEAT_NOTIFICATION_AFTER, "5"));
        if (isStart) {
            int delay = prefs.getInt(StringConstants.DELAY, 0); //todo reset on start yes

            delay += repeatAfter;
            prefs.edit().putInt(StringConstants.DELAY, delay).apply();
        }

        NotificationService.setMainNotification(
                context,
                LocalDate.now().plusDays(repeatAfter).atStartOfDay(ZoneId.systemDefault()));
    }
}
