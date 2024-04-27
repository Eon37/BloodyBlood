package com.eon37_dev.bloodyblood.settings;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.eon37_dev.bloodyblood.R;
import com.eon37_dev.bloodyblood.StringConstants;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private TimePicker timePicker;

    public static TimePreferenceDialogFragmentCompat newInstance() {
        final TimePreferenceDialogFragmentCompat fragment = new TimePreferenceDialogFragmentCompat();

        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, StringConstants.NOTIFICATION_TIME);

        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

         timePicker = view.findViewById(R.id.timePicker);
        if (timePicker == null) {
            throw new IllegalStateException("TimePicker view not found");
        }

        Integer minutes = null;
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            minutes = ((TimePreference) preference).getTime();
        }

        if (minutes != null) {
            int hour = minutes / 60;
            int minute = minutes % 60;
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            timePicker.setIs24HourView(is24hour);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            int minutes = (hour * 60) + minute;

            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference = ((TimePreference) preference);
                if (timePreference.callChangeListener(minutes)) {
                    timePreference.setTime(minutes);
                }
            }
        }
    }
}
