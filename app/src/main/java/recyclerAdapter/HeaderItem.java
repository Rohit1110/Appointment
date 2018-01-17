package recyclerAdapter;

import android.support.annotation.NonNull;

import java.util.Date;

public class HeaderItem extends ListItem {

	@NonNull
	private String date;

	public HeaderItem(@NonNull String date) {
		this.date = date;
	}

	@NonNull
	public String getDate() {
		return date;
	}

	// here getters and setters
	// for title and so on, built
	// using date

	@Override
	public int getType() {
		return TYPE_DATE;
	}

}