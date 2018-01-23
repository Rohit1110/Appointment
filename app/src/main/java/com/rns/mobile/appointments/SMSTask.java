package com.rns.mobile.appointments;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import model.Appointment;
import utils.Utility;

/**
 * Created by Admin on 17/01/2018.
 */

public class SMSTask extends AsyncTask<Void, Void, Void> {

    private static String GET_URL = "http://api.msg91.com/api/sendhttp.php" + "?sender=TIMEDE&route=4&authkey=193344AsiDSe0j5a5db681&country=91";
    private String smsType;
    private Appointment appointment;


    public SMSTask(String type, Appointment appointment) {
        this.smsType = type;
        this.appointment = appointment;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //
        if(appointment == null || appointment.getName() == null || appointment.getPhone() == null) {
            return null;
        }

        try {
            URL obj = new URL(GET_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");


            String mobiles = "&mobiles=" + appointment.getPhone();
            String message = "";

            if(Utility.NOTIFICATION_TYPE_NEW.equalsIgnoreCase(smsType)) {
                message = "&message=New appointment is booked for you by user " + appointment.getName() + " for the date " + appointment.getDate()
                        + " starting at - " + appointment.getStartTime() + " using a cool new app called as - TimeDe. Download the app free from Google play store";
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
        return null;
    }
}


