package model;

import java.util.List;

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
    private List<ActiveContact> contactList;

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public List<ActiveContact> getContactList() {
        return contactList;
    }

    public void setContactList(List<ActiveContact> contactList) {
        this.contactList = contactList;
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

    public Appointment(String name, String start, String end,String id,List<ActiveContact> contactList) {

        this.name = name;
        this.startTime = start;
        this.endTime = end;
        this.id =id;
        this.contactList=contactList;
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

    public Appointment duplicate(String userPhone) {
        Appointment otherUserAppointment = new Appointment();
        otherUserAppointment.setId(getId());
        otherUserAppointment.setPhone(userPhone);
        otherUserAppointment.setDate(getDate());
        otherUserAppointment.setStartTime(getStartTime());
        otherUserAppointment.setEndTime(getEndTime());
        otherUserAppointment.setDescription(getDescription());
        otherUserAppointment.setAppointmentStatus(getAppointmentStatus());
        String name = getName();
        if(name != null) {
            otherUserAppointment.setName(name);
        } else {
            otherUserAppointment.setName(userPhone);
        }
        return otherUserAppointment;
    }
}
