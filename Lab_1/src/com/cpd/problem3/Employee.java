package com.cpd.problem3;

import java.util.List;
import java.util.Random;

public class Employee extends Thread {

    private String employeeName;
//    private List<Document> documents;
    private int noDocuments;
    private Printer printer;
    private static final Random random = new Random();


    public Employee(String employeeName, Printer printer) {
        super();
        this.employeeName = employeeName;
        // get a random number of documents
        this.noDocuments = random.nextInt(10);
        this.printer = printer;
    }

    @Override
    public void run() {
        super.run();
        try {
            for(int i = 0; i < this.noDocuments; i++){
                // Elaborates a new document
                Document document = this.elaborateDocument(i);
                // Prints the newly created document
                printer.printDocument(this.employeeName, document);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Elaborates a documents
    private Document elaborateDocument(int documentNumber) {
        Document d = new Document(this.employeeName, documentNumber);
        try {
            // sleeps document's elaboration time
            sleep(d.getElaborationTime());
            System.out.println(this.employeeName + ": elaborating document_"+documentNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return d;

    }


    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }
}
