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

public class MainNotificationDisplay extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCalmBg = sharedPreferences.getBoolean(StringConstants.IS_CALM_BG, true);
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);

        Notification notification = constructMainNotification(context, isStart, isCalmBg);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NotificationService.CHANNEL_ID, "bloodyblood", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(NotificationIds.MAIN_NOTIFICATION.ordinal(), notification);
    }

    private Notification constructMainNotification(Context context, boolean isStart, boolean isCalmBg) {
        Intent afterStart = new Intent(context, AfterYesActionsActivity.class);
        afterStart.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);
        PendingIntent afterStartPendingIntent = PendingIntent.getActivity(
                context,
                RequestCodes.YES_ACTION.ordinal(),
                afterStart,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action yesAction = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                "Yes",
                afterStartPendingIntent)
                .build();

        Intent repeatIntent = new Intent(context, RepeatNotificationSchedule.class);
        repeatIntent.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);
        PendingIntent repeatPendingIntent = PendingIntent.getBroadcast( //todo mb activity (invisible?) also?
                context,
                RequestCodes.NO_ACTION.ordinal(),
                repeatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action noAction = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                "No",
                repeatPendingIntent)
                .build();

        return new Notification.Builder(context, NotificationService.CHANNEL_ID)
                .setContentTitle(isStart ? "Start titile" : "End titile")
                .setContentText(isStart ? "Start sext" : "End sext")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.blood_icon)
                .addAction(yesAction)
                .addAction(noAction)
                .setColor(isCalmBg ? Color.BLACK : Color.RED)
                .setColorized(true)
                .build();
    }
}
