package model;

/**
 * Created by Rohit on 1/23/2018.
 */

public class SmsField {
    private String SMS;
    private String authkey;
    private String country;
    private String route;
    private String sender;
    private String url;

  /*  public String getSMS() {
        return SMS;
    }

    public void setSMS(String SMS) {
        this.SMS = SMS;
    }*/



    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
        System.out.println("Sender----"+route);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
        System.out.println("Sender----"+sender);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        System.out.println("Sender----"+url);
    }
}
