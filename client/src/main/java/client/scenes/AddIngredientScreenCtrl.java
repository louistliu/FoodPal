package client.scenes;

import client.utils.Endpoint;
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
import javafx.util.StringConverter;

/**
 * Class, which handles logic for adding ingredients.
 */
public class AddIngredientScreenCtrl extends ScreenControl {

    private final ServerSockets serverIngredients;
    private final Ingredient otherOption = new Ingredient("Other");
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
    private RecipeIngredient result = null;
    private ObservableList<Ingredient> observableIngredients;
    private Ingredient ingredientToSelect;

    /**
     * Constructs the MainScreenCtrl, injecting the scene controller.
     *
     * @param serverIngredients - The server sockets to be received by the controller.
     */
    @Inject
    public AddIngredientScreenCtrl(ServerSockets serverIngredients) {
        this.serverIngredients = serverIngredients;
    }

    /**
     * Initializes the stage for the addIngredient-screen.
     */
    @FXML
    public void initialize() {
        observableIngredients = FXCollections.observableArrayList();
        ingredientChoiceBox.setItems(observableIngredients);

        // Reset UI fields
        amountTextField.clear();
        inputTextArea.clear();
        inputTextArea.setVisible(false);
        unitMenuButton.setText("Unit");

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

        serverIngredients.subscribe(Endpoint.INGREDIENT_FETCH,
              new ResponseHandler<List<Ingredient>>(this::onUpdateIngredientList) {
              });

        serverIngredients.subscribe(Endpoint.INGREDIENT_CREATE,
              new ResponseHandler<Ingredient>(this::onAddIngredient) {
              });

        serverIngredients.subscribe(Endpoint.INGREDIENT_USER_CREATE,
              new ResponseHandler<Ingredient>(this::onIngredientCreatedCallback) {
              });

        serverIngredients.subscribe(Endpoint.INGREDIENT_UPDATE,
              new ResponseHandler<Ingredient>(this::onUpdateIngredient) {
              });

        serverIngredients.subscribe(Endpoint.INGREDIENT_DELETE,
              new ResponseHandler<Ingredient>(this::onDeleteIngredient) {
              });
    }

    // WebSocket callbacks.

    private void onIngredientCreatedCallback(Ingredient ingredient) {
        Platform.runLater(() -> {
            try {
                float amount = Float.parseFloat(amountTextField.getText().trim());
                String unit = unitMenuButton.getText();

                this.result = new RecipeIngredient(ingredient, amount, unit);

                if (stage != null) {
                    stage.close();
                }
            } catch (NumberFormatException e) {
                ErrorScreenCtrl.showError("Error processing created ingredient.");
            }
        });
    }

    private void onUpdateIngredientList(List<Ingredient> ingredients) {
        Platform.runLater(() -> {
            observableIngredients.clear();
            observableIngredients.addAll(ingredients);
            observableIngredients.add(otherOption);

            if (ingredientToSelect != null) {
                for (Ingredient i : observableIngredients) {
                    if (i.getId() == ingredientToSelect.getId()) {
                        ingredientChoiceBox.getSelectionModel().select(i);
                        break;
                    }
                }
                ingredientToSelect = null;
            } else {
                ingredientChoiceBox.getSelectionModel().selectFirst();
            }
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

        if (selected == null) {
            ErrorScreenCtrl.showError("Invalid input: Please select an ingredient.");
            return;
        }

        if (amountStr.isEmpty()) {
            ErrorScreenCtrl.showError("Invalid input: Please fill in an amount.");
            return;
        }

        if ("Unit".equals(unit)) {
            ErrorScreenCtrl.showError("Invalid input: Please select an unit.");
            return;
        }

        float amount;
        try {
            amount = Float.parseFloat(amountStr);
        } catch (NumberFormatException e) {
            ErrorScreenCtrl.showError("Invalid Amount: Must be a number.");
            return;
        }

        if ("Other".equals(selected.getName())) {
            String newName = inputTextArea.getText().trim();
            if (newName.isEmpty()) {
                ErrorScreenCtrl.showError("Please enter a name for the new ingredient.");
                return;
            }
            serverIngredients.send(Endpoint.INGREDIENT_CREATE,
                  new Ingredient(newName));

        } else {
            this.result = new RecipeIngredient(selected, amount, unit);

            if (super.stage != null) {
                super.stage.close();
            }
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
     * @return The formatted string, or null if cancelled.
     */
    public RecipeIngredient getResult() {
        return result;
    }

    /**
     * Override init to handle passed parameters.
     *
     * @param params param[0] is the recipe ingredient from which the information
     *               should be extracted and put into the screen fields.
     */
    @Override
    public void init(Object... params) {
        this.result = null;
        super.init(params);

        if (params.length > 0 && params[0] instanceof RecipeIngredient item) {
            amountTextField.setText(String.valueOf(item.getAmount()));
            unitMenuButton.setText(item.getUnit());
            this.ingredientToSelect = item.getIngredient();
        }
    }
}
