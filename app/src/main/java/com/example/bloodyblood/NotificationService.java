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
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationService {

    private static final String START_DAY_KEY = "startDay";
    private static final String PERIOD_KEY = "period";
    private static final String DURATION_KEY = "duration";
    private static final String NEXT_START_DAY = "duration";

    private NotificationService() {}

    public static void recalculateTimings(Context context, SharedPreferences sharedPreferences) {
        if (context == null) {
            Log.d("NotificationService", "Context null");
            return;
        }

        int startDay = Integer.parseInt(sharedPreferences.getString(START_DAY_KEY, "1"));
        int period = Integer.parseInt(sharedPreferences.getString(PERIOD_KEY, "31"));
        int duration = Integer.parseInt(sharedPreferences.getString(DURATION_KEY, "5"));

        LocalDate currentDate = LocalDate.now();

        if (currentDate.isAfter(LocalDate.of(currentDate.getYear(), currentDate.getMonth(), startDay))) {
            int lengthOfMonth = currentDate.lengthOfMonth();
            int tmpNext = startDay + period;
            boolean nextMonth = tmpNext > lengthOfMonth;
            int nextStartDay = nextMonth ? tmpNext % lengthOfMonth : tmpNext;

            if (nextMonth) {
                if (Month.DECEMBER == currentDate.getMonth()) {
                    setNextNotification(context, LocalDate.of(currentDate.getYear() + 1, Month.JANUARY, nextStartDay), period, duration);
                } else {
                    setNextNotification(context, LocalDate.of(currentDate.getYear(), currentDate.getMonth().plus(1), nextStartDay), period, duration);
                }
            } else {
                setNextNotification(context, LocalDate.of(currentDate.getYear(), currentDate.getMonth(), nextStartDay), period, duration);
            }
        } else {
            setNextNotification(context, LocalDate.of(currentDate.getYear(), currentDate.getMonth(), startDay), period, duration);
        }
    }

    private static void setNextNotification(Context context, LocalDate date, int period, int duration) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent startIntent = new Intent(context, NotificationDisplay.class);
//        Intent endIntent = new Intent("com.example.bloodyblood.action.END_NOTIFICATION");

        //set start notification
        am.setRepeating(AlarmManager.RTC,
                date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                AlarmManager.INTERVAL_DAY * period,
                PendingIntent.getBroadcast(context, 100, startIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        //set end notification
//        am.setRepeating(AlarmManager.RTC,
//                date.plusDays(duration).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
//                TimeUnit.DAYS.toMillis(period),
//                PendingIntent.getBroadcast(context, 100, endIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
