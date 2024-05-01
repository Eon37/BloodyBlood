package com.eon37_dev.bloodyblood.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.eon37_dev.bloodyblood.StringConstants;
import com.eon37_dev.bloodyblood.calendar.DateUtils;
import com.eon37_dev.bloodyblood.enums.RequestCodes;

import java.time.LocalDate;

public class SilentEndActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, true).apply();

        DateUtils.saveHistory(prefs, LocalDate.now(), false);

        //remove end time in the end of period as it already occurred and thus to not recreate notification on app reinstall or phone restarts
        prefs.edit().remove(RequestCodes.END_NOTIFICATION.name()).apply();
    }
}
