package com.eon37_dev.bloodyblood.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.StringConstants;
import com.eon37_dev.bloodyblood.DateUtils;
import com.eon37_dev.bloodyblood.enums.NotificationIds;

import java.time.LocalDate;

public class ExactDaysInputReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);
        int period = Integer.parseInt(prefs.getString(StringConstants.PERIOD_KEY, "31"));
        int duration = Integer.parseInt(prefs.getString(StringConstants.DURATION_KEY, "5"));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        int exactDays = 0;
        if (remoteInput != null) {
            try {
                exactDays = Integer.parseInt(remoteInput.getString(StringConstants.INPUT_EXACT_DAYS));
                if (exactDays < 0 || exactDays > 31) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                NotificationChannel channel = new NotificationChannel(NotificationUtils.CHANNEL_ID, NotificationUtils.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(NotificationIds.EXACT_DAY_NOTIFICATION.ordinal(), NotificationUtils.constructExactDayNotification(context, isStart, false, 0));
                return;
            }
        }

        notificationManager.notify(NotificationIds.EXACT_DAY_NOTIFICATION.ordinal(), NotificationUtils.constructExactDayNotification(context, isStart, true, exactDays));

        DateUtils.saveHistory(prefs, LocalDate.now().minusDays(exactDays), isStart);

        if (isStart) {
            NotificationUtils.setMainNotification(
                    context,
                    true,
                    LocalDate.now().minusDays(exactDays).plusDays(period));
            NotificationUtils.setEndNotification(
                    context,
                    LocalDate.now().minusDays(exactDays).plusDays(duration));
        }
    }
}
