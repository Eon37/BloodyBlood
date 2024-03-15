package com.eon37_dev.bloodyblood;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.eon37_dev.bloodyblood.notifications.NotificationUtils;

import java.util.HashSet;
import java.util.TreeSet;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            this.getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            switch (s) {
                case StringConstants.START_DAY_KEY:
                    //if start is set after the start but before the end then it may broke history showing
                    boolean isCalmBg = sharedPreferences.getBoolean(StringConstants.IS_CALM_BG, false);
                    if (!isCalmBg) {
                        TreeSet<String> starts = new TreeSet<>(sharedPreferences.getStringSet(StringConstants.STARTS_SET, new TreeSet<>()));
                        TreeSet<String> ends = new TreeSet<>(sharedPreferences.getStringSet(StringConstants.ENDS_SET, new TreeSet<>()));
                        if (starts.size() > ends.size()) {
                            starts.remove(starts.last());
                        }
                        sharedPreferences.edit().putStringSet(StringConstants.STARTS_SET, starts).apply();
                    }
                    //no break 'cause should recalculate timings
                case StringConstants.PERIOD_KEY:
                    NotificationUtils.recalculateTimings(this.getContext(), sharedPreferences);
                    break;
                case StringConstants.STARTS_SET:
                    TreeSet<String> starts = new TreeSet<>(sharedPreferences.getStringSet(StringConstants.STARTS_SET, new TreeSet<>()));

                    int amount = Integer.parseInt(sharedPreferences.getString(StringConstants.STORE_AMOUNT, "12"));
                    if (starts.size() == amount + 1) {
                        TreeSet<String> ends = new TreeSet<>(sharedPreferences.getStringSet(StringConstants.ENDS_SET, new TreeSet<>()));
                        starts.remove(starts.first());
                        ends.remove(ends.first());

                        sharedPreferences.edit().putStringSet(StringConstants.STARTS_SET, starts).apply();
                        sharedPreferences.edit().putStringSet(StringConstants.ENDS_SET, ends).apply();
                    }
                    break;
            }
        }
    }
}