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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void settingsBtn_onClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
