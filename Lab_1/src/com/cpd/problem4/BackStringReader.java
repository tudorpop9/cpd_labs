package com.cpd.problem4;

public class BackStringReader extends Thread{
    private SharedString string;

    public BackStringReader(SharedString string) {
        this.string = string;
    }

    @Override
    public void run() {
        super.run();
        try {
            for(int i=0;i<string.length();i++){
                System.out.println("Back reader thread: " + this.string.readBack());
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
