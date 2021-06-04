package com.cpd.proiect.socket;

import com.cpd.proiect.gui.PubMainPanel;

import java.io.IOException;
import java.net.ServerSocket;

// https://www.baeldung.com/a-guide-to-java-sockets
public class AppServer extends Thread{
    private ServerSocket serverSocket;
    private int port;
    private PubMainPanel pubMainPanel;

    public AppServer(int port, PubMainPanel pubMainPanel) {
        this.port = port;
        this.pubMainPanel = pubMainPanel;
    }

    //astept mesaje/token-ul
    public void listenForToken() throws IOException {
        serverSocket = new ServerSocket(this.port);
        for(;;){
            new AppServerMsgHandler(serverSocket.accept(), pubMainPanel).start();
        }
    }

    public synchronized void stopListneningForToken() throws IOException {
        serverSocket.close();
    }

    @Override
    public void run() {
        super.run();
        try {
            this.listenForToken();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("AppServer metoda run");
        }
    }
}
