package com.ipss.worldbank.country;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipss.worldbank.R;
import com.ipss.worldbank.chart.mpchart.ShowChartActivity;
import com.ipss.worldbank.entity.Country;
import com.ipss.worldbank.topic.ScrollViewTopicActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/** Adapter class from country names list */
public class CountryAdapter extends BaseAdapter {
    private Country[] countries;
    private LayoutInflater inflater;
    private ScrollViewCountryActivity context;
    private String starter;

    public CountryAdapter(Country[] countries, ScrollViewCountryActivity context, String starter) {
        this.countries = countries;
        this.context = context;
        this.starter = starter;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return countries.length;
    }

    @Override
    public Object getItem(int position) {
        return countries[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View rowView;
        final Country country = countries[position];

        if (country == null) {
            return new View(context);
        }
        // Control to assign background color to the list view entries
        if (position % 2 == 0) {
            rowView = inflater.inflate(R.layout.scrollview_country_item_light, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.scrollview_country_item_dark, parent, false);
        }

        TextView countryTv = rowView.findViewById(R.id.countryTv);
        TextView isoTv = rowView.findViewById(R.id.isocodeTv);
        ImageView countryFlagIv = rowView.findViewById(R.id.countryFlagIv);
        ImageView background = rowView.findViewById(R.id.background);

        countryTv.setText(country.getName());
        countryTv.setTypeface(null, Typeface.BOLD);
        try {
            // Read the flags from the assets folder and create the menu icons
            InputStream input = context.getAssets().open("countries_flags/" + country.getIso2code().toLowerCase() + ".png");
            Drawable drawable = Drawable.createFromStream(input, null);
            countryFlagIv.setImageDrawable(drawable);
        } catch (IOException e) {
            // Don't do anything: leave the default image
        }

        isoTv.setText(country.getIso2code());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Choice's construct to decide which activity must be shown to the user

                if (starter.equals("country")) {
                    Intent intent = new Intent(context, ScrollViewTopicActivity.class);
                    intent.putExtra("starter", starter);
                    intent.putExtra("country", country.getIso2code());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ShowChartActivity.class);
                    intent.putExtra("country", country.getIso2code());
                    intent.putExtra("indicator", context.getIntent().getStringExtra("indicator"));
                    context.startActivity(intent);
                }
            }
        };

        countryTv.setOnClickListener(listener);
        background.setOnClickListener(listener);

        return rowView;
    }
}
