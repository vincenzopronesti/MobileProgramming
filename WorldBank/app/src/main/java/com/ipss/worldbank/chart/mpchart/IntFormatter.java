package com.ipss.worldbank.chart.mpchart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class IntFormatter implements IAxisValueFormatter {

    /**
     * Utility method to convert graphic years from float to string
     * @param  value the value to convert
     * @param axis
     * */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return Integer.toString(Math.round(value));
    }
}
