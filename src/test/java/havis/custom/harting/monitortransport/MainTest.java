package havis.custom.harting.monitortransport;

import havis.transport.Subscriber;
import havis.transport.common.Provider;
import havis.util.monitor.Capabilities;
import havis.util.monitor.CapabilityType;
import havis.util.monitor.Configuration;
import havis.util.monitor.ConfigurationType;
import havis.util.monitor.ConnectionError;
import havis.util.monitor.DeviceCapabilities;
import havis.util.monitor.ReaderSource;
import havis.util.monitor.ServiceSource;
import havis.util.monitor.TransportError;
import havis.util.monitor.TransportSource;
import havis.util.monitor.UsabilityChanged;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainTest {

	@BeforeClass
	public static void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		final Providers providers = (Providers) RuntimeDelegate.getInstance();
		RegisterBuiltin.register((ResteasyProviderFactory) providers);
		Provider.createFactory(new Provider() {
			@Override
			public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> clazz, Type type, Annotation[] annotations, MediaType mediaType) {
				ResteasyProviderFactory.pushContext(Providers.class, providers);
				return providers.getMessageBodyWriter(clazz, type, annotations, mediaType);
			}

			@Override
			public <T> void write(MessageBodyWriter<T> writer, T data, Class<?> clazz, Type type, Annotation[] annotations, MediaType mediaType,
					MultivaluedMap<String, Object> properties, OutputStream stream) throws Exception {
				ResteasyProviderFactory.pushContext(Providers.class, providers);
				writer.writeTo(data, clazz, type, annotations, mediaType, properties, stream);
			}

			@Override
			public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
				return null;
			}

			@Override
			public <T> T read(MessageBodyReader<T> arg0, Class<T> arg1, Type arg2, Annotation[] arg3, MediaType arg4, MultivaluedMap<String, String> arg5,
					InputStream arg6) throws Exception {
				return null;
			}
		});
	}

	@Test
	public void testCrud() throws Exception {
		Main main = new Main();
		Assert.assertEquals(0, main.getEventSets().size());

		String id = main.defineEventSet(new EventSet(new ArrayList<String>(Arrays.asList("ConnectionError", "TransportError")), new Subscriber(true,
				"udp://10.10.10.10:5555")));
		Assert.assertEquals(1, main.getEventSets().size());
		Assert.assertNotNull("ID must not be null", id);
		EventSet eventSet = main.getEventSet(id);
		Assert.assertEquals(id, eventSet.getId());
		Assert.assertEquals(Arrays.asList("ConnectionError", "TransportError"), eventSet.getEvents());
		Assert.assertEquals(id, eventSet.getSubscriber().getId());
		Assert.assertTrue(eventSet.getSubscriber().isEnable());
		Assert.assertEquals("udp://10.10.10.10:5555", eventSet.getSubscriber().getUri());

		main.updateEventSet(id, new EventSet(new ArrayList<String>(Arrays.asList("ConnectionError")), new Subscriber(true, "udp://10.10.10.10:5555")));
		Assert.assertEquals(1, main.getEventSets().size());
		EventSet updatedEventSet1 = main.getEventSet(id);
		Assert.assertEquals(id, updatedEventSet1.getId());
		Assert.assertEquals(Arrays.asList("ConnectionError"), updatedEventSet1.getEvents());
		Assert.assertEquals(id, updatedEventSet1.getSubscriber().getId());
		Assert.assertTrue(updatedEventSet1.getSubscriber().isEnable());
		Assert.assertEquals("udp://10.10.10.10:5555", updatedEventSet1.getSubscriber().getUri());

		main.updateEventSet(id, new EventSet(new ArrayList<String>(Arrays.asList("ConnectionError")), new Subscriber(true, "udp://10.10.10.10:5556")));
		Assert.assertEquals(1, main.getEventSets().size());
		EventSet updatedEventSet2 = main.getEventSet(id);
		Assert.assertEquals(id, updatedEventSet2.getId());
		Assert.assertEquals(Arrays.asList("ConnectionError"), updatedEventSet2.getEvents());
		Assert.assertEquals(id, updatedEventSet2.getSubscriber().getId());
		Assert.assertTrue(updatedEventSet2.getSubscriber().isEnable());
		Assert.assertEquals("udp://10.10.10.10:5556", updatedEventSet2.getSubscriber().getUri());

		main.updateEventSet(id, new EventSet(new ArrayList<String>(Arrays.asList("ConnectionError")), new Subscriber(false, "udp://10.10.10.10:5556")));
		Assert.assertEquals(1, main.getEventSets().size());
		EventSet updatedEventSet3 = main.getEventSet(id);
		Assert.assertEquals(id, updatedEventSet3.getId());
		Assert.assertEquals(Arrays.asList("ConnectionError"), updatedEventSet3.getEvents());
		Assert.assertEquals(id, updatedEventSet3.getSubscriber().getId());
		Assert.assertFalse(updatedEventSet3.getSubscriber().isEnable());
		Assert.assertEquals("udp://10.10.10.10:5556", updatedEventSet3.getSubscriber().getUri());

		main.undefineEventSet(id);
		Assert.assertEquals(0, main.getEventSets().size());
	}

	@Test
	public void transportTest() throws Exception {
		final CountDownLatch ready = new CountDownLatch(1);
		final CountDownLatch signal = new CountDownLatch(2);
		try (ServerSocket socket = new ServerSocket()) {
			new Thread(new Runnable() {

				void read(String expected) throws IOException {
					Socket s = socket.accept();
					try {
						InputStream stream = s.getInputStream();
						try {
							byte[] bytes = new byte[4092];
							int len = stream.read(bytes);
							Assert.assertEquals(expected, new String(bytes, 0, len));
						} finally {
							stream.close();
						}
					} finally {
						s.close();
					}
					signal.countDown();
				}

				@Override
				public void run() {
					try {
						socket.bind(null);
						ready.countDown();
						read("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><message xmlns=\"urn:havis:custom:harting:monitortransport:xsd:1\"><source>ReaderSource</source><name>Reader1</name><type>ConnectionError</type><state>true</state><timestamp>2019-01-08T15:09:27Z</timestamp><message>Connection failed</message></message>");
						read("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><message xmlns=\"urn:havis:custom:harting:monitortransport:xsd:1\"><source>TransportSource</source><name>tcp://test</name><type>TransportError</type><state>true</state><timestamp>2019-01-08T15:09:27Z</timestamp><message>Transport failed</message></message>");
					} catch (IOException e) {
						e.printStackTrace();
						Assert.fail();
					}
				}
			}).start();

			if (!ready.await(100, TimeUnit.MILLISECONDS)) {
				Assert.fail();
			}

			Main main = new Main();
			main.defineEventSet(new EventSet(new ArrayList<String>(Arrays.asList("ConnectionError", "TransportError")), new Subscriber(true, "tcp://localhost:"
					+ socket.getLocalPort())));

			// ignored
			main.monitor.notify(new ServiceSource() {

				@Override
				public String getName() {
					return "Service1";
				}
			}, new UsabilityChanged(new Date(1546960167000L), true));

			main.monitor.notify(new ReaderSource() {
				@Override
				public void setConfiguration(List<Configuration> arg0) {
				}

				@Override
				public List<Configuration> getConfiguration(ConfigurationType arg0, short arg1) {
					return null;
				}

				@Override
				public List<Capabilities> getCapabilities(CapabilityType arg0) {
					return new ArrayList<Capabilities>(Arrays.asList(new DeviceCapabilities("Reader1", null, null, null)));
				}
			}, new ConnectionError(new Date(1546960167000L), true, "Connection failed"));

			main.monitor.notify(new TransportSource() {
				@Override
				public String getUri() {
					return "tcp://test";
				}
			}, new TransportError(new Date(1546960167000L), true, "Transport failed"));

			if (!signal.await(2, TimeUnit.SECONDS))
				Assert.fail();
		}
	}

	@Test
	public void eventTypesTest() throws Exception {
		Main main = new Main();
		Assert.assertArrayEquals(new String[] { "AntennaError", "ConnectionError", "FirmwareError", "ReconnectError", "TagError", "TagEvent", "TransportError",
				"UsabilityChanged", "VisibilityChanged" }, main.getEventTypes().toArray());
	}
}
