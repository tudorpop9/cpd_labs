package com.cpd.problem4;

public class Problem4 {
    private SharedString string;
    private BackStringReader backReader;
    private FrontStringReader frontReader;

    public Problem4() {
        this.string = new SharedString("AnaAreMere");
        this.backReader = new BackStringReader(string);
        this.frontReader = new FrontStringReader(string);
    }

    public void perform(){
        this.frontReader.start();
        this.backReader.start();
    }

}
