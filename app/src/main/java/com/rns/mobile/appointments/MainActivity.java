package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    private String TAG="MainActivity Appointments";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TODO: read from auth
        phoneNumber = FirebaseUtil.getMobile();

        System.out.println("#### LOADING PROFILE FOR USER " + phoneNumber + ".. ###");

        dialog = Utility.showProgress(MainActivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Utility.hideProgress(dialog);
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc == null || !doc.exists()) {
                        System.out.println("No document found for this number!" + phoneNumber);
                        showEditProfile();
                    } else {
                        System.out.println("Document found!");
                        user = doc.toObject(User.class);
                        if(user == null || user.getFirstName() == null || user.getFirstName().trim().length() == 0) {
                            showEditProfile();
                        } else {
                            Utility.saveStringToSharedPreferences(new Gson().toJson(user), Utility.INTENT_VAR_USER, MainActivity.this);
                            Intent i = new Intent(MainActivity.this, AppointmentsActivity.class);
                            i.putExtra(Utility.INTENT_VAR_USER, new Gson().toJson(user));
                            startActivity(i);
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

    private void showEditProfile() {
        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
        i.putExtra("user", new Gson().toJson(user));
        startActivity(i);

    }




}
