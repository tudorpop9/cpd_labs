package com.cpd.serverClientStuff;

import com.cpd.entity.AirbnbClone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerRequestHandler extends Thread{
    private Socket client;
    private BufferedReader receiver;
    private PrintWriter sender;
    private AirbnbClone airbnbClone;

    public ServerRequestHandler(Socket client, AirbnbClone airbnbClone) {
        this.client = client;
        this.airbnbClone = airbnbClone;
    }

    @Override
    public void run() {
        try{
            receiver = new BufferedReader(new InputStreamReader(client.getInputStream()));
            sender = new PrintWriter(client.getOutputStream(), true);

            String request;
            while ((request = receiver.readLine()) != null ) {
                if("disconnect".equals(request)) {
                    sender.println("you are disconnected");
                    break;
                }

                try{
                    this.performRequest(request);
                }catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
                    sender.println("Not enough parameters, or wrong format");
                }catch (NumberFormatException numberFormatException){
                    sender.println("invalid number format, or parameter order");
                }catch (Exception e){
                    sender.println("Unsupported operation / Unknown exception ");
                }

            }

            client.close();
            receiver.close();
            sender.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void performRequest(String request) throws Exception {
        String[] splitReq = request.split(" ", 2);
        String reqName = splitReq[0];

        switch (reqName){
            case "addUser":
                String[] addUserParams = splitReq[1].split(" ");
                String userName = addUserParams[0];
                String userRole = addUserParams[1];

                this.airbnbClone.addUser(userName, userRole);
                break;
            case "addPlace":
                String[] requestParams = splitReq[1].split(" ");
                String city = requestParams[0];
                String placeName = requestParams[1];
                long ownerId = Long.parseLong(requestParams[2]);

                this.airbnbClone.addPlace(city, placeName, ownerId);
                break;
            case "schedulePlace":
                String[] schedulePlaceParams = splitReq[1].split(" ");
                String citySc = schedulePlaceParams[0];
                String placeNameSc = schedulePlaceParams[1];
                String monthSc = schedulePlaceParams[2];
                int yearSc = Integer.parseInt(schedulePlaceParams[3]);
                long touristIdSc = Long.parseLong(schedulePlaceParams[4]);

                this.airbnbClone.schedulePlace(citySc, placeNameSc, monthSc, yearSc, touristIdSc);
                break;
            case "viewAllAvailableMonths":

                this.airbnbClone.viewAllAvailableMonths();
                break;
            default:
                throw new Exception("Unsupported operation");
        }
    }
}

