package com.rns.mobile.appointments;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import model.Appointment;
import model.SmsField;
import utils.Utility;

/**
 * Created by Admin on 17/01/2018.
 */

public class SMSTask extends AsyncTask<Void, Void, Void> {

    private static String GET_URL = "http://api.msg91.com/api/sendhttp.php" + "?sender=TIMEDE&route=4&authkey=193344AsiDSe0j5a5db681&country=91";
    private String smsType;
    private Appointment appointment;
    private SmsField smsField;


    public SMSTask(String type, Appointment appointment, SmsField smsField) {
        this.smsType = type;
        this.appointment = appointment;
        this.smsField = smsField;
        System.out.println("SSSSSS" + smsField);
    }

    @Override
    protected Void doInBackground(Void... voids) {


        //
        if (appointment == null || appointment.getName() == null || appointment.getPhone() == null) {
            return null;
        }
        System.out.println("Size of ContactList "+appointment.getContactList());
        for (int i = 0; i < appointment.getContactList().size(); i++) {
            try {
                //System.out.println("Url for SMS"+ smsField.getUrl()+"?sender="+smsField.getSender()+"&route="+smsField.getRoute()+"&authkey="+smsField.getAuthkey()+"&country="+smsField.getCountry());
                System.out.println("Url for SMS1" + GET_URL);
                String url = smsField.getUrl() + "?sender=" + smsField.getSender() + "&route=" + smsField.getRoute() + "&authkey=" + smsField.getAuthkey() + "&country=" + smsField.getCountry();
                System.out.println("Url for SMS" + url);
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");


                String mobiles = "&mobiles=" + appointment.getContactList().get(i).getNumber();
                String message = "";

                if (Utility.NOTIFICATION_TYPE_NEW.equalsIgnoreCase(smsType)) {
                    System.out.println("SMS " + smsField.getSMS());
                    String msg = smsField.getSMS();
                    msg = msg.replaceAll("&user", appointment.getName());
                    msg = msg.replaceAll("&date", appointment.getDate());
                    msg = msg.replaceAll("&startTime", appointment.getStartTime());
                    message = msg + smsField.getAppurl();

// message = "&message=New appointment is booked for you by user " + appointment.getName() + " for the date " + appointment.getDate()
//                        + " smsTypetarting at - " + appointment.getStartTime() + " using a cool new app called as - TimeDe. Download the app free from Google play store";
                }

                String postString = GET_URL + mobiles + message;

                // For POST only - START
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();


                System.out.println("SMS request =>" + postString);

                os.write(postString.getBytes());
                os.flush();
                os.close();
                // For POST only - END

                int responseCode = con.getResponseCode();
                System.out.println("GET Response Code :: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // print result
                    System.out.println(".... NOTIFICATION SENT SUCCESSFULLY! ..." + response.toString());
                } else {
                    System.out.println("POST request not worked");
                }

            } catch (Exception e) {

            }
        }
        return null;


    }

}


