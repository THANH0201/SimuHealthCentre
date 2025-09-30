package simu.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import simu.model.dao.ConfigurationDao;
import simu.model.entity.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationController {
    @FXML
    public TextField registerTime, nurseTime, generalTime, specialistTime, laboratoryTime, arrivalTime, simulationTime;
    @FXML
    private ChoiceBox<String> registerChoice, nurseChoice, generalChoice, specialistChoice, laboratoryChoice;
    @FXML
    Spinner<Integer> delayField;
    @FXML
    private Button simulateButton;

    private final ConfigurationDao configurationDao = new ConfigurationDao();

    @FXML
    private void initialize() {
        setupChoiceBoxes();
        setupNumericValidation();
        loadSavedSettings();
        simulateButton.setOnAction(event -> {
            simulateButtonAction();
        });
    }

    private void setupChoiceBoxes() {
        registerChoice.getItems().addAll("1", "2");
        nurseChoice.getItems().addAll("1", "2");
        generalChoice.getItems().addAll("1", "2");
        specialistChoice.getItems().addAll("1", "2");
        laboratoryChoice.getItems().addAll("1", "2");

        delayField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(200, 2000, 1000, 200));

        registerChoice.setValue("1");
        nurseChoice.setValue("1");
        generalChoice.setValue("1");
        specialistChoice.setValue("1");
        laboratoryChoice.setValue("1");
    }

    private void setupNumericValidation() {
        addNumericValidation(arrivalTime);
        addNumericValidation(registerTime);
        addNumericValidation(nurseTime);
        addNumericValidation(generalTime);
        addNumericValidation(specialistTime);
        addNumericValidation(laboratoryTime);
        addNumericValidation(simulationTime);
    }

    private void addNumericValidation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            //allow empty input or valid numbers with decimals
            if (newValue.isEmpty() || newValue.matches("\\d+(\\.\\d+)?")) {
                return;
            }
            //revert to the previous valid value
            textField.setText(oldValue);

            //show alert only if the user enters invalid characters
            showAlert("Invalid Input", "Please enter only numbers.");
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadSavedSettings() {
        Map<String, Object> settings = this.loadConfigs();

        if (settings.containsKey("ArrivalTime")) arrivalTime.setText(String.valueOf(settings.get("ArrivalTime")));
        if (settings.containsKey("SimulationTime"))
            simulationTime.setText(String.valueOf(settings.get("SimulationTime")));
        if (settings.containsKey("DelayTime"))
            delayField.getValueFactory().setValue(Integer.parseInt(String.valueOf(settings.get("DelayTime"))));

        if (settings.containsKey("RegisterServiceTime"))
            registerTime.setText(String.valueOf(settings.get("RegisterServiceTime")));
        if (settings.containsKey("NurseServiceTime"))
            nurseTime.setText(String.valueOf(settings.get("NurseServiceTime")));
        if (settings.containsKey("GeneralServiceTime"))
            generalTime.setText(String.valueOf(settings.get("GeneralServiceTime")));
        if (settings.containsKey("SpecialistServiceTime"))
            specialistTime.setText(String.valueOf(settings.get("SpecialistServiceTime")));
        if (settings.containsKey("LaboratoryServiceTime"))
            laboratoryTime.setText(String.valueOf(settings.get("LaboratoryServiceTime")));

        if (settings.containsKey("RegisterServicePoint"))
            registerChoice.setValue(String.valueOf(settings.get("RegisterServicePoint")));
        if (settings.containsKey("NurseServicePoint"))
            nurseChoice.setValue(String.valueOf(settings.get("NurseServicePoint")));
        if (settings.containsKey("GeneralServicePoint"))
            generalChoice.setValue(String.valueOf(settings.get("GeneralServicePoint")));
        if (settings.containsKey("SpecialistServicePoint"))
            specialistChoice.setValue(String.valueOf(settings.get("SpecialistServicePoint")));
        if (settings.containsKey("LaboratoryServicePoint"))
            laboratoryChoice.setValue(String.valueOf(settings.get("LaboratoryServicePoint")));
    }

    private void saveCurrentSettings() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("ArrivalTime", Integer.parseInt(arrivalTime.getText()));
        configs.put("SimulationTime", Integer.parseInt(simulationTime.getText()));
        configs.put("DelayTime", delayField.getValue());

        configs.put("RegisterServiceTime", Integer.parseInt(registerTime.getText()));
        configs.put("NurseServiceTime", Integer.parseInt(nurseTime.getText()));
        configs.put("GeneralServiceTime", Integer.parseInt(generalTime.getText()));
        configs.put("SpecialistServiceTime", Integer.parseInt(specialistTime.getText()));
        configs.put("LaboratoryServiceTime", Integer.parseInt(laboratoryTime.getText()));

        configs.put("RegisterServicePoint", Integer.parseInt(registerChoice.getValue()));
        configs.put("NurseServicePoint", Integer.parseInt(nurseChoice.getValue()));
        configs.put("GeneralServicePoint", Integer.parseInt(generalChoice.getValue()));
        configs.put("SpecialistServicePoint", Integer.parseInt(specialistChoice.getValue()));
        configs.put("LaboratoryServicePoint", Integer.parseInt(laboratoryChoice.getValue()));

        this.saveConfigs(configs);
    }

    private void simulateButtonAction() {
        try {
            //check if text fields have values and alert if needed
            if (simulationTime.getText().isEmpty() || arrivalTime.getText().isEmpty() ||
                    generalTime.getText().isEmpty() || nurseTime.getText().isEmpty() ||
                    registerTime.getText().isEmpty() || specialistTime.getText().isEmpty() ||
                    laboratoryTime.getText().isEmpty()) {
                showAlert("Input Required", "Please enter all values");
                return;
            }
            //save current setting to SettingsController
            saveCurrentSettings();

            //load simulation scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/simu/nsimulator.fxml"));
            Parent root = loader.load();

            //load result scene
            FXMLLoader resultLoader = new FXMLLoader(getClass().getResource("/simu/result.fxml"));
            Parent resultRoot = resultLoader.load();
            ResultController resultController = resultLoader.getController();
            resultController.setRoot(resultRoot);

            //pass values to SimuViewControl
            SimulatorController simulatorView = loader.getController();
            simulatorView.initializeSimulation(getNumberRegister(), getNumberNurse(), getNumberGeneral(), getNumberSpecialist(), getNumberLaboratory(), this, resultController);

            //change scene
            Stage stage = (Stage) simulateButton.getScene().getWindow(); //get the current stage
            simulatorView.setCloseEventListener(stage);
            stage.setScene(new Scene(root)); //change scene to simulation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumberRegister() {
        return Integer.parseInt(registerChoice.getValue());
    }

    public int getNumberNurse() {
        return Integer.parseInt(nurseChoice.getValue());
    }

    public int getNumberGeneral() {
        return Integer.parseInt(generalChoice.getValue());
    }

    public int getNumberSpecialist() {
        return Integer.parseInt(specialistChoice.getValue());
    }

    public int getNumberLaboratory() {
        return Integer.parseInt(laboratoryChoice.getValue());
    }

    public int getRegisterTime() {
        return Integer.parseInt(this.registerTime.getText());
    }

    public int getNurseTime() {
        return Integer.parseInt(this.nurseTime.getText());
    }

    public int getGeneralTime() {
        return Integer.parseInt(this.generalTime.getText());
    }

    public int getSpecialistTime() {
        return Integer.parseInt(this.specialistTime.getText());
    }

    public int getLaboratoryTime() {
        return Integer.parseInt(this.laboratoryTime.getText());
    }

    public int getArrivalTime() {
        return Integer.parseInt(this.arrivalTime.getText());
    }

    public int getSimulationTime() {
        return Integer.parseInt(simulationTime.getText());
    }

    public int getDelayTime() {
        return delayField.getValue();
    }

    public Map<String, Object> loadConfigs() {
        Map<String, Object> configs = new HashMap<>();
        List<Configuration> configurations = configurationDao.getConfigurations();
        if (!configurations.isEmpty()) {
            for (Configuration config : configurations) {
                configs.put(config.getType(), config.getValue());
            }
        }
        return configs;
    }

    public void saveConfigs(Map<String, Object> configs) {
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            Configuration config = new Configuration(entry.getKey(), (int) entry.getValue());
            boolean isExist = false;
            isExist = configurationDao.existsByType(entry.getKey());
            if (!isExist) {
                configurationDao.persist(config);
            } else {
                configurationDao.update(config);
            }
        }
    }
}
