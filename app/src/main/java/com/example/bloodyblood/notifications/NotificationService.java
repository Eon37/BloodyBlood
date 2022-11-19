package com.example.bloodyblood.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.enums.NotificationIds;
import com.example.bloodyblood.enums.RequestCodes;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class NotificationService {
    public static final String CHANNEL_ID = "com.example.bloodyblood.channelId";

    private NotificationService() {}

    public static void recalculateTimings(Context context, SharedPreferences sharedPreferences) {
        if (context == null) {
            Log.d("NotificationService", "Context null");
            return;
        }

        int startDay = Integer.parseInt(sharedPreferences.getString(StringConstants.START_DAY_KEY, "1"));
        int period = Integer.parseInt(sharedPreferences.getString(StringConstants.PERIOD_KEY, "31"));

        //todo clear?
        sharedPreferences.edit().putBoolean(StringConstants.IS_CALM_BG, true).commit();
        setMainNotification(context, true, calculateNext(LocalDate.now(), startDay, period));
    }

    private static ZonedDateTime calculateNext(LocalDate currentDate, int startDay, int period) {
        int year = currentDate.getYear();
        Month month = currentDate.getMonth();
        int day = startDay;

        if (currentDate.isAfter(LocalDate.of(currentDate.getYear(), currentDate.getMonth(), startDay))) {
            int lengthOfMonth = currentDate.lengthOfMonth();
            int tmpNext = startDay + period;
            boolean nextMonth = tmpNext > lengthOfMonth;
            int nextStartDay = nextMonth ? tmpNext % lengthOfMonth : tmpNext;

            year = nextMonth && Month.DECEMBER == currentDate.getMonth() ? currentDate.getYear() + 1 : year;
            month = nextMonth ? currentDate.getMonth().plus(1) : month;
            day = nextMonth ? nextStartDay : day;
        }

        return LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault());
    }

    public static void setMainNotification(Context context, boolean isStart, ZonedDateTime date) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent firstIntent = new Intent(context, MainNotificationDisplayReceiver.class);
        firstIntent.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);

        RequestCodes code = isStart ? RequestCodes.MAIN_NOTIFICATION : RequestCodes.END_NOTIFICATION;
        long nextAlarmTime = date.toInstant().toEpochMilli();

        am.set(AlarmManager.RTC,
                nextAlarmTime,
                PendingIntent.getBroadcast(context, code.ordinal(), firstIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putLong(code.name(), nextAlarmTime).apply();
    }

    public static void cancelNotification(Context context, NotificationIds id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id.ordinal());
    }
}
