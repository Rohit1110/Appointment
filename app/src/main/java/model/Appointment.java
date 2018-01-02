package model;

/**
 * Created by Rohit on 12/26/2017.
 */

public class Appointment {

    private String startTime;
    private String endTime;
    private String name;
    private String phone;
    private String date;

    public Appointment() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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
        System.out.println("startTime in Model: " + startTime);
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        System.out.println("endTime in Model: " + endTime);
        this.endTime = endTime;
    }

    public void setName(String name) {

        System.out.println("name in Model: " + name);
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
