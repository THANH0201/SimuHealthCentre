package simu.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CustomerView {
    private int id;
    private double x;
    private double y;
    String serviceUnitName;
    private boolean isInQueue;
    private Circle circle;
    private Color color;
    private String customerType;

    public CustomerView(int id, double x, double y, String serviceUnitName) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.serviceUnitName = serviceUnitName;
        this.isInQueue = false;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerType() {
        return this.customerType;
    }

    public Color getColor() {
        return this.color;
    }

    public Circle getCircle() {
        return this.circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
        this.setColor(this.customerType);
        this.circle.setFill(this.color);
    }

    private void setColor(String customerType) {
        if (this.customerType.equals("general")) {
            this.color = Color.GREEN;
        } else {
            this.color = Color.RED;
        }
    }

    public void setServiceUnitName(String serviceUnitName) {
        this.serviceUnitName = serviceUnitName;
    }

    public String getServiceUnitName() {
        return this.serviceUnitName;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void toggleInQueue() {
        this.isInQueue = !this.isInQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.isInQueue = inQueue;
    }

    @Override
    public String toString() {
        if (this.serviceUnitName.equals("arrival")) {
            return "customer " + this.id + " arrive at system,  pos = (" + this.x + "," + this.y + ")";
        }
        String inQueueStr = "Queue";
        if (!this.isInQueue) {
            inQueueStr = "service point";
        }
        return "customer " + this.id + " is at service unit " + this.serviceUnitName + " , in " + inQueueStr + " pos = (" + this.x + "," + this.y + ")";
    }
}