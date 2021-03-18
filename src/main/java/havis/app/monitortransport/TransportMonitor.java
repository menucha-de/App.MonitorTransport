package havis.app.monitortransport;

import havis.transport.SubscriberFilter;
import havis.transport.SubscriberManager;
import havis.transport.Subscription;
import havis.util.monitor.Capabilities;
import havis.util.monitor.CapabilityType;
import havis.util.monitor.DeviceCapabilities;
import havis.util.monitor.Event;
import havis.util.monitor.Monitor;
import havis.util.monitor.ReaderError;
import havis.util.monitor.ReaderSource;
import havis.util.monitor.ServiceSource;
import havis.util.monitor.Source;
import havis.util.monitor.TagEvent;
import havis.util.monitor.TransportError;
import havis.util.monitor.TransportSource;
import havis.util.monitor.UsabilityChanged;
import havis.util.monitor.VisibilityChanged;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

public class TransportMonitor implements Monitor {

	private SubscriberManager manager;
	private EventSetFilter filter;

	private final static Logger log = Logger.getLogger(TransportMonitor.class.getName());

	public TransportMonitor(SubscriberManager manager, EventSetFilter filter) {
		this.manager = manager;
		this.filter = filter;
	}

	@Override
	public void notify(Source source, Event event) {
		String name = "";
		if (source instanceof ServiceSource) {
			name = ((ServiceSource) source).getName();
		} else if (source instanceof TransportSource) {
			name = ((TransportSource) source).getUri();
		} else if (source instanceof ReaderSource) {
			List<Capabilities> capabilities = ((ReaderSource) source).getCapabilities(CapabilityType.DEVICE_CAPABILITIES);
			if (capabilities != null) {
				for (Capabilities cap : capabilities) {
					if (cap instanceof DeviceCapabilities) {
						name = ((DeviceCapabilities) cap).getName();
						break;
					}
				}
			}
		}
		String state = "";
		String eventMessage = event.toString();
		if (event instanceof ReaderError) {
			state = Boolean.toString(((ReaderError) event).isState());
			eventMessage = ((ReaderError) event).getDescription();
		} else if (event instanceof TagEvent) {
			TagEvent e = (TagEvent) event;
			eventMessage = String.format("Transponder ID %s found on antenna %d with RSSI %d", DatatypeConverter.printHexBinary(e.getId()), e.getAntenna(),
					e.getRssi());
		} else if (event instanceof TransportError) {
			state = Boolean.toString(((TransportError) event).isState());
			eventMessage = ((TransportError) event).getDescription();
		} else if (event instanceof UsabilityChanged) {
			state = Boolean.toString(((UsabilityChanged) event).isUsable());
			eventMessage = "";
		} else if (event instanceof VisibilityChanged) {
			state = Boolean.toString(((VisibilityChanged) event).isVisible());
			eventMessage = "";
		}
		Message message = new Message();
		Class<?> clazz = source.getClass();
		for (Class<?> iface : getAllInterfaces(clazz)) {
			if (iface.getName().startsWith("havis.util.monitor.")) {
				clazz = iface;
				break;
			}
		}
		final String eventType = event.getClass().getSimpleName();
		message.setSender(System.getenv("HOSTNAME"));
		message.setSource(clazz != null ? clazz.getSimpleName() : "");
		message.setName(name);
		message.setType(eventType);
		message.setTimestamp(event.getTimestamp());
		message.setState(state);
		message.setMessage(eventMessage);
		log.fine("Sending message: " + message.toString());
		manager.send(message, new SubscriberFilter() {
			@Override
			public boolean accept(Subscription subscription) {
				return filter.accept(eventType, subscription);
			}
		});
	}

	private static List<Class<?>> getAllInterfaces(final Class<?> cls) {
		if (cls == null) {
			return null;
		}

		final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
		getAllInterfaces(cls, interfacesFound);

		return new ArrayList<>(interfacesFound);
	}

	private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
		while (cls != null) {
			final Class<?>[] interfaces = cls.getInterfaces();

			for (final Class<?> i : interfaces) {
				if (interfacesFound.add(i)) {
					getAllInterfaces(i, interfacesFound);
				}
			}

			cls = cls.getSuperclass();
		}
	}
}
