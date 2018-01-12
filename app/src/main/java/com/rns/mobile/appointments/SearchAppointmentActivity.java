package com.rns.mobile.appointments;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.collect.Ordering;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import adapter.ContactListAdapter;
import decorator.ContactDividerItemDecoration;
import decorator.SimpleDividerItemDecoration;
import model.Appointment;
import model.UserContact;
import utils.Utility;

public class SearchAppointmentActivity extends AppCompatActivity {
    private Button next;
    private EditText search;
    private String TAG = "Appointment Search";
    private static final String[] COUNTRIES = new String[]{"Dentist", "Aurtho", "homeopathi", "entc", "Aurvedik"};
    RecyclerView recyclerView_contact;
    private ContactListAdapter adapter;
    private List<UserContact> list;
    private List<UserContact> filterList;
    private UserContact a;
    //private ProgressDialog dialog;
    private Activity ctx;
    private boolean hideicon = true;
    private UserContact selectedContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //isReadContactPermissionGranted();
        ctx = this;

        setContentView(R.layout.activity_search_appointment);


        System.out.println("### SEARCH ACTIVITY LOADED ###");

        //  next = (Button) findViewById(R.id.btnnxt);
        search = (EditText) findViewById(R.id.editsearch);
        recyclerView_contact = (RecyclerView) findViewById(R.id.contact_reclyclerview);
        list = new CopyOnWriteArrayList<>();
        filterList = new CopyOnWriteArrayList<>();
        adapter = new ContactListAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView_contact.addItemDecoration(new ContactDividerItemDecoration(this));
        recyclerView_contact.setLayoutManager(mLayoutManager);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String text = search.getText().toString().toLowerCase(Locale.getDefault());

                filter(text);
            }


            @Override
            public void afterTextChanged(Editable s) {

                if (isNumeric(s.toString())) {


                    if (search.getText().toString().length() >= 10) {
                        hideicon = false;
                        invalidateOptionsMenu();
                    }
                }
            }
        });


        // Interface implementation.
        /*adapter.setOnRecyclerViewItemClickListener(new ContactListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClicked(CharSequence text) {
                Intent intent = new Intent(SearchAppointmentActivity.this, SelectDateAcitivity.class);
                Appointment appointment = new Appointment();
                appointment.setPhone("+918956711498");
                intent.putExtra("appointment", new Gson().toJson(appointment));
                startActivity(intent);
            }
        });*/
        recyclerView_contact.setAdapter(adapter);


        recyclerView_contact.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView_contact, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
              /*  Toast.makeText(SearchAppointmentActivity.this, "Single Click on position        :" + position,
                        Toast.LENGTH_SHORT).show();*/
                selectedContact = filterList.get(position);
                /*Toast.makeText(SearchAppointmentActivity.this, "Single Click on position        :" + selectedContact.getPhone(),
                        Toast.LENGTH_SHORT).show();*/
                search.setText(selectedContact.getPhone());
                hideicon = false;
                invalidateOptionsMenu();
            }

            @Override
            public void onLongClick(View view, int position) {
                // Toast.makeText(SearchAppointmentActivity.this, "Long press on position :" + position,
                // Toast.LENGTH_LONG).show();
            }
        }));


      /*  next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchAppointmentActivity.this, SelectDateAcitivity.class);
                Appointment appointment = new Appointment();
                appointment.setPhone(search.getText().toString());
                intent.putExtra("appointment", new Gson().toJson(appointment));
                startActivity(intent);
            }
        });*/


        if (!Utility.checkcontactPermission(SearchAppointmentActivity.this)) {
            return;
        }
        new FetchContact().execute();


        //getContactList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (hideicon) {
            MenuItem item = menu.findItem(R.id.actionok);
            item.setVisible(false);
            this.invalidateOptionsMenu();
        } else {
            MenuItem item = menu.findItem(R.id.actionok);
            item.setVisible(true);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionok:
                Intent intent = new Intent(SearchAppointmentActivity.this, SelectDateAcitivity.class);
                Appointment appointment = new Appointment();
                String phone = Utility.removeAllSpaces(search.getText().toString());
                //Select last 10 digits to avoid 0 or anything else in phone number
                if (phone.length() > Utility.PHONE_MAX_LENGTH) {
                    phone = phone.substring(phone.length() - Utility.PHONE_MAX_LENGTH);
                }
                if (phone != null && !phone.trim().contains(Utility.COUNTRY_CODE)) {
                    phone = Utility.COUNTRY_CODE + phone;
                }
                if (selectedContact != null && selectedContact.getName() != null) {
                    appointment.setName(selectedContact.getName());
                }
                appointment.setPhone(phone);
                System.out.println("Selected appointment is =>" + appointment);
                intent.putExtra(Utility.INTENT_VAR_APPOINTMENT, new Gson().toJson(appointment));
                startActivity(intent);
                return true;

        }
        return false;
    }

    private void getContactList() {


        System.out.println("### FETCHING CONTACTS ..");


        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                        //String  test = exam.getTestName().replaceAll("\\p{P}","");
                        a = new UserContact(name, phoneNo.replaceAll("\\p{P}", ""));
                        list.add(a);
                        //adapter.notifyDataSetChanged();


                    }
                    filterList.addAll(list);
                    pCur.close();
                }
            }

        }
        if (cur != null) {
            cur.close();
        }
        System.out.println("### DONE FETCHING CONTACTS ..");
        //Utility.hideProgress(dialog);
    }


    public class FetchContact extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(SearchAppointmentActivity.this);
            dialog.setMessage("Loading ..");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getContactList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            //Utility.hideProgress(dialog);
            dialog.dismiss();
        }
    }


    // Do Search...
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    hideicon = true;
                    invalidateOptionsMenu();

                    filterList.addAll(list);


                } else {
                    // Iterate in the original List and add it to filter list...
                    for (UserContact item : list) {
                        if (item.getName().toLowerCase().contains(text.toLowerCase()) || comparePhone(item, text)) {
                            // Adding Matched items
                            filterList.add(item);
                        }
                    }
                }

                // Set on UI Thread
                (SearchAppointmentActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        adapter = new ContactListAdapter(SearchAppointmentActivity.this, filterList);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(SearchAppointmentActivity.this, 1);
                        recyclerView_contact.setLayoutManager(mLayoutManager);
                        recyclerView_contact.setAdapter(adapter);


                    }
                });

            }
        }).start();

    }

    private boolean comparePhone(UserContact item, String text) {
        String phone = item.getPhone().toLowerCase();
        if (phone.contains(text.toLowerCase())) {
            return true;
        }
        phone = Utility.removeAllSpaces(phone);
        if (phone.contains(text.toLowerCase())) {
            return true;
        }
        return false;
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(.\\d+)?");
    }
}



