package com.cpd.serverClientStuff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerRequestHandler extends Thread{
    private Socket client;
    private BufferedReader receiver;
    private PrintWriter sender;

    public ServerRequestHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try{
            receiver = new BufferedReader(new InputStreamReader(client.getInputStream()));
            sender = new PrintWriter(client.getOutputStream(), true);

            String reuqest;
            while ((reuqest = receiver.readLine()) != null ) {
                if("disconnect".equals(reuqest)) {
                    sender.println("you are disconnected");
                    break;
                }



//                sender.println("received: " + reuqest);
            }

            client.close();
            receiver.close();
            sender.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

