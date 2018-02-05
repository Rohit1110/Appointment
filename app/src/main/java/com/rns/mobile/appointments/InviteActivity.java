package com.rns.mobile.appointments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import adapter.ContactListAdapter;
import adapter.InviteContactListAdapter;
import decorator.ContactDividerItemDecoration;
import model.InviteContact;
import model.UserContact;
import utils.Utility;

public class InviteActivity extends AppCompatActivity {
    private String TAG = "Invite Activity";
    private InviteContact a;
    private List<InviteContact> list;
    private RecyclerView recyclerView;
    private InviteContactListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);


        list = new ArrayList<>();
        recyclerView=(RecyclerView)findViewById(R.id.invite_reclyclerview);
        adapter = new InviteContactListAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.addItemDecoration(new ContactDividerItemDecoration(this));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);



       /* if (!Utility.checkcontactPermission(InviteActivity.this)) {
            return;
        }*/
        Log.v("SSSSSSSSSSSS","fetch contact");
        new FetchInviteContact().execute();
    }

    public class FetchInviteContact extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("On preexcute");

            dialog = new ProgressDialog(InviteActivity.this);
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


    private void getContactList() {


        System.out.println("### FETCHING CONTACTS ..");


        ContentResolver cr = getContentResolver();

        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


        //Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                /*String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
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
                }*/
                String name = cur.getString(0);
                String phone = cur.getString(1);
                Log.i(TAG, "Name: " + name);
                Log.i(TAG, "Phone Number: " + phone);
                //String  test = exam.getTestName().replaceAll("\\p{P}","");
                a = new InviteContact(name, phone.replaceAll("\\p{P}", ""));
                list.add(a);
                //adapter.notifyDataSetChanged();
            }


            //filterList.addAll(list);

        }
        if (cur != null) {
            cur.close();
        }
        System.out.println("### DONE FETCHING CONTACTS ..");
        //Utility.hideProgress(dialog);
    }
}
