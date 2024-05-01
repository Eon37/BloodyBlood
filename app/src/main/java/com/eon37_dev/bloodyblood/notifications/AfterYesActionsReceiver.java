package com.eon37_dev.bloodyblood.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.calendar.DateUtils;
import com.eon37_dev.bloodyblood.StringConstants;
import com.eon37_dev.bloodyblood.enums.NotificationIds;

import java.time.LocalDate;

public class AfterYesActionsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.cancelNotification(context, NotificationIds.YES_NO_NOTIFICATION);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean exactDayEnabled = prefs.getBoolean(StringConstants.EXACT_DAY_ENABLED, false);
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);

        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, !isStart).apply();

        if (exactDayEnabled) {
            Notification notification = NotificationUtils.constructExactDayNotification(context, isStart, false, 0);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NotificationUtils.CHANNEL_ID, NotificationUtils.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(NotificationIds.EXACT_DAY_NOTIFICATION.ordinal(), notification);
        } else {
            DateUtils.saveHistory(prefs, LocalDate.now(), isStart);
            NotificationUtils.resetNotifications(context, prefs, isStart);
        }
    }
}
