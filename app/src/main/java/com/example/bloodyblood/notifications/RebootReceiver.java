package com.example.bloodyblood.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.bloodyblood.StringConstants;
import com.example.bloodyblood.enums.RequestCodes;

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //reset start notification
        long mainNotificationTime = prefs.getLong(RequestCodes.MAIN_NOTIFICATION.name(), 0);

        if (mainNotificationTime > 0) {
            Intent firstIntent = new Intent(context, MainNotificationDisplayReceiver.class);
            firstIntent.putExtra(StringConstants.IS_START_NOTIFICATION, true);
            PendingIntent mainPendingIntent = PendingIntent.getBroadcast(context, RequestCodes.MAIN_NOTIFICATION.ordinal(), firstIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC, mainNotificationTime, mainPendingIntent);
        }

        //reset end notifications
        boolean endEnabled = prefs.getBoolean(StringConstants.END_NOTIFICATION_ENABLED, false);

        RequestCodes endCode = endEnabled ? RequestCodes.END_NOTIFICATION : RequestCodes.SILENT_END_ACTIONS;
        long endNotificationTime = prefs.getLong(endCode.name(), 0);

        if (endNotificationTime > 0) {
            Intent endIntent = new Intent(context, endEnabled ? MainNotificationDisplayReceiver.class : SilentEndActionReceiver.class);
            endIntent.putExtra(StringConstants.IS_START_NOTIFICATION, false);
            PendingIntent endPendingIntent = PendingIntent.getBroadcast(
                    context,
                    endCode.ordinal(),
                    endIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            am.set(AlarmManager.RTC, endNotificationTime, endPendingIntent);
        }
    }
}
