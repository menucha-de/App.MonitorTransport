package havis.custom.harting.monitortransport.rest;

import havis.custom.harting.monitortransport.EventSet;
import havis.custom.harting.monitortransport.Main;
import havis.custom.harting.monitortransport.MonitorTransportException;
import havis.net.rest.shared.Resource;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("custom/harting/monitortransport")
public class MonitorTransportService extends Resource {

	private Main main;

	public MonitorTransportService(Main main) {
		this.main = main;
	}

	@PermitAll
	@GET
	@Path("events")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<EventSet> getEventSets() {
		return main.getEventSets();
	}

	@GET
	@Path("events/{id}")
	@PermitAll
	@Produces({ MediaType.APPLICATION_JSON })
	public EventSet getEventSet(@PathParam("id") String id) {
		return main.getEventSet(id);
	}

	@PermitAll
	@POST
	@Path("events")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces(MediaType.TEXT_PLAIN)
	public String defineEventSet(EventSet eventSet) throws MonitorTransportException {
		return main.defineEventSet(eventSet);
	}

	@PermitAll
	@PUT
	@Path("events/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void updateEventSet(@PathParam("id") String id, EventSet eventSet) throws MonitorTransportException {
		main.updateEventSet(id, eventSet);
	}

	@PermitAll
	@DELETE
	@Path("events/{id}")
	public void undefineEventSet(@PathParam("id") String id) throws MonitorTransportException {
		main.undefineEventSet(id);
	}

	@GET
	@Path("events/types")
	@PermitAll
	@Produces({ MediaType.APPLICATION_JSON })
	public List<String> getEventTypes() throws MonitorTransportException {
		return main.getEventTypes();
	}

	@GET
	@Path("subscribers/types")
	@PermitAll
	@Produces({ MediaType.APPLICATION_JSON })
	public List<String> getSubscriberTypes() throws MonitorTransportException {
		return main.getSubscriberTypes();
	}

}