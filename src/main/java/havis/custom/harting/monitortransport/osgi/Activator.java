package havis.custom.harting.monitortransport.osgi;

import havis.custom.harting.monitortransport.Main;
import havis.custom.harting.monitortransport.rest.RESTApplication;
import havis.util.monitor.Monitor;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.ws.rs.core.Application;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(Activator.class.getName());

	private final static String MONITOR_NAME = "type";
	private final static String MONITOR_VALUE = "monitortransport";

	private Main main;
	private ServiceRegistration<Application> app;
	private ServiceRegistration<?> monitor;

	@Override
	public void start(BundleContext context) throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(Activator.class.getClassLoader());
			main = new Main();
		} finally {
			Thread.currentThread().setContextClassLoader(loader);
		}
		
		main.start();

		app = context.registerService(Application.class, new RESTApplication(main), null);
		monitor = context.registerService(Monitor.class, main.getMonitor(), new Hashtable<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(MONITOR_NAME, MONITOR_VALUE);
			}
		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (monitor != null) {
			monitor.unregister();
			monitor = null;
		}
		if (main != null) {
			main.stop();
			main = null;
		}
		if (app != null) {
			app.unregister();
			app = null;
		}
	}
}