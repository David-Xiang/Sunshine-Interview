package com.example.android.sunshineinterview.utilities;

import android.os.CountDownTimer;

public abstract class TimeCount extends CountDownTimer {
    public int count;
    public TimeCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        count = 0;
    }

    @Override
    public void onFinish() {
        // TODO: no connection for a long time
    }
}
