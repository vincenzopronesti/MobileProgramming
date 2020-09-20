package com.ipss.worldbank.text_watcher;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;

import com.ipss.worldbank.entity.Indicator;

/** Class which alert a filter thread every time the user digits a char into the edit text */
public class ListViewTextWatcher implements android.text.TextWatcher {
    private Handler handler;

    public ListViewTextWatcher(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String string = s.toString(); // Catch what the user has digit
        Message msg = new Message();
        msg.obj = string;
        handler.sendMessage(msg); // Alert the thread
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
