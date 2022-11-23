package com.eon37_dev.bloodyblood;

import android.content.SharedPreferences;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class DateUtils {

    public static long millisFromDate(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static void saveHistory(SharedPreferences prefs, LocalDate dateToSave, boolean isStart) {
        String setKey = isStart ? StringConstants.STARTS_SET : StringConstants.ENDS_SET;

        Set<String> set = new HashSet<>(prefs.getStringSet(setKey, new HashSet<>()));
        set.add(dateToSave.toString());

        prefs.edit().putStringSet(setKey, set).apply();
    }
}
