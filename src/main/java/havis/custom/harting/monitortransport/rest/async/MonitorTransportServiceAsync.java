package havis.custom.harting.monitortransport.rest.async;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;
import org.fusesource.restygwt.client.TextCallback;

import havis.custom.harting.monitortransport.EventSet;

@Path("../rest/custom/harting/monitortransport")
public interface MonitorTransportServiceAsync extends RestService {

	@GET
	@Path("")
	void get(MethodCallback<List<String>> callback);

	@GET
	@Path("events")
	public void getEventSets(MethodCallback<List<EventSet>> callback);

	@GET
	@Path("events/{id}")
	public void getEventSet(@PathParam("id") String id, MethodCallback<EventSet> callback);

	@POST
	@Path("events")
	public void defineEventSet(EventSet eventSet, TextCallback callback);

	@PUT
	@Path("events/{id}")
	public void updateEventSet(@PathParam("id") String id, EventSet eventSet, MethodCallback<Void> callback);

	@DELETE
	@Path("events/{id}")
	public void undefineEventSet(@PathParam("id") String id, MethodCallback<Void> callback);

	@GET
	@Path("events/types")
	public void getEventTypes(MethodCallback<List<String>> callback);

	@GET
	@Path("subscribers/types")
	public void getSubscriberTypes(MethodCallback<List<String>> callback);

}