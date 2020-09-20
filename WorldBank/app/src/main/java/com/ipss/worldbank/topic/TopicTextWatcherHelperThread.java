package com.ipss.worldbank.topic;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ipss.worldbank.entity.Topic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** This class runs on a secondary thread in order to execute the filtering algorithm */
public class TopicTextWatcherHelperThread implements Runnable {
    public Handler handler;
    private Topic[] topics;
    private Topic[] filteredTopics;
    private Looper looper;
    private ScrollViewTopicActivity activity;
    private ReentrantLock lock;
    private Condition condition;

    public TopicTextWatcherHelperThread(Topic[] topics, Topic[] filteredTopics, ScrollViewTopicActivity activity) {
        this.topics = topics;
        this.filteredTopics = filteredTopics;
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

            for (int i = 0; i < topics.length; i++) {
                if ((topics[i].getValue().toLowerCase().startsWith(string))) {
                    filteredTopics[j] = topics[i];
                    j++;
                }
            }

            for (;j < filteredTopics.length; j++) {
                filteredTopics[j] = null;
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

        Log.d("HandlerTopicHelper", "Goodbye world");
    }

    /** Utility method to return the thread's looper
     */
    public Looper getLooper() {
        return looper;
    }
}
