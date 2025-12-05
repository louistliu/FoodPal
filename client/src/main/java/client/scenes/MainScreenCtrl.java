package client.scenes;

import client.Main;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeList;
import commons.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * The Class that handles the logic of the main screen.
 */
public class MainScreenCtrl {

    private ObservableList<Recipe> observableRecipes;
    private Recipe selectedRecipe;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private ListView<Recipe> recipeListView;
    @FXML
    private ListView<String> ingredientListView;
    @FXML
    private ListView<String> instructionListView;

    @FXML
    private TextField searchRecipesField;
    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private TextField recipeNameField;
    @FXML
    private TextField recipeDescriptionField;

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

    RecipeList listOfRecipes = new RecipeList();

    /**
     * Constructs the MainScreenCtrl, injecting the scene controller.
     *
     * @param m The main application controller for scene transitions.
     */
    @Inject
    public MainScreenCtrl(MainCtrl m) {
    }

    /**
     * Initializes the controller, sets up the initial recipe list,
     * language options, and adds selection listeners.
     */
    public void initialize() {
        languageChoiceBox.setItems(FXCollections.observableArrayList(
                "English", "Dutch", "German"
        ));
        languageChoiceBox.getSelectionModel().selectFirst();

        // Load the actual list from the RecipeList singleton.
        observableRecipes = FXCollections.observableList(listOfRecipes.getRecipeList());
        recipeListView.setItems(observableRecipes);

        // --- Cell Factory and Listeners ---
        recipeListView.setCellFactory(lv -> new ListCell<>() {
            public void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                setText(empty ? null : recipe != null ? recipe.getName() : null);
            }
        });

        // This listener will fire immediately if data is bound, triggering showRecipeDetails
        recipeListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldRecipe, newRecipe) -> showRecipeDetails(newRecipe));

        System.out.println("FoodPal Main Screen UI initialized.");
        rightPane.setVisible(false);
    }

    /**
     * Updates the text areas to show the details of the selected recipe.
     */
    private void showRecipeDetails(Recipe recipe) {
        this.selectedRecipe = recipe;

        if (recipe == null) {
            recipeNameField.clear();
            recipeDescriptionField.clear();

            if (ingredientListView != null) ingredientListView.getItems().clear();
            if (instructionListView != null) instructionListView.getItems().clear();
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
                for (RecipeIngredient ri : recipe.getIngredients()) {
                    ingredientListView.getItems().add("Ingredient Item (Placeholder)");
                }
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

        recipeListView.refresh();

        System.out.println("Saved changes for: " + newName);
    }

    /**
     * Finds the lowest available integer for a given naming pattern.
     */
    private int findNextId(String prefix) {
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

        List<commons.RecipeIngredient> emptyIngredients = new ArrayList<>();
        List<String> emptyInstructions = new ArrayList<>();
        Recipe newRecipe = new Recipe(newName, "", emptyIngredients, emptyInstructions);

        observableRecipes.add(newRecipe);
        recipeListView.getSelectionModel().select(newRecipe);

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
        observableRecipes.remove(selected);
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
            clonedIngredients.addAll(selectedRecipe.getIngredients());
        }

        List<String> clonedInstructions = new ArrayList<>();
        if (selectedRecipe.getInstructions() != null) {
            clonedInstructions.addAll(selectedRecipe.getInstructions());
        }

        Recipe newRecipe = new Recipe(selectedRecipe.getName() + " Clone "
                + findNextId(selectedRecipe.getName() + " Clone "),
                selectedRecipe.getDescription(),
                clonedIngredients,
                clonedInstructions);

        observableRecipes.add(newRecipe);
        recipeListView.getSelectionModel().select(newRecipe);

        System.out.println("Added new recipe: " + newRecipe.getName());
    }

    /**
     * Helper method to launch a modal window.
     */
    private <T> T launchModal(Class<T> controllerClass, String fxmlFileName, String title) {
        try {
            Pair<T, Parent> pair = Main.getFxml().load(controllerClass, "client", "scenes", fxmlFileName);
            Stage modalStage = new Stage();

            // Set owner to block main window interactions
            if (recipeListView.getScene() != null) {
                modalStage.initOwner(recipeListView.getScene().getWindow());
            }

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(pair.getValue()));

            // Pass the stage to the controller so it can close itself
            if (pair.getKey() instanceof AddIngredientScreenCtrl) {
                ((AddIngredientScreenCtrl) pair.getKey()).setStage(modalStage);
            } else if (pair.getKey() instanceof AddInstructionScreenCtrl) {
                ((AddInstructionScreenCtrl) pair.getKey()).setStage(modalStage);
            }

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

        AddIngredientScreenCtrl controller = launchModal(AddIngredientScreenCtrl.class, "AddIngredientScreen.fxml", "Add New Ingredient");

        if (controller != null) {
            String inputResult = controller.getResult();
            if (inputResult != null && !inputResult.isEmpty()) {
                System.out.println("Ingredient input received: " + inputResult);

                ingredientListView.getItems().add(inputResult);
            }
        }
    }

    /**
     * Adds an instruction using the modal.
     */
    public void addInstruction() {
        if (selectedRecipe == null) {
            System.out.println("Error: No recipe selected to add instruction to.");
            return;
        }

        AddInstructionScreenCtrl controller = launchModal(AddInstructionScreenCtrl.class, "AddInstructionScreen.fxml", "Add Instruction");

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
        String selected = ingredientListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
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
        System.out.println("Downloading printable version of recipe.");
    }

    /**
     * Handles switching the recipe list view to show only favorite recipes.
     */
    public void showFavorites() {
        System.out.println("Switching view to show only favorite recipes.");
    }

    /**
     * Handles switching the recipe list view to show all recipes available on the server.
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