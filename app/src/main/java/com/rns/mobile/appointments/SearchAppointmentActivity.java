package com.rns.mobile.appointments;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import adapter.AppointmentsAdapter;
import adapter.ContactListAdapter;
import model.Appointment;
import model.Usercontact;
import utils.PermissionUtil;
import utils.Utility;

import static android.content.ContentValues.TAG;

public class SearchAppointmentActivity extends AppCompatActivity {
    private Button next;
    private AutoCompleteTextView search;
    private String TAG="Appointment Search";
    private static final String[] COUNTRIES = new String[]{"Dentist", "Aurtho", "homeopathi", "entc", "Aurvedik"};
    RecyclerView recyclerView_contact;
    private ContactListAdapter adapter;
    private List<Usercontact> list;
    private Usercontact a;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //isReadContactPermissionGranted();
        if(isReadContactPermissionGranted()) {
            setContentView(R.layout.activity_search_appointment);
            next = (Button) findViewById(R.id.btnnxt);
            search = (AutoCompleteTextView) findViewById(R.id.editsearch);
            recyclerView_contact = (RecyclerView) findViewById(R.id.contact_reclyclerview);
            list = new ArrayList<>();
            Utility.showProgress(dialog, SearchAppointmentActivity.this);
            adapter = new ContactListAdapter(this, list);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView_contact.setLayoutManager(mLayoutManager);
            recyclerView_contact.setAdapter(adapter);


            getContactList();


        }else{
            isReadContactPermissionGranted();
        }



            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SearchAppointmentActivity.this, SelectDateAcitivity.class);
                    Appointment appointment = new Appointment();
                    appointment.setPhone(search.getText().toString());
                    intent.putExtra("appointment", new Gson().toJson(appointment));
                    startActivity(intent);
                }
            });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionUtil.verifyPermissions(grantResults)) {
            // All required permissions have been granted, display contacts fragment.
        /*    Snackbar.make(mLayout, "permission granted",
                    Snackbar.LENGTH_SHORT)
                    .show();*/
            //Toast.makeText(PDfViewer.this,"Permission Grant",Toast.LENGTH_LONG).show();
        } else {
            /*Log.i(TAG, "Contacts permissions were NOT granted.");
            Snackbar.make(mLayout, "permissions were NOT granted",
                    Snackbar.LENGTH_SHORT)
                    .show();*/
           // Toast.makeText(SearchAppointmentActivity.this,"Permission not Grant",Toast.LENGTH_LONG).show();
        }
    }

    private void getContactList() {
        Utility.hideProgress(dialog);
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                         a = new Usercontact(name, phoneNo);
                        list.add(a);
                        adapter.notifyDataSetChanged();


                    }
                    pCur.close();
                }
            }

        }
        if(cur!=null){
            cur.close();
        }
    }

    public  boolean isReadContactPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}
