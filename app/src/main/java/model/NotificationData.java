package model;

/**
 * Created by Admin on 15/01/2018.
 */

public class NotificationData {

    private String name;
    private String type;
    private String appointmentId;
    private String startTime;
    private String endTime;
    private String date;

    public NotificationData(Appointment app, String type) {
        setName(app.getName());
        setAppointmentId(app.getId());
        setDate(app.getDate());
        setStartTime(app.getStartTime());
        setEndTime(app.getEndTime());
        setType(type);
    }

    public NotificationData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
