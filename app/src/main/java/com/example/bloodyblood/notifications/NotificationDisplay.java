package com.example.bloodyblood.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bloodyblood.R;
import com.example.bloodyblood.StringConstants;

public class NotificationDisplay extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.example.bloodyblood.channelId";
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isStart = intent.getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);
        Class<? extends AppCompatActivity> clas = isStart
                ? StartNotificationActivity.class
                : EndNotificationActivity.class;
        Intent nextIntent = new Intent(context, clas);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context)
                .addParentStack(clas)
                .addNextIntent(nextIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(100, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context, "0")
                .setContentTitle("Titil")
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
