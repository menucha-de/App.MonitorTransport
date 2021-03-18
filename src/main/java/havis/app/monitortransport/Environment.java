package havis.app.monitortransport;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Environment {

	private final static Logger log = Logger.getLogger(Environment.class.getName());
	private final static Properties properties = new Properties();

	static {
		try (InputStream stream = Environment.class.getClassLoader().getResourceAsStream("havis.app.monitortransport.properties")) {
			properties.load(stream);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load environment properties", e);
		}
	}

	public static final String CONFIG_FILE = properties.getProperty("havis.app.monitortransport.configFile",
			"conf/havis/app/monitortransport/config.json");
}