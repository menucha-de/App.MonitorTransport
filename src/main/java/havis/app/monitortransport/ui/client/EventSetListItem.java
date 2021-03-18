package havis.app.monitortransport.ui.client;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

import havis.app.monitortransport.EventSet;
import havis.app.monitortransport.ui.client.event.SaveEventSetEvent;
import havis.app.monitortransport.ui.client.event.SaveEventSetEvent.Handler;
import havis.net.ui.shared.client.table.CustomWidgetRow;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

public class EventSetListItem extends CustomWidgetRow implements ValueAwareEditor<EventSet>, SaveEventSetEvent.HasHandlers {

	SimpleEditor<String> id = SimpleEditor.of();

	@Path("subscriber.uri")
	Label uri = new Label();

	@Path("subscriber.enable")
	ToggleButton enable = new ToggleButton();
	
	private EventSet eventSet;
	
	public EventSetListItem() {
		uri.setStyleName(ResourceBundle.INSTANCE.css().webuiCustomTableLabel());

		enable.setStyleName("webui-EnableButton subscriber subscriberList");
		enable.getDownFace().setText("Active");
		enable.getUpFace().setText("Inactive");
		enable.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				fireEvent(new SaveEventSetEvent(eventSet));
			}
		});

		addColumn(uri);
		addColumn(enable);
	}

	@Override
	public void setDelegate(EditorDelegate<EventSet> delegate) {
	}

	@Override
	public void flush() {
	}

	@Override
	public void onPropertyChange(String... paths) {
	}

	@Override
	public void setValue(EventSet value) {
		eventSet = value;
	}

	@Override
	public HandlerRegistration addSaveEventSetHandler(Handler handler) {
		return addHandler(handler, SaveEventSetEvent.getType());
	}

}
