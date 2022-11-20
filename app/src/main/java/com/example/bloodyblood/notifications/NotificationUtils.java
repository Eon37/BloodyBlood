package com.example.bloodyblood.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.bloodyblood.R;
import com.example.bloodyblood.enums.NotificationIds;
import com.example.bloodyblood.enums.RequestCodes;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class NotificationUtils {
    public static final String CHANNEL_ID = "com.example.bloodyblood.channelId";
    public static final String CHANNEL_NAME = "bloodyblood";

    private NotificationUtils() {}

    public static void recalculateTimings(Context context, SharedPreferences sharedPreferences) {
        if (context == null) {
            Log.d("NotificationService", "Context null");
            return;
        }

        int startDay = Integer.parseInt(sharedPreferences.getString(StringConstants.START_DAY_KEY, "1"));
        int period = Integer.parseInt(sharedPreferences.getString(StringConstants.PERIOD_KEY, "31"));

        setMainNotification(context, true, calculateNext(LocalDate.now(), startDay, period));
    }

    /**
     * Calculates the next day to send first notification
     * If start day is today then the next day is also today
     * If start day is in the past days of the current month, then the next day is after the period
     * If start day is later in the current month, then the next day is at that date
     * @param currentDate - today's date
     * @param startDay - the day of the current month when already started or will start
     * @param period - the period for calculating if the next day is in the current month/year
     * @return the day to send notification
     */
    public static ZonedDateTime calculateNext(LocalDate currentDate, int startDay, int period) {
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

    public static void setEndNotification(Context context, ZonedDateTime date) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean endEnabled = prefs.getBoolean(StringConstants.END_NOTIFICATION_ENABLED, false);
        RequestCodes endCode = endEnabled ? RequestCodes.END_NOTIFICATION : RequestCodes.SILENT_END_ACTIONS;

        Intent endIntent = new Intent(context, endEnabled ? MainNotificationDisplayReceiver.class : SilentEndActionReceiver.class);
        endIntent.putExtra(StringConstants.IS_START_NOTIFICATION, false);
        PendingIntent endPendingIntent = PendingIntent.getBroadcast(
                context,
                endCode.ordinal(),
                endIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long nextAlarmTime = date.toInstant().toEpochMilli();

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, nextAlarmTime, endPendingIntent);

        prefs.edit().putLong(endCode.name(), nextAlarmTime).apply();

    }

    public static Notification constructMainNotification(Context context, boolean isStart, boolean isCalmBg) {
        Intent afterStart = new Intent(context, AfterYesActionsReceiver.class);
        afterStart.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);
        PendingIntent afterStartPendingIntent = PendingIntent.getBroadcast(
                context,
                RequestCodes.YES_ACTION.ordinal(),
                afterStart,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action yesAction = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                "Yes",
                afterStartPendingIntent)
                .build();

        Intent repeatIntent = new Intent(context, RepeatNotificationScheduleReceiver.class);
        repeatIntent.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);
        PendingIntent repeatPendingIntent = PendingIntent.getBroadcast(
                context,
                RequestCodes.NO_ACTION.ordinal(),
                repeatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action noAction = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                "No",
                repeatPendingIntent)
                .build();

        return new Notification.Builder(context, NotificationUtils.CHANNEL_ID)
                .setContentTitle(isStart ? "Start titile" : "End titile")
                .setContentText(isStart ? "Start sext" : "End sext")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.blood_icon)
                .addAction(yesAction)
                .addAction(noAction)
                .setOngoing(true)
                .setColor(isCalmBg ? Color.BLACK : Color.RED)
                .setColorized(true)
                .build();
    }

    public static Notification constructExactDayNotification(Context context, boolean isStart) {
        Intent remoteInputIntent = new Intent(context, ExactDaysInputReceiver.class);
        remoteInputIntent.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);
        PendingIntent remoteInputPendingIntent = PendingIntent.getBroadcast(
                context,
                RequestCodes.SPECIFY_EXACT_DAY.ordinal(),
                remoteInputIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action inputAction = new Notification.Action.Builder(
                Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                "Set days (Numbers from 0 to 31 only)",
                remoteInputPendingIntent)
                .addRemoteInput(new RemoteInput.Builder(StringConstants.INPUT_EXACT_DAYS)
                        .setLabel("Days since the " + (isStart ? "start" : "end"))
                        .build())
                .build();

        return new Notification.Builder(context, NotificationUtils.CHANNEL_ID)
                .setContentTitle("Exact days")
                .setContentText(isStart
                        ? "How long have you been bleeding already?"
                        : "How long have you stopped bleeding already?")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.blood_icon)
                .addAction(inputAction)
                .setOngoing(true)
                .setColor(isStart ? Color.RED : Color.BLACK)
                .setColorized(true)
                .build();
    }

    public static void cancelNotification(Context context, NotificationIds id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id.ordinal());
    }
}
