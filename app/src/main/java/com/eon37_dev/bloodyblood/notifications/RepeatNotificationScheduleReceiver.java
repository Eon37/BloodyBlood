package com.eon37_dev.bloodyblood.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.StringConstants;
import com.eon37_dev.bloodyblood.enums.NotificationIds;

import java.time.LocalDate;

public class RepeatNotificationScheduleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.cancelNotification(context, NotificationIds.YES_NO_NOTIFICATION);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);
        int repeatAfter = Integer.parseInt(prefs.getString(StringConstants.REPEAT_NOTIFICATION_AFTER, "3"));
        LocalDate repeatDate = LocalDate.now().plusDays(repeatAfter);

        if (isStart) {
            NotificationUtils.setStartNotification(context, repeatDate);
        } else {
            NotificationUtils.setEndNotification(context, repeatDate);
        }
    }
}
