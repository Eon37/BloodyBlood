package com.eon37_dev.bloodyblood.settings;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.eon37_dev.bloodyblood.R;
import com.eon37_dev.bloodyblood.StringConstants;
import com.eon37_dev.bloodyblood.notifications.NotificationUtils;

import java.time.LocalDate;
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
        public void onDisplayPreferenceDialog(Preference preference) {
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference) {
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance();
            }

            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(), "androidx.preference.PreferenceFragment.DIALOG");
            }
            else {
                super.onDisplayPreferenceDialog(preference);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            switch (s) {
                case StringConstants.START_DAY_KEY:
                    int startDay = Integer.parseInt(sharedPreferences.getString(StringConstants.START_DAY_KEY, "1"));
                    if (startDay < 1 || startDay > 31) {
                        AlertDialog dialog = new AlertDialog.Builder(this.getContext())
                                .setIcon(R.drawable.ic_launcher_foreground)
                                .setTitle("Incorrect start day")
                                .setMessage("Set the day of month\nNumbers 1-31 only")
                                .create();
                        dialog.show();

                        sharedPreferences.edit().remove(StringConstants.START_DAY_KEY).commit();
                        return;
                    }
                    //no break 'cause should recalculate timings
                case StringConstants.NOTIFICATION_TIME:
                case StringConstants.PERIOD_KEY:
                    NotificationUtils.recalculateTimings(this.getContext(), sharedPreferences);
                    break;
                case StringConstants.HISTORY_SET:
                    TreeSet<String> history = new TreeSet<>(sharedPreferences.getStringSet(StringConstants.HISTORY_SET, new TreeSet<>()));

                    int amount = Integer.parseInt(sharedPreferences.getString(StringConstants.STORE_AMOUNT, "12"));
                    history.removeIf(day -> LocalDate.parse(day).isBefore(LocalDate.now().minusMonths(amount)));

                    break;
            }
        }
    }
}