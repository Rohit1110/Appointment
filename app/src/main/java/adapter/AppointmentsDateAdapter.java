package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rns.mobile.appointments.R;

import java.util.Date;
import java.util.List;

import model.Appointment;
import recyclerAdapter.EventItem;
import recyclerAdapter.HeaderItem;
import recyclerAdapter.ListItem;
import utils.Utility;

/**
 * Created by Rohit on 12/26/2017.
 */

public class AppointmentsDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //List<Appointment> item;
    private Context mContext = null;
    private List<ListItem> items;

    public List<ListItem> getItems() {
        return items;
    }

    public AppointmentsDateAdapter(List<ListItem> items) {
        this.items = items;
    }

    class ViewHolder0 extends RecyclerView.ViewHolder {
        TextView txt_header;

        public ViewHolder0(View itemView) {
            super(itemView);
            txt_header = (TextView) itemView.findViewById(R.id.txt_header);
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        private TextView description;
        private TextView time, name, date;

        public ViewHolder1(View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.txttime);
            name = (TextView) itemView.findViewById(R.id.txtname);
            date = (TextView) itemView.findViewById(R.id.txtdate);
            description = (TextView) itemView.findViewById(R.id.txtdecription);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ListItem.TYPE_DATE: {
                View item = inflater.inflate(R.layout.view_list_item_header, parent, false);
                return new ViewHolder0(item);
            }
            case ListItem.TYPE_GENERAL: {
                View item = inflater.inflate(R.layout.layout_appointment_row, parent, false);
                return new ViewHolder1(item);
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ListItem.TYPE_DATE: {
                HeaderItem header = (HeaderItem) items.get(position);

                ViewHolder0 dateholder = (ViewHolder0) holder;

                Date formattedDate = Utility.formatDate(header.getDate(), Utility.DATE_FORMAT_USED);
                System.out.println(formattedDate + " new Date Format");
                if (formattedDate != null) {
                    dateholder.txt_header.setText(Utility.formatDate(formattedDate, Utility.DATE_FORMAT_DISPLAY));
                }

                break;
            }
            case ListItem.TYPE_GENERAL: {
                EventItem event = (EventItem) items.get(position);
                ViewHolder1 gholder = (ViewHolder1) holder;
                // your logic here

                if (event.getEvent().getName() != null && !event.getEvent().getName().trim().equals(""))

                {
                    gholder.name.setText(event.getEvent().getName());
                } else

                {
                    gholder.name.setText(event.getEvent().getPhone());
                }

                if (event.getEvent().getDate() != null) {
                    Date formattedDate = Utility.formatDate(event.getEvent().getDate(), Utility.DATE_FORMAT_USED);
                    System.out.println(formattedDate + " new Date Format");
                    if (formattedDate != null) {
                        gholder.date.setText(Utility.formatDate(formattedDate, Utility.DATE_FORMAT_DISPLAY));
                    }

                }

                gholder.time.setText(event.getEvent().getStartTime() + " - " + event.getEvent().getEndTime());
                gholder.description.setText(event.getEvent().getDescription());
                System.out.println("name in Adapter " + event.getEvent().getName());
                System.out.println("time in Adapter " + event.getEvent().getStartTime() + " - " + event.getEvent().getEndTime());


                //gholder.txt_title.setText(event.getEvent().getTitle());
                break;
            }
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {

        return items.get(position).getType();

    }

    public Appointment getAppointment(int position) {
        if (items == null || items.size() <= position) {
            return null;
        }
        ListItem item = items.get(position);
        if (ListItem.TYPE_GENERAL == item.getType()) {
            EventItem eventItem = (EventItem) item;
            if (eventItem != null) {
                return eventItem.getEvent();
            }

        }
        return null;
    }

}
