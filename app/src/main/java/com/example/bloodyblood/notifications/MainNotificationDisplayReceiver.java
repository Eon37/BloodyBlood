package com.example.bloodyblood.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Icon;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.enums.NotificationIds;
import com.example.bloodyblood.R;
import com.example.bloodyblood.enums.RequestCodes;
import com.example.bloodyblood.StringConstants;

public class MainNotificationDisplayReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCalmBg = sharedPreferences.getBoolean(StringConstants.IS_CALM_BG, true);
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);

        Notification notification = NotificationUtils.constructMainNotification(context, isStart, isCalmBg);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NotificationUtils.CHANNEL_ID, NotificationUtils.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(NotificationIds.MAIN_NOTIFICATION.ordinal(), notification);
    }
}
