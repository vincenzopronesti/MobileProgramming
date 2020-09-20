package com.ipss.worldbank.chart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipss.worldbank.R;

import java.util.LinkedList;
import java.util.List;

/** Adapter class from the saved image's list */
public class ChartAdapter extends BaseAdapter {
    private List<String> chartImages;
    private Context context;
    private LayoutInflater inflater;

    public ChartAdapter(List<String> chartImages, Context context) {
        this.chartImages = chartImages;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chartImages.size();
    }

    @Override
    public Object getItem(int position) {
        return chartImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View rowView;

        // Control to assign background color to the list view entries
        if (position % 2 == 0) {
            rowView = inflater.inflate(R.layout.load_chart_item_light, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.load_chart_item_dark, parent, false);
        }

        ImageView background = rowView.findViewById(R.id.background);
        TextView chartName = rowView.findViewById(R.id.chartNameTv);
        final String chartImage = chartImages.get(position);

        chartName.setText(chartImage);

        // Clicking on a table entry, a .png graph is shown to the user
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowPNGActivity.class);
                intent.putExtra("filename", chartImage);
                context.startActivity(intent);
            }
        };

        background.setOnClickListener(listener);
        chartName.setOnClickListener(listener);

        return rowView;
    }
}
