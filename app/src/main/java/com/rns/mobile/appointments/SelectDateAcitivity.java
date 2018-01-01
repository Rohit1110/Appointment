package com.rns.mobile.appointments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import model.Appointment;
import utils.Utility;

public class SelectDateAcitivity extends AppCompatActivity {
    private Button book;
    private Spinner from, to;
    private TextView appointmentPhone;
    private Appointment appointment;
    private DatePicker selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);
        book = (Button) findViewById(R.id.btnbook);
        appointmentPhone = (TextView) findViewById(R.id.txt_appointment_phone);
        selectedDate = (DatePicker) findViewById(R.id.datepicker);

        appointment = Utility.extractAppointment(SelectDateAcitivity.this);

        if(appointment != null) {
            appointmentPhone.setText(appointment.getPhone());
        }

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectDateAcitivity.this, BookAppointmentActivity.class);
                if(appointment != null) {
                    appointment = new Appointment();
                }
                appointment.setDate(Utility.getDate(selectedDate));
                intent.putExtra("appointment", new Gson().toJson(appointment));
                startActivity(intent);
            }
        });
    }

}
