package com.rns.mobile.appointments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import adapter.AppointmentDetailsAdapter;
import adapter.ContactListAdapter;
import decorator.ContactDividerItemDecoration;
import model.ActiveContact;
import model.Appointment;
import model.DetailContact;
import model.SmsField;
import utils.FirebaseUtil;
import utils.Utility;

public class AppointmentDetails extends AppCompatActivity {
    TextView appointmentDescriptions;
    RecyclerView memberlist;
    private AppointmentDetailsAdapter adapter;
    private List<ActiveContact> totalmember;
    DetailContact detailContact = new DetailContact();
    DetailContact contact1,selecteduser;
    List<DetailContact> detailContacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);
        String appointmentid = getIntent().getStringExtra("appointmentid");
        String userPhone = FirebaseUtil.getMobile();
        appointmentDescriptions = (TextView) findViewById(R.id.descriptions);
        memberlist = (RecyclerView) findViewById(R.id.list_apoointment_details);
       /* memberlist.addOnItemTouchListener(new RecyclerTouchListener(this, memberlist, new ClickListener(){


            @Override
            public void onClick(View view, int position) {
                selecteduser=detailContacts.get(position);
                String number=selecteduser.getNumber();

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+number));
                startActivity(intent);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
        detailContacts=new CopyOnWriteArrayList<>();

        FirebaseUtil.db.collection(FirebaseUtil.DOC_USERS).document(userPhone).collection(FirebaseUtil.DOC_APPOINTMENTS).document(appointmentid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Appointment appointment = null;
                if (task.getResult() != null && task.getResult().exists()) {
                    appointment = new Appointment();


                    if (task.getResult().getString("description") != null) {
                        appointment.setDescription(task.getResult().getString("description"));
                        appointmentDescriptions.setText(task.getResult().getString("description"));
                    }

                    if (task.getResult().get("contactList") != null) {

                        appointment.setContactList((List<ActiveContact>) task.getResult().getData().get("contactList"));
                        System.out.println("appointment.setContactList"+appointment.getContactList().size());
                        //totalmember= (List<ActiveContact>) task.getResult().getData().get("contactList");

                        for (int i = 0; i < appointment.getContactList().size(); i++) {
                            Map<String, String> contact = (Map<String, String>) appointment.getContactList().get(i);
                            detailContact.setContact(contact.get("contact"));
                            detailContact.setNumber(contact.get("number"));
                            detailContact.setStatus(contact.get("status"));
                            System.out.println("!!!!!!!!!!!!!!!!"+contact.get("number"));
                            contact1 = new DetailContact(contact.get("contact"), contact.get("number"),contact.get("status"));
                            detailContacts.add(contact1);



                        }



                        // adapter.notifyDataSetChanged();
                        System.out.println("DetailContact " + detailContacts.size());

                        adapter = new AppointmentDetailsAdapter(AppointmentDetails.this, detailContacts);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(AppointmentDetails.this, 1);
                        memberlist.addItemDecoration(new ContactDividerItemDecoration(AppointmentDetails.this));
                        memberlist.setLayoutManager(mLayoutManager);
                        memberlist.setAdapter(adapter);


                    }

                }

            }


        });


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
}
