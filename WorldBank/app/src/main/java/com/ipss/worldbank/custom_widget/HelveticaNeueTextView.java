package com.ipss.worldbank.custom_widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.ipss.worldbank.R;

/** Class to customize application's font */
public class HelveticaNeueTextView extends android.support.v7.widget.AppCompatTextView {
    public HelveticaNeueTextView(Context context) {
        super(context);
        init();
    }

    public HelveticaNeueTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HelveticaNeueTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        try {
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.helveticaneue);
            setTypeface(typeface);
        } catch (Resources.NotFoundException e) {
            Log.d("HelveticaNeueTextView", e.getMessage());
        }
    }
}
