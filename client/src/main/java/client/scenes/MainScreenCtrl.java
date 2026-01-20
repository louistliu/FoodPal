package client.scenes;

import client.Main;
import client.utils.Endpoint;
import client.utils.PrintRecipe;
import client.utils.ResponseHandler;
import client.utils.ServerSockets;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * The Class that handles the logic of the main screen.
 */
public class MainScreenCtrl {

    private final ServerSockets serverRecipes;
    RecipeList listOfRecipes = new RecipeList();
    private ObservableList<Recipe> observableRecipes;
    private ObservableList<RecipeIngredient> observableIngredients;
    private Recipe selectedRecipe;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private ListView<Recipe> recipeListView;
    @FXML
    private ListView<RecipeIngredient> ingredientListView;
    @FXML
    private ListView<String> instructionListView;
    @FXML
    private TextField searchRecipesField;
    @FXML
    private ChoiceBox<String> languageChoiceBox;
    @FXML
    private TextField recipeNameField;
    @FXML
    private TextArea recipeDescriptionField;
    @FXML
    private ToggleButton switchMenuButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addInstructionButton;
    @FXML
    private Button deleteInstructionButton;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button deleteIngredientButton;
    @FXML
    private Button duplicateButton;
    @FXML
    private Button favoritesButton;
    @FXML
    private Button allButton;
    @FXML
    private Button printButton;

    /**
     * Constructs the MainScreenCtrl, injecting the scene controller.
     *
     * @param m The main application controller for scene transitions.
     */
    @Inject
    public MainScreenCtrl(MainCtrl m, ServerSockets server) {
        this.serverRecipes = server;
    }

    /**
     * Initializes the controller, sets up the initial recipe list,
     * language options, and adds selection listeners.
     */
    public void initialize() {
        languageChoiceBox.setItems(FXCollections.observableArrayList("English", "Dutch", "German"));
        languageChoiceBox.getSelectionModel().selectFirst();

        if (recipeDescriptionField != null) {
            recipeDescriptionField.setWrapText(true);
        }

        // --- Cell Factory and Listeners ---
        recipeListView.setCellFactory(lv -> new ListCell<>() {
            public void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                setText(empty ? null : recipe != null ? recipe.getName() : null);
            }
        });

        ingredientListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(RecipeIngredient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setContextMenu(null);
                } else {
                    setText(item.getIngredient().getName() + " " + item.getAmount() + " "
                          + item.getUnit());

                    ContextMenu cm = new ContextMenu();
                    MenuItem editItem = new MenuItem("Edit");
                    editItem.setOnAction(event -> editIngredient(item));
                    cm.getItems().add(editItem);
                    setContextMenu(cm);
                }
            }
        });

        // This listener will fire immediately if data is bound, triggering
        // showRecipeDetails
        recipeListView.getSelectionModel().selectedItemProperty()
              .addListener((obs, oldRecipe, newRecipe) -> showRecipeDetails(newRecipe));

        setupInstructionDragAndDrop();

        System.out.println("FoodPal Main Screen UI initialized.");
        rightPane.setVisible(false);

        observableRecipes = FXCollections.observableList(new ArrayList<>());
        recipeListView.setItems(observableRecipes);
        observableIngredients = FXCollections.observableArrayList();
        ingredientListView.setItems(observableIngredients);

        serverRecipes.subscribe(Endpoint.RECIPE_FETCH,
              new ResponseHandler<List<Recipe>>(this::onUpdateRecipeList) {
              });

        serverRecipes.subscribe(Endpoint.RECIPE_CREATE,
              new ResponseHandler<Recipe>(this::onAddRecipe) {
              });
        serverRecipes.subscribe(Endpoint.RECIPE_USER_CREATE,
              new ResponseHandler<Recipe>(this::onCreateUserRecipe) {
              });
        serverRecipes.subscribe(Endpoint.RECIPE_UPDATE,
              new ResponseHandler<Recipe>(this::onUpdateRecipe) {
              });
        serverRecipes.subscribe(Endpoint.RECIPE_DELETE,
              new ResponseHandler<Recipe>(this::onDeleteRecipe) {
              });

    }

    private void onCreateUserRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            selectRecipe(recipe);
        });
    }

    private void onUpdateRecipeList(List<Recipe> recipes) {

        System.out.print("RECIPES ARRIVED");
        Platform.runLater(() -> {
            // Load the actual list from the RecipeList singleton.
            observableRecipes.addAll(recipes);
            recipeListView.refresh();
        });
    }

    private void onAddRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            if (observableRecipes.contains(recipe)) {
                return;
            }
            observableRecipes.add(recipe);
            recipeListView.refresh();
        });
    }

    private void onDeleteRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            observableRecipes.remove(recipe);
            recipeListView.refresh();
        });
    }

    private void onUpdateRecipe(Recipe recipe) {
        System.out.println(recipe);
        Platform.runLater(() -> {
            var recipes =
                  observableRecipes.stream().filter(x -> x.getId() == recipe.getId()).toList();

            if (recipes.isEmpty()) {
                observableRecipes.add(recipe);
                recipeListView.refresh();
                System.err.println(
                      "Recipe does not exist in recipe list"
                            + " even though it is being updated and not created");
                return;
            }
            int ind = observableRecipes.indexOf(recipes.getLast());


            if (recipe.getId() != selectedRecipe.getId()) {
                observableRecipes.remove(ind);
                observableRecipes.add(ind, recipe);
                recipeListView.refresh();
                return;
            }
            observableRecipes.remove(ind);
            observableRecipes.add(ind, recipe);
            recipeListView.refresh();


            selectRecipe(recipe);
        });
    }

    private void selectRecipe(Recipe recipe) {
        if (!observableRecipes.contains(recipe)) {
            observableRecipes.add(recipe);
            recipeListView.refresh();
        }

        recipeListView.getSelectionModel().select(recipe);
        selectedRecipe = recipe;
    }

    private void setupInstructionDragAndDrop() {
        instructionListView.setCellFactory(
              param -> new InstructionListCell(this::editInstructionHandler));
    }

    private Optional<String> editInstructionHandler(String instruction) {
        AddInstructionScreenCtrl control =
              launchModal(AddInstructionScreenCtrl.class, "AddInstructionScreen.fxml",
                    "Edit Instruction", instruction);

        if (control == null) {
            ErrorScreenCtrl.showError("Failed to edit instruction");
            return Optional.empty();
        }
        return control.getResult() == null ? Optional.empty() :
              Optional.of(control.getResult());
    }

    /**
     * Updates the text areas to show the details of the selected recipe.
     */
    private void showRecipeDetails(Recipe recipe) {
        this.selectedRecipe = recipe;

        if (recipe == null) {
            recipeNameField.clear();
            recipeDescriptionField.clear();

            if (ingredientListView != null) {
                ingredientListView.getItems().clear();
            }
            if (instructionListView != null) {
                instructionListView.getItems().clear();
            }
            rightPane.setVisible(false);
            return;
        }
        rightPane.setVisible(true);

        recipeNameField.setText(recipe.getName());
        recipeDescriptionField.setText(recipe.getDescription());

        // Refresh Instructions View
        if (instructionListView != null) {
            instructionListView.getItems().clear();
            if (recipe.getInstructions() != null) {
                instructionListView.getItems().addAll(recipe.getInstructions());
            }
        }

        // Refresh Ingredients View (NOT IMPLEMENTED YET)
        if (ingredientListView != null) {
            ingredientListView.getItems().clear();
            if (recipe.getIngredients() != null) {
                observableIngredients.addAll(recipe.getIngredients());
            }
        }
    }

    /**
     * Saves the changes made in the text fields to the selected Recipe object.
     * Linked to the Save button in FXML.
     */
    public void saveRecipe() {
        if (selectedRecipe == null) {
            return;
        }

        String newName = recipeNameField.getText();
        String newDescription = recipeDescriptionField.getText();

        selectedRecipe.setName(newName);
        selectedRecipe.setDescription(newDescription);

        if (instructionListView != null) {
            List<String> currentInstructions = new ArrayList<>(instructionListView.getItems());
            selectedRecipe.setInstructions(currentInstructions);
        }

        // if recipe is already created - update recipe otherwise save recipe,
        // update the list of recipes once server sends response
        if (observableRecipes.contains(selectedRecipe)) {
            serverRecipes.send(Endpoint.RECIPE_UPDATE, selectedRecipe);
        } else {
            serverRecipes.send(Endpoint.RECIPE_CREATE, selectedRecipe);
        }

        System.out.println("Saved changes for: " + newName);
    }

    /**
     * Finds the lowest available integer for a given naming pattern.
     */
    public int findNextId(String prefix) {
        Set<Integer> takenNumbers = new HashSet<>();

        for (Recipe r : observableRecipes) {
            String name = r.getName();

            if (name.startsWith(prefix)) {
                try {
                    String numberPart = name.substring(prefix.length()).trim();
                    int number = Integer.parseInt(numberPart);
                    takenNumbers.add(number);
                } catch (NumberFormatException e) {
                    // Ignore names that don't match pattern
                }
            }
        }
        int i = 1;
        while (takenNumbers.contains(i)) {
            i++;
        }
        return i;
    }

    /**
     * Handles the addition of a new recipe placeholder.
     */
    public void addRecipe() {

        String newName = "Recipe " + findNextId("Recipe ");
        showRecipeDetails(new Recipe(newName));

        System.out.println("Added new recipe: " + newName);
    }

    /**
     * Handles the deletion of the currently selected recipe.
     */
    public void deleteRecipe() {
        Recipe selected = recipeListView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No recipe selected!");
            return;
        }
        serverRecipes.send(Endpoint.RECIPE_DELETE, selected);
        System.out.println("Deleted recipe: " + selected.getName());
    }

    /**
     * Handles the duplication (cloning) of the currently selected recipe.
     */
    public void duplicateRecipe() {
        if (selectedRecipe == null) {
            return;
        }
        List<RecipeIngredient> clonedIngredients = new ArrayList<>();
        if (selectedRecipe.getIngredients() != null) {
            selectedRecipe.getIngredients().forEach(r -> clonedIngredients.add(r.copy()));
        }

        List<String> clonedInstructions = new ArrayList<>();
        if (selectedRecipe.getInstructions() != null) {
            clonedInstructions.addAll(selectedRecipe.getInstructions());
        }

        String recipeName = selectedRecipe.getName();
        System.out.println("RECIPE: " + recipeName);

        Recipe newRecipe =
              new Recipe(selectedRecipe.getName() + " Clone " + findNextId(recipeName + " Clone"),
                    selectedRecipe.getDescription(), clonedIngredients, clonedInstructions);

        serverRecipes.send(Endpoint.RECIPE_CREATE, newRecipe);

        System.out.println("Added new recipe: " + newRecipe.getName());
    }

    /**
     * Helper method to launch a modal window.
     *
     * @param defaultParams parameters that should be passed to the screen every time it launches.
     */
    private <T extends ScreenControl> T launchModal(Class<T> controllerClass, String fxmlFileName,
                                                    String title,
                                                    Object... defaultParams) {
        try {
            Stage modalStage = new Stage();

            // Set owner to block main window interactions
            if (recipeListView.getScene() != null) {
                modalStage.initOwner(recipeListView.getScene().getWindow());
            }

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            Pair<T, Parent> pair =
                  Main.getFxml().load(controllerClass, "client", "scenes", fxmlFileName);
            modalStage.setScene(new Scene(pair.getValue()));

            // Pass the stage to the controller so it can close itself
            pair.getKey().setStage(modalStage);
            pair.getKey().init(defaultParams);

            modalStage.showAndWait();
            return pair.getKey();
        } catch (Exception e) {
            System.err.println("Failed to load modal '" + title + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Handles the click of the 'Add Ingredient' button.
     */
    public void addIngredient() {
        if (selectedRecipe == null) {
            System.out.println("Error: No recipe selected to add ingredient to.");
            return;
        }

        AddIngredientScreenCtrl controller =
              launchModal(AddIngredientScreenCtrl.class, "AddIngredientScreen.fxml",
                    "Add New Ingredient");

        if (controller != null && controller.getResult() != null) {
            RecipeIngredient newIngredient = controller.getResult();
            if (containsIngredient(selectedRecipe, newIngredient.getIngredient())) {
                ErrorScreenCtrl.showError(
                      "Ingredient " + newIngredient.getIngredient().getName()
                            + " is already in the recipe.\n "
                            + "Please edit the existing ingredient (right click option)");
                System.out.println("Ingredient already contained");
                return;
            }
            System.out.println("Ingredient added: " + newIngredient.getIngredient().getName());
            selectedRecipe.getIngredients().add(newIngredient);
            observableIngredients.add(newIngredient);
            ingredientListView.refresh();
        }
    }

    /**
     * Handles the edit ingredient function.
     *
     * @param item - The recipe ingredient to be edited.
     */
    private void editIngredient(RecipeIngredient item) {
        if (selectedRecipe == null || item == null) {
            return;
        }

        AddIngredientScreenCtrl controller = launchModal(
              AddIngredientScreenCtrl.class,
              "AddIngredientScreen.fxml",
              "Edit Ingredient",
              item
        );

        if (controller != null && controller.getResult() != null) {
            RecipeIngredient updatedItem = controller.getResult();

            if (!item.getIngredient().getName().equals(updatedItem.getIngredient().getName())
                  && containsIngredient(selectedRecipe, updatedItem.getIngredient())) {
                ErrorScreenCtrl.showError(
                      "Ingredient " + updatedItem.getIngredient().getName()
                            + " is already in the recipe.\n "
                            + "Please edit the existing ingredient (right click option)");
                System.out.println("Ingredient already contained");
                return;
            }

            int index = selectedRecipe.getIngredients().indexOf(item);
            if (index != -1) {
                selectedRecipe.getIngredients().set(index, updatedItem);
                int obsIndex = observableIngredients.indexOf(item);
                if (obsIndex != -1) {
                    observableIngredients.set(obsIndex, updatedItem);
                } else {
                    observableIngredients.setAll(selectedRecipe.getIngredients());
                }
                ingredientListView.refresh();
            }
        }
    }

    private boolean containsIngredient(Recipe recipe, Ingredient ingredient) {
        return recipe.getIngredients().stream()
              .anyMatch(i -> i.getIngredient().getName().equals(ingredient.getName()));
    }

    /**
     * Adds an instruction using the modal.
     */
    public void addInstruction() {
        if (selectedRecipe == null) {
            System.out.println("Error: No recipe selected to add instruction to.");
            return;
        }

        AddInstructionScreenCtrl controller =
              launchModal(AddInstructionScreenCtrl.class, "AddInstructionScreen.fxml",
                    "Add Instruction");

        if (controller != null) {
            String instructionText = controller.getResult();

            if (instructionText != null && !instructionText.isEmpty()) {
                System.out.println("Instruction added: " + instructionText);

                instructionListView.getItems().add(instructionText);
            }
        }
    }

    /**
     * Deletes an ingredient.
     */
    public void deleteIngredient() {
        RecipeIngredient selected = ingredientListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedRecipe.removeIngredient(selected);
            ingredientListView.getItems().remove(selected);
        }
    }

    /**
     * Deletes an instruction.
     */
    public void deleteInstruction() {
        String selected = instructionListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            instructionListView.getItems().remove(selected);
            selectedRecipe.getInstructions().remove(selected);
        }
    }

    /**
     * Handles the request to download a printable version of the recipe.
     */
    public void printRecipe() {

        if (selectedRecipe == null) {
            return;
        }

        PrintRecipe.exportRecipe(printButton.getScene().getWindow(), selectedRecipe);

        System.out.println("Exported recipe: " + selectedRecipe.getName());
    }

    /**
     * Handles switching the recipe list view to show only favorite recipes.
     */
    public void showFavorites() {
        System.out.println("Switching view to show only favorite recipes.");
    }

    /**
     * Handles switching the recipe list view to show all recipes available on the
     * server.
     */
    public void showAllRecipes() {
        System.out.println("Switching view to show all recipes.");
    }

    /**
     * Handles the user changing the selected language in the choice box.
     */
    public void onLanguageChange() {
        System.out.println("Language switched to: " + languageChoiceBox.getValue());
    }
}
