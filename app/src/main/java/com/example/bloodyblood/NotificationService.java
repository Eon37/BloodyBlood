package com.example.bloodyblood;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class NotificationService {

    private NotificationService() {}

    public static void recalculateTimings(Context context, SharedPreferences sharedPreferences) {
        if (context == null) {
            Log.d("NotificationService", "Context null");
            return;
        }

        int startDay = Integer.parseInt(sharedPreferences.getString(StringConstants.START_DAY_KEY, "1"));
        int period = Integer.parseInt(sharedPreferences.getString(StringConstants.PERIOD_KEY, "31"));
        int duration = Integer.parseInt(sharedPreferences.getString(StringConstants.DURATION_KEY, "5"));

        setNotifications(context, calculateNext(LocalDate.now(), startDay, period), period, duration);
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

    private static void setNotifications(Context context, ZonedDateTime date, int period, int duration) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent startIntent = new Intent(context, NotificationDisplay.class);
//        Intent endIntent = new Intent("com.example.bloodyblood.action.END_NOTIFICATION");

        //set start notification
        am.setRepeating(AlarmManager.RTC,
                date.toInstant().toEpochMilli(),
                AlarmManager.INTERVAL_DAY * period,
                PendingIntent.getBroadcast(context, 100, startIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        //set end notification
//        am.setRepeating(AlarmManager.RTC,
//                date.plusDays(duration).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
//                TimeUnit.DAYS.toMillis(period),
//                PendingIntent.getBroadcast(context, 100, endIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
