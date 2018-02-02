package com.rns.mobile.appointments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.UserContact;
import utils.Utility;

/**
 * Created by umesh on 25-02-2017.
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final int REQUEST_INVITE = 1;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext, btnSend;
    private PreferenceManager prefManager;
    public static final int REQUEST_CODE_PICK_CONTACT = 1;
    public static final int MAX_PICK_CONTACT = 10;
    private ArrayList<String> contacts;
    private List<String> data = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PreferenceManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.slide_screen1,
                R.layout.slide_screen2,
                R.layout.slide_screen3,
                R.layout.slide_screen4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        System.out.println("size of dots " + dots.length);

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);


            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                btnSend = (Button) viewPager.findViewById(R.id.btnsend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utility.isReadContactPermissionGranted(WelcomeActivity.this);
                        if (!Utility.checkcontactPermission(WelcomeActivity.this)) {
                            return;
                        }
                        new FetchContact().execute();

                       // onInviteClicked();
                        //getContactsList(WelcomeActivity.this,WelcomeActivity.this);
                    }
                });

                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);

            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static class Contact {
        String cname;
        String cnumber;
        public Contact(String name, String phoneNumber) {
            this.cname=name;
            this.cnumber=phoneNumber;
            System.out.println("Contact "+cname+" number "+phoneNumber);
        }

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        public String getCnumber() {
            return cnumber;
        }

        public void setCnumber(String cnumber) {
            this.cnumber = cnumber;
        }
    }


    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private static ArrayList<Contact> getContactsList(Context context,Activity activity) {
        Utility.isReadContactPermissionGranted(activity);
        if (!Utility.checkcontactPermission(activity)) {
            return null;
        }

        ArrayList<Contact> contacts=new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(new Contact(name,phoneNumber));
        }

        //new InviteSMSTask(Utility.NOTIFICATION_TYPE_NEW, contacts).execute();
        phones.close();
        return contacts;
    }

    private void onInviteClicked() {
        /*Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_INVITE);*/

       /*Intent phonebookIntent = new Intent("intent.action.INTERACTION_TOPMENU");
        phonebookIntent.putExtra("additional", "phone-multi");
       phonebookIntent.putExtra("maxRecipientCount", MAX_PICK_CONTACT);
        phonebookIntent.putExtra("FromMMS", true);
        startActivityForResult(phonebookIntent, REQUEST_CODE_PICK_CONTACT);*/

       /* Utility.isReadContactPermissionGranted(WelcomeActivity.this);
        if (!Utility.checkcontactPermission(WelcomeActivity.this)) {
            return;
        }*/


        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if("1".equals(hasPhone) || Boolean.parseBoolean(hasPhone)) {
                // You know it has a number so now query it like this
                Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int itype = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                    final boolean isMobile =
                            itype == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ||
                                    itype == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE;

                    // Do something here with 'phoneNumber' such as saving into
                    // the List or Array that will be used in your 'ListView'.
                    System.out.println("Mobile "+phoneNumber.replaceAll("\\p{P}", "").trim()+ "  isMobile "+isMobile);

                    new InviteSMSTask(Utility.NOTIFICATION_TYPE_NEW,phoneNumber.replaceAll("\\p{P}", "").trim()).execute();
                }

                phones.close();
            }
        }





      /*  Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.app_lilk)))


                .build();
        startActivityForResult(intent, REQUEST_INVITE);*/


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
     /* ArrayList<Integer> contactNumbers=new ArrayList<>();
        /*if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
               *//**//**//**//* String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("", "onActivityResult: sent invitation " + id);
                }*//**//**//**//*

                Uri contactData = data.getData();
                Cursor cur =  managedQuery(contactData, null, null, null, null);
                while (cur != null && cur.moveToNext()) {
                    contactNumbers.add(cur.getInt(1));
                    //System.out.println("Contact number"+contactNumbers[cur]);
                }

            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }*/


        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_PICK_CONTACT) {

                Bundle bundle = data.getExtras();


                 contacts = bundle.getStringArrayList("result");


           System.out.println("Selected contact :--"+contacts.toString());

               // Log.i("TAg", "launchMultiplePhonePicker bundle.toString()= " + contactsPick.toString());
                 Sendcontact();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

   private void Sendcontact() {

        for(int i=0;i<contacts.size();i++){
            String newcontact=contacts.get(i).substring(contacts.get(i).lastIndexOf(";") + 1).trim().replaceAll("[\\s|\\u00A0]+", "");;
          System.out.println("sssssssss"+contacts.get(i).replaceAll(";", ""));
            System.out.println("sssssssss new "+newcontact.substring(newcontact.lastIndexOf(";") + 1).trim());

            new InviteSMSTask(Utility.NOTIFICATION_TYPE_NEW, newcontact).execute();

        }
        System.out.println("new contact"+data.toString());

    }


    public class FetchContact extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(WelcomeActivity.this);
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
                Log.i("TAG", "Name: " + name);
                Log.i("TAG", "Phone Number: " + phone.replaceAll("\\s", "").replaceAll("\\p{P}", ""));
                //String  test = exam.getTestName().replaceAll("\\p{P}","");
               // a = new UserContact(name, phone.replaceAll("\\p{P}", ""));
                new InviteSMSTask(Utility.NOTIFICATION_TYPE_NEW,phone.replaceAll("\\s", "").replaceAll("\\p{P}", "")).execute();
            }

        }
        if (cur != null) {
            cur.close();
        }
        System.out.println("### DONE FETCHING CONTACTS ..");
        //Utility.hideProgress(dialog);
    }

}
