package com.ipss.worldbank.indicator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipss.worldbank.R;
import com.ipss.worldbank.chart.mpchart.ShowChartActivity;
import com.ipss.worldbank.country.ScrollViewCountryActivity;
import com.ipss.worldbank.entity.Indicator;

import static android.graphics.Typeface.BOLD;

/** Adapter class from indicator names list */
public class IndicatorAdapter extends BaseAdapter {
    private Indicator[] indicators;
    private ScrollViewIndicatorActivity context;
    private LayoutInflater inflater;
    private String starter;

    public IndicatorAdapter(Indicator[] indicators, ScrollViewIndicatorActivity context, String starter) {
        this.indicators = indicators;
        this.context = context;
        this.starter = starter;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return indicators.length;
    }

    @Override
    public Object getItem(int position) {
        return indicators[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        final Indicator indicator = indicators[position];

        if (indicator == null) {
            return new View(context);
        }

        // Control to assign background color to the list view entries
        if (position % 2 == 0) {
            rowView = inflater.inflate(R.layout.scrollview_indicator_item_light, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.scrollview_indicator_item_dark, parent, false);
        }

        TextView indicatorNameTv = rowView.findViewById(R.id.indicatorNameTv);
        TextView sourceNameTv = rowView.findViewById(R.id.sourceNameTv);
        TextView indicatorName = rowView.findViewById(R.id.indicatorName);
        TextView sourceName = rowView.findViewById(R.id.sourceName);
        Button helpBtn = rowView.findViewById(R.id.indicatorHelpBtn);
        ImageView background = rowView.findViewById(R.id.background);

        indicatorName.setTypeface(null, BOLD);
        sourceName.setTypeface(null, BOLD);

        indicatorNameTv.setText(indicator.getName());
        sourceNameTv.setText(indicator.getSource());
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
                dialog.setMessage(Html.fromHtml(context.getString(R.string.indicator_src_description) + indicator.getSourceDescription() + context.getString(R.string.indicator_org_description) + indicator.getOrganizationName()));
                dialog.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        // Choice's construct to decide which activity must be shown to the user

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (starter.equals("country")) {
                    Intent intent = new Intent(context, ShowChartActivity.class);
                    String country = context.getIntent().getStringExtra("country");
                    Log.d("IntentIndicator", country);
                    intent.putExtra("country", country);
                    intent.putExtra("indicator", indicator.getId());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ScrollViewCountryActivity.class);
                    intent.putExtra("indicator", indicator.getId());
                    intent.putExtra("starter", starter);
                    context.startActivity(intent);
                }
            }
        };

        indicatorNameTv.setOnClickListener(listener);
        sourceNameTv.setOnClickListener(listener);
        background.setOnClickListener(listener);

        return rowView;
    }
}
