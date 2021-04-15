package com.cpd.entity;

import com.cpd.enums.Months;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class Place implements Comparable<Place>{
    private static final List<String> ALL_MONTHS = new ArrayList<String>(Arrays.asList(Months.getMonthsString()));
    private String name;
    private String city;
    private List<BookingSchedule> bookingSchedules;
    private long ownerId;

    public Place(String name, String city,  long ownerId) {
        this.name = name;
        this.city = city;
        this.bookingSchedules = new ArrayList<>();
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<BookingSchedule> getBookingSchedules() {
        return bookingSchedules;
    }

    public void setBookingSchedules(List<BookingSchedule> bookingSchedules) {
        this.bookingSchedules = bookingSchedules;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
    // returns a list of the available months in the current year
    public List<String> getCrtYearAvailableMonths(){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> notAvaiblableMonths = this.bookingSchedules.stream().filter(bookingSchedule -> bookingSchedule.getYear().equals(Integer.toString(currentYear)))
                                                                               .map(BookingSchedule::getMonth)
                                                                               .collect(Collectors.toList());

        List<String> availableMonths = new ArrayList<>();
        for(String month : ALL_MONTHS){
            if(!notAvaiblableMonths.contains(month)){
                availableMonths.add(month);
            }
        }
        return  availableMonths;
    }

    // returns a list of the available months in the year received as a parameter
    public List<String> getAvailableMonths(int year){
        List<String> notAvaiblableMonths = this.bookingSchedules.stream().filter(bookingSchedule -> bookingSchedule.getYear().equals(Integer.toString(year)))
                .map(BookingSchedule::getMonth)
                .collect(Collectors.toList());

        List<String> availableMonths = new ArrayList<>();
        for(String month : ALL_MONTHS){
            if(!notAvaiblableMonths.contains(month)){
                availableMonths.add(month);
            }
        }
        return  availableMonths;
    }


    @Override
    public int compareTo(Place o) {
        return (this.city + this.name).compareTo(o.getCity()+o.getName());
    }
}
