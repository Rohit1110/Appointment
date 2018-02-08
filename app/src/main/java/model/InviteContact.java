package model;

/**
 * Created by Rohit on 2/5/2018.
 */

public class InviteContact {
    private String name;
    private String phone;
    private boolean isSelected;

    public InviteContact(String name, String number,boolean isSelected) {
      System.out.println("In Model "+name);
        this.name = name;
        this.phone = number;
        this.isSelected=isSelected;

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

