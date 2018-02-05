package model;

/**
 * Created by Rohit on 2/5/2018.
 */

public class InviteContact {
    private String name;
    private String phone;

    public InviteContact(String name, String number) {
      System.out.println("In Model "+name);
        this.name = name;
        this.phone = number;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

