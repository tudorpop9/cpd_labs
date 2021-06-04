package com.cpd.proiect.socket;

import com.cpd.proiect.control.TokenManager;
import com.cpd.proiect.gui.PubMainPanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//https://www.baeldung.com/a-guide-to-java-sockets
public class AppClient extends Thread{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private PubMainPanel pubMainPanel;

    private String ip;
    private int port;

    public AppClient(String ip, int port, PubMainPanel pubMainPanel) {
        this.ip = ip;
        this.port = port;
        this.pubMainPanel = pubMainPanel;
    }

    public synchronized void  connectToServer() throws IOException {
        clientSocket = new Socket(this.ip, this.port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public synchronized void sendMessage(String msg) throws IOException {
        out.println(msg);
    }

    public synchronized void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public void run() {
        super.run();

        for(;;){
            try {
                this.connectToServer();
                System.out.println("Connected");
                break;
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }

        for(;;){
            if(TokenManager.hasToken()){
                // daca a expirat timpul
                // trebuie eliberat token-ul si trims mai departe
                if (TokenManager.getTokenCounter() == 0){
                    TokenManager.toggleTokenFlag();
                    try {
                        this.sendMessage("token");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("AppClient: exceptie la passarea token-ului");
                    }
                    //in gui evidentiez faptul ca nu avem token
                    pubMainPanel.setLabel1Red();
                    pubMainPanel.setLabel2Red();

                    //int gui blochez user-ul din a mai scrie
                    pubMainPanel.setTextArea1Editable(TokenManager.hasToken());
                    pubMainPanel.setTextArea2Editable(TokenManager.hasToken());

                    System.out.println("Am eliberat token-ul si l-am trimis mai departe !");
                }

            }
        }

    }
}
