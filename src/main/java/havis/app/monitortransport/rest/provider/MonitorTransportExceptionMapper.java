package havis.app.monitortransport.rest.provider;

import havis.app.monitortransport.MonitorTransportException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MonitorTransportExceptionMapper implements ExceptionMapper<MonitorTransportException> {

	@Override
	public Response toResponse(MonitorTransportException e) {
		return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
	}
}