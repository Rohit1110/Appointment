package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import model.Appointment;
import model.User;
import utils.FirebaseUtil;
import utils.Utility;

public class SelectDateAcitivity extends AppCompatActivity {

    public static final String COUNTRY_CODE = "+91";
    private Button book;
    private Spinner from, to;
    private TextView appointmentPhone;
    private Appointment appointment;
    private DatePicker selectedDate;
    private ProgressDialog dialog;
    private User otherUser;
    ListView availableslots;
    private ArrayAdapter<String> adapter;
    String[] available={"10.30-11.00","11.00-11.30","11.30-12.0"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);
        book = (Button) findViewById(R.id.btnbook);
        appointmentPhone = (TextView) findViewById(R.id.txt_appointment_phone);
        //selectedDate = (DatePicker) findViewById(R.id.datepicker);
        availableslots=(ListView)findViewById(R.id.list_availableslot);
       // String[] Slots = getResources().getStringArray();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, available);
        availableslots.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        availableslots.setAdapter(adapter);
        appointment = Utility.extractAppointment(SelectDateAcitivity.this);

        if (appointment != null && appointment.getPhone() != null) {
            appointmentPhone.setText(appointment.getPhone());
            if (!appointment.getPhone().trim().contains("+")) {
                appointment.setPhone(COUNTRY_CODE + appointment.getPhone());
                dialog = Utility.showProgress(SelectDateAcitivity.this);
                System.out.println("## Loading profile for .. " + appointment.getPhone() + " dialog=" + dialog);
                FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        System.out.println("## Done profile .." + task.getResult().exists());
                        Utility.hideProgress(dialog);
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            otherUser = task.getResult().toObject(User.class);
                            otherUser.setPhone(appointment.getPhone());
                            appointment.setName(otherUser.getFirstName() + " " + otherUser.getLastName());
                            System.out.println("Appointment for =>" + appointment.getName());
                            appointmentPhone.setText(appointment.getName());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("## Error in profile .." + e);
                        e.printStackTrace();
                    }
                });
            }
        }

        if (appointment != null && appointment.getName() != null) {
            appointmentPhone.setText(appointment.getName());
        }

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectDateAcitivity.this, BookAppointmentActivity.class);
                if (appointment == null) {
                    appointment = new Appointment();
                }
               // appointment.setDate(Utility.getDate(selectedDate));
                intent.putExtra(Utility.INTENT_VAR_APPOINTMENT, new Gson().toJson(appointment));
                if(otherUser != null) {
                    intent.putExtra(Utility.INTENT_VAR_OTHER_USER, new Gson().toJson(otherUser));
                }
                startActivity(intent);
            }
        });
    }

}
