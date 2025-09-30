package simu.framework;

import java.util.PriorityQueue;

/**
 * EventList class manages a list of events using a priority queue.
 * Events are ordered by their scheduled time, ensuring that the event with the
 * earliest time is processed first.
 */

public class EventList {

    // Priority queue to store and manage events in chronological order
    private PriorityQueue<Event> eventlist;

    public EventList() {
        eventlist = new PriorityQueue<>();
    }

    // Removes and returns the event at the front of the event list (earliest event) and logs the action using the Trace class
    public Event remove() {
//		Trace.out(Trace.Level.INFO,"Removing from the event list " + eventlist.peek().getType() + " " + eventlist.peek().getValue());
        return eventlist.remove();
    }

    // Adds a new event to the event list and logs the action using the Trace class

    public void add(Event t) {
//		Trace.out(Trace.Level.INFO,"Adding to the event list " + t.getType() + " " + t.getValue());
        eventlist.add(t);
    }

    // Returns the time of the next event in the event list
    public double getNextEventTime(){
        return eventlist.peek().getTime();
    }
}