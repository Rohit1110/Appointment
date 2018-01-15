package model;

import java.util.List;

/**
 * Created by Admin on 15/01/2018.
 */

public class NotificationPayload {

    private NotificationData data;
    private List<String> registration_ids;

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(List<String> registration_ids) {
        this.registration_ids = registration_ids;
    }
}
