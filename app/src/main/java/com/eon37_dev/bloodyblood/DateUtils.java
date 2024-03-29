package com.eon37_dev.bloodyblood;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.ZoneOffset;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DateUtils {

    public static long millisFromDate(LocalDate date, int hour, int minute) {
        return date.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static void saveHistory(SharedPreferences prefs, LocalDate dateToSave, boolean isStart) {
        String setKey = isStart ? StringConstants.STARTS_SET : StringConstants.ENDS_SET;

        Set<String> set = new TreeSet<>(prefs.getStringSet(setKey, new TreeSet<>()));
        set.add(dateToSave.toString());

        prefs.edit().putStringSet(setKey, set).apply();
    }

    public static Collection<CalendarDay> extractHistoryDays(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        TreeSet<String> starts = new TreeSet<>(prefs.getStringSet(StringConstants.STARTS_SET, new TreeSet<>()));
        TreeSet<String> ends = new TreeSet<>(prefs.getStringSet(StringConstants.ENDS_SET, new TreeSet<>()));

        return extractHistoryDays(starts, ends);
    }

    public static Collection<CalendarDay> extractHistoryDays(TreeSet<String> startsTree, TreeSet<String> endsTree) {
        Iterator<String> startsIterator = startsTree.iterator();
        Iterator<String> endsIterator = endsTree.iterator();
        Collection<CalendarDay> extracted = new HashSet<>();

        while (startsIterator.hasNext()) {
            org.threeten.bp.LocalDate start = org.threeten.bp.LocalDate.parse(startsIterator.next());
            org.threeten.bp.LocalDate end = endsIterator.hasNext()
                    ? org.threeten.bp.LocalDate.parse(endsIterator.next())
                    : org.threeten.bp.LocalDate.now();

            for (org.threeten.bp.LocalDate ld = start; !ld.isAfter(end); ld = ld.plusDays(1)) {
                extracted.add(CalendarDay.from(ld));
            }
        }

        return extracted;
    }
}
