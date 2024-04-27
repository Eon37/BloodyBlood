package com.eon37_dev.bloodyblood.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.AlertDialog;
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

import com.eon37_dev.bloodyblood.R;
import com.eon37_dev.bloodyblood.StringConstants;
import com.eon37_dev.bloodyblood.enums.RequestCodes;
import com.eon37_dev.bloodyblood.calendar.DateUtils;
import com.eon37_dev.bloodyblood.enums.NotificationIds;

import java.time.LocalDate;
import java.time.Month;

public class NotificationUtils {
    public static final String CHANNEL_ID = "com.eon37_dev.bloodyblood.channelId";
    public static final String CHANNEL_NAME = "bloodyblood";

    private NotificationUtils() {
    }

    public static void recalculateTimings(Context context, SharedPreferences sharedPreferences) {
        if (context == null) {
            Log.d("NotificationService", "Context null");
            return;
        }

        int startDay = Integer.parseInt(sharedPreferences.getString(StringConstants.START_DAY_KEY, "-1"));
        int period = Integer.parseInt(sharedPreferences.getString(StringConstants.PERIOD_KEY, "31"));

        if (startDay < 1) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setTitle("Set the period start day")
                    .setMessage("The start day is not set at the moment\n" +
                            "To start using the app specify the approximate start day in settings")
                    .create();
            dialog.show();
            return;
        }

        setStartNotification(context, calculateNext(LocalDate.now(), startDay, period));
    }

    /**
     * Calculates the next day to send first notification
     * If start day is today then the next day is also today
     * If start day is in the past days of the current month, then the next day is after the period
     * If start day is later in the current month, then the next day is at that date
     *
     * @param currentDate - today's date
     * @param startDay    - the day of the current month when already started or will start
     * @param period      - the period for calculating if the next day is in the current month/year
     * @return the day to send notification
     */
    public static LocalDate calculateNext(LocalDate currentDate, int startDay, int period) {
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

        return LocalDate.of(year, month, day);
    }

    public static void setStartNotification(Context context, LocalDate date) {
        Intent firstIntent = new Intent(context, YesNoNotificationDisplayReceiver.class);
        firstIntent.putExtra(StringConstants.IS_START_NOTIFICATION, true);

        setNotification(context, date, RequestCodes.START_NOTIFICATION, firstIntent);
    }

    public static void setEndNotification(Context context, LocalDate date) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean endEnabled = prefs.getBoolean(StringConstants.END_NOTIFICATION_ENABLED, false);

        Intent endIntent = new Intent(context, endEnabled ? YesNoNotificationDisplayReceiver.class : SilentEndActionReceiver.class);
        endIntent.putExtra(StringConstants.IS_START_NOTIFICATION, false);

        setNotification(context, date, RequestCodes.END_NOTIFICATION, endIntent);
    }

    public static void setNotification(Context context, LocalDate date, RequestCodes requestCode, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int notificationTime = prefs.getInt(StringConstants.NOTIFICATION_TIME, 720);
        int hour = notificationTime / 60;
        int minute = notificationTime % 60;

        long nextAlarmTime = DateUtils.millisFromDate(date, hour, minute);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC,
                nextAlarmTime,
                PendingIntent.getBroadcast(context, requestCode.ordinal(), intent, PendingIntent.FLAG_UPDATE_CURRENT));

        prefs.edit().putLong(requestCode.name(), nextAlarmTime).apply();
    }

    public static Notification constructMainNotification(Context context, boolean isStart, boolean isCalmBg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
                .setContentTitle(isStart
                                 ? prefs.getString(StringConstants.START_TITLE, "Well, well, well")
                                 : prefs.getString(StringConstants.END_TITLE, "Well, well, well"))
                .setContentText(isStart
                                ? prefs.getString(StringConstants.START_TEXT, "Are you bleeding already?")
                                : prefs.getString(StringConstants.END_TEXT, "Have you stopped bleeding?"))
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.blood_icon)
                .addAction(yesAction)
                .addAction(noAction)
                .setOngoing(true)
                .setColor(isCalmBg ? Color.BLACK : Color.RED)
                .setColorized(true)
                .build();
    }

    public static Notification constructExactDayNotification(Context context, boolean isStart, boolean isCancel, int input) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Notification.Builder builder = new Notification.Builder(context, NotificationUtils.CHANNEL_ID)
                .setContentTitle(prefs.getString(StringConstants.EXACT_TITLE, "At which day exactly?"))
                .setAutoCancel(isCancel)
                .setSmallIcon(R.drawable.blood_icon)
                .setOngoing(!isCancel)
                .setColor(isStart ? Color.RED : Color.BLACK)
                .setColorized(true);

        if (!isCancel) {
            builder.setContentText(prefs.getString(StringConstants.EXACT_TEXT, "Set the day of month"));

            Intent remoteInputIntent = new Intent(context, ExactDayInputReceiver.class);
            remoteInputIntent.putExtra(StringConstants.IS_START_NOTIFICATION, isStart);
            PendingIntent remoteInputPendingIntent = PendingIntent.getBroadcast(
                    context,
                    RequestCodes.SPECIFY_EXACT_DAY.ordinal(),
                    remoteInputIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Action inputAction = new Notification.Action.Builder(
                    Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
                    "Set the day of month",
                    remoteInputPendingIntent)
                    .addRemoteInput(new RemoteInput.Builder(StringConstants.INPUT_EXACT_DAYS)
                            .setLabel("The day of " + (isStart ? "start" : "end"))
                            .build())
                    .build();

            builder.addAction(inputAction);
        } else {
            builder.setContentText("Exact day's set to " + input);
        }

        return builder.build();
    }

    public static void cancelNotification(Context context, NotificationIds id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id.ordinal());
    }
}
