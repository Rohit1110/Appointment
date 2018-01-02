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

        dataAdapter = new SlotsAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(Utility.TIME_SLOTS));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(dataAdapter);


        dataAdapter2 = new SlotsAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(Utility.TIME_SLOTS));
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to.setAdapter(dataAdapter2);

        otherUser = Utility.extractUser(BookAppointmentActivity.this);

        if (otherUser != null) {

            updateSlots(dataAdapter, dataAdapter2);
            System.out.println("Fetch User's appointments");
            dialog = Utility.showProgress(BookAppointmentActivity.this);
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(otherUser.getPhone()).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", appointment.getDate()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Utility.hideProgress(dialog);
                    System.out.println("Done fetching users apps!" + task.isSuccessful());
                    if(task.isSuccessful()) {
                        for(DocumentSnapshot doc: task.getResult()) {
                            Appointment appointment = doc.toObject(Appointment.class);
                            if(appointment != null) {
                                if(appointment.getStartTime() != null && dataAdapter.isPresent(appointment.getStartTime())) {
                                    dataAdapter.remove(appointment.getStartTime());
                                }
                                if(appointment.getEndTime() != null && dataAdapter2.isPresent(appointment.getEndTime())) {
                                    dataAdapter2.remove(appointment.getEndTime());
                                }
                            }
                        }
                    } else {
                        System.out.println("ERROR in fetching users apps =>" + task.getException());
                    }
                }
            });

        }

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

    private void updateSlots(SlotsAdapter adapter1, SlotsAdapter adapter2) {
        if (otherUser != null && otherUser.getStartTime() != null && otherUser.getEndTime() != null) {
            Set<String> validSlots = new HashSet<>();
            for (String slot : Utility.TIME_SLOTS) {
                if (slot.compareTo(otherUser.getStartTime()) >= 0 && slot.compareTo(otherUser.getEndTime()) <= 0) {
                    validSlots.add(slot);
                }
            }

            /*for (String slot : Utility.TIME_SLOTS_TO) {
                if (slot.compareTo(otherUser.getEndTime()) <= 0) {
                    validSlots.add(slot);
                }
            }*/
            adapter1.clear();
            adapter2.clear();
            ArrayList<String> slotsList = new ArrayList<>(validSlots);
            Collections.sort(slotsList);
            adapter1.addItems(slotsList);
            adapter2.addItems(slotsList);
            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
            from.setAdapter(adapter1);
            to.setAdapter(adapter2);
            System.out.println("Slots after filtering by other user = >" + validSlots.size() + " -- " + adapter1.getCount() + " Data:" + slotsList);

        }
    }

}
