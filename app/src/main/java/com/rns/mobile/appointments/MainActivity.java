package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import model.User;
import utils.FirebaseUtil;
import utils.Utility;

public class MainActivity extends AppCompatActivity {

    private String phoneNumber;
    private User user;
    private ProgressDialog dialog;
    private String TAG = "MainActivity Appointments";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TODO: read from auth
        phoneNumber = FirebaseUtil.getMobile();

        System.out.println("#### LOADING PROFILE FOR USER " + phoneNumber + ".. ###");

        //startCalendar();

        dialog = Utility.showProgress(MainActivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Utility.hideProgress(dialog);
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc == null || !doc.exists()) {
                        System.out.println("No document found for this number!" + phoneNumber);
                        showEditProfile();
                    } else {
                        System.out.println("Document found!");
                        user = doc.toObject(User.class);
                        if (user == null || user.getFirstName() == null || user.getFirstName().trim().length() == 0) {
                            showEditProfile();
                        } else {
                            if (isReadCalenderPermissionGranted()) {
                                goToHome();
                            }
                        }
                    }
                } else {
                    System.out.println("No document found for this number!!" + task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.hideProgress(dialog);
                System.out.println("### ERROR IN READING PROFILE ---" + e);
                e.printStackTrace();
            }
        });
    }

    private void goToHome() {
        Utility.saveStringToSharedPreferences(new Gson().toJson(user), Utility.INTENT_VAR_USER, MainActivity.this);
        //Save FCM Token
        saveFCMToken();

        Intent i = new Intent(MainActivity.this, AppointmentsActivity.class);
        i.putExtra(Utility.INTENT_VAR_USER, new Gson().toJson(user));
        startActivity(i);
    }

    private void saveFCMToken() {
        if(MyFirebaseInstanceIDService.getToken() != null) {
            if(user.getFcmTokens() == null || user.getFcmTokens().size() == 0) {
                MyFirebaseInstanceIDService.sendRegistrationToServer();
            } else {
                boolean tokenFound = false;
                for(String token: user.getFcmTokens()) {
                    if(MyFirebaseInstanceIDService.getToken().equals(token)) {
                        System.out.println("Token found!!");
                        tokenFound = true;
                        break;
                    }
                }
                if(!tokenFound) {
                    MyFirebaseInstanceIDService.sendRegistrationToServer();
                }
            }
        }
    }

    private void showEditProfile() {
        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
        i.putExtra("user", new Gson().toJson(user));
        startActivity(i);

    }

    private void saveToken(String fcmToken) {
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).update("fcmToken", fcmToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utility.hideProgress(dialog);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("EDIT", "Error writing document", e);
            }
        });
    }


    public boolean isReadCalenderPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALENDAR, android.Manifest.permission.WRITE_CALENDAR, android.Manifest.permission.READ_CONTACTS}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_WRITE_CALENDAR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                }
                break;
        }*/
        goToHome();
    }

}
