package com.rns.mobile.appointments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapter.ContactListAdapter;
import model.Appointment;
import model.Usercontact;
import utils.PermissionUtil;
import utils.Utility;

public class SearchAppointmentActivity extends AppCompatActivity {
    private Button next;
    private EditText search;
    private String TAG = "Appointment Search";
    private static final String[] COUNTRIES = new String[]{"Dentist", "Aurtho", "homeopathi", "entc", "Aurvedik"};
    RecyclerView recyclerView_contact;
    private ContactListAdapter adapter;
    private List<Usercontact> list;
    private ArrayList<Usercontact> filterList;
    private Usercontact a;
    //private ProgressDialog dialog;
    private Activity ctx;


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
        list = new ArrayList<>();
        filterList=new ArrayList<>();
        adapter = new ContactListAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
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

            }
        });

        // Interface implementation.
        adapter.setOnRecyclerViewItemClickListener(new ContactListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClicked(CharSequence text) {
                Intent intent = new Intent(SearchAppointmentActivity.this, SelectDateAcitivity.class);
                Appointment appointment = new Appointment();
                appointment.setPhone(text.toString());
                intent.putExtra("appointment", new Gson().toJson(appointment));
                startActivity(intent);
            }
        });
        recyclerView_contact.setAdapter(adapter);


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

        new FetchContact().execute();


        //getContactList();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
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
                        a = new Usercontact(name, phoneNo);
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

                    filterList.addAll(list);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Usercontact item : list) {
                        if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getPhone().toLowerCase().contains(text.toLowerCase())) {
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

}


