package com.rns.mobile.appointments;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.Appointment;
import model.NotificationData;
import model.NotificationPayload;
import model.User;
import utils.FirebaseUtil;

/**
 * Created by Admin on 14/01/2018.
 */

public class NotificationTask extends AsyncTask<Void, Void, Void> {

    private static final String POST_URL = "https://fcm.googleapis.com/fcm/send";
    //private static final String APP_KEY = "key=AAAArVtpfx8:APA91bE974pR5MHFZLzdknWFVM_sW2oA-wEVBwf_f0vf-mgpKA91eSiATUCWaEyw-M-6BgaFJkSB7VIu5c7Efo_EAASpM73RLXv7J9yy5Lu7LndpOrcm8Vp7YAdrESbpPX35a8Kertv0";
    private static final String APP_KEY = "key=AAAAQi7x3Dg:APA91bH0-amKrSrFBojfq4-z0XXULfPjMeGImdB63TFrsptSpGvcWOJDEo_a8J4J2y0MnBewkvyu4s8B8xyXu3HykOUhbEp-x0gTpwgRkbQjvzVCwH3CDOOCYaaCf-JOwaBzA6YkBH3v";


    private String type;
    private Appointment appointment;
    private List<String> tokens;

    public NotificationTask(Appointment appointment, String type) {
        this.type = type;
        this.appointment = appointment;
        tokens = new ArrayList<>();
        System.out.println("SSSSSSSSSSS in Notification" + " sss");
    }


    @Override
    protected Void doInBackground(Void... voids) {
        System.out.println("in doingbackground");
        try {

            send();

        } catch (Exception e) {
            System.out.println("Error in sending notification =>" + e);
            e.printStackTrace();
        }

        return null;
    }

    private void send() throws IOException {
        System.out.println("In Send notification");
        URL obj = new URL(POST_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", APP_KEY);


        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();

        String name = appointment.getName() != null ? appointment.getName() : appointment.getPhone();

        // String postData = "{\"name\":\"" + name + "\",\"type\":\"" + type + "\",\"appointmentId\":\"" + appointment.getId() + "\"," + "\"startTime\":\"" + appointment.getStartTime() + "\",\"endTime\":\"" + appointment.getEndTime() + "\",\"date\":\"" + appointment.getDate() + "\"}";

        //String postString = "{ \"data\":" + postData + ", \"registration_ids\" : \"" + new Gson().toString(tokens) + "\"}";

        NotificationPayload payload = new NotificationPayload();
        NotificationData data = new NotificationData(appointment, type);
        payload.setRegistration_ids(tokens);
        payload.setData(data);
        String postString = new Gson().toJson(payload);
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
        System.out.println("In sendnotification AAAA" + appointment.getContactList().size());
        for (int i = 0; i < appointment.getContactList().size(); i++) {
            final int finalI = i;
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document("+91"+appointment.getContactList().get(i).getNumber()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    System.out.println("in on sucesss " + appointment.getContactList().get(finalI).getNumber());
                    //System.out.println("fcmtoken " + documentSnapshot.getString("fcmTokens"));
                    if (documentSnapshot != null && documentSnapshot.exists()) {


                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getFcmTokens() != null && user.getFcmTokens().size() > 0) {

                            tokens = user.getFcmTokens();
                            System.out.println("getFcmTokens " + tokens);
                            execute();
                        } else {
                            System.out.println("SSSSSSSSSSSSSS AAAA");
                        }

                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Failed to get FCmtoken");
                }
            });

        }
    }
}