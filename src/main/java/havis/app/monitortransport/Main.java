package havis.app.monitortransport;

import havis.transport.SubscriberManager;
import havis.transport.Subscription;
import havis.transport.ValidationException;
import havis.transport.common.CommonSubscriberManager;
import havis.util.monitor.Event;
import havis.util.monitor.Monitor;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

	private final static Logger log = Logger.getLogger(Main.class.getName());

	private ObjectMapper mapper = new ObjectMapper();

	private Configuration configuration;

	TransportMonitor monitor;

	private List<String> eventTypes = new ArrayList<>();
	private SubscriberManager manager;
	private EventSetFilter filter = new EventSetFilter() {
		@Override
		public boolean accept(String eventType, Subscription subscription) {
			for (EventSet eventSet : configuration.getEventSets()) {
				if (subscription.getId().equals(eventSet.getId())) {
					return eventSet.getEvents().contains(eventType);
				}
			}
			return false;
		}
	};

	public Main() {
		Method[] methods = havis.util.monitor.ObjectFactory.class.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("create") && Modifier.isPublic(method.getModifiers()) && !method.getReturnType().equals(Void.TYPE)
					&& Event.class.isAssignableFrom(method.getReturnType()))
				eventTypes.add(method.getReturnType().getSimpleName());
		}
		Collections.sort(eventTypes);

		loadConfiguration();

		try {
			manager = new CommonSubscriberManager(Message.class, configuration.getSubscribers());
			monitor = new TransportMonitor(manager, filter);
		} catch (ValidationException e) {
			// ignore
		}
	}

	public void start() {
	}

	public void stop() {
	}

	private void loadConfiguration() {
		log.log(Level.FINE, "Trying to load configuration from ''{0}''", Environment.CONFIG_FILE);
		try {
			File configFile = new File(Environment.CONFIG_FILE);
			if (configFile.exists()) {
				this.configuration = mapper.readValue(configFile, Configuration.class);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load configuration '" + Environment.CONFIG_FILE + "'", e);
		} finally {
			if (this.configuration == null)
				// default config
				this.configuration = new Configuration();
		}
	}

	private void saveConfiguration() {
		log.log(Level.FINE, "Saving configuration to ''{0}''", Environment.CONFIG_FILE);
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(Environment.CONFIG_FILE), this.configuration);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to save configuration '" + Environment.CONFIG_FILE + "'", e);
		}
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public List<String> getSubscriberTypes() {
		return manager.getSubscriberTypes();
	}

	public List<String> getEventTypes() {
		return new ArrayList<>(eventTypes);
	}

	public EventSet getEventSet(String id) {
		for (EventSet eventSet : configuration.getEventSets()) {
			if (eventSet.getId().equals(id)) {
				return eventSet;
			}
		}
		return null;
	}

	public List<EventSet> getEventSets() {
		return new ArrayList<>(configuration.getEventSets());
	}

	public String defineEventSet(EventSet eventSet) throws MonitorTransportException {
		if (eventSet == null || eventSet.getSubscriber() == null) {
			throw new MonitorTransportException("Invalid event set");
		}
		if (eventSet.getId() != null) {
			throw new MonitorTransportException("Event set ID must not be set");
		}
		if (eventSet.getEvents() == null) {
			eventSet.setEvents(new ArrayList<String>());
		}
		try {
			String id = manager.add(eventSet.getSubscriber());
			eventSet.setId(id);
			configuration.getEventSets().add(eventSet);
			saveConfiguration();
			return id;
		} catch (ValidationException e) {
			throw new MonitorTransportException(e.getMessage());
		}
	}

	public void updateEventSet(String id, EventSet eventSet) throws MonitorTransportException {
		if (eventSet == null || eventSet.getSubscriber() == null) {
			throw new MonitorTransportException("Invalid event set");
		}
		if (eventSet.getEvents() == null) {
			eventSet.setEvents(new ArrayList<String>());
		}
		try {
			// ID might be unset
			eventSet.setId(id);
			eventSet.getSubscriber().setId(id);
			manager.update(eventSet.getSubscriber());
			configuration.getEventSets().set(configuration.getEventSets().indexOf(eventSet), eventSet);
			saveConfiguration();
		} catch (ValidationException e) {
			throw new MonitorTransportException(e.getMessage());
		}
	}

	public void undefineEventSet(String id) throws MonitorTransportException {
		try {
			manager.remove(id);
			EventSet remove = new EventSet();
			remove.setId(id);
			configuration.getEventSets().remove(remove);
			saveConfiguration();
		} catch (ValidationException e) {
			throw new MonitorTransportException(e.getMessage());
		}
	}
}