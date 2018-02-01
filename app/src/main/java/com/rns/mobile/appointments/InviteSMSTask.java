package com.rns.mobile.appointments;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Appointment;
import model.SmsField;
import utils.Utility;

/**
 * Created by Admin on 17/01/2018.
 */

public class InviteSMSTask extends AsyncTask<Void, Void, Void> {

    private static String GET_URL = "http://bhashsms.com/api/sendmsg.php?user=7350182285&pass=a5c84b9&sender=EDOFOX&priority=Priority&stype=normal";//"http://api.msg91.com/api/sendhttp.php" + "?sender=TIMEDE&route=4&authkey=193344AsiDSe0j5a5db681&country=91";
    private String smsType;
    private String appointment;
    private SmsField smsField;
    private String number;

/*  http://bhashsms.com/api/sendmsg.php?user=7350182285&pass=********&sender=Sender ID&phone=MobileNo1,MobileNo2..&text=Test SMS&priority=Priority&stype=smstype

Note : smstype - normal/flash/unicode , Priority - ndnd/dnd , Mobile Number without 91*/

    public InviteSMSTask(String type, String appointment) {
        System.out.println("Call SMS");
        this.smsType = type;
        this.appointment = appointment;


    }

    @Override
    protected Void doInBackground(Void... voids) {
        //
       /* if(appointment == null) {
            return null;
        }*/

        try {
            //System.out.println("Url for SMS"+ smsField.getUrl()+"?sender="+smsField.getSender()+"&route="+smsField.getRoute()+"&authkey="+smsField.getAuthkey()+"&country="+smsField.getCountry());
            System.out.println("Url for SMS1"+GET_URL);
            String url=GET_URL;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");


            String mobiles = "&phone=" + appointment;
            String message = "&text=Hey. I am using TimeDe app to be more productive! I can book your time and you can book my time with this app. Use this link to donwload the app: \n" +
                    "https://goo.gl/BBFWM7";

            if(Utility.NOTIFICATION_TYPE_NEW.equalsIgnoreCase(smsType)) {

                String msg="&message=Hey. I am using TimeDe app to be more productive! I can book your time and you can book my time with this app. Use this link to donwload the app: \n" +
                        "https://goo.gl/BBFWM7";



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
        return null;
    }
}


