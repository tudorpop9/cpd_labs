package com.cpd.proiect.control;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownStarter extends TimerTask {
    private Timer timer;

    @Override
    public void run() {
        TokenManager.decrementTokenCounter();
    }

    public void beginCountdown(){
        timer = new Timer();
        timer.schedule(this, 0L, 1000L);
    }

    public void stopCoundown(){
        timer.cancel();
    }
}
