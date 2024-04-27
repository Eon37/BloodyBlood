package com.eon37_dev.bloodyblood.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.R;
import com.eon37_dev.bloodyblood.StringConstants;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;

import java.util.HashSet;
import java.util.Set;

public class OnDateSelectedListenerImpl implements OnDateSelectedListener {
    private final Context context;

    public OnDateSelectedListenerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean editModeEnabled = prefs.getBoolean(StringConstants.IS_EDIT_MODE_ENABLED, false);

        if (!editModeEnabled) return;

        if (date.getDate().isAfter(LocalDate.now())) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setTitle("You cannot add or remove this day")
                    .setMessage("You cannot add or remove the day after the current date")
                    .create();
            dialog.show();
            return;
        }

        Set<String> history = new HashSet<>(prefs.getStringSet(StringConstants.HISTORY_SET, new HashSet<>()));
        if (history.contains(date.getDate().toString())) {
            history.remove(date.getDate().toString());
        } else {
            history.add(date.getDate().toString());
        }
        prefs.edit().putStringSet(StringConstants.HISTORY_SET, history).apply();

        widget.removeDecorators();
        widget.addDecorator(new CalendarHistoryDecorator(context, DateUtils.extractHistoryDays(context)));
        widget.clearSelection();
    }
}