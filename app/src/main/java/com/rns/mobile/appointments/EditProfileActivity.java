package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.Arrays;

import model.User;
import utils.FirebaseUtil;
import utils.Utility;

/**
 * Created by Admin on 31/12/2017.
 */

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etBusinessName;
    private Spinner etStartTime;
    private Spinner etEndTime;
    private User user;
    private Button btnSaveProfile;
    private String phoneNumber;
    private boolean hideicon = true;
    private ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        String userJson = getIntent().getStringExtra("user");
        phoneNumber = FirebaseUtil.getMobile();

        if (userJson != null) {
            user = new Gson().fromJson(userJson, User.class);
        }

        etFirstName = (EditText) findViewById(R.id.et_first_name);
        if (user != null) {
            etFirstName.setText(user.getFirstName());
        }

        etLastName = (EditText) findViewById(R.id.et_last_name);
        if (user != null) {
            etLastName.setText(user.getLastName());
        }

        etEmail = (EditText) findViewById(R.id.et_email);
        if (user != null) {
            etEmail.setText(user.getEmail());
        }

        etBusinessName = (EditText) findViewById(R.id.et_business_name);
        if (user != null) {
            etBusinessName.setText(user.getBusinessName());
        }

        etStartTime = (Spinner) findViewById(R.id.et_start_time);
        etEndTime = (Spinner) findViewById(R.id.et_end_time);

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Utility.TIME_SLOTS);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etStartTime.setAdapter(fromAdapter);

        if (user != null && user.getStartTime() != null) {
            etStartTime.setSelection(Arrays.asList(Utility.TIME_SLOTS).indexOf(user.getStartTime()));
        }

        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Utility.TIME_SLOTS);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etEndTime.setAdapter(toAdapter);

        if (user != null && user.getEndTime() != null) {
            etStartTime.setSelection(Arrays.asList(Utility.TIME_SLOTS).indexOf(user.getEndTime()));
        }

    /*    btnSaveProfile = (Button) findViewById(R.id.btn_save_profile);
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(user == null) {
                    user = new User();
                }

                user.setFirstName(etFirstName.getText().toString());
                user.setLastName(etLastName.getText().toString());
                user.setBusinessName(etBusinessName.getText().toString());
                user.setEmail(etEmail.getText().toString());
                user.setStartTime(etStartTime.getSelectedItem().toString());
                user.setEndTime(etEndTime.getSelectedItem().toString());
                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EDIT", "DocumentSnapshot successfully written!");
                        Intent i = new Intent(EditProfileActivity.this, AppointmentsActivity.class);
                        i.putExtra("user", new Gson().toJson(user));
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EDIT", "Error writing document", e);
                    }
                });
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_mark:

                if (user == null) {
                    user = new User();
                }
                if (!etFirstName.getText().toString().equals("") && !etLastName.getText().toString().equals("") && !etEmail.getText().toString().equals("")) {
                    user.setFirstName(etFirstName.getText().toString());
                    user.setLastName(etLastName.getText().toString());
                    user.setBusinessName(etBusinessName.getText().toString());
                    user.setEmail(etEmail.getText().toString());
                    user.setStartTime(etStartTime.getSelectedItem().toString());
                    user.setEndTime(etEndTime.getSelectedItem().toString());
                    dialog = Utility.showProgress(EditProfileActivity.this);
                    FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Utility.hideProgress(dialog);
                            Log.d("EDIT", "DocumentSnapshot successfully written!");
                            Intent i = new Intent(EditProfileActivity.this, AppointmentsActivity.class);
                            i.putExtra("user", new Gson().toJson(user));
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("EDIT", "Error writing document", e);
                        }
                    });
                } else {
                    etFirstName.setError("fill first name");
                    etLastName.setError("fill last name");
                    etEmail.setError("fill email");
                }

                return true;


        }
        return false;

    }


}

