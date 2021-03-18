package havis.app.monitortransport;

import havis.transport.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Configuration {

	@JsonIgnore
	private List<EventSet> eventSets = new CopyOnWriteArrayList<>();

	public Configuration() {
	}

	@JsonProperty("eventSets")
	public EventSet[] getEventSetArray() {
		return this.eventSets.toArray(new EventSet[this.eventSets.size()]);
	}

	public void setEventSetArray(EventSet[] eventSets) {
		if (eventSets != null) {
			for (EventSet eventSet : eventSets) {
				if (eventSet.getId() != null && eventSet.getSubscriber() != null && eventSet.getSubscriber().getId() != null
						&& eventSet.getSubscriber().getUri() != null) {
					if (eventSet.getEvents() == null) {
						eventSet.setEvents(new ArrayList<String>());
					}
					this.eventSets.add(eventSet);
				}
			}
		}
	}

	public List<EventSet> getEventSets() {
		return this.eventSets;
	}

	@JsonIgnore
	public List<Subscriber> getSubscribers() {
		List<Subscriber> subscribers = new ArrayList<>();
		for (EventSet eventSet : this.eventSets) {
			subscribers.add(eventSet.getSubscriber());
		}
		return subscribers;
	}
}
