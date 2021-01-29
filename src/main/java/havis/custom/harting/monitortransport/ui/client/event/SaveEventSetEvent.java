package havis.custom.harting.monitortransport.ui.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import havis.custom.harting.monitortransport.EventSet;

public class SaveEventSetEvent extends GwtEvent<SaveEventSetEvent.Handler> {

	public interface Handler extends EventHandler {
		void onSaveEventSet(SaveEventSetEvent event);
	}

	public interface HasHandlers {
		HandlerRegistration addSaveEventSetHandler(SaveEventSetEvent.Handler handler);
	}

	private static final Type<SaveEventSetEvent.Handler> TYPE = new Type<>();

	private EventSet eventSet;

	public SaveEventSetEvent(EventSet eventSet) {
		super();
		this.eventSet = eventSet;
	}

	public EventSet getEventSet() {
		return eventSet;
	}

	@Override
	public Type<SaveEventSetEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveEventSetEvent.Handler handler) {
		handler.onSaveEventSet(this);
	}

	public static Type<SaveEventSetEvent.Handler> getType() {
		return TYPE;
	}
}
