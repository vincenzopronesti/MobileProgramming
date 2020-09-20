package com.ipss.worldbank.custom_widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.ipss.worldbank.R;

/** Class to customize application's edit text underline */
public class CustomEditText extends AppCompatEditText {
    public CustomEditText(Context context) {
        super(context);
        getBackground().setColorFilter(context.getColor(R.color.editTextBackground), PorterDuff.Mode.SRC_IN);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        getBackground().setColorFilter(context.getColor(R.color.editTextBackground), PorterDuff.Mode.SRC_IN);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getBackground().setColorFilter(context.getColor(R.color.editTextBackground), PorterDuff.Mode.SRC_IN);
    }
}
