package com.example.bloodyblood.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.example.bloodyblood.R;
import com.example.bloodyblood.RequestCodes;
import com.example.bloodyblood.StringConstants;

import java.time.LocalDate;
import java.time.ZoneId;

public class AfterYesActionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int duration = Integer.parseInt(prefs.getString(StringConstants.DURATION_KEY, "3"));
        boolean exactDayEnabled = Boolean.parseBoolean(prefs.getString(StringConstants.EXACT_DAY_ENABLED, "false"));
        boolean endEnabled = Boolean.parseBoolean(prefs.getString(StringConstants.END_NOTIFICATION_ENABLED, "false"));
        boolean isStart = getIntent().getBooleanExtra(StringConstants.IS_START_NOTIFICATION, true);

        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, !isStart).apply();

        LinearLayout mainLayout = findViewById(R.id.activity_main_id);
        mainLayout.setBackground(ResourcesCompat.getDrawable(getResources(),
                isStart
                        ? R.drawable.background_black_red_gradient
                        : R.drawable.background_black_white_gradient,
                null));

        TextView mainTextView = findViewById(R.id.activity_main_text_view_id);
        mainTextView.setBackground(ResourcesCompat.getDrawable(getResources(),
                isStart ? R.drawable.black_red_gradient : R.drawable.black_white_gradient,
                null));

        mainTextView.setTextColor(isStart ? Color.RED : Color.WHITE);

        if (isStart) {
            prefs.edit().putString(StringConstants.START_DAY_KEY, String.valueOf(LocalDate.now().getDayOfMonth())).commit();
        }

        if (exactDayEnabled) {
            //todo howlong notification
        }

        Intent endIntent = new Intent(this, endEnabled ? MainNotificationDisplay.class : SilentEndNotificationActions.class);
        endIntent.putExtra(StringConstants.IS_START_NOTIFICATION, false);
        PendingIntent endPendingIntent = endEnabled
                ? PendingIntent.getBroadcast(this, RequestCodes.END_NOTIFICATION.ordinal(), endIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                : PendingIntent.getActivity(this, RequestCodes.SILENT_END_ACTIONS.ordinal(), endIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC,
                LocalDate.now().plusDays(duration).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                endPendingIntent);
    }
}
