package com.cpd.problem2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class FileOperator extends Thread{
    private static final Random random = new Random();
    private String name;
    private SharedFile sharedFile;

    public FileOperator(String name, SharedFile sharedFile) {
        this.name = name;
        this.sharedFile = sharedFile;
    }

    @Override
    public void run() {
        super.run();
        for(int i = 0;i<10;i++){
            int decision = random.nextInt(1001);
            // randomly chose 10 times, even = write, odd = read
            if(decision % 2 == 0){
                //Write
                try {
                    sharedFile.writeFile(name + " wrote: " + Integer.toString(decision));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Could not write in file");
                }

            }else{
                //Read
                String line = "";
                try {
                    line = sharedFile.readFile();
                    System.out.println(name + " read: " + line);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Could not read file");
                } catch (Exception e){
                    System.out.println(name + ": No line to read");
                }
            }
        }
    }
}
