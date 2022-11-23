package com.example.bloodyblood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

        LinearLayout mainActivityLayout = findViewById(R.id.activity_main_id);
        mainActivityLayout.setBackground(ResourcesCompat.getDrawable(getResources(),
                isCalmBg
                        ? R.drawable.background_black_white_gradient
                        : R.drawable.background_black_red_gradient,
                null));

        TextView mainTextView = findViewById(R.id.activity_main_text_view_id);
        mainTextView.setBackground(ResourcesCompat.getDrawable(getResources(),
                isCalmBg ? R.drawable.black_white_gradient : R.drawable.black_red_gradient,
                null));

        mainTextView.setTextColor(isCalmBg ? Color.WHITE : Color.RED);
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
