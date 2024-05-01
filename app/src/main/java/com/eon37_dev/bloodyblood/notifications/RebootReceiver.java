package com.eon37_dev.bloodyblood.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.StringConstants;
import com.eon37_dev.bloodyblood.enums.RequestCodes;

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        resetStartNotification(context);
        resetEndNotification(context);
    }

    private void resetStartNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Intent firstIntent = new Intent(context, YesNoNotificationDisplayReceiver.class);
        firstIntent.putExtra(StringConstants.IS_START_NOTIFICATION, true);

        resetNotification(context, prefs, firstIntent, RequestCodes.START_NOTIFICATION);
    }

    private void resetEndNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean endEnabled = prefs.getBoolean(StringConstants.END_NOTIFICATION_ENABLED, false);

        Intent endIntent = new Intent(context, endEnabled ? YesNoNotificationDisplayReceiver.class : SilentEndActionReceiver.class);
        endIntent.putExtra(StringConstants.IS_START_NOTIFICATION, false);

        resetNotification(context, prefs, endIntent, RequestCodes.END_NOTIFICATION);
    }

    private void resetNotification(Context context, SharedPreferences prefs, Intent intent, RequestCodes requestCode) {
        long notificationTime = prefs.getLong(requestCode.name(), -1);

        if (notificationTime >= 0) {
            PendingIntent endPendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode.ordinal(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC, notificationTime, endPendingIntent);
        }
    }
}
