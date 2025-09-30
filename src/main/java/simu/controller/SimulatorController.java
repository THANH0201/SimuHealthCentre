package simu.controller;

import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import simu.model.logic.Customer;
import simu.model.logic.ServicePoint;
import simu.model.logic.ServiceUnit;
import simu.model.logic.SimulatorModel;
import simu.view.CustomerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimulatorController {
    // FXML Components
    @FXML
    public Label registerQueue, nurseQueue, generalQueue, specialistQueue, laboratoryQueue,
            registerLabel1, registerLabel2, registerLabel3, nurseLabel1, nurseLabel2, nurseLabel3,
            generalLabel1, generalLabel2, generalLabel3, specialistLabel1, specialistLabel2, specialistLabel3,
            laboratoryLabel1, laboratoryLabel2, laboratoryLabel3, timeLabel;
    @FXML
    public Button backButton;
    @FXML
    private Line registerLine, nurseLine, generalLine, specialistLine, laboratoryLine;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Slider speedSlider;

    private SimuController controller;
    private boolean activated = false;
    private HashMap<Integer, CustomerView> customerViewList;
    private double[] registerCoors, nurseCoors, generalCoors, specialistCoors, laboratoryCoors,
            registerQueueCoors, nurseQueueCoors, generalQueueCoors, specialistQueueCoors, laboratoryQueueCoors,
            arrivalCoors, exitCoors;
    private Thread simulatorThread;
    private Thread speedMonitorThread;
    private Stage stage;

    @FXML
    public void backButtonAction(MouseEvent mouseEvent) {
        try {
            //stop all threads
            if (simulatorThread != null && simulatorThread.isAlive()) {
                simulatorThread.interrupt();
            }
            if (speedMonitorThread != null && speedMonitorThread.isAlive()) {
                speedMonitorThread.interrupt();
            }

            //reset values
            activated = false;
            controller = null;

            registerCoors = null;
            nurseCoors = null;
            generalCoors = null;
            specialistCoors = null;
            laboratoryCoors = null;

            registerQueueCoors = null;
            nurseQueueCoors = null;
            generalQueueCoors = null;
            specialistQueueCoors = null;
            laboratoryQueueCoors = null;

            arrivalCoors = null;
            exitCoors = null;

            //load main menu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulationConfig.fxml"));
            Parent mainMenuRoot = loader.load();
            stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(mainMenuRoot);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        //start speed monitor thread
        speedMonitorThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) { // Check for thread interruptions
                if (activated && controller != null) {
                    long speed = (long) speedSlider.getValue(); //fetch speed from UI
                    long delay = 2200 - speed;
                    controller.setDelayTime(delay);
                    try {
                        Thread.sleep(100); // Polling interval for speed adjustments
                    } catch (InterruptedException e) {
                        System.err.println("Speed monitor thread interrupted.");
                        break; // Exit loop on interruption
                    }
                } else {
                    try {
                        Thread.sleep(100); // Wait until simulation is activated
                    } catch (InterruptedException e) {
                        System.err.println("Speed monitor thread interrupted.");
                        break; // Exit loop on interruption
                    }
                }
            }
        });
        speedMonitorThread.start();
    }

    public void initializeSimulation(int registerCount, int nurseCount, int generalCount, int specialistCount, int laboratoryCount, ConfigurationController configView, ResultController resultController) {
        this.customerViewList = new HashMap<>();
        controller = new SimuController(configView, this, resultController);
        int delayTime = configView.getDelayTime();
        speedSlider.setValue(2200 - delayTime);
        controller.initializeModel();
        simulatorThread = new Thread(controller);

        // must move simu.controller and thread to top because setupCoor from simu.view to simu.model need simu.controller
        // setup Simulation background
        setupScene(registerCount, nurseCount, generalCount, specialistCount, laboratoryCount);

        // calculate and set coordinates
        setCoordinates(registerCount, nurseCount, generalCount, specialistCount, laboratoryCount);

        simulatorThread.start();//start simu.controller thread parallel with UI
        activated = true;

    }

    private void setupScene(int registerCount, int nurseCount, int generalCount, int specialistCount, int laboratoryCount) {
        setServiceUnitVisibility(registerLine, registerLabel1, registerLabel2, registerLabel3, registerCount);
        setServiceUnitVisibility(nurseLine, nurseLabel1, nurseLabel2, nurseLabel3, nurseCount);
        setServiceUnitVisibility(generalLine, generalLabel1, generalLabel2, generalLabel3, generalCount);
        setServiceUnitVisibility(specialistLine, specialistLabel1, specialistLabel2, specialistLabel3, specialistCount);
        setServiceUnitVisibility(laboratoryLine, laboratoryLabel1, laboratoryLabel2, laboratoryLabel3, laboratoryCount);
        updateSpecialistLabelsBasedOnGeneralCount(generalCount);
    }

    private void setCoordinates(int registerCount, int nurseCount, int generalCount, int specialistCount, int laboratoryCount) {
        //wait for the rootPane's layout to complete
        SimulatorModel simulatorModel = this.controller.getSimuModel();

        ServiceUnit registerUnit = simulatorModel.getServiceUnits()[0];
        ServiceUnit nurseUnit = simulatorModel.getServiceUnits()[1];
        ServiceUnit generalUnit = simulatorModel.getServiceUnits()[2];
        ServiceUnit specialistUnit = simulatorModel.getServiceUnits()[3];
        ServiceUnit laboratoryUnit = simulatorModel.getServiceUnits()[4];

        ArrayList<ServicePoint> registerSPs = registerUnit.getServicePoints();
        ArrayList<ServicePoint> nurseSPs = nurseUnit.getServicePoints();
        ArrayList<ServicePoint> generalSPs = generalUnit.getServicePoints();
        ArrayList<ServicePoint> specialistSPs = specialistUnit.getServicePoints();
        ArrayList<ServicePoint> laboratorySPs = laboratoryUnit.getServicePoints();

        rootPane.boundsInParentProperty().addListener((observable, oldBounds, newBounds) -> {
            if (rootPane.isVisible()) {
                // Register coordinates
                registerCoors = this.setCoordinates(registerCount, registerLabel1, registerLabel2, registerLabel3);
                nurseCoors = this.setCoordinates(nurseCount, nurseLabel1, nurseLabel2, nurseLabel3);
                generalCoors = this.setCoordinates(generalCount, generalLabel1, generalLabel2, generalLabel3);
                specialistCoors = this.setCoordinates(specialistCount, specialistLabel1, specialistLabel2, specialistLabel3);
                laboratoryCoors = this.setCoordinates(laboratoryCount, laboratoryLabel1, laboratoryLabel2, laboratoryLabel3);

//                if (registerCount >= 1) {
//                    registerCoors = new double[]{registerLabel1.localToScene(registerLabel1.getBoundsInLocal()).getMinX(), registerLabel1.localToScene(registerLabel1.getBoundsInLocal()).getMinY()};
//                }
//                if (registerCount == 2) {
//                    registerCoors = new double[]{registerLabel2.localToScene(registerLabel2.getBoundsInLocal()).getMinX(), registerLabel2.localToScene(registerLabel2.getBoundsInLocal()).getMinY(), registerLabel3.localToScene(registerLabel3.getBoundsInLocal()).getMinX(), registerLabel3.localToScene(registerLabel3.getBoundsInLocal()).getMinY()};
//                }

                // General coordinates
//                if (generalCount >= 1) {
//                    generalCoors = new double[]{generalLabel1.localToScene(generalLabel1.getBoundsInLocal()).getMinX(), generalLabel1.localToScene(generalLabel1.getBoundsInLocal()).getMinY()};
//                }
//                if (generalCount == 2) {
//                    generalCoors = new double[]{generalLabel2.localToScene(generalLabel2.getBoundsInLocal()).getMinX(), generalLabel2.localToScene(generalLabel2.getBoundsInLocal()).getMinY(), generalLabel3.localToScene(generalLabel3.getBoundsInLocal()).getMinX(), generalLabel3.localToScene(generalLabel3.getBoundsInLocal()).getMinY()};
//                }

                // Specialist coordinates
//                if (specialistCount >= 1) {
//                    specialistCoors = new double[]{specialistLabel1.localToScene(specialistLabel1.getBoundsInLocal()).getMinX(), specialistLabel1.localToScene(specialistLabel1.getBoundsInLocal()).getMinY()};
//                }
//                if (specialistCount == 2) {
//                    specialistCoors = new double[]{specialistLabel2.localToScene(specialistLabel2.getBoundsInLocal()).getMinX(), specialistLabel2.localToScene(specialistLabel2.getBoundsInLocal()).getMinY(), specialistLabel3.localToScene(specialistLabel3.getBoundsInLocal()).getMinX(), specialistLabel3.localToScene(specialistLabel3.getBoundsInLocal()).getMinY()};
//                }

                registerQueueCoors = new double[]{registerQueue.localToScene(registerQueue.getBoundsInLocal()).getMinX(), registerQueue.localToScene(registerQueue.getBoundsInLocal()).getMinY()};
                nurseQueueCoors = new double[]{nurseQueue.localToScene(nurseQueue.getBoundsInLocal()).getMinX(), nurseQueue.localToScene(nurseQueue.getBoundsInLocal()).getMinY()};
                generalQueueCoors = new double[]{generalQueue.localToScene(generalQueue.getBoundsInLocal()).getMinX(), generalQueue.localToScene(generalQueue.getBoundsInLocal()).getMinY()};
                specialistQueueCoors = new double[]{specialistQueue.localToScene(specialistQueue.getBoundsInLocal()).getMinX(), specialistQueue.localToScene(specialistQueue.getBoundsInLocal()).getMinY()};
                laboratoryQueueCoors = new double[]{laboratoryQueue.localToScene(laboratoryQueue.getBoundsInLocal()).getMinX(), laboratoryQueue.localToScene(laboratoryQueue.getBoundsInLocal()).getMinY()};

                arrivalCoors = new double[]{0, rootPane.getHeight() / 2};
                exitCoors = new double[]{rootPane.getWidth(), rootPane.getHeight() / 2};

                //set queue coordinate in simu.model
                this.registerServiceUnitCoordinate(registerUnit, registerQueueCoors);
                this.registerServiceUnitCoordinate(nurseUnit, nurseQueueCoors);
                this.registerServiceUnitCoordinate(generalUnit, generalQueueCoors);
                this.registerServiceUnitCoordinate(specialistUnit, specialistQueueCoors);
                this.registerServiceUnitCoordinate(laboratoryUnit, laboratoryQueueCoors);

                //set SP coordinate in simu.model
                this.registerServicePointsCoordinate(registerSPs, registerCoors);
                this.registerServicePointsCoordinate(nurseSPs, nurseCoors);
                this.registerServicePointsCoordinate(generalSPs, generalCoors);
                this.registerServicePointsCoordinate(specialistSPs, specialistCoors);
                this.registerServicePointsCoordinate(laboratorySPs, laboratoryCoors);
            }
        });
    }

    private double[] setCoordinates(int count, Label label1, Label label2, Label label3) {
        if (count == 2) {
            return new double[]{label2.localToScene(label2.getBoundsInLocal()).getMinX(), label2.localToScene(label2.getBoundsInLocal()).getMinY(), label3.localToScene(label3.getBoundsInLocal()).getMinX(), label3.localToScene(label3.getBoundsInLocal()).getMinY()};
        }
        return new double[]{label1.localToScene(label1.getBoundsInLocal()).getMinX(), label1.localToScene(label1.getBoundsInLocal()).getMinY()};
    }

    private void setServiceUnitVisibility(Line line, Label label1, Label label2, Label label3, int count) {
        line.setVisible(count == 2);
        label1.setVisible(count == 1);
        label2.setVisible(count == 2);
        label3.setVisible(count == 2);
    }

    private void updateSpecialistLabelsBasedOnGeneralCount(int generalCount) {
        String label1Text = (generalCount == 1) ? "2" : "3";
        String label2Text = (generalCount == 1) ? "2" : "3";
        String label3Text = (generalCount == 1) ? "3" : "4";

        specialistLabel1.setText(label1Text);
        specialistLabel2.setText(label2Text);
        specialistLabel3.setText(label3Text);
    }

    private void registerServiceUnitCoordinate(ServiceUnit serviceUnit, double[] serviceUnitCoor) {
        serviceUnit.setX((int) serviceUnitCoor[0]);
        serviceUnit.setY((int) serviceUnitCoor[1]);

    }

    private void registerServicePointsCoordinate(ArrayList<ServicePoint> spList, double[] spCoors) {
        for (int i = 0; i < spList.size(); i++) {
            ServicePoint currenSP = spList.get(i);
            currenSP.setX((int) spCoors[2 * i]);
            currenSP.setY((int) spCoors[2 * i + 1]);

        }
    }

    public void setCloseEventListener(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            if (simulatorThread != null && simulatorThread.isAlive()) {
                simulatorThread.interrupt();
            }
            if (speedMonitorThread != null && speedMonitorThread.isAlive()) {
                speedMonitorThread.interrupt();
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    @FXML
    public void displayClock(double time) {
        String timeStr = String.format(Locale.US, "%.2f", time);

        this.timeLabel.setText(timeStr + " min");
    }

    public void displayBEvent(Customer customer, ServiceUnit su) {
        String serviceUnitName;
        double newX, newY;

        int serviceUnitNumber = -1;
        int customerId = customer.getId();

        if (su == null) {
            serviceUnitName = "exit";
            newX = exitCoors[0];
            newY = exitCoors[1];
        } else {
            serviceUnitNumber = su.getIndex();
            serviceUnitName = getSerViceUnitName(serviceUnitNumber);
            newX = su.getX();
            newY = su.getY();
        }

        CustomerView customerView = getCustomerInfo(customerId);
        customerView.setCustomerType(customer.getCustomerType());

        customerView.setServiceUnitName(serviceUnitName);
        if (serviceUnitNumber != 0) {
            customerView.setInQueue(true);
        }
        this.animateCirle(customerView, newX, newY);

    }

    public boolean isCustomerExist(int customerid) {
        return this.customerViewList.containsKey(customerid);
    }

    public CustomerView getCustomerInfo(int cusomterId) {
        CustomerView foundCustomer = null;
        // if no customer found, create this customer and add to customerViewList
        if (!this.isCustomerExist(cusomterId)) {
            CustomerView newCustomerView = new CustomerView(cusomterId, arrivalCoors[0], arrivalCoors[1], "arrival");
            this.customerViewList.put(cusomterId, newCustomerView);
            return newCustomerView;
        }
        // if found, return the found customer
        for (Map.Entry<Integer, CustomerView> currentCustomerView : this.customerViewList.entrySet()) {
            int curCustomerId = currentCustomerView.getKey();
            if (curCustomerId == cusomterId) {
                foundCustomer = currentCustomerView.getValue();
            }
        }
        return foundCustomer;

    }

    public static String getSerViceUnitName(int serviceUnitNumber) {
        switch (serviceUnitNumber) {
            case 1:
                return "register";
            case 2:
                return "nurse";
            case 3:
                return "general";
            case 4:
                return "specialist";
            case 5:
                return "laboratory";
        }
        return null;
    }

    public void displayCEvent(Customer curstomer, ServicePoint sp) {
        int customerId = curstomer.getId();
        int servicePointId = sp.getId();
        CustomerView customerView = getCustomerInfo(customerId);
        String serviceUnitName = customerView.getServiceUnitName();

        double newX = sp.getX();
        double newY = sp.getY();


        // animation
        customerView.setInQueue(false);
        this.animateCirle(customerView, newX, newY);
    }

    private void animateCirle(CustomerView customerView, double newX, double newY) {
        Circle movingCircle = customerView.getCircle();

        if (movingCircle == null) {

            movingCircle = new Circle(10); //Create a new circle with radius 10
//            moving.set
            rootPane.getChildren().add(movingCircle); //Add the circle to the root pane
            customerView.setCircle(movingCircle);
        }

        double curX = customerView.getX();
        double curY = customerView.getY();
        long delay = controller.getDelayTime();

        Path path = new Path();
        path.getElements().add(new MoveTo(curX, curY));
        path.getElements().add(new LineTo(newX, newY));

        PathTransition pathTransition = new PathTransition();

        //mock duration
//        pathTransition.setDuration(Duration.millis(300));
        //real delay
        // this delay must be shorter than delay in simu.controller to make sure the the ball complete transition before  calculate in C
        // delay = one cycle ABC
        // A B delay/2 C delay/2
        pathTransition.setDuration(Duration.millis(delay * 0.3));

        pathTransition.setPath(path);
        pathTransition.setNode(movingCircle);
        // remove the old circle after the animation is finished
        pathTransition.setOnFinished(event -> {
            customerView.setX(newX);
            customerView.setY(newY);
        });
        pathTransition.play();
    }
}
