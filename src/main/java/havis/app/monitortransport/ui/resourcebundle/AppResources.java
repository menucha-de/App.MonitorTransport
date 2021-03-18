package havis.app.monitortransport.ui.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AppResources extends ClientBundle {

	public static final AppResources INSTANCE = GWT.create(AppResources.class);

	@Source("resources/CssResources.css")
	CssResources css();

	@Source("resources/close.png")
	ImageResource close();
}