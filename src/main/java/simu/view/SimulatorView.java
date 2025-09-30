package simu.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulatorView extends Application {
//    private ChoiceBox<Integer> numberRegister;
//    private ChoiceBox<Integer> numberNurse;
//    private ChoiceBox<Integer> numberGeneral;
//    private ChoiceBox<Integer> numberSpecialist;
//    private TextField registerTime;
//    private TextField nurseTime;
//    private TextField generalTime;
//    private TextField specialistTime;
//    private TextField arrivalTime;
//    private TextField simulationTime;
//    private TextField delayTime;
//    private Button startButton;
//    private SimuController simu.controller;
//
//    @Override
//    public void init() {
//        this.simu.controller = new SimuController(this);
//        this.numberRegister = new ChoiceBox<>();
//        this.numberRegister.getItems().addAll(1, 2, 3);
//        this.numberNurse = new ChoiceBox<>();
//        this.numberNurse.getItems().addAll(1, 2, 3);
//        this.numberGeneral = new ChoiceBox<>();
//        this.numberGeneral.getItems().addAll(1, 2, 3);
//        this.numberSpecialist = new ChoiceBox<>();
//        this.numberSpecialist.getItems().addAll(1, 2, 3);
//        this.registerTime = new TextField("Register Time");
//        this.nurseTime = new TextField("Nurse Time");
//        this.generalTime = new TextField("General Time");
//        this.specialistTime = new TextField("Specialist Time");
//        this.arrivalTime = new TextField("Arrival Time");
//        this.simulationTime = new TextField("Simulation Time");
//        this.delayTime = new TextField("Delay time");
//        this.startButton = new Button("Start");
//    }
//
//    public int getNumberRegister() {
//        return this.numberRegister.getValue();
//    }
//
//    public int getNumberNurse() {
//        return this.numberNurse.getValue();
//    }
//
//    public int getNumberGeneral() {
//        return this.numberGeneral.getValue();
//    }
//
//    public int getNumberSpecialist() {
//        return this.numberSpecialist.getValue();
//    }
//
//    public double getRegisterTime() {
//        return Double.parseDouble(this.registerTime.getText());
//    }
//
//    public double getNurseTime() {
//        return Double.parseDouble(this.nurseTime.getText());
//    }
//
//    public double getGeneralTime() {
//        return Double.parseDouble(this.generalTime.getText());
//    }
//
//    public double getSpecialistTime() {
//        return Double.parseDouble(this.specialistTime.getText());
//    }
//
//    public double getArrivalTime() {
//        return Double.parseDouble(this.arrivalTime.getText());
//    }
//
//    public double getSimulationTime() {
//        return Double.parseDouble(this.simulationTime.getText());
//    }
//
//    public long getDelayTime() {
//        return Long.parseLong(this.delayTime.getText());
//    }
//
//    public void displayClock(double time) {
//        System.out.printf("Clock is at: %.2f\n", time);
//    }
//
//    public void displayBEvent(int customerId, int serviceUnitNumber){
//        if (serviceUnitNumber != 0) {
//            System.out.printf("Customer %d move to queue of Service Unit %d\n", customerId, serviceUnitNumber);
//        } else {
//            System.out.printf("Customer %d completed service, is removed from system\n", customerId);
//        }
//    }
//
//    public void displayCEvent(int customerId, int servicePointId) {
//        System.out.printf("Customer %d is being served at service point %d\n", customerId, servicePointId);
//    }
//
//    public void start(Stage stage) {
//        stage.setTitle("Health Centre Simulator");
//        TilePane pane = new TilePane();
//        pane.getChildren().addAll(
//                numberRegister, numberNurse, numberGeneral, numberSpecialist,
//                registerTime, nurseTime, generalTime, specialistTime,
//                arrivalTime, delayTime, simulationTime, startButton
//        );
//        Scene scene = new Scene(pane);
//        stage.setScene(scene);
//        stage.show();
//
//        final boolean[] activated = {false}; // Use an array to wrap the boolean
//        final Thread[] simulator = {null};   // Use an array for the simulator thread
//
//        startButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                simu.controller.initializeModel();
//                simulator[0] = new Thread(simu.controller);
//                simulator[0].start();
//                activated[0] = true; // Modify the wrapped boolean
//            }
//        });
//
//        // Monitor the activation status in a new thread
//        new Thread(() -> {
//            while (!activated[0]) {
//                try {
//                    Thread.sleep(100); // Wait until activated
//                } catch (InterruptedException e) {
//                    System.err.println("Waiting thread interrupted");
//                }
//            }
//
//            // During simulation, if the user changes the value of delay time
//            while (simulator[0] != null && simulator[0].isAlive()) {
//                long delay = getDelayTime();
//                simu.controller.setDelayTime(delay);
//                try {
//                    Thread.sleep(100); // Avoid constant polling
//                } catch (InterruptedException e) {
//                    System.err.println("Delay adjustment thread interrupted");
//                }
//            }
//        }).start();
//    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeScreen.fxml"));
            Parent root = loader.load();

            //Set up stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("HealthCentre Simulator System");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
