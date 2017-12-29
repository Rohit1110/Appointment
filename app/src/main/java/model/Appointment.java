package model;

/**
 * Created by Rohit on 12/26/2017.
 */

public class Appointment {
    private String time;
    private  String name;
    private String phone;
    public Appointment(String name,String time){

        this.name=name;
        this.time=time;
        System.out.println("name: "+ name+" time: "+ time);
    }

    public String getTime() {
        return time;
    }



    public String getName() {
        return name;
    }



    public String getPhone() {
        return phone;
    }


}
