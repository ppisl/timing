package org.apidesign.gate.timing.server;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apidesign.gate.timing.shared.Event;

@Path("/timing/") @Singleton
public final class TimingResource {
    private final List<Event> events = new CopyOnWriteArrayList<>();
    private int counter;
    
    public TimingResource() {
        events.add(
            new Event().withId(++counter).withWhen(System.currentTimeMillis()).withType("STARTED")
        );
    }
    
    @GET @Produces(MediaType.APPLICATION_JSON)
    public List<Event> allEvents() {
        return events;
    }
    
    @GET @Produces(MediaType.APPLICATION_JSON)
    @Path("add")
    public Event addEvent(
        @QueryParam("type") String type, @QueryParam("when") long when,
        @QueryParam("ref") int ref
    ) {
        if (when <= 0) {
            when = System.currentTimeMillis();
        }
        final Event newEvent = new Event().withId(++counter).
            withWhen(when).
            withRef(ref).
            withType(type);
        events.add(0, newEvent);
        return newEvent;
    }
    
}
