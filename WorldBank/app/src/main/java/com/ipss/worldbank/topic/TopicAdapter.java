package com.ipss.worldbank.topic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipss.worldbank.R;
import com.ipss.worldbank.entity.Topic;
import com.ipss.worldbank.indicator.ScrollViewIndicatorActivity;

import java.util.LinkedList;

/** Adapter class from topic names list */
public class TopicAdapter extends BaseAdapter {
    private Topic[] topics;
    private LayoutInflater inflater;
    private ScrollViewTopicActivity context;
    private String starter;

    public TopicAdapter(Topic[] topics, ScrollViewTopicActivity context, String starter) {
        this.topics = topics;
        this.context = context;
        this.starter = starter;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return topics.length;
    }

    @Override
    public Object getItem(int position) {
        return topics[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final View rowView;
        final Topic topic = topics[position];

        if (topic == null) {
            return new View(context);
        }

        // Control to assign background color to the list view entries
        if (position % 2 == 0) {
            rowView = inflater.inflate(R.layout.scrollview_topic_item_light, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.scrollview_topic_item_dark, parent, false);
        }

        TextView topicTv = rowView.findViewById(R.id.topicTv);
        Button helpButtonTopic = rowView.findViewById(R.id.indicatorHelpBtn);
        ImageView background = rowView.findViewById(R.id.background);

        topicTv.setTypeface(null, Typeface.BOLD);
        topicTv.setText(topic.getValue());
        helpButtonTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context,
                        R.style.Theme_AppCompat_Light_Dialog_Alert);
                dialog.setTitle(R.string.description);
                dialog.setMessage(topic.getSourceNote());
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
                Intent intent = new Intent(context, ScrollViewIndicatorActivity.class);
                intent.putExtra("starter", starter);
                intent.putExtra("topic", topic.getId());
                if (starter.equals("country")) {
                    String country = context.getIntent().getStringExtra("country");
                    Log.d("IntentTopic", country);
                    intent.putExtra("country", country);
                }

                context.startActivity(intent);
            }
        };

        topicTv.setOnClickListener(listener);
        background.setOnClickListener(listener);

        return rowView;
    }
}
