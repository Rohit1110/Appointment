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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private boolean hideicon = false;
    private ProgressDialog dialog;
    private MultiSelectionSpinner spinner;
    private String selectedDays;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        final String[] stringsGameCat = getResources().getStringArray(R.array.off_days);
        String userJson = getIntent().getStringExtra("user");
        phoneNumber = FirebaseUtil.getMobile();


        if (userJson != null) {
            user = new Gson().fromJson(userJson, User.class);

        }

        etFirstName = (EditText) findViewById(R.id.et_first_name);


        if (user != null) {
            etFirstName.setText(user.getFirstName());
            etFirstName.setEnabled(false);
            hideicon = true;
        }

        etLastName = (EditText) findViewById(R.id.et_last_name);


        if (user != null) {
            etLastName.setText(user.getLastName());
            etLastName.setEnabled(false);

        }

        etEmail = (EditText) findViewById(R.id.et_email);


        if (user != null) {
            etEmail.setText(user.getEmail());
            etEmail.setEnabled(false);
        }

        etBusinessName = (EditText) findViewById(R.id.et_business_name);


        if (user != null) {
            etBusinessName.setText(user.getBusinessName());
            etBusinessName.setEnabled(false);
        }

        etStartTime = (Spinner) findViewById(R.id.et_start_time);

        etEndTime = (Spinner) findViewById(R.id.et_end_time);
        spinner = (MultiSelectionSpinner) findViewById(R.id.spinner_off_days);


        ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Utility.TIME_SLOTS);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etStartTime.setAdapter(fromAdapter);

        if (user != null && user.getStartTime() != null) {
            etStartTime.setSelection(Arrays.asList(Utility.TIME_SLOTS).indexOf(user.getStartTime()));
            etStartTime.setEnabled(false);
            spinner.setEnabled(false);
        }

        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Utility.TIME_SLOTS);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etEndTime.setAdapter(toAdapter);

        if (user != null && user.getEndTime() != null) {
            etEndTime.setSelection(Arrays.asList(Utility.TIME_SLOTS).indexOf(user.getEndTime()));
            etEndTime.setEnabled(false);
        }





        spinner.setItems(getResources().getStringArray(R.array.off_days));




        // String[] items = (user.getSelectedDays().split(","));

        if (user != null && user.getSelectedDays() != null) {
            List<String> myList = new ArrayList<String>(Arrays.asList(user.getSelectedDays().replaceAll("\'", "").split(",")));
            spinner.setSelection(myList);
            spinner.setEnabled(false);
        }else{
            spinner.setText();
            //spinner.setEnabled(false);
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
        if (hideicon) {
            MenuItem item = menu.findItem(R.id.menu_mark);
            item.setVisible(false);
            this.invalidateOptionsMenu();

            MenuItem items = menu.findItem(R.id.menu_edit);
            items.setVisible(true);
            this.invalidateOptionsMenu();
        } else {
            MenuItem item = menu.findItem(R.id.menu_mark);
            item.setVisible(true);
            this.invalidateOptionsMenu();

            MenuItem items = menu.findItem(R.id.menu_edit);
            items.setVisible(false);
            this.invalidateOptionsMenu();

        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_edit:
                hideicon = false;
                invalidateOptionsMenu();
                etFirstName.setEnabled(true);
                etFirstName.setFocusable(true);
                etLastName.setEnabled(true);

                etEmail.setEnabled(true);

                etBusinessName.setEnabled(true);

                etStartTime.setEnabled(true);

                etEndTime.setEnabled(true);
                spinner.setEnabled(true);


                return true;
            case R.id.menu_mark:

                if (user == null) {
                    user = new User();
                }
                if (!etFirstName.getText().toString().equals("") && !etLastName.getText().toString().equals("") && !etEmail.getText().toString().equals("")) {
                    hideicon = true;
                    invalidateOptionsMenu();
                    user.setFirstName(etFirstName.getText().toString());
                    user.setLastName(etLastName.getText().toString());
                    user.setBusinessName(etBusinessName.getText().toString());
                    user.setEmail(etEmail.getText().toString());
                    user.setStartTime(etStartTime.getSelectedItem().toString());
                    user.setEndTime(etEndTime.getSelectedItem().toString());
                    if (spinner.getSelectedStrings().toString() != "[]") {
                        selectedDays = "'" + spinner.getSelectedStrings().toString().replace("[", "").replace("]", "").replace(", ", "','") + "'";
                        System.out.println("Selected Days :  " + selectedDays);

                        user.setSelectedDays(selectedDays.replaceAll("\'", ""));


                    }

                    if(!Utility.isInternetOn(EditProfileActivity.this)) {
                        Utility.createAlert(EditProfileActivity.this, Utility.ERROR_CONNECTION);
                        return false;
                    }

                    dialog = Utility.showProgress(EditProfileActivity.this);

                    FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Utility.hideProgress(dialog);
                            Log.d("EDIT", "DocumentSnapshot successfully written!");
                            Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                            i.putExtra("user", new Gson().toJson(user));
                            startActivity(i);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("EDIT", "Error writing document", e);
                            Utility.createAlert(EditProfileActivity.this, Utility.ERROR_CONNECTION);
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

