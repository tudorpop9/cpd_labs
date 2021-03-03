package com.cpd.problem3;

import java.util.Random;

public class Document {
    private static final Random random = new Random();
    private long elaborationTime;
    private int docIndex;
    private String employeeName;

    public Document() {
    }

    public Document(String employeeName, int docIndex) {
        this.employeeName = employeeName;
        this.docIndex = docIndex;
        // random elaboration time
        this.elaborationTime = 1000 + (long)random.nextInt(2000);
    }

    public int getDocIndex() {
        return docIndex;
    }

    public void setDocIndex(int docIndex) {
        this.docIndex = docIndex;
    }

    public long getElaborationTime() {
        return elaborationTime;
    }

    public void setElaborationTime(int elaborationTime) {
        this.elaborationTime = elaborationTime;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
