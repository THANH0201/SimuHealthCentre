package simu.model.logic;

import eduni.distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

import java.util.ArrayList;
import java.util.LinkedList;

public class ServiceUnit {
    private LinkedList<Customer> queue = new LinkedList<>();
    private LinkedList<Customer> servingQueue = new LinkedList<>();
    private ArrayList<ServicePoint> servicePoints = new ArrayList<>();
    private ContinuousGenerator generator;
    private EventList eventList;
    private EventType eventTypeScheduled;
    private int x;
    private int y;
    private int index;
    private static int count = 1;

    public ServiceUnit(ContinuousGenerator generator, EventList eventList, EventType type, int servicePointNumber) {
        this.eventList = eventList;
        this.generator = generator;
        this.eventTypeScheduled = type;
        for (int i = 1; i <= servicePointNumber; i++) {
            ServicePoint servicePoint = new ServicePoint();
            servicePoints.add(servicePoint);
        }
        this.index = count++;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIndex() {
        return this.index;
    }

    // Adds a customer to the queue. The first customer in the queue will be serviced
    public void addQueue(Customer a) {    // The first customer of the queue is always in service
        this.queue.add(a);
//		Trace.out(Trace.Level.INFO, "Add customer" + a.getId() + " to queue type " + type );
    }

    // Remove customer from serving queue, complete the service
    public Customer endService() {
        return this.servingQueue.poll();
    }

    // Begins servicing the first customer in the queue ( remove from queue and add to the serving queue. The first customer will complete service first)
    public ServicePoint beginService() {
        Customer servingCustomer = this.queue.poll();
        this.servingQueue.add(servingCustomer);
        ServicePoint selectedServicePoint = null;
//		Trace.out(Trace.Level.INFO, "Starting a new service for the customer #" + servingCustomer.getId());
        for (ServicePoint servicePoint : this.servicePoints) {
            if (servicePoint.isAvailable()) {
                servicePoint.setCurrentCustomer(servingCustomer);
                selectedServicePoint = servicePoint;
                break;
            }
        }
        double serviceTime;
        do {
            serviceTime = this.generator.sample(); // Generate service time
        } while (serviceTime <= 0);

        servingCustomer.addServiceTime(serviceTime);
        selectedServicePoint.addServiceTime(serviceTime);
        selectedServicePoint.addCustomer();
        this.eventList.add(new Event(this.eventTypeScheduled, Clock.getInstance().getClock() + serviceTime));
        return selectedServicePoint;
    }

    // Checks if the service point is currently reserved
    public boolean isReserved() {
        for (ServicePoint servicePoint : this.servicePoints) {
            if (servicePoint.isAvailable()) {
                return false;
            }
        }
        return true;
    }

    // Checks if there are any customers waiting in the queue
    public boolean isOnQueue() {
        return !queue.isEmpty();
    }

    // retrieve selected service point
    public ServicePoint getSelectedServicePoint(Customer customer) {
        for (ServicePoint servicePoint : this.servicePoints) {
            if (!servicePoint.isAvailable() && servicePoint.getCurrentCustomer().equals(customer)) {
                return servicePoint;
            }
        }
        return null;
    }

    public ArrayList<ServicePoint> getServicePoints() {
        return this.servicePoints;
    }

    public static void reset() {
        count = 1;
    }
}
