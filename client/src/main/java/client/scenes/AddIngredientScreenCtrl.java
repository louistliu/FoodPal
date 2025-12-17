package client.scenes;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddIngredientScreenCtrl {

    @FXML
    private TextArea inputTextArea; // Visible only when "Other" is selected

    @FXML
    private ChoiceBox<String> ingredientChoiceBox; // Dropdown for ingredients

    @FXML
    private TextField amountTextField;

    @FXML
    private MenuButton unitMenuButton;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private Stage stage;
    private String result = null;

    @FXML
    public void initialize() {
        ingredientChoiceBox.setItems(
              FXCollections.observableArrayList("Ingredient 1", "Ingredient 2", "Ingredient 3",
                    "Other"));

        ingredientChoiceBox.getSelectionModel().selectFirst();

        // Add listener to show/hide the inputTextArea based on selection
        ingredientChoiceBox.getSelectionModel().selectedItemProperty()
              .addListener((obs, oldVal, newVal) -> {
                  if ("Other".equals(newVal)) {
                      inputTextArea.setVisible(true);
                      inputTextArea.clear();
                      inputTextArea.requestFocus();
                  } else {
                      inputTextArea.setVisible(false);
                  }
              });
    }

    /**
     * Sets the stage for this scene, necessary for closing the window.
     *
     * @param stage The modal stage.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles unit selection from the MenuButton items.
     * Updates the MenuButton text to show the selected unit.
     */
    @FXML
    public void handleUnitSelect(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        unitMenuButton.setText(source.getText());
    }

    /**
     * Handles the OK button click.
     * Validates input, formats the result string, and closes the window.
     */
    @FXML
    public void handleOk() {
        String amount = amountTextField.getText().trim();
        String unit = unitMenuButton.getText();
        String ingredientName;

        String selected = ingredientChoiceBox.getValue();
        if ("Other".equals(selected)) {
            ingredientName = inputTextArea.getText().trim();
        } else {
            ingredientName = selected;
        }

        if (ingredientName.isEmpty() || amount.isEmpty() || "Unit".equals(unit)) {
            System.out.println("Invalid input: Please fill in Amount, Unit, and Ingredient.");
            return;
        }

        // TODO: this should not be string, it's an object
        this.result = amount + " " + unit + " " + ingredientName;

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
     * @return The formatted string, or null if cancelled.
     */
    public String getResult() {
        return result;
    }
}
