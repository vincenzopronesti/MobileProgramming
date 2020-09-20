package com.ipss.worldbank.indicator;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ipss.worldbank.entity.Indicator;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** This class runs on a secondary thread in order to execute the filtering algorithm */
public class IndicatorTextWatcherHelperThread implements Runnable {
    public Handler handler;
    private Indicator[] indicators;
    private Indicator[] filteredIndicators;
    private Looper looper;
    private ScrollViewIndicatorActivity activity;
    private ReentrantLock lock;
    private Condition condition;

    public IndicatorTextWatcherHelperThread(Indicator[] indicators, Indicator[] filteredIndicators,
                                            ScrollViewIndicatorActivity activity) {
        this.indicators = indicators;
        this.filteredIndicators = filteredIndicators;
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

            for (int i = 0; i < indicators.length; i++) {
                String[] tokens = indicators[i].getName().split(" ");
                for (int k = 0; k < tokens.length; k++) {
                    if (tokens[k].toLowerCase().startsWith(string)) {
                        filteredIndicators[j] = indicators[i];
                        j++;
                        break;
                    }
                }
            }

            for (; j < filteredIndicators.length; j++) {
                filteredIndicators[j] = null;
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

        Log.d("HandlerIndicatorHelper", "Goodbye world");
    }

    /** Utility method to return the thread's looper
     */
    public Looper getLooper() {
        return looper;
    }
}

