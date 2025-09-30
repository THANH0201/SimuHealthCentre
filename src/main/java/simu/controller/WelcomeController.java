package simu.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Button startButton;

    @FXML
    private void initialize() {
        startButton.setOnAction(event -> {
            try {
                //load configuration scene
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SimulationConfig.fxml"));
                Parent root = loader.load();

                //Change scene
                Stage stage = (Stage) startButton.getScene().getWindow(); //get the current stage
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
