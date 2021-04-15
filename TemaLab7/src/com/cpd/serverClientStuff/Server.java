package com.cpd.serverClientStuff;

import com.cpd.entity.AirbnbClone;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

public class Server {
    public static String IP = "127.0.0.1";
    public static int PORT = 7777;
    public static AirbnbClone airbnbClone = new AirbnbClone();
    private ServerSocket serverSocket;

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);

        for(;;){
            Socket client = serverSocket.accept();

            new ServerRequestHandler(client, airbnbClone).start();
        }
    }

    private void stop() throws IOException {
        serverSocket.close();
    }
}
