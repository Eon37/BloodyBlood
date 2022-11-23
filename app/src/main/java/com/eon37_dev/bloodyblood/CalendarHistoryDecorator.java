package com.eon37_dev.bloodyblood;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

public class CalendarHistoryDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;
    private Drawable drawable;

    public CalendarHistoryDecorator(Context context, Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
        this.drawable = ResourcesCompat.getDrawable(
                context.getResources(),
                R.drawable.blood_transparent_icon,
                null);
        this.drawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}
