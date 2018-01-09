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
    private String id;
    private String appointmentStatus;

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }



    public String getId() {
        return id;
    }

    public void setId(String getid) {
        this.id = getid;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public Appointment() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Appointment(String name, String start, String end,String id) {

        this.name = name;
        this.startTime = start;
        this.endTime = end;
        this.id =id;
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

    @Override
    public String toString() {
        return date + " -- " + startTime + " to " + endTime;
    }
}
