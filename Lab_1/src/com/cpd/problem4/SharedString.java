package com.cpd.problem4;

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

    private synchronized void readFront() throws InterruptedException {
        if(isBusy){
            wait();
        }
        // block the other thread
        this.toggleBusy();

        if(string.length() > 0){
            System.out.println(string.charAt(this.lo));
            // on overflow, reset
            if(++lo >= string.length()){
                lo = 0;
            }
        }
        // release
        this.toggleBusy();
    }

    private synchronized void readBack() throws InterruptedException {
        if(isBusy){
            wait();
        }
        // block the other thread
        this.toggleBusy();

        if(string.length() > 0){
            System.out.println(string.charAt(this.lo));
            // update index, and on underflow, reset
            if(--hi < 0){
                hi = string.length() - 1;
            }
        }
        // release
        this.toggleBusy();
    }

}
