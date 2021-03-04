package com.cpd.problem4;

public class FrontStringReader extends Thread{

    private SharedString string;

    public FrontStringReader(SharedString string) {
        this.string = string;
    }

    @Override
    public void run() {
        super.run();
        try {
            for(int i=0;i<string.length();i++){
                System.out.println("Front reader thread: " + this.string.readConcurrently(true));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public SharedString getString() {
        return string;
    }

    public void setString(SharedString string) {
        this.string = string;
    }
}
