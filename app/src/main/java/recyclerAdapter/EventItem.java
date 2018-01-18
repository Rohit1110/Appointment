package recyclerAdapter;

import android.support.annotation.NonNull;



import model.Appointment;

public class EventItem extends ListItem {

	@NonNull
	private Appointment event;

	public EventItem(@NonNull Appointment event) {
		this.event = event;
	}

	@NonNull
	public Appointment getEvent() {
		return event;
	}

	// here getters and setters
	// for title and so on, built
	// using event

	@Override
	public int getType() {
		return TYPE_GENERAL;
	}

}