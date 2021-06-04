package com.cpd;

import com.cpd.serverClientStuff.Client;
import com.cpd.serverClientStuff.Server;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
//        Runnable runnableStart = new Thread() {
//            public void run() {
//                Server server = new Server();
//                try {
//                    server.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        runnableStart.run();
//
        Server server = new Server();
        server.start();

        Client client2 = new Client();
        Client client3 = new Client();

        client2.connect();
        client3.connect();

        // client 3
        client2.sendRequest(
                "addUser Andrei OWNER"
        );
        client2.sendRequest(
                "viewAllAvailableMonths"
        );

        // client3 tries to schedule a place that does not exist
        client3.sendRequest(
                "schedulePlace Cluj idk JAN 2021 1"
        );

        // client2 adds that specific place
        client2.sendRequest(
                "addPlace Cluj idk 2"
        );

        // reserve a place afer the first request failed, this time
        client3.sendRequest(
                "viewAllAvailableMonths"
        );
        client3.sendRequest(
                "schedulePlace Cluj idk JAN 2021 1"
        );
        client3.sendRequest(
                "viewAllAvailableMonths"
        );

        client2.disconnect();
        client3.disconnect();


    }
}
