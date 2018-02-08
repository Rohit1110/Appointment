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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import adapter.ContactListAdapter;
import adapter.InviteContactListAdapter;
import decorator.ContactDividerItemDecoration;
import model.InviteContact;
import model.User;
import model.UserContact;
import utils.Utility;

public class InviteActivity extends AppCompatActivity {
    private String TAG = "Invite Activity";
    private InviteContact a;
    private List<InviteContact> list;
    private RecyclerView recyclerView;
    private InviteContactListAdapter adapter;
    Button btnSend;
    EditText search;
    private User user;
    String userName;
    private List<InviteContact> filterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        btnSend = (Button) findViewById(R.id.sendinvitebtn);
        search = (EditText) findViewById(R.id.txt_contact_search);

        String userJson = getIntent().getStringExtra("user");

        if (userJson != null) {
            user = new Gson().fromJson(userJson, User.class);

        }

        if (user != null) {
            userName = user.getFirstName() + " " + user.getLastName();

        }


        list = new CopyOnWriteArrayList<>();
        filterList = new CopyOnWriteArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.invite_reclyclerview);
        adapter = new InviteContactListAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.addItemDecoration(new ContactDividerItemDecoration(this));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


               /* String text = search.getText().toString().toLowerCase(Locale.getDefault());

                filter(text);*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());

                filter(text);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = "";
                List<InviteContact> stList = ((InviteContactListAdapter) adapter)
                        .getStudentist();
                System.out.println("Size of checked student "+ list.size());

                for (int i = 0; i < list.size(); i++) {
                    //InviteContact singleStudent = stList.get(i);
                    InviteContact singleStudent = list.get(i);
                    if (singleStudent.isSelected() == true) {

                        data = validatenumber(singleStudent.getPhone().toString().replaceAll("\\s", "")) + "," + data;
                        System.out.println("validate number "+validatenumber(singleStudent.getPhone().toString().replaceAll("\\s", "")));


                        //new InviteSMSTask(Utility.NOTIFICATION_TYPE_NEW, methodchar(singleStudent.getPhone().toString()).replaceAll("\\s", ""), InviteActivity.this, userName).execute();


                       // Toast.makeText( InviteActivity.this, " " +singleStudent.getName() + " " +singleStudent.getPhone() + " " +singleStudent.isSelected(), Toast.LENGTH_SHORT).show();
                    }
                }

              new InviteSMSTask(Utility.NOTIFICATION_TYPE_NEW, methodchar(data).replaceAll("\\s", ""), InviteActivity.this, userName).execute();

                System.out.println("Selected Contact..." + methodchar(data));

            }

        });


        if (!Utility.checkcontactPermission(InviteActivity.this)) {
            return;
        }
        Log.v("SSSSSSSSSSSS", "fetch contact");
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
                a = new InviteContact(name, phone.replaceAll("\\p{P}", ""),false);
                list.add(a);
                //adapter.notifyDataSetChanged();
            }

            System.out.println("Size of " + list.size());


            filterList.addAll(list);

        }
        if (cur != null) {
            cur.close();
        }
        System.out.println("### DONE FETCHING CONTACTS ..");
        //Utility.hideProgress(dialog);
    }


    public String methodchar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == 'x') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }


    public void filter(final String text) {
    // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
        @Override
        public void run() {

            try {
                // Clear the filter list
                filterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {



                    filterList.addAll(list);


                } else {
                    // Iterate in the original List and add it to filter list...
                    for (InviteContact item : list) {
                        if (item.getName().toLowerCase().contains(text.toLowerCase()) || comparePhone(item, text)) {
                            // Adding Matched items
                            filterList.add(item);
                        }

                    }
                }

                // Set on UI Thread
                (InviteActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        adapter = new InviteContactListAdapter(InviteActivity.this, filterList);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(InviteActivity.this, 1);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(adapter);



                    }
                });
            } catch (Exception e) {
                System.out.println("Error in filter contacts");
                e.printStackTrace();
            }


        }
    }).start();

}


    private boolean comparePhone(InviteContact item, String text) {
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

    private String validatenumber(String word)
    {
        if (word.length() == 10) {
            return word;
        } else if (word.length() > 10) {
            return word.substring(word.length()-10,word.length());
        } else {
            // whatever is appropriate in this case
            throw new IllegalArgumentException("word has less than 3 characters!");
        }
    }
}
