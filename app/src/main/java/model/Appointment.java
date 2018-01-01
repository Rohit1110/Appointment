package model;

import java.util.Date;

/**
 * Created by Rohit on 12/26/2017.
 */

public class Appointment {

    private String startTime;
    private String endTime;
    private String name;
    private String phone;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Appointment(String name, String start, String end) {

        this.name = name;
        this.startTime = start;
        this.endTime = end;
        System.out.println("name: " + name + " time: " + start);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }


    public String getPhone() {
        return phone;
    }


}
