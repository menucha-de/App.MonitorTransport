package havis.custom.harting.monitortransport.ui.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import havis.custom.harting.monitortransport.EventSet;
import havis.custom.harting.monitortransport.ui.client.event.SaveEventSetEvent;
import havis.net.ui.shared.client.event.DialogCloseEvent;
import havis.net.ui.shared.client.widgets.CommonEditorDialog;
import havis.net.ui.shared.client.widgets.Util;
import havis.transport.Subscriber;
import havis.transport.ui.client.TransportPanel;
import havis.transport.ui.client.TransportType;
import havis.transport.ui.client.event.SaveTransportEvent;
import havis.transport.ui.client.event.TransportErrorEvent;

public class EventSetEditorDialog extends Composite implements ValueAwareEditor<EventSet>, SaveEventSetEvent.HasHandlers {

	@UiField
	TransportPanel transport;

	@UiField
	@Path("subscriber.enable")
	ToggleButton enable;

	@UiField
	CommonEditorDialog dialog;
	
	@UiField
	FlowPanel eventTypesPanel;
	
	private Map<String, EventTypeToggle> eventTypes = new HashMap<>();

	private EditorDelegate<EventSet> delegate;
	private EventSet eventSet;

	interface ItemDriver extends SimpleBeanEditorDriver<EventSet, EventSetEditorDialog> {
	}

	ItemDriver itemDriver = GWT.create(ItemDriver.class);

	private static EventSetEditorDialogUiBinder uiBinder = GWT.create(EventSetEditorDialogUiBinder.class);

	interface EventSetEditorDialogUiBinder extends UiBinder<Widget, EventSetEditorDialog> {
	}

	public EventSetEditorDialog(EventSet eventSet, List<String> types, List<TransportType> transportTypes) {
		initWidget(uiBinder.createAndBindUi(this));
		generateEventTypeList(types);
		transport.setTypes(transportTypes);
		itemDriver.initialize(this);
		itemDriver.edit(eventSet);
	}

	@UiHandler("dialog")
	void onDialogClose(DialogCloseEvent event) {
		if (event.isAccept()) {
			transport.saveTransportData();
		}
		setVisible(false);
	}
	
	@UiHandler("transport")
	void onSaveTransport(SaveTransportEvent event) {
		Subscriber subscriber = eventSet.getSubscriber();
		if (subscriber == null) {
			subscriber = new Subscriber();
		}
		subscriber.setUri(event.getUri());
		subscriber.setProperties(event.getProperties());
		itemDriver.flush();
		fireEvent(new SaveEventSetEvent(eventSet));
	}

	@UiHandler("transport")
	void onTransportError(TransportErrorEvent event) {
		String error = event.isException() ? Util.getThrowableMessage(event.getException()) : event.getErrorMessage();
		delegate.recordError(error, null, null);
	}

	@Override
	public void setDelegate(EditorDelegate<EventSet> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void flush() {
		eventSet.getEvents().clear();
		for (Map.Entry<String, EventTypeToggle> entry : eventTypes.entrySet()) {
			if (entry.getValue().toggle.getValue()) {
				eventSet.getEvents().add(entry.getKey());
			}
		}
	}

	@Override
	public void onPropertyChange(String... paths) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(EventSet value) {
		eventSet = value;
		for (String event : eventSet.getEvents()) {
			EventTypeToggle toggle = eventTypes.get(event);
			if (toggle != null) {
				toggle.toggle.setValue(true);
			}
		}
		Subscriber subscriber = eventSet.getSubscriber();
		transport.setData(subscriber.getUri(), subscriber.getProperties());
	}

	@Override
	public HandlerRegistration addSaveEventSetHandler(SaveEventSetEvent.Handler handler) {
		return addHandler(handler, SaveEventSetEvent.getType());
	}
	
	private void generateEventTypeList(List<String> types) {
		for (String type : types) {
			EventTypeToggle toggle = new EventTypeToggle();
			toggle.label.setText(type);
			eventTypes.put(type, toggle);
			eventTypesPanel.add(toggle);
		}
	}
}
