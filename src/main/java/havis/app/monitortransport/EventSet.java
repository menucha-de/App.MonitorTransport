package havis.app.monitortransport;

import havis.transport.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class EventSet {

	private String id;
	private List<String> events = new ArrayList<>();
	private Subscriber subscriber;

	public EventSet() {
	}

	public EventSet(List<String> events, Subscriber subscriber) {
		this.events = events;
		this.subscriber = subscriber;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getEvents() {
		return events;
	}

	public void setEvents(List<String> events) {
		this.events = events;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EventSet))
			return false;
		EventSet other = (EventSet) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
