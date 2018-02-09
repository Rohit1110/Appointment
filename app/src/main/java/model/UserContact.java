package model;

/**
 * Created by Rohit on 1/1/2018.
 */

public class UserContact {

    private String name;
    private String phone;
    private boolean isSelected;

    public UserContact(String name, String number) {
        this.name = name;
        this.phone = number;

    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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
