package havis.app.monitortransport.ui.client;

import com.google.gwt.core.shared.GWT;

import havis.app.monitortransport.rest.async.MonitorTransportServiceAsync;
import havis.app.monitortransport.ui.resourcebundle.AppResources;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

public abstract class Helper {
	private static MonitorTransportServiceAsync service = GWT.create(MonitorTransportServiceAsync.class);
	
	public static MonitorTransportServiceAsync getService() {
		return service;
	}
	
	public static ResourceBundle getSharedResources() {
		return ResourceBundle.INSTANCE;
	}
	
	public static AppResources getAppResources() {
		return AppResources.INSTANCE;
	}
}
