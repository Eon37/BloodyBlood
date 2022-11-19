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
import java.time.ZonedDateTime;

public class RepeatNotificationScheduleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtils.cancelNotification(context, NotificationIds.MAIN_NOTIFICATION);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);
        int repeatAfter = Integer.parseInt(prefs.getString(StringConstants.REPEAT_NOTIFICATION_AFTER, "5"));
        ZonedDateTime repeatDate = LocalDate.now().plusDays(repeatAfter).atStartOfDay(ZoneId.systemDefault());

        NotificationUtils.setMainNotification(context, isStart, repeatDate);
    }
}
