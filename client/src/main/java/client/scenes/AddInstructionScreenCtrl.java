package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AddInstructionScreenCtrl {

    @FXML
    private TextArea inputTextArea;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private Stage stage;
    private String result = null;

    /**
     * Sets the stage for this scene, necessary for closing the window.
     *
     * @param stage The modal stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the OK button click.
     * Saves the text input and closes the window.
     */
    @FXML
    public void handleOk() {
        String input = inputTextArea.getText().trim();

        if (!input.isEmpty()) {
            this.result = input;
            if (stage != null) {
                stage.close();
            }
        } else {
            System.out.println("Instruction cannot be empty");
        }
    }

    /**
     * Handles the Cancel button click.
     * Clears result and closes the window.
     */
    @FXML
    public void handleCancel() {
        this.result = null;
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Retrieves the input provided by the user.
     *
     * @return The text input string, or null if cancelled.
     */
    public String getResult() {
        return result;
    }
}