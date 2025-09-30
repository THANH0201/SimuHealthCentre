package simu.framework;

import eduni.distributions.ContinuousGenerator;

/**
 * Manages the generation of arrival events in the simulation.
 * Uses a ContinuousGenerator to produce random inter-arrival times
 * and schedules new events in the event list.
 */

public class ArrivalProcess {
    private ContinuousGenerator generator;			// Generator for producing random inter-arrival times
    private EventList eventList;
    private IEventType type;

    public ArrivalProcess(ContinuousGenerator generator, EventList eventlist, IEventType type) {
        this.generator = generator;
        this.eventList = eventlist;
        this.type = type;
    }

    // Generates the next arrival event using the generator's sample and add to eventList
    public void generateNextEvent() {
        Event event1 = new Event(type, Clock.getInstance().getClock() + generator.sample());
        eventList.add(event1);
    }
}
