package havis.custom.harting.monitortransport;

public class MonitorTransportException extends Exception {

	private static final long serialVersionUID = 1L;

	public MonitorTransportException(String message) {
		super(message);
	}

	public MonitorTransportException(String message, Throwable cause) {
		super(message, cause);
	}

	public MonitorTransportException(Throwable cause) {
		super(cause);
	}
}