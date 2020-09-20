package com.ipss.worldbank.topic;

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
import com.ipss.worldbank.entity.Topic;
import com.ipss.worldbank.network.rest.WorldBanksRest;
import com.ipss.worldbank.text_watcher.ListViewTextWatcher;

import java.util.Arrays;
import java.util.Comparator;

/** Class to filter topic search on another thread */
public class ScrollViewTopicActivity extends AppCompatActivity {
    private ListView listView;
    private ProgressBar progressBar;
    private CustomEditText searchEt;
    private ScrollViewTopicActivity context;
    private String starter;
    private Thread helperThread;
    private TopicTextWatcherHelperThread textWatcherHelperThread;
    private TopicAdapter adapter;
    private TextWatcher watcher;
    private Topic[] topics;
    private Topic[] topicsToFilter;
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

        searchEt.setHint(R.string.topic_hint);
        searchEt.setEnabled(false);

        // Hide the virtual keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = this;
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.spinner), PorterDuff.Mode.MULTIPLY);
        WorldBanksRest worldBanksRest = new WorldBanksRest(this);
        starter = getIntent().getStringExtra("starter");

        // Call REST service to obtain topic list names
        worldBanksRest.getTopicsList(new ScrollViewTopicActivity.TopicListener(), new ScrollViewTopicActivity.TopicErrorListener());
    }

    class TopicListener implements Response.Listener<Topic[]> {
        @Override
        public void onResponse(final Topic[] response) {
            if (!active) {
                return;
            }

            progressBar.setVisibility(View.GONE);
            Arrays.sort(response, new Comparator<Topic>() {
                @Override
                public int compare(Topic o1, Topic o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });

            topicsToFilter = new Topic[response.length];
            for (int i = 0; i < topicsToFilter.length; i++) {
                topicsToFilter[i] = response[i];
            }

            topics = response;

            // Create the runnable interface
            textWatcherHelperThread = new TopicTextWatcherHelperThread(response, topicsToFilter, context);
            helperThread = new Thread(textWatcherHelperThread);
            helperThread.start();
            adapter = new TopicAdapter(topicsToFilter, context, starter);

            // Wait until the filter thread has done its initialization
            textWatcherHelperThread.waitUntilFinish();
            watcher = new ListViewTextWatcher(textWatcherHelperThread.handler);

            // Add the TextWatcher to the EditText
            searchEt.addTextChangedListener(watcher);
            listView.setAdapter(adapter);

            // Enable the long click option to show the complete topic name
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast,
                            (ViewGroup) findViewById(R.id.custom_toast_container));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText(response[position].getValue());

                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                    return true;
                }
            });
            searchEt.setEnabled(true);
        }
    }

    class TopicErrorListener implements Response.ErrorListener {

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
            textWatcherHelperThread = new TopicTextWatcherHelperThread(topics, topicsToFilter, context);
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
                Log.d("Topic-onPause", e.getMessage());
            }

            Log.d("Topic-onPause", "onDestroy ended");
        } catch (NullPointerException e) {
            Log.d("ScrollViewTopic", e.getMessage());
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
                    Log.d("Topic-OnDestroy", e.getMessage());
                }

                Log.d("Topic-OnDestroy", "onDestroy ended");
            } catch (NullPointerException e) {
                Log.d("ScrollViewTopic", e.getMessage());
            }
        }
        super.onDestroy();
    }
}