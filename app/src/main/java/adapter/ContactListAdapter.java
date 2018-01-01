package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.List;

import model.Appointment;
import model.Usercontact;

/**
 * Created by Rohit on 1/1/2018.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHoldercontact> {
    private Context mContext;
   private List<Usercontact> item;



    public class MyViewHoldercontact extends RecyclerView.ViewHolder {
        private TextView phone,name;
        public MyViewHoldercontact(View itemView) {
            super(itemView);
            phone = (TextView) itemView.findViewById(R.id.txt_user_phone);
            name = (TextView) itemView.findViewById(R.id.txt_user_contactname);

        }
    }
    public ContactListAdapter(Context mContext, List<Usercontact> item){
        this.mContext = mContext;
        this.item = item;
        System.out.println("item list:" + item);

    }


    @Override
    public MyViewHoldercontact onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_row, parent, false);
        return new MyViewHoldercontact(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHoldercontact holder, int position) {
        Usercontact usercontact = item.get(position);
        holder.name.setText(usercontact.getName());
        holder.phone.setText(usercontact.getPhone());
        System.out.println("name in Adapter " + usercontact.getName());
    }





    @Override
    public int getItemCount() {
        return item.size();
    }

}
