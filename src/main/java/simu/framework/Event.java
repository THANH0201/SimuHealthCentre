package simu.framework;

/**
 * Even class represents a simulation event with a type and time,
 * enabling chronological sorting for proper processing.
 */

public class Event implements Comparable<Event> {
	private IEventType type;
	private double time;
	
	public Event(IEventType type, double time) {
		this.type = type;
		this.time = time;
	}
	
	public void setType(IEventType type) {
		this.type = type;
	}
	public IEventType getType() {
		return type;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getTime() {
		return time;
	}

    // Compares this event with another event based on their scheduled times, used to sort events in a priority queue
    @Override
	public int compareTo(Event arg) {
		if (this.time < arg.time) return -1;
		else if (this.time > arg.time) return 1;
		return 0;
	}
}
