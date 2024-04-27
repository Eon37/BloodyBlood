package com.eon37_dev.bloodyblood.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.eon37_dev.bloodyblood.R;

public class TimePreference extends DialogPreference {
    private int time;

    public TimePreference(Context context) {
        this(context, null);
    }
    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }
    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }
    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setPositiveButtonText("OK");
        setNegativeButtonText("CANCEL");
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        persistInt(time);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }
    @Override
    protected void onSetInitialValue(Object defaultValue) {
        int persistedInt = getPersistedInt(-1);

        setTime(persistedInt > -1 ? persistedInt : (Integer) defaultValue);
    }
    @Override
    public int getDialogLayoutResource() {
        return R.layout._time_picker;
    }
}
