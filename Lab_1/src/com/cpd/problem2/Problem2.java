package com.cpd.problem2;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Problem2 {
    private SharedFile sharedFile;

    public Problem2() {
        this.sharedFile = new SharedFile(createFile("Problema_2.txt"));
    }

    public void perform(){
        int numberOfThreads = 10;
        for(int i=0;i<numberOfThreads;i++){
            FileOperator fileOperator = new FileOperator("Operator_" + (i + 1),sharedFile);
            fileOperator.start();
        }
    }

    //https://www.w3schools.com/java/java_files_create.asp
    public File createFile(String fileName){
        File myObj = null;
        try {
            myObj = new File(fileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return myObj;
    }
}
