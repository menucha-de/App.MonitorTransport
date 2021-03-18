package havis.app.monitortransport.ui.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.adapters.EditorSource;
import com.google.gwt.editor.client.adapters.ListEditor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import havis.app.monitortransport.EventSet;
import havis.app.monitortransport.rest.async.MonitorTransportServiceAsync;
import havis.app.monitortransport.ui.client.event.SaveEventSetEvent;
import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.table.ChangeRowEvent;
import havis.net.ui.shared.client.table.CreateRowEvent;
import havis.net.ui.shared.client.table.CustomTable;
import havis.net.ui.shared.client.table.DeleteRowEvent;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;
import havis.net.ui.shared.client.widgets.Util;
import havis.transport.Subscriber;
import havis.transport.ui.client.TransportType;

public class EventSetList extends Composite implements Editor<List<EventSet>> {

	private static EventSetListUiBinder uiBinder = GWT.create(EventSetListUiBinder.class);

	interface EventSetListUiBinder extends UiBinder<Widget, EventSetList> {
	}

	interface ListDriver extends SimpleBeanEditorDriver<List<EventSet>, EventSetList> {
	}

	private ListDriver listDriver = GWT.create(ListDriver.class);

	interface ItemDriver extends SimpleBeanEditorDriver<EventSet, EventSetEditorDialog> {
	}

	ItemDriver itemDriver = GWT.create(ItemDriver.class);

	MonitorTransportServiceAsync service = Helper.getService();

	@UiField
	CustomTable table;

	private EventSetEditorDialog editor;
	private List<TransportType> transportTypes;
	private List<String> eventTypes;

	@UiField
	FlowPanel eventSetPanel;

	@Path("")
	ListEditor<EventSet, EventSetListItem> eventSets;

	private class EventListEditorSource extends EditorSource<EventSetListItem> {

		@Override
		public EventSetListItem create(int index) {
			EventSetListItem item = new EventSetListItem();
			item.addSaveEventSetHandler(new SaveEventSetEvent.Handler() {

				@Override
				public void onSaveEventSet(SaveEventSetEvent event) {
					listDriver.flush();
					service.updateEventSet(event.getEventSet().getId(), event.getEventSet(), new MethodCallback<Void>() {
						@Override
						public void onSuccess(Method method, Void response) {
							loadEventSets();
						}

						@Override
						public void onFailure(Method method, Throwable exception) {
							// TODO Auto-generated method stub

						}
					});
				}
			});
			table.addRow(item);
			return item;
		}

		@Override
		public void dispose(EventSetListItem subEditor) {
			table.deleteRow(subEditor);
		}
	}

	private SaveEventSetEvent.Handler saveHandler = new SaveEventSetEvent.Handler() {

		@Override
		public void onSaveEventSet(SaveEventSetEvent event) {
			closeEventSetEditorDialog();
			EventSet eventSet = event.getEventSet();
			if (eventSet.getId() != null) {
				updateEventSet(eventSet);
			} else {
				createEventSet(eventSet);
			}
		}
	};

	private void showEventSetEditorDialog(EventSet eventSet) {
		editor = new EventSetEditorDialog(eventSet, eventTypes, transportTypes);
		editor.addSaveEventSetHandler(saveHandler);
		eventSetPanel.add(editor);
	}

	private void closeEventSetEditorDialog() {
		editor.removeFromParent();
	}

	private void loadEventSets() {
		Helper.getService().getEventSets(new MethodCallback<List<EventSet>>() {

			@Override
			public void onSuccess(Method method, List<EventSet> response) {
				listDriver.edit(response);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	private void createEventSet(EventSet eventSet) {
		service.defineEventSet(eventSet, new TextCallback() {

			@Override
			public void onSuccess(Method method, String response) {
				loadEventSets();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	private void updateEventSet(EventSet eventSet) {
		service.updateEventSet(eventSet.getId(), eventSet, new MethodCallback<Void>() {

			@Override
			public void onSuccess(Method method, Void response) {
				loadEventSets();
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	private void deleteEventSet(String id) {
		service.undefineEventSet(id, new MethodCallback<Void>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				loadEventSets();
			}
		});
	}

	public EventSetList() {
		initWidget(uiBinder.createAndBindUi(this));
		eventSets = ListEditor.of(new EventListEditorSource());
		listDriver.initialize(this);
		loadEventSets();
		table.setHeader(Arrays.asList("New Subscriber", " "));
		table.setColumnWidth(1, 8, Unit.EM);
		service.getSubscriberTypes(new MethodCallback<List<String>>() {

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}

			@Override
			public void onSuccess(Method method, List<String> response) {
				List<TransportType> types = new ArrayList<>();
				types.add(TransportType.CUSTOM);
				if (response != null) {
					for (String type : response) {
						TransportType tType = null;
						try {
							tType = TransportType.valueOf(type.toUpperCase());
						} catch (Exception e) {
							// ignore
						}
						if (tType != null && tType != TransportType.CUSTOM) {
							types.add(tType);
						}
					}
				}
				transportTypes = types;
			}
		});
		service.getEventTypes(new MethodCallback<List<String>>() {

			@Override
			public void onSuccess(Method method, List<String> response) {
				eventTypes = response;
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	@UiHandler("table")
	void onCreateRow(CreateRowEvent event) {
		EventSet eventSet = new EventSet(new ArrayList<String>(), new Subscriber());
		showEventSetEditorDialog(eventSet);
	}

	@UiHandler("table")
	void onChangeRow(ChangeRowEvent event) {
		final EventSet eventSet = eventSets.getList().get(event.getIndex());
		EventSetListItem item = (EventSetListItem) event.getRow();
		service.getEventSet(item.id.getValue(), new MethodCallback<EventSet>() {

			@Override
			public void onSuccess(Method method, EventSet response) {
				if (eventSet.getSubscriber() == null) {
					eventSet.setSubscriber(new Subscriber());
				} else {
					eventSet.setSubscriber(response.getSubscriber());
				}
				if (eventSet.getEvents() == null) {
					eventSet.setEvents(new ArrayList<String>());
				} else {
					eventSet.setEvents(response.getEvents());
				}
				eventSet.setId(response.getId());
				showEventSetEditorDialog(eventSet);
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				CustomMessageWidget.show(Util.getThrowableMessage(exception), MessageType.ERROR);
			}
		});
	}

	@UiHandler("table")
	void onDeleteRow(DeleteRowEvent event) {
		EventSetListItem item = (EventSetListItem) event.getRow();
		deleteEventSet(item.id.getValue());
	}
}
