package com.ipss.worldbank.country;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ipss.worldbank.entity.Country;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** This class runs on a secondary thread in order to execute the filtering algorithm */
public class CountryTextWatcherHelperThread implements Runnable {
    public Handler handler;
    private Country[] countries;
    private Country[] filteredCountries;
    private Looper looper;
    private ScrollViewCountryActivity activity;
    private ReentrantLock lock;
    private Condition condition;

    public CountryTextWatcherHelperThread(Country[] countries, Country[] filteredCountries, ScrollViewCountryActivity activity) {
        this.countries = countries;
        this.filteredCountries = filteredCountries;
        this.activity = activity;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    /** Callback executed every time the user modify the text inside the EditText */
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String string = (String)msg.obj;
            string = string.toLowerCase();
            int j = 0;

            for (int i = 0; i < countries.length; i++) {
                if ((countries[i].getName().toLowerCase().startsWith(string))) {
                    filteredCountries[j] = countries[i];
                    j++;
                }
            }

            for (; j < filteredCountries.length; j++) {
                filteredCountries[j] = null;
            }

            activity.updateListView();

            return true;
        }
    };

    /** Called by the activity in order to wait that this thread has done its event loop
     * initialization
     */
    public void waitUntilFinish() {
        lock.lock();
        while (handler == null) {
            try {
                condition.await(5, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {

            }
        }

        lock.unlock();
    }

    @Override
    public void run() {
        Looper.prepare();

        lock.lock();
        handler = new Handler(Looper.myLooper(), callback);
        condition.signal();
        lock.unlock();
        looper = Looper.myLooper();
        Looper.loop();

        Log.d("HandlerCountryHelper", "Goodbye world");
    }

    /** Utility method to return the thread's looper
     */
    public Looper getLooper() {
        return looper;
    }
}
