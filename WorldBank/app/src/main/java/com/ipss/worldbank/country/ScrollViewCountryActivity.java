package com.ipss.worldbank.country;

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
import com.ipss.worldbank.entity.Country;
import com.ipss.worldbank.network.rest.WorldBanksRest;
import com.ipss.worldbank.text_watcher.ListViewTextWatcher;

import java.util.Arrays;
import java.util.Comparator;

/** Class to filter country search on another thread */
public class ScrollViewCountryActivity extends AppCompatActivity {
    private ListView listView;
    private ScrollViewCountryActivity context;
    private ProgressBar progressBar;
    private CustomEditText searchEt;
    private String starter;
    private Thread helperThread;
    private CountryTextWatcherHelperThread textWatcherHelperThread;
    private CountryAdapter adapter;
    private TextWatcher watcher;
    private Country[] countries;
    private Country[] countriesToFilter;
    private boolean paused;

    // Flag to check if activity is on focus; in negative case, do nothing
    private static boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);

        paused = false;

        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.listViewProgressBar);
        searchEt = findViewById(R.id.searchEt);

        searchEt.setEnabled(false);

        // Hide the virtual keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        context = this;
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.spinner), PorterDuff.Mode.MULTIPLY);
        WorldBanksRest worldBanksRest = new WorldBanksRest(this);

        // Call REST service to obtain country list names
        worldBanksRest.getCountryList(new CountryListener(), new CountryErrorListener());
        starter = getIntent().getStringExtra("starter");
    }

    class CountryListener implements Response.Listener<Country[]> {
        @Override
        public void onResponse(final Country[] response) {
            if (!active) {
                return;
            }

            progressBar.setVisibility(View.GONE);
            Arrays.sort(response, new Comparator<Country>() {
                @Override
                public int compare(Country o1, Country o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            countriesToFilter = new Country[response.length];
            for (int i = 0; i < countriesToFilter.length; i++) {
                countriesToFilter[i] = response[i];
            }

            countries = response;

            // Create the runnable interface
            textWatcherHelperThread = new CountryTextWatcherHelperThread(response, countriesToFilter, context);
            helperThread = new Thread(textWatcherHelperThread);
            helperThread.start();
            adapter = new CountryAdapter(countriesToFilter, context, starter);

            // Wait until the filter thread has done its initialization
            textWatcherHelperThread.waitUntilFinish();

            // Add the TextWatcher to the EditText
            searchEt.addTextChangedListener(new ListViewTextWatcher(textWatcherHelperThread.handler));
            listView.setAdapter(adapter);

            // Enable the long click option to show the complete country name
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast,
                            (ViewGroup) findViewById(R.id.custom_toast_container));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText(response[position].getName());

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

    class CountryErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (!active) {
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
            textWatcherHelperThread = new CountryTextWatcherHelperThread(countries, countriesToFilter,
                    context);
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
                Log.d("Country-OnPause", e.getMessage());
            }

            Log.d("Country-OnPause", "onPause ended");
        } catch (NullPointerException e) {
            Log.d("ScrollViewCountry", e.getMessage());
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
                    Log.d("Country-OnDestroy", e.getMessage());
                }

                Log.d("Country-OnDestroy", "onDestroy ended");
            } catch (NullPointerException e) {
                Log.d("ScrollViewCountry", e.getMessage());
            }
        }

        super.onDestroy();
    }
}
