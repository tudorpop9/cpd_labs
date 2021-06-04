package com.cpd.serverClientStuff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private BufferedReader receiver;
    private PrintWriter sender;
    Socket server;

    public void connect() throws IOException {
        server = new Socket(Server.IP, Server.PORT);

        receiver = new BufferedReader(new InputStreamReader(server.getInputStream()));
        sender = new PrintWriter(server.getOutputStream(), true);
    }

    public String sendRequest(String requestBody) throws IOException {
        sender.println(requestBody);

        String return_msg = receiver.readLine();
        System.out.printf(return_msg);
        return return_msg;
    }

    public void disconnect() throws IOException {
        receiver.close();
        sender.close();
        server.close();
    }
}
