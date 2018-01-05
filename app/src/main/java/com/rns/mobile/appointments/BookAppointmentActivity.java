package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapter.SlotsAdapter;
import model.Appointment;
import model.User;
import utils.FirebaseUtil;
import utils.Utility;

public class BookAppointmentActivity extends AppCompatActivity {

    private Spinner from, to;
    private Appointment appointment;
    private TextView userPhone;
    private TextView selectedDate;
    private Button btnBook;
    private String phoneNumber;
    private ProgressDialog dialog;
    private User otherUser;
    private SlotsAdapter dataAdapter, dataAdapter2;
    private List<String> slotsFromList;
    private List<String> slotsToList;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        from = (Spinner) findViewById(R.id.fromtime);
        to = (Spinner) findViewById(R.id.totime);
        userPhone = (TextView) findViewById(R.id.txt_book_appointment_phone);
        selectedDate = (TextView) findViewById(R.id.txt_book_appointment_date);
        btnBook = (Button) findViewById(R.id.btn_book_appointment);

        phoneNumber = FirebaseUtil.getMobile();
        appointment = Utility.extractAppointment(BookAppointmentActivity.this);

        if (appointment != null) {
            userPhone.setText(appointment.getPhone());
            selectedDate.setText(appointment.getDate());
        }

        slotsFromList = new ArrayList<>(Arrays.asList(Utility.TIME_SLOTS));
        slotsToList = new ArrayList<>(Arrays.asList(Utility.TIME_SLOTS));

        setSlotAdapters();

        otherUser = Utility.extractUser(BookAppointmentActivity.this);

        if (otherUser != null) {

            updateUserSlots(otherUser);
            System.out.println("Fetch User's appointments");
            dialog = Utility.showProgress(BookAppointmentActivity.this);
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(otherUser.getPhone()).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", appointment.getDate()).orderBy("startTime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Utility.hideProgress(dialog);
                    System.out.println("Done fetching users apps!" + task.getResult().size());
                    if(task.isSuccessful()) {
                        List<String> availableSlots = new ArrayList<>(slotsFromList);
                        for(DocumentSnapshot doc: task.getResult()) {
                            Appointment appointment = doc.toObject(Appointment.class);
                            if(appointment != null) {
                                System.out.println(appointment);
                                if(appointment.getStartTime() != null && slotsFromList.contains(appointment.getStartTime())) {
                                    //dataAdapter.remove(appointment.getStartTime());
                                    Integer index = slotsFromList.indexOf(appointment.getStartTime());
                                    index++;
                                    while(index < slotsFromList.size()) {
                                        String slot = slotsFromList.get(index);
                                        if(slot.equals(appointment.getEndTime())) {
                                            break;
                                        }
                                        //remove slots in between for big selections
                                        slotsFromList.remove(slot);
                                    }
                                    slotsFromList.remove(appointment.getStartTime());
                                    slotsToList.remove(appointment.getEndTime());
                                }
                                if(appointment.getEndTime() != null && dataAdapter2.isPresent(appointment.getEndTime())) {
                                    dataAdapter2.remove(appointment.getEndTime());
                                }
                            }
                        }
                    } else {
                        System.out.println("ERROR in fetching users apps =>" + task.getException());
                    }
                    setSlotAdapters();
                }
            });

        } else {

        }

        /*user = Utility.getUserFromSharedPrefs(BookAppointmentActivity.this);

        if(user != null) {

        }*/

        btnBook.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                System.out.println("Book appointment clicked!");
                appointment.setStartTime(from.getSelectedItem().toString());
                appointment.setEndTime(to.getSelectedItem().toString());
                dialog = Utility.showProgress(BookAppointmentActivity.this);
                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber).collection(FirebaseUtil.DOC_APPOINTMENTS).add(appointment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        System.out.println("Completed!!!");
                        Utility.hideProgress(dialog);
                        if (task.isSuccessful()) {
                            System.out.println("Appointment added successfully!!" + task.getResult().getId());
                            Intent i = new Intent(BookAppointmentActivity.this, AppointmentsActivity.class);
                            startActivity(i);
                        } else {
                            System.out.println("Appointment failed to add!!" + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Appointment failed to add => " + e);
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void updateUserSlots(User user) {
        if (user != null && user.getStartTime() != null && user.getEndTime() != null) {
            Set<String> validSlots = new HashSet<>();
            for (String slot : Utility.TIME_SLOTS) {
                if (slot.compareTo(user.getStartTime()) >= 0 && slot.compareTo(user.getEndTime()) <= 0) {
                    validSlots.add(slot);
                }
            }

            slotsFromList = new ArrayList<>(validSlots);
            slotsToList = new ArrayList<>(validSlots);
            Collections.sort(slotsFromList);
            Collections.sort(slotsToList);
            System.out.println("Slots after filtering by other user = >" + validSlots);
            //setSlotAdapters(slotsFromList);
        }
    }

    private void setSlotAdapters() {

        System.out.println("Setting adapters for =>" + slotsFromList);
        System.out.println(slotsToList);

        dataAdapter = new SlotsAdapter(this, android.R.layout.simple_spinner_item, slotsFromList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(dataAdapter);


        dataAdapter2 = new SlotsAdapter(this, android.R.layout.simple_spinner_item, slotsToList);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to.setAdapter(dataAdapter2);
    }

}
