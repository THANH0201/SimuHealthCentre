package simu.model.logic;

public class ServicePoint {
    private int id;
    private static int count = 1;
    private Customer currentCustomer = null;
    private double totalServiceTime;
    private int totalCustomer;
    private double meanServiceTime;
    private double utilization;
    private int x;
    private int y;

    public ServicePoint() {
        this.id = count++;
        this.totalServiceTime = 0;
        this.totalCustomer = 0;
    }

    public boolean isAvailable() {
        return this.currentCustomer == null;
    }

    public int getId() {
        return this.id;
    }

    public Customer getCurrentCustomer() {
        return this.currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public void addServiceTime(double serviceTime) {
        this.totalServiceTime += serviceTime;
    }

    public double getTotalServiceTime() {
        return this.totalServiceTime;
    }

    public void addCustomer() {
        this.totalCustomer++;
    }

    public int getTotalCustomer() {
        return this.totalCustomer;
    }

    public double getMeanServiceTime() {
        this.meanServiceTime = this.totalServiceTime / this.totalCustomer;
        return this.meanServiceTime;
    }

    public double getUtilization() {
        return this.utilization;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
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

    public static void reset() {
        count = 1;
    }
}
