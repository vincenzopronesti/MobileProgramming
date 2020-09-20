package com.ipss.worldbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipss.worldbank.chart.LoadActivity;
import com.ipss.worldbank.chart.LoadType;
import com.ipss.worldbank.country.ScrollViewCountryActivity;
import com.ipss.worldbank.custom_widget.GradientTextView;
import com.ipss.worldbank.topic.ScrollViewTopicActivity;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private ImageView base1;
    private ImageView base2;
    private ImageView base3;
    private ImageView base4;
    private Button helpCountryBtn;
    private Button helpTopicBtn;
    private Button helpChartBtn;
    private Button helpDataBtn;
    private GradientTextView researchCountryTv;
    private ImageView worldIv;
    private GradientTextView researchTopicTv;
    private ImageView topicIv;
    private GradientTextView chartTv;
    private ImageView chartIv;
    private GradientTextView dataTv;
    private ImageView dataIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.toolbar_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews(); // Initialize every views

        setResearchForCountryOnClickListener(); // Set "Search for country" button
        setResearchForTopicOnClickListener();   // Set "Search for topic" button
        setLoadChartOnClickListener();          // Set "Load chart" button
        setLoadDataOnClickListener();           // Set "Load data" button
    }

    /** Utility method to initialize every graphic component
     */
    private void findViews() {
        base1 = findViewById(R.id.base1);
        base2 = findViewById(R.id.base2);
        base3 = findViewById(R.id.base3);
        base4 = findViewById(R.id.base4);
        helpCountryBtn = findViewById(R.id.helpCountryBtn);
        helpTopicBtn = findViewById(R.id.helpTopicBtn);
        helpChartBtn = findViewById(R.id.helpChartBtn);
        helpDataBtn = findViewById(R.id.helpDataBtn);
        researchCountryTv = findViewById(R.id.searchCountryTv);
        worldIv = findViewById(R.id.worldIv);
        researchTopicTv = findViewById(R.id.searchTopicTv);
        topicIv = findViewById(R.id.topicIv);
        chartTv = findViewById(R.id.chartTv);
        chartIv = findViewById(R.id.chartIv);
        dataTv = findViewById(R.id.dataTv);
        dataIv = findViewById(R.id.dataIv);
    }

    /** Utility method to set and initialize the "Search for country" button
     */
    private void setResearchForCountryOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScrollViewCountryActivity.class);
                intent.putExtra("starter", "country");
                startActivity(intent);
            }
        };

        base1.setOnClickListener(listener);
        researchCountryTv.setOnClickListener(listener);
        worldIv.setOnClickListener(listener);

        helpCountryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alert.setTitle(R.string.search_by_country);
                alert.setMessage(R.string.research_for_country_help_msg);
                alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    /** Utility method to set and initialize the "Search for topic" button
     * */
    private void setResearchForTopicOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScrollViewTopicActivity.class);
                intent.putExtra("starter", "topic");
                startActivity(intent);
            }
        };

        base2.setOnClickListener(listener);
        researchTopicTv.setOnClickListener(listener);
        topicIv.setOnClickListener(listener);

        helpTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alert.setTitle(R.string.search_by_topic);
                alert.setMessage(R.string.research_for_topic_help_msg);
                alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    /** Utility method to set and initialize the "Load chart" button
     * */
    private void setLoadChartOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoadActivity.class).putExtra("type", LoadType.CHART.ordinal()));
            }
        };

        base3.setOnClickListener(listener);
        chartTv.setOnClickListener(listener);
        chartIv.setOnClickListener(listener);

        helpChartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alert.setTitle(R.string.load_chart);
                alert.setMessage(R.string.load_chart_help_msg);
                alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    /** Utility method to set and initialize the "Load data" button
     * */
    private void setLoadDataOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoadActivity.class).putExtra("type", LoadType.DATA.ordinal()));
            }
        };

        base4.setOnClickListener(listener);
        dataTv.setOnClickListener(listener);
        dataIv.setOnClickListener(listener);

        helpDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alert.setTitle(R.string.load_data);
                alert.setMessage(R.string.load_data_help_msg);
                alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
            final SpannableString s = new SpannableString(getString(R.string.about_msg));
            Linkify.addLinks(s, Linkify.ALL);
            alert.setTitle(R.string.action_info);
            alert.setMessage(s);
            alert.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();
            try {
                ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            } catch (NullPointerException e) {
                Log.d("setMovementMethod", "NullPointer catched");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}