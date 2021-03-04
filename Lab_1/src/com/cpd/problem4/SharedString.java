package com.cpd.problem4;

import static java.lang.Thread.sleep;

public class SharedString {

    private String string;
    public int hi;
    public int lo;
    private boolean isBusy;

    public SharedString(String string) {
        this.string = string;
        this.hi = string.length() - 1;
        this.lo = 0;
        this.isBusy = false;
    }

    private synchronized void toggleBusy(){
        this.isBusy = ! this.isBusy;
    }

    public synchronized char readFront() throws InterruptedException {
        if(isBusy){
            wait();
        }
        // block the other thread
        this.toggleBusy();

        char returnChar = '\0';
        if(string.length() > 0){
            //simulate some kind of delay
            sleep(1000);

            returnChar = string.charAt(this.lo);
            // on overflow, reset
            if(++lo >= string.length()){
                lo = 0;
            }
        }
        // release
        this.toggleBusy();
        return returnChar;
    }

    public synchronized char readBack() throws InterruptedException {
        if(isBusy){
            wait();
        }
        // block the other thread
        this.toggleBusy();
        char returnChar = '\0';
        if(string.length() > 0){
            //simulate some kind of delay
            sleep(1000);

            returnChar = string.charAt(this.hi);
            // update index, and on underflow, reset
            if(--hi < 0){
                hi = string.length() - 1;
            }
        }
        // release
        this.toggleBusy();

        return returnChar;
    }

    public int length(){
        return this.string.length();
    }


}
