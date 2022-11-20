package com.example.bloodyblood.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.StringConstants;
import com.example.bloodyblood.enums.NotificationIds;

import java.time.LocalDate;
import java.time.ZoneId;

public class ExactDaysInputReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);
        int period = Integer.parseInt(prefs.getString(StringConstants.PERIOD_KEY, "31"));
        int duration = Integer.parseInt(prefs.getString(StringConstants.DURATION_KEY, "5"));

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        int exactDays = 0;
        if (remoteInput != null) {
            try {
                exactDays = Integer.parseInt(remoteInput.getString(StringConstants.INPUT_EXACT_DAYS));
                if (exactDays < 0 || exactDays > 31) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel(NotificationUtils.CHANNEL_ID, NotificationUtils.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(NotificationIds.EXACT_DAY_NOTIFICATION.ordinal(), NotificationUtils.constructExactDayNotification(context, isStart));
                return;
            }
        }

        NotificationUtils.cancelNotification(context, NotificationIds.EXACT_DAY_NOTIFICATION);

        if (isStart) {
            NotificationUtils.setMainNotification(
                    context,
                    true,
                    LocalDate.now().minusDays(exactDays).plusDays(period).atStartOfDay(ZoneId.systemDefault()));
            NotificationUtils.setEndNotification(
                    context,
                    LocalDate.now().minusDays(exactDays).plusDays(duration).atStartOfDay(ZoneId.systemDefault()));

            //todo save history
        } else {
            //todo save history
        }
    }
}
