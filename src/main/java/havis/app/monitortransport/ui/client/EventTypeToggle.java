package havis.app.monitortransport.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class EventTypeToggle extends Composite {

	@UiField
	Label label;

	@UiField
	ToggleButton toggle;

	private static EventTypeToggleUiBinder uiBinder = GWT.create(EventTypeToggleUiBinder.class);

	interface EventTypeToggleUiBinder extends UiBinder<Widget, EventTypeToggle> {
	}

	public EventTypeToggle() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
