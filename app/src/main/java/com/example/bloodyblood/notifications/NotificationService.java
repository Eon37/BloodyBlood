package com.example.bloodyblood.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.util.Log;

import com.example.bloodyblood.R;
import com.example.bloodyblood.RequestCodes;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        Intent firstIntent = new Intent(context, MainNotificationDisplay.class);
        firstIntent.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);

        //set next start notification
        am.set(AlarmManager.RTC,
                date.toInstant().toEpochMilli(),
                PendingIntent.getBroadcast(context,
                        RequestCodes.MAIN_NOTIFICATION.ordinal(),
                        firstIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
