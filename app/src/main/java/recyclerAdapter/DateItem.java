package recyclerAdapter;

/**
 * Created by Rohit on 1/16/2018.
 */

public class DateItem extends ListItem {

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}