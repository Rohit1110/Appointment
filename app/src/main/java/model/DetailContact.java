package model;

/**
 * Created by Rohit on 2/9/2018.
 */

public class DetailContact {
    private String contact;
    private String number;
    private String status;

    public DetailContact(String contact, String number,String status) {
        this.contact = contact;
        this.number=number;
        this.status=status;
        System.out.println("Active contact name "+contact);
    }

    public DetailContact() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
