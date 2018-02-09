package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.List;

import model.InviteContact;
import model.UserContact;

/**
 * Created by Rohit on 1/1/2018.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHoldercontact> {
    private Context mContext;

    private List<UserContact> item, filterList;


    public class MyViewHoldercontact extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView phone, name;
        private CheckBox selectContact;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        public MyViewHoldercontact(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            phone = (TextView) itemView.findViewById(R.id.txt_user_phone);
            name = (TextView) itemView.findViewById(R.id.txt_user_contactname);
            //selectContact = (CheckBox) itemView.findViewById(R.id.chk_contact);


          /*  this.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send the text to the listener, i.e Activity.
                    mListener.onItemClicked(((TextView)v).getText());
                }
            });

            this.phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send the text to the listener, i.e Activity.
                    mListener.onItemClicked(((TextView)v).getText());
                }
            });*/

        }

        @Override
        public void onClick(View v) {
            if (selectedItems.get(getAdapterPosition(), false)) {
                selectedItems.delete(getAdapterPosition());
                v.setSelected(false);

            } else {
                selectedItems.put(getAdapterPosition(), true);
                v.setSelected(true);
            }
            // Toast.makeText(mContext, "item clicked"+selectedItems.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();

        }
    }

    public ContactListAdapter(Context mContext, List<UserContact> item) {
        this.mContext = mContext;
        this.item = item;
        // this.filterList = new List<UserContact>();
        // we copy the original list to the filter list and use it for setting row values

        //System.out.println("item list:" + item);

    }

    // Define listener member variable
    private static OnRecyclerViewItemClickListener mListener;

    // Define the listener interface
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(CharSequence text);
    }

    // Define the method that allows the parent activity or fragment to define the listener.
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mListener = listener;
    }


    @Override
    public MyViewHoldercontact onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_row, parent, false);
        return new MyViewHoldercontact(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHoldercontact holder, int position) {
      final int pos=position;
        UserContact usercontact = item.get(position);
        holder.name.setText(usercontact.getName());
        holder.phone.setText(usercontact.getPhone());
      /*  holder.selectContact.setOnCheckedChangeListener(null);
        holder.selectContact.setChecked(item.get(position).isSelected());

        holder.selectContact.setTag(item.get(position));
        holder.selectContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                InviteContact contact = (InviteContact) cb.getTag();
                contact.setSelected(cb.isChecked());

                item.get(pos).setSelected(cb.isChecked());


            }
        });*/

        System.out.println("name in Adapter " + usercontact.getName());
    }


    public void updateList(List<UserContact> list) {
        item = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return item.size();
    }

    public void Delete(int position)
    {
       item.remove(position);
       notifyDataSetChanged();
    }


}
