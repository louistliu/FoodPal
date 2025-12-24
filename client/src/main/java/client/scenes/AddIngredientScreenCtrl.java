package client.scenes;

import commons.Ingredient;
import commons.RecipeIngredient;
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

/**
 * Class, which handles logic for adding ingredients.
 */
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
    private RecipeIngredient result = null;

    /**
     * Initializes the stage for the addIngredient-screen.
     */
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
        String amountStr = amountTextField.getText().trim();
        String unit = unitMenuButton.getText();
        String ingredientName;

        String selected = ingredientChoiceBox.getValue();
        if ("Other".equals(selected)) {
            ingredientName = inputTextArea.getText().trim();
        } else {
            ingredientName = selected;
        }

        if (ingredientName.isEmpty() || amountStr.isEmpty() || "Unit".equals(unit)) {
            ErrorScreenCtrl.showError("Invalid input: Please fill "
                  + "in Amount, Unit, and Ingredient.");
            return;
        }

        try {
            float amount = Float.parseFloat(amountStr);
            Ingredient ingredient = new Ingredient(ingredientName);
            this.result = new RecipeIngredient(ingredient, amount, unit);

            if (stage != null) {
                stage.close();
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid Amount: Must be a whole number.");
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
    public RecipeIngredient getResult() {
        return result;
    }
}
