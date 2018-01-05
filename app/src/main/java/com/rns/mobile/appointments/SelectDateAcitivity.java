package com.rns.mobile.appointments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import model.Appointment;
import model.User;
import utils.FirebaseUtil;
import utils.Utility;

public class SelectDateAcitivity extends AppCompatActivity {

    private Button book;
    private Spinner from, to;
    private TextView appointmentPhone, today, tommarow;
    private TextView setdate;
    private Appointment appointment;
    private DatePicker selectedDate;
    private ProgressDialog dialog;
    private User otherUser;
    private ListView availableSlotsListView;
    private ArrayAdapter<String> adapter;
    Calendar myCalendar = Calendar.getInstance();
    private List<String> slotsList;
    private Set<String> blockedSlots;
    private String userPhone;
    private Set<String> selectedSlots;
    private List<String> filteredSlots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);
        // book = (Button) findViewById(R.id.btnbook);
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
                        String selectedDateString = Utility.createDate(dayOfMonth, monthOfYear, year);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dates = null;
                        try {
                            dates = sdf.parse(selectedDateString);
                            if (dates != null) {
                                setdate.setText(new SimpleDateFormat("dd MMM yyyy").format(dates));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //
                        if (selectedDateString != null && userPhone != null) {
                            appointment.setDate(selectedDateString);
                            updateUserAppointments();
                        }
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
        availableSlotsListView = (ListView) findViewById(R.id.list_availableslot);

        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, slotsList);
        availableSlotsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        availableSlotsListView.setAdapter(adapter);
        availableSlotsListView.setOnItemClickListener(new SlotsSelected());

        appointment = Utility.extractAppointment(SelectDateAcitivity.this);

        blockedSlots = new HashSet<String>();
        slotsList = Arrays.asList(Utility.TIME_SLOTS);
        userPhone = FirebaseUtil.getMobile();
        selectedSlots = new HashSet<>();
        System.out.println("Slot list initialized = " + slotsList);

        if (appointment != null && appointment.getName() != null) {
            appointmentPhone.setText(appointment.getName());
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                //Book appointment
                bookAppointment();
                /*Intent intent = new Intent(SelectDateAcitivity.this, MainActivity.class);
                startActivity(intent);
                finish();*/
                return (true);
        }
        return false;

    }

    private void bookAppointment() {
        System.out.println("Book appointment clicked!");
        prepareAppointmentSlots();
        dialog = Utility.showProgress(SelectDateAcitivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(userPhone).collection(FirebaseUtil.DOC_APPOINTMENTS).add(appointment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                System.out.println("Completed Booking for user!!!" + userPhone);
                if (task.isSuccessful()) {
                    System.out.println("Appointment added successfully!!" + task.getResult().getId());
                    Appointment otherUserAppointment = new Appointment();
                    User currentUser = Utility.getUserFromSharedPrefs(SelectDateAcitivity.this);
                    if (currentUser != null) {
                        otherUserAppointment.setName(Utility.getStringValue(currentUser.getFirstName()) + " " + Utility.getStringValue(currentUser.getLastName()));
                    }
                    otherUserAppointment.setPhone(userPhone);
                    otherUserAppointment.setDate(appointment.getDate());
                    otherUserAppointment.setStartTime(appointment.getStartTime());
                    otherUserAppointment.setEndTime(appointment.getEndTime());
                    FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).collection(FirebaseUtil.DOC_APPOINTMENTS).add(otherUserAppointment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            System.out.println("Completed booking for other user!!!" + appointment.getPhone());
                            Utility.hideProgress(dialog);
                            if (task.isSuccessful()) {
                                System.out.println("Appointment added successfully!!" + task.getResult().getId());

                            } else {
                                System.out.println("Appointment failed to add!!" + task.getException());
                            }
                            goToHome();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Appointment failed to add => " + e);
                            e.printStackTrace();
                        }
                    });


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

    private void goToHome() {
        Intent i = new Intent(SelectDateAcitivity.this, AppointmentsActivity.class);
        startActivity(i);
        finish();
    }


    private void updateUserSlots(User user) {
        if (user != null && user.getStartTime() != null && user.getEndTime() != null) {
            Set<String> validSlots = new HashSet<>();
            for (String slot : Utility.TIME_SLOTS) {
                if (slot.compareTo(user.getStartTime()) >= 0 && slot.compareTo(user.getEndTime()) <= 0) {
                    validSlots.add(slot);
                }
            }

            slotsList = new ArrayList<>(validSlots);
            //slotsToList = new ArrayList<>(validSlots);
            Collections.sort(slotsList);
            //Collections.sort(slotsToList);
            System.out.println("Slots after filtering by other user = >" + validSlots);
            //setSlotAdapters(slotsFromList);
        }

    }

    private void updateUserAppointments() {
        dialog = Utility.showProgress(SelectDateAcitivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(userPhone).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", appointment.getDate()).orderBy("startTime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Utility.hideProgress(dialog);
                System.out.println("Done fetching users apps for " + userPhone + " == " + task.getResult().size() + " Success:" + task.isSuccessful());
                blockSlots(task);
                blockOtherUserSlots();
            }
        });
    }

    private void blockSlots(@NonNull Task<QuerySnapshot> task) {
        if (task.getResult() != null && task.getResult().size() > 0) {
            System.out.println("Blocking slots for user");
            for (DocumentSnapshot doc : task.getResult()) {
                if (!doc.exists()) {
                    continue;
                }
                Appointment appointment = doc.toObject(Appointment.class);
                if (appointment != null) {
                    System.out.println(appointment);
                    if (appointment.getStartTime() != null && slotsList.contains(appointment.getStartTime())) {
                        //dataAdapter.remove(appointment.getStartTime());
                        System.out.println("Blocking slots for .." + appointment);
                        Integer index = slotsList.indexOf(appointment.getStartTime());
                        index++;
                        while (index < slotsList.size()) {
                            String blockedSlot = slotsList.get(index - 1) + Utility.SLOT_APPENDER + slotsList.get(index);
                            blockedSlots.add(blockedSlot);
                            if (slotsList.get(index).compareTo(appointment.getEndTime()) >= 0) {
                                break;
                            }
                            index++;
                        }

                    }

                }
            }
            System.out.println("Blocked slots after user =>" + blockedSlots);
        } else {
            System.out.println("ERROR in fetching users apps =>" + task.getException());
        }
        System.out.println("Blocked slots after user =>" + blockedSlots);
    }

    private void blockOtherUserSlots() {
        System.out.println("Blocking other user slots .." + appointment);
        if (appointment != null && appointment.getPhone() != null) {
            appointmentPhone.setText(appointment.getPhone());
            appointmentPhone.setText(appointment.getName());
            if (!appointment.getPhone().trim().contains("+")) {
                appointment.setPhone(Utility.COUNTRY_CODE + appointment.getPhone());
            }
            System.out.println("## Loading profile for .. " + appointment.getPhone() + " dialog=" + dialog);
            dialog = Utility.showProgress(SelectDateAcitivity.this);
            FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Utility.hideProgress(dialog);
                    System.out.println("## Done profile .." + task.getResult().exists());
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        otherUser = task.getResult().toObject(User.class);
                        otherUser.setPhone(appointment.getPhone());
                        if (appointment.getName() == null) {
                            appointment.setName(otherUser.getFirstName() + " " + otherUser.getLastName());
                            appointmentPhone.setText(appointment.getName());
                        }
                        System.out.println("Appointment for =>" + appointment.getName());
                        //Update slotsList based on this other users available slotsList
                        updateUserSlots(otherUser);
                        updateOtherUserAppointments();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("## Error in blocking slots .." + e);
                    e.printStackTrace();
                }
            });


        } else {
            updateAvailableSlots();
        }
    }

    private void updateOtherUserAppointments() {
        dialog = Utility.showProgress(SelectDateAcitivity.this);
        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(appointment.getPhone()).collection(FirebaseUtil.DOC_APPOINTMENTS).whereEqualTo("date", appointment.getDate()).orderBy("startTime").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Utility.hideProgress(dialog);
                System.out.println("Done fetching users apps!" + task.getResult().size());
                blockSlots(task);
                updateAvailableSlots();
            }
        });
    }

    private void updateAvailableSlots() {
        Set<String> availableSlots = new HashSet<>();
        for (String slot : slotsList) {
            int index = slotsList.indexOf(slot);
            if ((index + 1) < slotsList.size()) {
                String slotString = slot + Utility.SLOT_APPENDER + slotsList.get(index + 1);
                if (blockedSlots.contains(slotString)) {
                    continue;
                }
                availableSlots.add(slotString);
                System.out.println(" -- Added slot ---" + availableSlots);
            }
        }
        filteredSlots = new ArrayList<>(availableSlots);
        Collections.sort(filteredSlots);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, filteredSlots);
        availableSlotsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        availableSlotsListView.setAdapter(adapter);

        availableSlotsListView.setOnItemClickListener(new SlotsSelected());

    }

    public class SlotsSelected implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            CheckedTextView ctv = (CheckedTextView) arg1;
            String selectedSlot = ctv.getText().toString();
            if (ctv.isChecked()) {
                if(validateSlots(selectedSlot)) {
                    selectedSlots.add(selectedSlot);
                    System.out.println(selectedSlots);
                    Toast.makeText(SelectDateAcitivity.this, "now it is unchecked", Toast.LENGTH_SHORT).show();
                } else {
                    ctv.setChecked(false);
                }

            } else {
                selectedSlots.remove(selectedSlot);
                System.out.println(selectedSlots);
                Toast.makeText(SelectDateAcitivity.this, "now it is checked", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateSlots(String selectedSlot) {
        if (selectedSlots == null || selectedSlots.size() == 0) {
            return true;
        }
        //Validate that the current slot is only one unit ahead or behind the max and min selected Slots
        Integer selectedStart = slotsList.indexOf(selectedSlot.split(Utility.SLOT_APPENDER)[0]);
        Integer selectedEnd = slotsList.indexOf(selectedSlot.split(Utility.SLOT_APPENDER)[1]);
        List<String> list = new ArrayList<>(selectedSlots);
        Collections.sort(list);
        Integer startIndex = slotsList.indexOf(list.get(0).split(Utility.SLOT_APPENDER)[0]);
        Integer endIndex = slotsList.indexOf(list.get(list.size() - 1).split(Utility.SLOT_APPENDER)[1]);
        //Check if current slot is just before the selected first
        Integer diff = (startIndex - selectedEnd);
        if (Math.abs(diff) <= 0) {
            return true;
        }
        diff = (selectedStart - endIndex);
        if (Math.abs(diff) <= 0) {
            return true;
        }
        Utility.createAlert(SelectDateAcitivity.this, "Incorrect slot selection! Please select continuous slots.");
        return false;
    }

    public void prepareAppointmentSlots() {
        if (selectedSlots == null || selectedSlots.size() == 0) {
            return;
        }
        List<String> list = new ArrayList<>(selectedSlots);
        Collections.sort(list);
        appointment.setStartTime(list.get(0).split(Utility.SLOT_APPENDER)[0]);
        appointment.setEndTime(list.get(list.size() - 1).split(Utility.SLOT_APPENDER)[1]);

        System.out.println("Appointment timings set as ==> " + appointment);

    }

}
