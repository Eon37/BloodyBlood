package com.eon37_dev.bloodyblood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eon37_dev.bloodyblood.calendar.OnDateSelectedListenerImpl;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeColors();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        enableEditMode(prefs.getBoolean(StringConstants.IS_EDIT_MODE_ENABLED, false));

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.removeDecorators();
        calendarView.addDecorator(new CalendarHistoryDecorator(this, DateUtils.extractHistoryDays(this)));
        calendarView.setOnDateChangedListener(new OnDateSelectedListenerImpl(this));
    }

    private void changeColors() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isCalmBg = prefs.getBoolean(StringConstants.IS_CALM_BG, true);
        int selectedCalmColor = Color.parseColor(prefs.getString(StringConstants.CALM_COLORS, "#FFFFFF"));

        Drawable calmBGGradient = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.BLACK, Color.BLACK, selectedCalmColor});

        LinearLayout mainActivityLayout = findViewById(R.id.activity_main_id);
        mainActivityLayout.setBackground(isCalmBg
                ? calmBGGradient
                : ResourcesCompat.getDrawable(getResources(), R.drawable.background_black_red_gradient, null));


        Drawable calmTextBGGradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.BLACK, Color.BLACK, selectedCalmColor});

        TextView mainTextView = findViewById(R.id.activity_main_text_view_id);
        mainTextView.setBackground(isCalmBg
                ? calmTextBGGradient
                : ResourcesCompat.getDrawable(getResources(), R.drawable.black_red_gradient, null));

        mainTextView.setTextColor(isCalmBg ? selectedCalmColor : Color.RED);
    }

    @Override
    protected void onResume() {
        super.onResume();

        changeColors();

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.removeDecorators();
        calendarView.addDecorator(new CalendarHistoryDecorator(this, DateUtils.extractHistoryDays(this)));
    }

    public void settingsBtn_onClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void editBtn_onClick(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean editModeEnabled = prefs.getBoolean(StringConstants.IS_EDIT_MODE_ENABLED, false);

        prefs.edit().putBoolean(StringConstants.IS_EDIT_MODE_ENABLED, !editModeEnabled).commit();

        enableEditMode(!editModeEnabled);
    }

    private void enableEditMode(boolean editModeEnabled) {
        Button editBtn = findViewById(R.id.editBtn);
        editBtn.setText(editModeEnabled ? "Disable edit mode" : "Enable edit mode");

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectionMode(editModeEnabled
                ? MaterialCalendarView.SELECTION_MODE_SINGLE
                : MaterialCalendarView.SELECTION_MODE_NONE);
    }
}
