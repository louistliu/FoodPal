package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Class, which handles logic for adding instructions.
 */
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
     * Configures keyboard shortcuts for the instruction text area.
     * Maps ENTER to confirmation and ESCAPE to cancellation. [cite: 149]
     */
    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> inputTextArea.requestFocus());

        inputTextArea.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    handleOk();
                    event.consume();
                    break;
                case ESCAPE:
                    handleCancel();
                    event.consume();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Sets the stage for this scene, necessary for closing the window.
     *
     * @param stage The modal stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;

        inputTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleOk();
                event.consume();
            } else if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                handleCancel();
                event.consume();
            }
        });
    }

    /**
     * Handles the OK button click.
     * Saves the text input and closes the window.
     */
    @FXML
    public void handleOk() {
        String input = inputTextArea.getText().trim();

        if (input.isEmpty()) {
            ErrorScreenCtrl.showError("Instructions cannot be empty.");
            return;
        }

        this.result = input;
        if (stage != null) {
            stage.close();
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