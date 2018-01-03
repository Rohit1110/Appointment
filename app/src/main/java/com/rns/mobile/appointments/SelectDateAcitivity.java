package com.rns.mobile.appointments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import model.Appointment;
import model.User;
import utils.FirebaseUtil;
import utils.Utility;

public class SelectDateAcitivity extends AppCompatActivity {

    public static final String COUNTRY_CODE = "+91";
    private Button book;
    private Spinner from, to;
    private TextView appointmentPhone, today, tommarow;
    static TextView setdate;
    private Appointment appointment;
    private DatePicker selectedDate;
    private ProgressDialog dialog;
    private User otherUser;
    ListView availableslots;
    private ArrayAdapter<String> adapter;
    Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);
        book = (Button) findViewById(R.id.btnbook);
        setdate = (TextView) findViewById(R.id.selecteddate);
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String dateString = sdf.format(date);
        setdate.setText(dateString);

        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");*/
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(SelectDateAcitivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectdate = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dates = null;
                        try {
                            dates = sdf.parse(selectdate);
                            if(dates != null) {
                                setdate.setText(new SimpleDateFormat("dd MMM yyyy").format(dates));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });
        today = (TextView) findViewById(R.id.txttoday);
        tommarow = (TextView) findViewById(R.id.txttommarow);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long date = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                String dateString = sdf.format(date);
                setdate.setText(dateString);
            }
        });

        tommarow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                GregorianCalendar calendar = new GregorianCalendar();

//Display the date now:
                Date now = calendar.getTime();
                DateFormat fmt = sdf.getDateInstance(DateFormat.FULL, Locale.US);
                String formattedDate = fmt.format(now);
                System.out.println(formattedDate);

//Advance the calendar one day:
                calendar.add(calendar.DAY_OF_MONTH, 1);
                Date tomorrow = calendar.getTime();
                formattedDate = sdf.format(tomorrow);
                System.out.println(formattedDate);
                setdate.setText(formattedDate);
            }
        });

        appointmentPhone = (TextView) findViewById(R.id.txt_appointment_phone);
        //selectedDate = (DatePicker) findViewById(R.id.datepicker);
        availableslots = (ListView) findViewById(R.id.list_availableslot);
        String[] slots = getResources().getStringArray(R.array.sports_array);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, slots);
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
               /* Intent intent = new Intent(SelectDateAcitivity.this, BookAppointmentActivity.class);
                if (appointment == null) {
                    appointment = new Appointment();
                }
                // appointment.setDate(Utility.getDate(selectedDate));
                intent.putExtra(Utility.INTENT_VAR_APPOINTMENT, new Gson().toJson(appointment));
                if (otherUser != null) {
                    intent.putExtra(Utility.INTENT_VAR_OTHER_USER, new Gson().toJson(otherUser));
                }
                startActivity(intent);*/

                AlertDialog alertDialog = new AlertDialog.Builder(SelectDateAcitivity.this).create(); //Read Update
                alertDialog.setTitle("Book your appointment");
                alertDialog.setMessage("Appointment book succesfully");

                alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                        Intent i=new Intent(SelectDateAcitivity.this,MainActivity.class);
                        startActivity(i);
                    }
                });

                alertDialog.show();
            }
        });
    }





}
