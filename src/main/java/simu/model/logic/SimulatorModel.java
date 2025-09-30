package simu.model.logic;

import eduni.distributions.ContinuousGenerator;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.framework.ArrivalProcess;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;

import java.util.*;

public class SimulatorModel {
    private double simulationTime = 0;	// time when the simulation will be stopped
    private Clock clock;				// to simplify the code (clock.getClock() instead Clock.getInstance().getClock())
    protected EventList eventList;
    private final ArrivalProcess arrivalProcess;
    private final ServiceUnit[] serviceUnits;
    private double avgWaitingTime = 0;
    private List<Integer> customerCount = new ArrayList<>();
    private List<Double> utilization = new ArrayList<>();

    /*
     * This is the place where you implement your own simulator
     *
     * Demo simulation case:
     * Simulate three service points, customer goes through all three service points to get serviced
     * 		--> SP1 --> SP2 --> SP3 -->
     */
    public SimulatorModel(int numberRegister, double avgServiceTime1, int numberNurse, double avgServiceTime2, int numberGeneral, double avgServiceTime3, int numberSpecialist, double avgServiceTime4, int numberLaboratory, double avgLaboratoryTime, double avgArrivalTime) {
        this.clock = Clock.getInstance();

        this.clock.reset();
        ServiceUnit.reset();
        Customer.reset();
        ServicePoint.reset();

        this.eventList = new EventList();
        this.serviceUnits = new ServiceUnit[5];

        // exponential distribution is used to simu.model customer arrivals times, to get variability between programs runs, give a variable seed
        ContinuousGenerator arrivalTime = new Negexp(avgArrivalTime, 5);

        // Initialize the service points with the chosen service time distribution
        this.serviceUnits[0] = new ServiceUnit(new Normal(avgServiceTime1, 6, 2), eventList, EventType.DEP1, numberRegister);
        this.serviceUnits[1] = new ServiceUnit(new Normal(avgServiceTime2, 6, 2), eventList, EventType.DEP2, numberNurse);
        this.serviceUnits[2] = new ServiceUnit(new Normal(avgServiceTime3, 6, 2), eventList, EventType.DEP3, numberGeneral);
        this.serviceUnits[3] = new ServiceUnit(new Normal(avgServiceTime4, 6, 2), eventList, EventType.DEP4, numberSpecialist);
        this.serviceUnits[4] = new ServiceUnit(new Normal(avgServiceTime4, 6, 2), eventList, EventType.DEP5, numberLaboratory);

        // Initialize the arrival process
        arrivalProcess = new ArrivalProcess(arrivalTime, eventList, EventType.ARR1);
    }

    public void setSimulationTime(double time) {	// define how long we will run the simulation
        this.simulationTime = time;
    }

    // Initializes the simulation by generating the first arrival event
    public void initialize() {    // First arrival in the system
        this.arrivalProcess.generateNextEvent();
    }

    // Get the time of the next event in the event list
    public double currentTime(){
        return this.eventList.getNextEventTime();
    }

    // Checks if the simulation should continue
    public boolean simulate(){
        return this.clock.getClock() < this.simulationTime;
    }

    // process event in event list
    public Event processEvent() {
        return this.eventList.remove();
    }

    // get service unit list
    public ServiceUnit[] getServiceUnits() {
        return this.serviceUnits;
    }

    public double getAvgWaitingTime() {
        return this.avgWaitingTime;
    }

    public List<Integer> getCustomerCount() {
        return this.customerCount;
    }

    public List<Double> getUtilization() {
        return this.utilization;
    }

    public Clock getClock() {
        return this.clock;
    }

    // Processes B-phase events, such as arrivals and departures
    public AbstractMap.SimpleEntry<Customer, ServiceUnit> runEvent(Event t) {  // B phase events
        Customer customer;
        ServicePoint currentServicePoint = null;
        AbstractMap.SimpleEntry<Customer, ServiceUnit> result = new AbstractMap.SimpleEntry<>(null, null);
        boolean needLaboratory = false;

        switch ((EventType) t.getType()) {
            case ARR1:
                // Handle a new customer arrival: add to the queue of the first service point
                customer = new Customer();
                serviceUnits[0].addQueue(customer);
                arrivalProcess.generateNextEvent();        // Schedule the next arrival
                result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[0]);
                System.out.printf("Customer %d is added to queue Register.\n", customer.getId());
                break;

            case DEP1:
                // Handle departure from service point 1: move customer to the queue of service point 2
                customer = serviceUnits[0].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[0].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                System.out.printf("Customer %d finished service at Register %d.\n", customer.getId(), currentServicePoint.getId());
                serviceUnits[1].addQueue(customer);
                result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[1]);
                System.out.printf("Customer %d is added to queue Nurse.\n", customer.getId());
                break;

            case DEP2:
                // Handle departure from service point 2: move customer to the queue of service point 3 or 4
                customer = serviceUnits[1].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[1].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                System.out.printf("Customer %d finished service at Nurse Desk %d.\n", customer.getId(), currentServicePoint.getId());
                if (customer.getCustomerType().equals("general")) {        // add customer to next suitable service unit according to customer type
                    serviceUnits[2].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[2]);
                    System.out.printf("Customer %d is added to queue General.\n", customer.getId());
                } else {
                    serviceUnits[3].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[3]);
                    System.out.printf("Customer %d is added to queue Specialist.\n", customer.getId());
                }
                break;

            case DEP3:
                // Handle departure from service unit 3: complete service and remove customer from the system or move customer to the queue of service point 5
                customer = serviceUnits[2].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[2].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                System.out.printf("Customer %d finished service at General Doctor %d.\n", customer.getId(), currentServicePoint.getId());
                needLaboratory = new Random().nextBoolean();
                if (needLaboratory) {   // add customer to queue of service point 5
                    serviceUnits[4].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[4]);
                    System.out.printf("Customer %d is added to queue Laboratory.\n", customer.getId());
                } else {
                    customer.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                    customer.reportResults();
                    result = new AbstractMap.SimpleEntry<>(customer, null);       // customer is removed from system, return new position = null
                }
                break;

            case DEP4:
                // Handle departure from service unit 4: complete service and remove customer from the system or move customer to the queue of service point 5
                customer = serviceUnits[3].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[3].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                System.out.printf("Customer %d finished service at Specialist %d.\n", customer.getId(), currentServicePoint.getId());
                needLaboratory = new Random().nextBoolean();
                if (needLaboratory) {   // add customer to queue of service point 5
                    serviceUnits[4].addQueue(customer);
                    result = new AbstractMap.SimpleEntry<>(customer, serviceUnits[4]);
                    System.out.printf("Customer %d is added to queue Laboratory.\n", customer.getId());
                } else {
                    customer.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                    customer.reportResults();
                    result = new AbstractMap.SimpleEntry<>(customer, null);   // customer is removed from system, return new position = null
                }
                break;

            case DEP5:
                // Handle departure from service unit 5: complete service and remove customer from the system
                customer = serviceUnits[4].endService();           // finish service, remove first customer from serving queue
                currentServicePoint = serviceUnits[4].getSelectedServicePoint(customer);
                currentServicePoint.setCurrentCustomer(null);       // remove customer info from the served service point
                customer.setRemovalTime(Clock.getInstance().getClock());   // set end time for customer
                customer.reportResults();
                result = new AbstractMap.SimpleEntry<>(customer, null);       // customer is removed from system, return new position = null
                break;
        }
        return result;
    }

    // Processes all B-events scheduled for the current time
    public void runBEvents() {
        while (this.eventList.getNextEventTime() == this.clock.getClock()){
            runEvent(this.eventList.remove());		// Execute and remove the event from the list
        }
    }

    // Processes C-phase events, checking if any service points can begin servicing a customer
    public HashMap<Customer, ServicePoint> tryCEvents() {
        HashMap<Customer, ServicePoint> results = new HashMap<>();
        for (ServiceUnit serviceUnit : serviceUnits) {
            // check in the service unit if any service point is available and customer is on queue
            if (!serviceUnit.isReserved() && serviceUnit.isOnQueue()) {
                ServicePoint servicePoint = serviceUnit.beginService();         // Start servicing a customer if conditions are met
                Customer customer = servicePoint.getCurrentCustomer();
                results.put(customer, servicePoint);
                System.out.printf("Customer %d is being served at service point %d\n", customer.getId(), servicePoint.getId());
            }
        }
        return results;
    }

    // Outputs the results of the simulation
    public void results() {
        System.out.println("Simulation ended at " + Clock.getInstance().getClock());
        System.out.println("Average waiting time of customers " + Customer.getAvrWaitingTime());
        this.avgWaitingTime = Customer.getAvrWaitingTime();
        for (ServiceUnit serviceUnit : this.serviceUnits) {
            for (ServicePoint servicePoint : serviceUnit.getServicePoints()) {
                double serviceTime = servicePoint.getTotalServiceTime();
                int totalCustomer = servicePoint.getTotalCustomer();
                servicePoint.setUtilization(serviceTime / this.simulationTime);
                System.out.printf("Service Point %d:\n", servicePoint.getId());
                System.out.printf("Total service time: %.1f, mean service time: %.1f, total customer: %d, utilization: %.2f\n", serviceTime, servicePoint.getMeanServiceTime(), totalCustomer, servicePoint.getUtilization());
                this.customerCount.add(totalCustomer);
                this.utilization.add(servicePoint.getUtilization());
            }
        }
    }
}
