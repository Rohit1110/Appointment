package com.rns.mobile.appointments;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.List;

import model.User;
import utils.FirebaseUtil;

/**
 * Created by Rohit on 1/9/2018.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static String token;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token = getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer();
    }

    public static String getToken() {

        token = FirebaseInstanceId.getInstance().getToken();
        return token;
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     *
     */
    public static void sendRegistrationToServer() {
        System.out.println("Saving FCM TOKEN =>" + token);
        String phoneNumber = FirebaseUtil.getMobile();
        if(phoneNumber != null) {

            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot != null && documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        List<String> fcmTokens = user.getFcmTokens();
                        if(fcmTokens == null) {
                            fcmTokens = new ArrayList<>();
                        }
                        fcmTokens.add(token);
                        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(documentSnapshot.getId()).update("fcmTokens", fcmTokens).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("EDIT", "FCM Token successfully written!");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("EDIT", "Error writing document", e);
                            }
                        });
                    }
                }
            });


        }
    }
}
