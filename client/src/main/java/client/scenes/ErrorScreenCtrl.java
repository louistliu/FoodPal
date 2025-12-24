package client.scenes;

import client.Main;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Class, which handles logic for the error-screen.
 */
public class ErrorScreenCtrl {

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button okButton;

    private Stage stage;

    /**
     * Sets the stage for this scene, necessary for closing the window.
     *
     * @param stage The modal stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the text to be displayed in the error window.
     *
     * @param message The custom error message.
     */
    public void setErrorMessage(String message) {
        if (errorMessageLabel != null) {
            errorMessageLabel.setText(message);
        }
    }

    /**
     * Handles the OK button click.
     * Closes the window.
     */
    @FXML
    public void handleOk() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * static helper method to display an error screen from anywhere.
     *
     * @param message The error message to display.
     */
    public static void showError(String message) {
        try {
            Pair<ErrorScreenCtrl, Parent> pair = Main.getFxml().load(ErrorScreenCtrl.class, "client", "scenes", "ErrorScreen.fxml");

            ErrorScreenCtrl ctrl = pair.getKey();
            ctrl.setErrorMessage(message);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Block all other windows
            stage.setTitle("Error");
            stage.setScene(new Scene(pair.getValue()));

            ctrl.setStage(stage);
            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Failed to show error screen: " + e.getMessage());
        }
    }
}