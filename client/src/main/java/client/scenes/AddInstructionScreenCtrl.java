package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Class, which handles logic for adding instructions.
 */
public class AddInstructionScreenCtrl extends ScreenControl {

    @FXML
    private TextArea inputTextArea;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private String result = null;

    private LanguageController languageController;

    /**
     * constructor.
     *
     * @param languageController languageController for translating
     */
    @Inject
    public AddInstructionScreenCtrl(LanguageController languageController) {
        this.languageController = languageController;
    }

    /**
     * Sets the stage for this scene, necessary for closing the window.
     *
     * @param stage The modal stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * If contains no params then is a normal add screen.
     * If contains one param, and it is a string then
     * sets the text field to have the string for editing the instruction.
     *
     * @param params Parameters passed in during init
     */
    @Override
    public void init(Object... params) {
        if (params.length == 0) {
            return;
        }

        if (params.length == 1 && params[0] instanceof String str) {
            inputTextArea.setText(str);
            return;
        }
        ErrorScreenCtrl.showError("Failed to load instruction for editing");
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
            if (super.stage != null) {
                super.stage.close();
            }
        } else {
            ErrorScreenCtrl.showError(languageController.get("error.emptyInstruction"));
        }
    }

    /**
     * Handles the Cancel button click.
     * Clears result and closes the window.
     */
    @FXML
    public void handleCancel() {
        this.result = null;
        if (super.stage != null) {
            super.stage.close();
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