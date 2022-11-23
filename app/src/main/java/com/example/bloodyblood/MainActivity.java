package com.example.bloodyblood;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeColors();

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.removeDecorators();
        calendarView.addDecorator(new CalendarHistoryDecorator(this, extractHistoryDays()));
    }

    private Collection<CalendarDay> extractHistoryDays() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Set<String> starts = prefs.getStringSet(StringConstants.STARTS_SET, new HashSet<>());
        Set<String> ends = prefs.getStringSet(StringConstants.ENDS_SET, new HashSet<>());

        Iterator<String> startsIterator = starts.stream().sorted().iterator();
        Iterator<String> endsIterator = ends.stream().sorted().iterator();
        Collection<CalendarDay> extracted = new HashSet<>();

        while (startsIterator.hasNext()) {
            LocalDate start = LocalDate.parse(startsIterator.next());
            LocalDate end = endsIterator.hasNext() ? LocalDate.parse(endsIterator.next()) : LocalDate.now();

            for (LocalDate ld = start; !ld.isAfter(end); ld = ld.plusDays(1)) {
                extracted.add(CalendarDay.from(ld));
            }
        }

        return extracted;
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
        calendarView.addDecorator(new CalendarHistoryDecorator(this, extractHistoryDays()));
    }

    public void settingsBtn_onClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
