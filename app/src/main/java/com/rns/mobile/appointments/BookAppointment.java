package com.rns.mobile.appointments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class BookAppointment extends AppCompatActivity {
    Button book;
    Spinner from,to;
    private static final String[] fromtime = new String[] {
          "Select From",  "10", "11", "12", "1", "2","3","4","5","6"
    };
    private static final String[] totime = new String[] {
            "Select to",  "10", "11", "12", "1", "2","3","4","5","6"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        book=(Button)findViewById(R.id.btnbook);
        //from=(Spinner)findViewById(R.id.fromspinner);
        //to=(Spinner)findViewById(R.id.tospinner);
      /*  List<String> list = new ArrayList<String>();
        list.add("select from time");
        list.add("10");
        list.add("11");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");*/
      /*  ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, fromtime);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        from.setAdapter(dataAdapter);



        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, totime);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        to.setAdapter(dataAdapter2);*/
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookAppointment.this,Book.class);
                startActivity(intent);
            }
        });
    }

    public void showDialog() throws Exception
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookAppointment.this);

        builder.setMessage("Booking Success ");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
               Intent intent= new Intent(BookAppointment.this,AppointmentsActivity.class);
               startActivity(intent);
               finish();

                dialog.dismiss();
            }
        });



        builder.show();
    }

}
