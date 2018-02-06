package com.rns.mobile.appointments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private static final String MY_PREFS_NAME = "mypref";
    private String phoneNumber;
    private User user;
    private ProgressDialog dialog;
    private String TAG = "MainActivity Appointments";
    private boolean showcancel=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
        SharedPreferences prefs=getSharedPreferences(MY_PREFS_NAME,0);
        String dontask=prefs.getString("selected", "");


        System.out.println("Dont ask "+dontask);
        if(!dontask.equals("yes")) {
            CharSequence[] array = {"Don't show again","Show"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("App Invitations");
            dialog.setMessage("Are you sure you want to Invite your friends?");
            dialog.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Utility.saveStringToSharedPreferences("selected ", "yes", MainActivity.this);


                }
            });
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Yes".
                    SharedPreferences sp=getSharedPreferences(MY_PREFS_NAME,0);
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putString("selected","yes");
                    edit.apply();
                    Intent i = new Intent(MainActivity.this, InviteActivity.class);
                    i.putExtra(Utility.INTENT_VAR_USER, new Gson().toJson(user));
                    startActivity(i);

                }
            })
                    .setNegativeButton("No ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Action for "Cancel".
                            Utility.saveStringToSharedPreferences(new Gson().toJson(user), Utility.INTENT_VAR_USER, MainActivity.this);
                            //Save FCM Token
                            saveFCMToken();

                            Intent i = new Intent(MainActivity.this, AppointmentsActivity.class);
                            i.putExtra(Utility.INTENT_VAR_USER, new Gson().toJson(user));
                            i.putExtra("showcancel", "false");
                            i.putExtra("states", "1");

                            startActivity(i);
                            finish();
                        }
                    });

            final AlertDialog alert = dialog.create();
            alert.show();

        }else {
            Intent i = new Intent(MainActivity.this, AppointmentsActivity.class);
            i.putExtra(Utility.INTENT_VAR_USER, new Gson().toJson(user));
            i.putExtra("showcancel", "false");
            i.putExtra("states", "1");

            startActivity(i);
            finish();

        }



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
