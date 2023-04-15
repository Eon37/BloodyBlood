package com.eon37_dev.bloodyblood.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.CalendarHistoryDecorator;
import com.eon37_dev.bloodyblood.DateUtils;
import com.eon37_dev.bloodyblood.R;
import com.eon37_dev.bloodyblood.StringConstants;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class OnDateSelectedListenerImpl implements OnDateSelectedListener {
    private final Context context;

    public OnDateSelectedListenerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean editModeEnabled = prefs.getBoolean(StringConstants.IS_EDIT_MODE_ENABLED, false);

        if (!selected) return;
        if (!editModeEnabled) return;
        if (date.isAfter(CalendarDay.from(LocalDate.now()))) return;

        Set<String> starts = new HashSet<>(prefs.getStringSet(StringConstants.STARTS_SET, new TreeSet<>()));
        Set<String> ends = new HashSet<>(prefs.getStringSet(StringConstants.ENDS_SET, new TreeSet<>()));

        if (starts.contains(date.getDate().toString())) {
            //First day of the period tapped - remove
            starts.remove(date.getDate().toString());
            starts.add(date.getDate().plusDays(1).toString());
        } else if (ends.contains(date.getDate().toString())) {
            //Last day of the period tapped - remove
            ends.remove(date.getDate().toString());
            ends.add(date.getDate().minusDays(1).toString());
        } else if (starts.contains(date.getDate().plusDays(1).toString())) {
            //Pre first day of the period tapped - add
            starts.remove(date.getDate().plusDays(1).toString());
            starts.add(date.getDate().toString());
        } else if (ends.contains(date.getDate().minusDays(1).toString())) {
            //Next to last day of the period tapped - add
            starts.remove(date.getDate().minusDays(1).toString());
            starts.add(date.getDate().toString());
        } else {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setTitle("You cannot add or remove this day")
                    .setMessage("To remove a day from a period tap the first or the last day of the period\n\n" +
                                "To add a day to a period tap the previous to the first or the next to the last day of a period\n\n" +
                                "You cannot add or remove day in the end of the current period")
                    .create();
            dialog.show();
            return;
        }

        prefs.edit().putStringSet(StringConstants.STARTS_SET, new TreeSet<>(starts)).apply();
        prefs.edit().putStringSet(StringConstants.ENDS_SET, new TreeSet<>(ends)).apply();

        widget.removeDecorators();
        widget.addDecorator(new CalendarHistoryDecorator(context, DateUtils.extractHistoryDays(starts, ends)));
        widget.clearSelection();
    }
}