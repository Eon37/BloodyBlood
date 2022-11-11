package com.example.bloodyblood.notifications;

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
import com.example.bloodyblood.StringConstants;

public class SilentEndNotificationActions extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        prefs.edit().putBoolean(StringConstants.IS_CALM_BG, true).apply();

        LinearLayout mainLayout = findViewById(R.id.activity_main_id);
        mainLayout.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.background_black_white_gradient,
                null));

        TextView mainTextView = findViewById(R.id.activity_main_text_view_id);
        mainTextView.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.black_white_gradient,
                null));

        mainTextView.setTextColor(Color.WHITE);
    }
}
