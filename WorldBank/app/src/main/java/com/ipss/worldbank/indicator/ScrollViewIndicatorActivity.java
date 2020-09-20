package com.ipss.worldbank.indicator;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ipss.worldbank.R;
import com.ipss.worldbank.custom_widget.CustomEditText;
import com.ipss.worldbank.entity.Indicator;
import com.ipss.worldbank.entity.Topic;
import com.ipss.worldbank.network.rest.WorldBanksRest;
import com.ipss.worldbank.text_watcher.ListViewTextWatcher;

import java.util.Arrays;
import java.util.Comparator;

/** Class to filter indicator search on another thread */
public class ScrollViewIndicatorActivity extends AppCompatActivity {
    private ListView listView;
    private ProgressBar progressBar;
    private CustomEditText searchEt;
    private ScrollViewIndicatorActivity context;
    private String starter;
    private Thread helperThread;
    private IndicatorTextWatcherHelperThread textWatcherHelperThread;
    private IndicatorAdapter adapter;
    private TextWatcher watcher;
    private Indicator[] indicators;
    private Indicator[] indicatorsToFilter;
    private boolean paused;

    // Flag to check if activity is on focus; in negative case, do nothing
    private static boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_scrollview);

        searchEt = findViewById(R.id.searchEt);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.listViewProgressBar);

        paused = false;

        searchEt.setHint(R.string.indicator_hint);
        searchEt.setEnabled(false);

        // Hide the virtual keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.spinner), PorterDuff.Mode.MULTIPLY);
        WorldBanksRest worldBanksRest = new WorldBanksRest(this);
        String topic = getIntent().getStringExtra("topic");
        Topic t = new Topic();
        t.setId(topic);

        // Call REST service to obtain indicator list names
        worldBanksRest.getIndicatorsListFromTopic(t, new IndicatorListener(), new IndicatorErrorListener());
        starter = getIntent().getStringExtra("starter");
    }

    class IndicatorListener implements Response.Listener<Indicator[]> {
        @Override
        public void onResponse(final Indicator[] response) {
            if (!active) {
                return;
            }

            progressBar.setVisibility(View.GONE);
            Arrays.sort(response, new Comparator<Indicator>() {
                @Override
                public int compare(Indicator o1, Indicator o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            indicatorsToFilter = new Indicator[response.length];
            for (int i = 0; i < indicatorsToFilter.length; i++) {
                indicatorsToFilter[i] = response[i];
            }

            indicators = response;

            // Create the runnable interface
            textWatcherHelperThread = new IndicatorTextWatcherHelperThread(response, indicatorsToFilter, context);
            helperThread = new Thread(textWatcherHelperThread);
            helperThread.start();
            adapter = new IndicatorAdapter(indicatorsToFilter, context, starter);

            // Wait until the filter thread has done its initialization
            textWatcherHelperThread.waitUntilFinish();
            watcher = new ListViewTextWatcher(textWatcherHelperThread.handler);

            // Add the TextWatcher to the EditText
            searchEt.addTextChangedListener(watcher);
            listView.setAdapter(adapter);
            searchEt.setEnabled(true);
        }
    }

    /** Called by the helper thread to update the ListView */
    public void updateListView() {
        runOnUiThread(action);
    }

    /** The action to be executed on the UI thread */
    private Runnable action = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
        }
    };

    class IndicatorErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (!active) {
                return;
            }

            progressBar.setVisibility(View.GONE);
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
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
            textWatcherHelperThread = new IndicatorTextWatcherHelperThread(indicators, indicatorsToFilter, context);
            helperThread = new Thread(textWatcherHelperThread);
            helperThread.start();
            textWatcherHelperThread.waitUntilFinish();
            watcher = new ListViewTextWatcher(textWatcherHelperThread.handler);
            searchEt.addTextChangedListener(watcher);
            paused = false;
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        paused = true;

        try {
            // Remove the text watcher and join the helper thread
            searchEt.removeTextChangedListener(watcher);
            textWatcherHelperThread.getLooper().quit();
            try {
                helperThread.join(500);
            } catch (InterruptedException e) {
                Log.d("Indicator-onPause", e.getMessage());
            }

            Log.d("Indicator-onPause", "onDestroy ended");
        } catch (NullPointerException e) {
            Log.d("ScrollViewIndicator", e.getMessage());
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (!paused) {
            try {
                // As in onPause()
                textWatcherHelperThread.getLooper().quit();
                try {
                    helperThread.join(500);
                } catch (InterruptedException e) {
                    Log.d("Indicator-OnDestroy", e.getMessage());
                }

                Log.d("Indicator-OnDestroy", "onDestroy ended");
            } catch (NullPointerException e) {
                Log.d("ScrollViewIndicator", e.getMessage());
            }
        }

        super.onDestroy();
    }
}
