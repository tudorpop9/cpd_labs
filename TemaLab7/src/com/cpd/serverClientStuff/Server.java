package com.cpd.serverClientStuff;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static String IP = "127.0.0.1";
    public static int PORT = 7777;
    private ServerSocket serverSocket;

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);

        for(;;){
            Socket receiver = serverSocket.accept();
            // handle client
        }
    }

    private void stop() throws IOException {
        serverSocket.close();
    }
}
