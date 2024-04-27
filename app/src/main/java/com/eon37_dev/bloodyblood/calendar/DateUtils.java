package com.eon37_dev.bloodyblood.calendar;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.StringConstants;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DateUtils {

    public static long millisFromDate(LocalDate date, int hour, int minute) {
        return date.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static void saveHistory(SharedPreferences prefs, LocalDate dateToSave, boolean isStart) {
        TreeSet<String> set = new TreeSet<>(prefs.getStringSet(StringConstants.HISTORY_SET, new TreeSet<>()));

        if (isStart) {
            set.add(dateToSave.toString());
        } else {
            for (LocalDate i = LocalDate.parse(set.last()); !i.isAfter(dateToSave); i = i.plusDays(1)) {
                set.add(i.toString());
            }
        }

        prefs.edit().putStringSet(StringConstants.HISTORY_SET, set).apply();
    }

    public static Collection<CalendarDay> extractHistoryDays(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        TreeSet<String> set = new TreeSet<>(prefs.getStringSet(StringConstants.HISTORY_SET, new TreeSet<>()));

        return extractHistoryDays(prefs, set);
    }

    public static Collection<CalendarDay> extractHistoryDays(SharedPreferences prefs, TreeSet<String> history) {
        if (history.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<CalendarDay> extracted = new HashSet<>();
        history.stream()
                .map(org.threeten.bp.LocalDate::parse)
                .map(CalendarDay::from)
                .collect(Collectors.toCollection(() -> extracted));

        boolean isCalmBg = prefs.getBoolean(StringConstants.IS_CALM_BG, true);
        if (!isCalmBg) {
            for (LocalDate i = LocalDate.parse(history.last()); !i.isAfter(LocalDate.now()); i = i.plusDays(1)) {
                history.add(i.toString());
                extracted.add(CalendarDay.from(org.threeten.bp.LocalDate.parse(i.toString())));
            }

            prefs.edit().putStringSet(StringConstants.HISTORY_SET, history).apply();
        }

        return extracted;
    }
}
