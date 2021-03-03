package com.cpd.problem3;

public class Office extends Thread{

    private static final int MAX_NO_ITERATIONS = 5;
    private static final int MAX_NO_EMPLOYEES = 8;

    private static final Printer printer = new Printer();

    @Override
    public void run() {
        super.run();
        for(int j = 0; j < MAX_NO_EMPLOYEES; j++){
            Employee e = new Employee("Employee_" + (j+1), printer);
            e.start();
        }
    }
}
