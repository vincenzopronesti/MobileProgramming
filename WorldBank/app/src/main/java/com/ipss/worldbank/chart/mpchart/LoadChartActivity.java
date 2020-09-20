package com.ipss.worldbank.chart.mpchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ipss.worldbank.R;
import com.ipss.worldbank.database.Dao;
import com.ipss.worldbank.entity.DataChart;
import com.ipss.worldbank.entity.Point;

import java.util.ArrayList;
import java.util.List;

/** Class that collects the data necessary for the construction of the graph from the application
 * database */
public class LoadChartActivity extends AppCompatActivity {
    private LineChart lineChart;
    private DataChart dataChart;
    private List<Entry> entries;
    private LineDataSet lineDataSet;
    private LineData lineData;
    private String country;
    private String indicator;
    private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_load_chart);
        lineChart = findViewById(R.id.chart);

        lineChart.setNoDataText("");

        paused = false; // Flag used to manage the activity in case of onResume() call

        country = getIntent().getStringExtra("country");
        indicator = getIntent().getStringExtra("indicator");
        dataChart = Dao.get(this).getDataChart(country, indicator); // Database is called
        entries = fromPointsToEntries(dataChart);
        lineDataSet = new LineDataSet(entries, dataChart.getUnit());
        lineData = new LineData(lineDataSet);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IntFormatter());
        xAxis.setEnabled(true);
        lineChart.setData(lineData);
        lineChart.setBackgroundColor(getColor(R.color.chart_background));
        Description description = new Description();
        description.setText(country + " - " + indicator);
        lineChart.setDescription(description);
        lineChart.invalidate(); // Draw the graph
    }

    /** Utility method to convert database's points to entries used by MPChart Android lib
     * @param dataChart graph data from the database
     * */
    private List<Entry> fromPointsToEntries(DataChart dataChart) {
        List<Entry> entries = new ArrayList<>();

        for (Point p : dataChart.getPointList()) {
            Entry entry = new Entry(p.getYear(), (float)p.getValue());
            entries.add(entry);
        }

        return entries;
    }

    @Override
    protected void onPause() {
        if (!paused) {
            paused = true;
            lineChart.clear(); // In case of activity pause the graph and its data are deleted
            entries.clear();
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        if (paused) {
            paused = false;
            entries = fromPointsToEntries(dataChart);
            lineDataSet = new LineDataSet(entries, dataChart.getUnit());
            lineData = new LineData(lineDataSet);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new IntFormatter());
            xAxis.setEnabled(true);
            lineChart.setData(lineData);
            lineChart.setBackgroundColor(getColor(R.color.chart_background));
            Description description = new Description();
            description.setText(country + " - " + indicator);
            lineChart.setDescription(description);
            lineChart.invalidate();
        }

        super.onResume();
    }
}
