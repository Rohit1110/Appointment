package model;

/**
 * Created by Rohit on 2/9/2018.
 */

public class ActiveContact {
    private String contact;
    private String number;

    public ActiveContact(String contact, String number) {
        this.contact = contact;
        this.number = number;
        System.out.println("Active contact name "+contact+" number "+number);
    }

    public ActiveContact() {

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
