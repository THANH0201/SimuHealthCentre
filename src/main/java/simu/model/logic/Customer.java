package simu.model.logic;

import simu.framework.Clock;
import simu.framework.Trace;

import java.util.Random;

/**
 * Customer class represents a customer in the simulation.
 * It tracks the customer's arrival and removal times and assigns a unique ID to each customer.
 * It also reports results and calculates the mean service time across all customers.
 */
public class Customer {
    private double arrivalTime;
    private double removalTime;
    private double serviceTime;
    private int id;
    private String customerType;
    private static int customerCount = 1;       // Counter for generating unique IDs
    private static double sumWaitingTime = 0;   // Sum of all customer service times
    private int x;
    private int y;
    private static int servedCustomerCount = 0;

    public Customer() {
        this.id = customerCount++;
        this.customerType = new Random().nextBoolean() ? "general" : "specialist";
        this.arrivalTime = Clock.getInstance().getClock();   // Set the arrival time to the current clock time
        Trace.out(Trace.Level.INFO, "New customer #" + this.id + " type: " + this.customerType + " arrived at  " + this.arrivalTime);
    }

    public double getRemovalTime() {
        return this.removalTime;
    }

    public void setRemovalTime(double removalTime) {
        this.removalTime = removalTime;
    }

    public double getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void addServiceTime(double serviceTime) {
        this.serviceTime += serviceTime;
    }

    public int getId() {
        return this.id;
    }

    public String getCustomerType() {
        return this.customerType;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int getServedCustomerCount() {
        return servedCustomerCount;
    }

    public static void setServedCustomerCount() {
        servedCustomerCount++;
    }

    public static double getAvrWaitingTime() {
        return sumWaitingTime / customerCount;
    }

    public static void reset() {
        customerCount = 1;
        servedCustomerCount = 0;
        sumWaitingTime = 0;
    }

    public void reportResults() {
        double waitingTime = this.removalTime - this.arrivalTime - this.serviceTime;
        sumWaitingTime += waitingTime;
        Trace.out(Trace.Level.INFO, "\nCustomer " + id + " type: " + customerType + " ready! ");
        Trace.out(Trace.Level.INFO, "Customer " + id + " arrived: " + arrivalTime);
        Trace.out(Trace.Level.INFO, "Customer " + id + " removed: " + removalTime);
        Trace.out(Trace.Level.INFO, "Customer " + id + " stayed: " + (removalTime - arrivalTime));
        Trace.out(Trace.Level.INFO, "Customer " + id + " waiting for " + waitingTime);

        System.out.println("Current mean of the customer service times " + getAvrWaitingTime());
    }
}
