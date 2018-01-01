package com.rns.mobile.appointments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import model.User;
import utils.FirebaseUtil;

public class MainActivity extends AppCompatActivity {

    private String phoneNumber;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //TODO: read from auth
        phoneNumber = FirebaseUtil.getMobile();

        System.out.println("#### LOADING PROFILE .. ###");

        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document("9423040642").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                            Intent i = new Intent(MainActivity.this, AppointmentsActivity.class);
                            i.putExtra("user", new Gson().toJson(user));
                            startActivity(i);
                        }
                    }
                } else {
                    System.out.println("No document found for this number!!" + task.getException());
                }
            }
        });
    }

    private void showEditProfile() {
        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
        i.putExtra("user", new Gson().toJson(user));
        startActivity(i);

    }


}
