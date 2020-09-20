package com.ipss.worldbank.chart;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.ipss.worldbank.R;
import com.ipss.worldbank.custom_widget.HelveticaNeueTextView;
import com.ipss.worldbank.database.Dao;
import com.ipss.worldbank.database.Item;
import com.ipss.worldbank.entity.ChartImage;
import com.ipss.worldbank.entity.DataChart;
import com.ipss.worldbank.entity.Point;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/** Main class to show png images and data currently saved */
public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.load);

        ListView chartListView = findViewById(R.id.chartListView);
        HelveticaNeueTextView noDataTv = findViewById(R.id.noDataTv);

        LoadType type = fromOrdinalToLoadType(getIntent().getIntExtra("type",
                LoadType.CHART.ordinal()));

        if (type == LoadType.CHART) { // User wants to load a png file
            List<String> chartImages = new LinkedList<>();
            listAllFiles(chartImages);
            if (chartImages.isEmpty()) {
                noDataTv.setVisibility(View.VISIBLE);
            }
            chartListView.setAdapter(new ChartAdapter(chartImages, this));
        } else { // User wants to load data
            List<Item> items = Dao.get(this).getListOfSavedItems();
            if (items.isEmpty()) {
                noDataTv.setVisibility(View.VISIBLE);
            }
            chartListView.setAdapter(new DataAdapter(items, this));
        }
    }

    private LoadType fromOrdinalToLoadType(int ordinal) {
        switch (ordinal) {
            case 0:
                return LoadType.CHART;

            case 1:
                return LoadType.DATA;

            default:
                return null;
        }
    }

    /** Method to show to the user png charts saved
     * @param myDataSet the list to show */
    private void listAllFiles(List<String> myDataSet) {
        ContextWrapper cw = new ContextWrapper(this);

        // path to /data/data/worldbank/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File[] files = directory.listFiles();
        if(files != null){
            for(File f : files){
                String fileName = f.getName();
                myDataSet.add(fileName);
            }
        }
    }
}
