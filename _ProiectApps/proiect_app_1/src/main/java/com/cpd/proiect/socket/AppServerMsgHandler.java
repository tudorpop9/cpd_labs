package com.cpd.proiect.socket;

import com.cpd.proiect.control.TokenManager;
import com.cpd.proiect.gui.PubMainPanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// https://www.baeldung.com/a-guide-to-java-sockets
public class AppServerMsgHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private PubMainPanel pubMainPanel;

    public AppServerMsgHandler(Socket clientSocket, PubMainPanel pubMainPanel) {
        this.clientSocket = clientSocket;
        this.pubMainPanel = pubMainPanel;
    }

    @Override
    public void run() {
        super.run();
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("AppServerMsgHandler: could not get in/out stream");
        }

        String inputMsg;
        try{
            while ((inputMsg = in.readLine()) != null){
                //Daca am primit token-ul setez flag-ul si timer-ul,
                //altfel ma opresc, sau printez un mesaj
                if(inputMsg.equals("token")){
                    TokenManager.setTokenCounter();
                    TokenManager.toggleTokenFlag();

                    //in gui evidentiez faptul ca avem token
                    pubMainPanel.setLabel1Green();
                    pubMainPanel.setLabel2Green();

                    //dau voie user-ului sa scrie
                    pubMainPanel.setTextArea1Editable(TokenManager.hasToken());
                    pubMainPanel.setTextArea2Editable(TokenManager.hasToken());

                    System.out.println("Am primit token-ul !");
                }else if (inputMsg.equals("end")){
                    break;
                }else{
                    System.out.println("Received: " + inputMsg);
                }
            }

            in.close();
            out.close();
            clientSocket.close();

        }catch (IOException e){
            e.printStackTrace();
        }


    }
}
