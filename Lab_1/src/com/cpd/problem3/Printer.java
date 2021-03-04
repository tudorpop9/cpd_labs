package com.cpd.problem3;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Printer {
    private static final Random random = new Random();
    private static boolean isPrinterBusy = false;
//    private Queue<Employee> queue = new LinkedList<Employee>();

    private synchronized void toggleBusy(){
        isPrinterBusy = !isPrinterBusy;
    }

    public synchronized void printDocument(String employeeName, Document document) throws InterruptedException {
        if(isPrinterBusy){
            wait();
        }
        // here used to be an else attached to the if, I think that implementation is bad
        toggleBusy();
        System.out.println("Printer: " + employeeName + " has printed document_" + document.getDocIndex());
        // keeps the printer busy for 1-3 seconds
        sleep(1000 + (long)random.nextInt(2000));
        toggleBusy();
        notify();
    }

}
