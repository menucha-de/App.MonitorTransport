package havis.custom.harting.monitortransport.ui.client;

import com.google.gwt.core.shared.GWT;

import havis.custom.harting.monitortransport.rest.async.MonitorTransportServiceAsync;
import havis.custom.harting.monitortransport.ui.resourcebundle.AppResources;
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
