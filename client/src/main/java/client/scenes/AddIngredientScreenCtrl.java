package client.scenes;

import client.utils.ResponseHandler;
import client.utils.ServerSockets;
import commons.Ingredient;
import commons.RecipeIngredient;
import jakarta.inject.Inject;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Class, which handles logic for adding ingredients.
 */
public class AddIngredientScreenCtrl {

    @FXML
    private TextArea inputTextArea; // Visible only when "Other" is selected

    @FXML
    private ChoiceBox<Ingredient> ingredientChoiceBox; // Dropdown for ingredients

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

    private ServerSockets serverIngredients;
    private ObservableList<Ingredient> observableIngredients;
    private final Ingredient otherOption = new Ingredient("Other");

    /**
     * Initializes the stage for the addIngredient-screen.
     */
    @FXML
    public void initialize() {
        observableIngredients = FXCollections.observableArrayList();
        ingredientChoiceBox.setItems(observableIngredients);

        ingredientChoiceBox.setConverter(new StringConverter<Ingredient>() {
            @Override
            public String toString(Ingredient i) {
                return (i == null) ? "" : i.getName();
            }

            @Override
            public Ingredient fromString(String string) {
                return null;
            }
        });
        // Add listener to show/hide the inputTextArea based on selection
        ingredientChoiceBox.getSelectionModel().selectedItemProperty()
              .addListener((obs, oldVal, newVal) -> {
                  if ("Other".equals(newVal.getName())) {
                      inputTextArea.setVisible(true);
                      inputTextArea.clear();
                      inputTextArea.requestFocus();
                  } else {
                      inputTextArea.setVisible(false);
                  }
              });
    }

    public void setServer(ServerSockets server) {
        serverIngredients = server;
    }

    /**
     * Adding several listeners to server events.
     */
    public void refresh() {
        // Reset UI fields
        amountTextField.clear();
        inputTextArea.clear();
        inputTextArea.setVisible(false);
        unitMenuButton.setText("Unit");

        if (serverIngredients != null) {
            serverIngredients.subscribe(ServerSockets.setDestination("/app/ingredients/fetch"),
                  new ResponseHandler<List<Ingredient>>(this::onUpdateIngredientList) {});

            serverIngredients.subscribe(ServerSockets.setDestination("/topic/ingredients/create"),
                  new ResponseHandler<Ingredient>(this::onAddIngredient) {});

            serverIngredients.subscribe(ServerSockets.setDestination("/topic/ingredients/update"),
                  new ResponseHandler<Ingredient>(this::onUpdateIngredient) {});

            serverIngredients.subscribe(ServerSockets.setDestination("/topic/ingredients/delete"),
                  new ResponseHandler<Ingredient>(this::onDeleteIngredient) {});
        }
    }

    // WebSocket callbacks.

    private void onUpdateIngredientList(List<Ingredient> ingredients) {
        Platform.runLater(() -> {
            observableIngredients.clear();
            observableIngredients.addAll(ingredients);
            observableIngredients.add(otherOption);
            ingredientChoiceBox.getSelectionModel().selectFirst();
        });
    }

    private void onAddIngredient(Ingredient ingredient) {
        Platform.runLater(() -> {
            observableIngredients.remove(otherOption);
            observableIngredients.add(ingredient);
            observableIngredients.add(otherOption);

            if (inputTextArea.isVisible() && inputTextArea.getText().equals(ingredient.getName())) {
                ingredientChoiceBox.getSelectionModel().select(ingredient);
                inputTextArea.clear();
                inputTextArea.setVisible(false);
            }
        });
    }

    private void onUpdateIngredient(Ingredient ingredient) {
        Platform.runLater(() -> {
            for (int i = 0; i < observableIngredients.size(); i++) {
                if (observableIngredients.get(i).getId() == ingredient.getId()) {
                    observableIngredients.set(i, ingredient);
                    break;
                }
            }
        });
    }

    private void onDeleteIngredient(Ingredient ingredient) {
        Platform.runLater(() -> {
            observableIngredients.removeIf(i -> i.getId() == ingredient.getId());
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

        Ingredient selected = ingredientChoiceBox.getValue();
        Ingredient finalIngredient;

        if (selected == null) {
            ErrorScreenCtrl.showError("Please select an ingredient.");
            return;
        }

        if ("Other".equals(selected.getName())) {
            String newName = inputTextArea.getText().trim();
            if (newName.isEmpty()) {
                ErrorScreenCtrl.showError("Please enter a name for the new ingredient.");
                return;
            }
            finalIngredient = new Ingredient(newName);
            if (serverIngredients != null) {
                serverIngredients.send(ServerSockets.setDestination("/app/ingredients/create"),
                      finalIngredient);
            }

        } else {
            finalIngredient = selected;
        }

        if (amountStr.isEmpty() || "Unit".equals(unit)) {
            ErrorScreenCtrl.showError("Invalid input: Please fill "
                  + "in Amount, Unit, and Ingredient.");
            return;
        }

        try {
            float amount = Float.parseFloat(amountStr);
            this.result = new RecipeIngredient(finalIngredient, amount, unit);

            if (stage != null) {
                stage.close();
            }

        } catch (NumberFormatException e) {
            ErrorScreenCtrl.showError("Invalid Amount: Must be a number.");
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
