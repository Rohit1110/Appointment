package com.rns.mobile.appointments;

import android.os.AsyncTask;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Appointment;
import model.User;
import utils.FirebaseUtil;

/**
 * Created by Admin on 14/01/2018.
 */

public class NotificationTask extends AsyncTask<Void, Void, Void> {

    private static final String POST_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String APP_KEY = "key=AAAArVtpfx8:APA91bE974pR5MHFZLzdknWFVM_sW2oA-wEVBwf_f0vf-mgpKA91eSiATUCWaEyw-M-6BgaFJkSB7VIu5c7Efo_EAASpM73RLXv7J9yy5Lu7LndpOrcm8Vp7YAdrESbpPX35a8Kertv0";


    private String type;
    private Appointment appointment;
    private List<String> tokens;

    public NotificationTask(Appointment appointment, String type) {
        this.type = type;
        this.appointment = appointment;
        tokens = new ArrayList<>();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {

            send();

        } catch (Exception e) {
            System.out.println("Error in sending notification =>" + e);
            e.printStackTrace();
        }

        return null;
    }

    private void send() throws IOException {
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", APP_KEY);


        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();

        String name = appointment.getName() != null ? appointment.getName() : appointment.getPhone();

        String postData = "{\"name\":\"" + name + "\",\"type\":\"" + type + "\",\"appointmentId\":\"" + appointment.getId() + "\"," + "\"startTime\":\"" + appointment.getStartTime() + "\",\"endTime\":\"" + appointment.getEndTime() + "\",\"date\":\"" + appointment.getDate() + "\"}";

        String postString = "{ \"data\":" + postData + ", \"registration_ids\" : \"" + tokens + "\"}";

        System.out.println("Notification request =>" + postString);

        os.write(postString.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

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

    }

    public void sendNotification() {
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getFcmTokens() != null && user.getFcmTokens().size() > 0) {
                        tokens = user.getFcmTokens();
                        execute();
                    }

                }
            }
        });

    }
}