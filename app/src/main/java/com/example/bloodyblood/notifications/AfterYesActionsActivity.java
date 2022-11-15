package com.example.bloodyblood.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.bloodyblood.enums.NotificationIds;
import com.example.bloodyblood.enums.RequestCodes;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.ZoneId;

public class AfterYesActionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationService.cancelNotification(this, NotificationIds.MAIN_NOTIFICATION);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int duration = Integer.parseInt(prefs.getString(StringConstants.DURATION_KEY, "3"));
        boolean exactDayEnabled = prefs.getBoolean(StringConstants.EXACT_DAY_ENABLED, false);
        boolean endEnabled = prefs.getBoolean(StringConstants.END_NOTIFICATION_ENABLED, false);
        boolean isStart = getIntent().getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);

        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, !isStart).apply();

        //todo if isStart calculate next start day and save history.
        // After calculation it will reset notification in change preference listener
        // if !isStart save history
//        if (isStart) {
//            prefs.edit().putString(StringConstants.START_DAY_KEY, String.valueOf(LocalDate.now().getDayOfMonth())).commit();
//        }

        if (exactDayEnabled) {
            //todo howlong notification
        }

        if (isStart) {
            Intent endIntent = new Intent(this, endEnabled ? MainNotificationDisplay.class : SilentEndActionActivity.class);
            endIntent.putExtra(StringConstants.IS_START_NOTIFICATION, false);
            PendingIntent endPendingIntent = endEnabled
                    ? PendingIntent.getBroadcast(this, RequestCodes.END_NOTIFICATION.ordinal(), endIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    : PendingIntent.getActivity(this, RequestCodes.SILENT_END_ACTIONS.ordinal(), endIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC,
                    LocalDate.now().plusDays(duration).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    endPendingIntent);
        }

        finish();
    }
}
