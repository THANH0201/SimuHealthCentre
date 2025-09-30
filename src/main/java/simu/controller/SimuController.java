package simu.controller;

import javafx.application.Platform;
import simu.framework.Clock;
import simu.framework.Trace;
import simu.model.logic.Customer;
import simu.model.logic.ServicePoint;
import simu.model.logic.ServiceUnit;
import simu.model.logic.SimulatorModel;

import java.util.AbstractMap;
import java.util.List;

public class SimuController implements Runnable {
    private SimulatorModel simuModel;
    private ConfigurationController configController;
    private SimulatorController simulatorController;
    private ResultController resultController;
    private long delayTime;
    private final Clock clock;
    private int numberRegister;
    private int numberNurse;
    private int numberGeneral;
    private int numberSpecialist;
    private int numberLaboratory;
    private int avgRegisterTime;
    private int avgNurseTime;
    private int avgGeneralTime;
    private int avgSpecialistTime;
    private int avgLaboratoryTime;

    public SimuController(ConfigurationController configController, SimulatorController simulatorController, ResultController resultController) {
        this.configController = configController;
        this.simulatorController = simulatorController;
        this.clock = Clock.getInstance();
        this.delayTime = configController.getDelayTime(); //initialize with initial delay
        this.resultController = resultController;
    }

    public void initializeModel() {
        this.numberRegister = this.configController.getNumberRegister();
        this.numberNurse = this.configController.getNumberNurse();
        this.numberGeneral = this.configController.getNumberGeneral();
        this.numberSpecialist = this.configController.getNumberSpecialist();
        this.numberLaboratory = this.configController.getNumberLaboratory();

        this.avgRegisterTime = this.configController.getRegisterTime();
        this.avgNurseTime = this.configController.getNurseTime();
        this.avgGeneralTime = this.configController.getGeneralTime();
        this.avgSpecialistTime = this.configController.getSpecialistTime();
        this.avgLaboratoryTime = this.configController.getLaboratoryTime();

        double avgArrivalTime = this.configController.getArrivalTime();
        double simulationTime = this.configController.getSimulationTime();

        this.simuModel = new SimulatorModel(numberRegister, avgRegisterTime, numberNurse, avgNurseTime,
                numberGeneral, avgGeneralTime, numberSpecialist, avgSpecialistTime, numberLaboratory, avgLaboratoryTime,
                avgArrivalTime);
        this.simuModel.setSimulationTime(simulationTime);
    }

    public long getDelayTime() {
        return this.delayTime;
    }

    public SimulatorModel getSimuModel() {
        return this.simuModel;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public void run() {
        Trace.setTraceLevel(Trace.Level.INFO);
        if (simuModel == null) {
            System.err.println("SimulatorModel is not initialized. Please set up the parameters first.");
            return;
        }

        simuModel.initialize();
        while (simuModel.simulate()) {
            // set clock
            clock.setClock(simuModel.currentTime());
            // display clock
            Platform.runLater(() -> simulatorController.displayClock(clock.getClock()));

            // Processes all B-events scheduled for the current time
            while (simuModel.currentTime() == clock.getClock()) {
                // process each B-event and display result
                AbstractMap.SimpleEntry<Customer, ServiceUnit> result = simuModel.runEvent(simuModel.processEvent());        // Execute and remove the event from the list
                // get necessary value from result
                Customer customer = result.getKey();
                ServiceUnit serviceUnit = result.getValue();
                // call display method from simu.view
                Platform.runLater(() -> simulatorController.displayBEvent(customer, serviceUnit));
            }

            // add some delay between 2 phases, wait for animation to complete in phase B
            try {
                Thread.sleep(delayTime / 2);
            } catch (InterruptedException e) {
                Trace.out(Trace.Level.ERR, "Simulation thread interrupted.");
                Thread.currentThread().interrupt(); // Reset the interrupted status
                break; // Exit the loop
            }

            // Processes C-phase events, checking if any service points can begin servicing a customer
            for (ServiceUnit serviceUnit : simuModel.getServiceUnits()) {
                // check in the service unit if any service point is available and customer is on queue
                if (!serviceUnit.isReserved() && serviceUnit.isOnQueue()) {
                    // start servicing a customer if conditions are met
                    ServicePoint servicePoint = serviceUnit.beginService();
                    Customer customer = servicePoint.getCurrentCustomer();
                    // get necessary value from result and display in simu.view
                    Platform.runLater(() -> simulatorController.displayCEvent(customer, servicePoint));
                }
            }

            try {
                Thread.sleep(delayTime / 2);
            } catch (InterruptedException e) {
                Trace.out(Trace.Level.ERR, "Simulation thread interrupted.");
                Thread.currentThread().interrupt(); // Reset the interrupted status
                break; // Exit the loop
            }
        }

        // Ensure results are printed only if the simulation time is completed
        if (clock.getClock() >= configController.getSimulationTime()) {
            Platform.runLater(() -> {
                simuModel.results();
                // Get the results from the simu.model
                double avgWaitingTime = simuModel.getAvgWaitingTime();
                List<Integer> customerCount = simuModel.getCustomerCount();
                List<Double> utilization = simuModel.getUtilization();

                // Display the results
                resultController.setTable(numberRegister, numberNurse, numberGeneral, numberSpecialist, numberLaboratory,
                        avgRegisterTime, avgNurseTime, avgGeneralTime, avgSpecialistTime, avgLaboratoryTime);
                resultController.display(avgWaitingTime, customerCount, utilization, simulatorController.getStage());
            });
        }
    }
}
