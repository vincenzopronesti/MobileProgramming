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
import com.ipss.worldbank.chart.mpchart.LoadChartActivity;
import com.ipss.worldbank.database.Item;
import com.ipss.worldbank.entity.DataChart;

import java.util.LinkedList;
import java.util.List;

/** Adapter class from the saved data's list */
public class DataAdapter extends BaseAdapter {
    private List<Item> items;
    private Context context;
    private LayoutInflater inflater;

    public DataAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        // Control to assign background color to the list view entries
        if (position % 2 == 0) {
            rowView = inflater.inflate(R.layout.load_chart_item_light, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.load_chart_item_dark, parent, false);
        }

        final Item item = items.get(position);

        ImageView background = rowView.findViewById(R.id.background);
        TextView chartName = rowView.findViewById(R.id.chartNameTv);
        chartName.setText(context.getString(R.string.data_name, item.getCountry(), item.getIndicator()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoadChartActivity.class);
                intent.putExtra("country", item.getCountry());
                intent.putExtra("indicator", item.getIndicator());
                context.startActivity(intent);
            }
        };

        background.setOnClickListener(listener);
        chartName.setOnClickListener(listener);

        return rowView;
    }
}
