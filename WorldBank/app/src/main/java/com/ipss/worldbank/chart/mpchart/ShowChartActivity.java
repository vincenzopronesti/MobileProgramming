package com.ipss.worldbank.chart.mpchart;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ipss.worldbank.R;
import com.ipss.worldbank.database.Dao;
import com.ipss.worldbank.entity.Country;
import com.ipss.worldbank.entity.DataChart;
import com.ipss.worldbank.entity.Indicator;
import com.ipss.worldbank.entity.IndicatorData;
import com.ipss.worldbank.entity.Point;
import com.ipss.worldbank.network.rest.WorldBanksRest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Class that shows the graph and its data to the user  */
public class ShowChartActivity extends AppCompatActivity {
    private LineChart lineChart;
    private ProgressBar progressBar;
    private String country;
    private String indicator;
    private ShowChartActivity context;
    private Toolbar toolbar;
    private List<Entry> entries;
    private String unit;
    private boolean paused;

    // Flag to check if activity is on focus; in negative case, do nothing
    private static boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.toolbar_show_chart);

        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.chartProgressBar);
        setSupportActionBar(toolbar); // Toolbar with action menu to save graph (.png format) and data

        country = getIntent().getStringExtra("country");
        indicator = getIntent().getStringExtra("indicator");
        lineChart = findViewById(R.id.chart);

        lineChart.setNoDataText("");

        context = this;
        paused = false;

        toolbar.setEnabled(false);
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.spinner),
                PorterDuff.Mode.MULTIPLY);

        WorldBanksRest worldBanksRest = new WorldBanksRest(this);
        Country c = new Country(null, country, null);
        Indicator i = new Indicator(indicator, null);

        // Call REST service to takes data from db giving country and indicator values
        worldBanksRest.getDataFromCountryAndIndicator(c, i, new ShowChartListener(),
                new ShowChartErrorListener());
    }

    class ShowChartListener implements Response.Listener<IndicatorData[]> {

        @Override
        public void onResponse(final IndicatorData[] response) {
            if (!active) {
                return;
            }

            progressBar.setVisibility(View.GONE);

            if (response == null) {
                showNoDataErrorDialog();
            }

            List<Entry> entries = convertIndicatorDatatoEntry(response);

            /*
             *  HACK: This try catch exists because of a known bug of MPAndroidChart.
             *  (https://github.com/PhilJay/MPAndroidChart/issues/2450)
             */
            try {
                unit = response[0].getUnit();
                LineDataSet lineDataSet = new LineDataSet(entries, unit);
                LineData lineData = new LineData(lineDataSet);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new IntFormatter());
                xAxis.setEnabled(true);
                lineChart.setData(lineData);
                lineChart.setBackgroundColor(context.getColor(R.color.chart_background));
                Description description = new Description();
                description.setText(country + " - " + indicator);
                lineChart.setDescription(description);
                lineChart.invalidate();
                toolbar.setEnabled(true);
            } catch (ArrayIndexOutOfBoundsException e) {
                showNoDataErrorDialog();
            }
        }
    }

    class ShowChartErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (!active) { // Activity is not on focus
                return;
            }

            progressBar.setVisibility(View.GONE);

            AlertDialog.Builder alert = new AlertDialog.Builder(context,
                    R.style.Theme_AppCompat_Light_Dialog_Alert);
            alert.setTitle(R.string.error);
            alert.setMessage(R.string.error_msg);
            alert.setCancelable(false);
            alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            alert.show();
        }
    }

    @Override
    protected void onStop() {
        active = false;
        super.onStop();
    }

    @Override
    protected void onStart() {
        active = true;
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (paused) {
            paused = false;
            toolbar.setEnabled(false);
            progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.spinner),
                    PorterDuff.Mode.MULTIPLY);
            WorldBanksRest worldBanksRest = new WorldBanksRest(this);
            Country c = new Country(null, country, null);
            Indicator i = new Indicator(indicator, null);
            worldBanksRest.getDataFromCountryAndIndicator(c, i, new ShowChartListener(),
                    new ShowChartErrorListener());
            progressBar.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (!paused) {
            paused = true;
            if (entries != null && entries.size() > 0) {
                lineChart.clear(); // In case of activity pause the graph and its data are deleted
                entries.clear();

            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (!paused) {
            if (entries != null && entries.size() > 0) {
                lineChart.clear();
                entries.clear();
            }
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_data) {
            Dao dao = Dao.get(this);

            try {
                DataChart dataChart = new DataChart(country, indicator, convertEntriesToPoints(entries), unit);
                dao.addDataChart(dataChart); // Data are saved into the database
                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.data_not_saved, Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.action_save_png) {
            Bitmap b = loadBitmapFromView(lineChart); // Creating bitmap from graph view

            try {
                saveToInternalStorage(b);
                Toast.makeText(this, R.string.png_saved, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.png_not_saved, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Utility method to convert graph's view into bitmap format, to save dta into the database
     * @param view graph to save
     * */
    public static Bitmap loadBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);

        return returnedBitmap;
    }

    /** Method to save the bitmap into a .png file
     * @param bitmapImage the bitmap to save
     * */
    private String saveToInternalStorage(Bitmap bitmapImage) throws Exception {
        ContextWrapper cw = new ContextWrapper(this);

        // path to /data/data/worldbank/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String filename = country + "-" + indicator + "-" +
                new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date())
                + ".png";

        File mypath = new File(directory, filename);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    /** Utility method to convert and sort indicator's value into entry format
     * @param response indicators obtained by REST call
     * */
    private List<Entry> convertIndicatorDatatoEntry(IndicatorData[] response) {
        entries = new ArrayList<>();
        for (IndicatorData d : response) {
            Entry e = new Entry(d.getDate(), (float)d.getValue());
            entries.add(e);
        }

        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                if (o1.getX() - o2.getX() < 0.f) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return entries;
    }

    /** Utility method to convert entries data into graphic's points format
     * @param entries the entries to convert
     * */
    private List<Point> convertEntriesToPoints(List<Entry> entries) {
        List<Point> points = new ArrayList<>();

        for (Entry e : entries) {
            Point p = new Point(Math.round(e.getX()), e.getY());
            points.add(p);
        }

        return points;
    }

    /** Utility method to show an error dialog message
     */
    private void showNoDataErrorDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context,
                R.style.Theme_AppCompat_Light_Dialog_Alert);
        alert.setTitle(R.string.error);
        alert.setCancelable(false);
        alert.setMessage(R.string.no_data_error_msg);
        alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        alert.show();
    }
}
