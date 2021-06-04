package com.cpd.entity;

import com.cpd.enums.UserType;

import java.util.*;
import java.util.stream.Collectors;

public class AirbnbClone {

    private List<User> users;
    private Set<Place> places;

    public AirbnbClone() {
        this.users = new ArrayList<>();
        this.places = new TreeSet<>();
        init();
    }

    private void init(){
        User ownerUser1 = new User("Lucretia", UserType.OWNER);
        User ownerUser2 = new User("Eugen", UserType.OWNER);
        User touristUser1 = new User("Ana", UserType.TOURIST);
        User touristUser2 = new User("Marcel", UserType.TOURIST);
        this.users.add(ownerUser1);
        this.users.add(ownerUser2);
        this.users.add(touristUser1);
        this.users.add(touristUser2);

        this.addPlace("Cluj","Dej", ownerUser1.getUserId());
        this.addPlace("Iasi","Parc", ownerUser2.getUserId());
    }

    public Set<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Set<Place> places) {
        this.places = places;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public synchronized User addUser(String name, String role){
        User newUser = new User(name, UserType.valueOf(role));
        this.users.add(newUser);

        return newUser;
    }

    public synchronized void addPlace(String city, String placeName, long ownerId){
        this.places.add(new Place(placeName, city, ownerId));
    }

    public synchronized void schedulePlace(String city, String placeName, String month, int year, long userId){
        List<Place> matchPlaces = this.places.stream().filter(p -> p.getName().equalsIgnoreCase(placeName) && p.getCity().equalsIgnoreCase(city)).collect(Collectors.toList());
        if(matchPlaces.size() != 1){
            System.out.println("Invalid places name or city");
        }
        Place place = matchPlaces.get(0);
        List<String> availableMonths = place.getAvailableMonths(year);

        if(availableMonths.contains(month)){
            place.getBookingSchedules().add(new BookingSchedule(month, Integer.toString(year),userId));
        }else{
            System.out.println(month + " is not available");
        }
    }

    public synchronized void viewAllAvailableMonths(){
        for(Place place : this.places){
            List<String> availableMonths = place.getCrtYearAvailableMonths();
            System.out.println("For " + place.getName() + " in " + place.getCity() + " available months are: ");
            for(String s :availableMonths){
                System.out.println(s);
            }
        }
    }
}
