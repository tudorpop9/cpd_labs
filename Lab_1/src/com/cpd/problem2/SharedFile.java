package com.cpd.problem2;

import java.io.*;
import java.util.Scanner;

public class SharedFile {

    private File file;
    private boolean isBusy;

    public SharedFile(File file) {
        this.file = file;
    }

    private synchronized void toggleBusy(){
        this.isBusy = ! this.isBusy;
    }

    public synchronized String readFile() throws InterruptedException, FileNotFoundException {
        if (isBusy){
            wait();
        }
        toggleBusy();
        Scanner reader = null;
        String fileLine = "";
        // if an exception occurs, release the lock and close reader
        try{
            reader = new Scanner(file);
            fileLine = reader.nextLine();

        }finally{
            reader.close();
            notifyAll();
            toggleBusy();
        }
        return fileLine;
    }

    public synchronized void writeFile(String data) throws InterruptedException, IOException {
        if (isBusy){
            wait();
        }
        toggleBusy();

        FileWriter writer = new FileWriter(file);
        writer.write(data + '\n');
        writer.close();

        notifyAll();
        toggleBusy();

    }


}
