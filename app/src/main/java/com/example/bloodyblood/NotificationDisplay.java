package com.example.bloodyblood;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDisplay extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.example.bloodyblood.channelId";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent nextIntent = new Intent(context, StartNotificationActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context)
                .addParentStack(StartNotificationActivity.class)
                .addNextIntent(nextIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context, "0")
                .setContentTitle("Titi")
                .setContentText("Sext")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "bloodyblood", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(0, notification);
    }
}
