package com.example.bloodyblood.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.DateUtils;
import com.example.bloodyblood.enums.NotificationIds;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class AfterYesActionsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.cancelNotification(context, NotificationIds.MAIN_NOTIFICATION);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int duration = Integer.parseInt(prefs.getString(StringConstants.DURATION_KEY, "5"));
        int period = Integer.parseInt(prefs.getString(StringConstants.PERIOD_KEY, "31"));
        boolean exactDayEnabled = prefs.getBoolean(StringConstants.EXACT_DAY_ENABLED, false);
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);

        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, !isStart).apply();

        if (exactDayEnabled) {
            Notification notification = NotificationUtils.constructExactDayNotification(context, isStart);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NotificationUtils.CHANNEL_ID, NotificationUtils.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(NotificationIds.EXACT_DAY_NOTIFICATION.ordinal(), notification);
        } else {
            DateUtils.saveHistory(prefs, LocalDate.now(), isStart);

            if (isStart) {
                NotificationUtils.setMainNotification(
                        context,
                        true,
                        LocalDate.now().plusDays(period));
                NotificationUtils.setEndNotification(
                        context,
                        LocalDate.now().plusDays(duration));
            }
        }
    }
}
