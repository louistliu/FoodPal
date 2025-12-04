package client.scenes;

import commons.Recipe;
import client.scenes.MainCtrl;
import com.google.inject.Inject;
import commons.RecipeList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import commons.RecipeIngredient;

import java.util.*;

public class MainScreenCtrl {

    private final MainCtrl mainCtrl;
    private ObservableList<Recipe> observableRecipes;
    private Recipe selectedRecipe;

    @FXML private ListView<Recipe> recipeListView;
    @FXML private ListView<String> IngredientListView;
    @FXML private ListView<String> InstructionListView;

    @FXML private TextField searchRecipesField;
    @FXML private ChoiceBox<String> languageChoiceBox;

    @FXML private TextField recipeNameField;
    @FXML private TextField recipeDescriptionField;

    @FXML private Button saveButton;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button addInstructionButton;
    @FXML private Button deleteInstructionButton;
    @FXML private Button addIngredientButton;
    @FXML private Button deleteIngredientButton;
    @FXML private Button duplicateButton;
    @FXML private Button favoritesButton;
    @FXML private Button allButton;
    @FXML private Button printButton;

    RecipeList ListOfRecipes = new RecipeList();

    /**
     * Constructs the MainScreenCtrl, injecting the scene controller.
     * @param m The main application controller for scene transitions.
     */
    @Inject
    public MainScreenCtrl(MainCtrl m) {
        this.mainCtrl = m;
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

        observableRecipes = FXCollections.observableList(ListOfRecipes.getRecipeList());

        recipeListView.setItems(observableRecipes);

        recipeListView.setCellFactory(lv -> new ListCell<>() {
            public void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                setText(empty ? null : recipe != null ? recipe.getName() : null);
            }
        });

        recipeListView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldRecipe, newRecipe) -> {
                    showRecipeDetails(newRecipe);
                });

        System.out.println("FoodPal Main Screen UI initialized.");
    }

    /**
     * Updates the text areas to show the details of the selected recipe.
     */
    private void showRecipeDetails(Recipe recipe) {
        this.selectedRecipe = recipe;

        if (recipe == null) {
            recipeNameField.clear();
            recipeDescriptionField.clear();
            IngredientListView.getItems().clear();
            InstructionListView.getItems().clear();
            return;
        }

        // 1. Set Name and Description
        recipeNameField.setText(recipe.getName());
        recipeDescriptionField.setText(recipe.getDescription());
    }
    /**
     * Saves the changes made in the text fields to the selected Recipe object.
     * Linked to the Save button in FXML.
     */
    public void saveRecipe() {
        if (selectedRecipe == null) {
            return;
        }

        // 1. Get text from fields
        String newName = recipeNameField.getText();
        String newDescription = recipeDescriptionField.getText();

        // 2. Update the object
        selectedRecipe.setName(newName);
        selectedRecipe.setDescription(newDescription);

        // 3. Refresh the list view so the name updates in the sidebar immediately
        recipeListView.refresh();

        System.out.println("Saved changes for: " + newName);
    }
    /**
     * Finds the lowest available integer for a given naming pattern.
     * Example: If inputs are "Recipe 1", "Recipe 3", and prefix is "Recipe ", returns 2.
     *
     * @param prefix The start of the string to look for (e.g., "Recipe " or "Chocolate Cake clone ")
     * @return The first available integer.
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
                } catch (NumberFormatException e) {}
            }
        }
        int i = 1;
        while (takenNumbers.contains(i)) {
            i++;
        }
        return i;
    }

    /**
     * Handles the addition of a new recipe placeholder, increments the counter, and selects the new item.
     */
    public void addRecipe() {

        String newName = "Recipe " + findNextId("Recipe ");

        List<commons.RecipeIngredient> emptyIngredients = Collections.emptyList();
        List<String> emptyInstructions = Collections.emptyList();
        Recipe newRecipe = new Recipe(newName, "", emptyIngredients, emptyInstructions);

        observableRecipes.add(newRecipe);

        System.out.println("Added new recipe: " + newName);
        System.out.println("Logic List Size: " + ListOfRecipes.getRecipeList().size());
    }

    /**
     * Handles the deletion of the currently selected recipe.
     */
    public void deleteRecipe() {
        if (selectedRecipe == null) {
            System.out.println("No recipe selected!");
            return;
        }
        observableRecipes.remove(selectedRecipe);
        System.out.println("Deleted recipe: " + selectedRecipe.getName());
    }

    /**
     * Handles the duplication (cloning) of the currently selected recipe.
     */
    public void duplicateRecipe() {
        if (selectedRecipe == null) {
            return;
        }
        Recipe newRecipe = new Recipe(selectedRecipe.getName() + " Clone " + findNextId(selectedRecipe.getName() + " Clone "), selectedRecipe.getDescription(), selectedRecipe.getIngredients(), selectedRecipe.getInstructions());

        observableRecipes.add(newRecipe);

        System.out.println("Added new recipe: " + newRecipe.getName());
        System.out.println("Logic List Size: " + ListOfRecipes.getRecipeList().size());

        System.out.println("Cloning " + selectedRecipe.getName());
    }

    public void addIngredient() {
        System.out.println("Add Ingredient clicked (Not implemented)");
    }

    public void deleteIngredient() {
        System.out.println("Delete Ingredient clicked (Not implemented)");
    }

    public void addInstruction() {
        System.out.println("Add Instruction clicked (Not implemented)");
    }

    public void deleteInstruction() {
        System.out.println("Delete Instruction clicked (Not implemented)");
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