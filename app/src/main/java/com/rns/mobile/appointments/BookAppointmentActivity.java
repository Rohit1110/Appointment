package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import model.Appointment;
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

        if(appointment != null) {
            userPhone.setText(appointment.getPhone());
            selectedDate.setText(appointment.getDate());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Utility.TIME_SLOTS_FROM);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(dataAdapter);


        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Utility.TIME_SLOTS_TO);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to.setAdapter(dataAdapter2);

        btnBook.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                System.out.println("Book appointment clicked!");
                appointment.setStartTime(from.getSelectedItem().toString());
                appointment.setEndTime(to.getSelectedItem().toString());
                Utility.showProgress(dialog, BookAppointmentActivity.this);
                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(phoneNumber)
                        .collection(FirebaseUtil.DOC_APPOINTMENTS).add(appointment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        System.out.println("Completed!!!");
                        Utility.hideProgress(dialog);
                        if(task.isSuccessful()) {
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
}
