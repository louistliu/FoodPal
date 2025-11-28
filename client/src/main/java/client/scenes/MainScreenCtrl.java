package client.scenes;

import commons.Recipe;
import client.scenes.MainCtrl;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import commons.RecipeIngredient;

import java.util.*;

public class MainScreenCtrl {

    private final MainCtrl mainCtrl;
    private Recipe selectedRecipe;

    private int recipeCounter = 0;

    @FXML private ListView<Recipe> recipeListView;
    @FXML private TextField searchRecipesField;
    @FXML private ChoiceBox<String> languageChoiceBox;

    @FXML private TextField recipeNameField;
    @FXML private TextArea ingredientsTextArea;
    @FXML private TextArea preparationTextArea;

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button duplicateButton;
    @FXML private Button favoritesButton;
    @FXML private Button allButton;
    @FXML private Button printButton;

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

        List<Recipe> initialRecipes = createDummyRecipes();
        recipeListView.setItems(FXCollections.observableList(initialRecipes));

        this.recipeCounter = initialRecipes.size();

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
     * Updates the detail fields on the right pane with the contents of the selected recipe.
     * @param recipe The recipe object whose details should be displayed.
     */
    private void showRecipeDetails(Recipe recipe) {
        this.selectedRecipe = recipe;
        if (recipe != null) {
            recipeNameField.setText(recipe.getName());
            if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                ingredientsTextArea.setText("Ingredients loaded (" + recipe.getIngredients().size() + " items)");
            } else {
                ingredientsTextArea.setText("[No Ingredients Defined]");
            }

            if (recipe.getInstructions() != null) {
                preparationTextArea.setText(String.join("\n", recipe.getInstructions()));
            } else {
                preparationTextArea.setText("");
            }

        } else {
            recipeNameField.clear();
            ingredientsTextArea.clear();
            preparationTextArea.clear();
        }
    }

    /**
     * Creates a list of dummy recipe objects for initial display and testing.
     * @return A mutable list containing initial Recipe objects.
     */
    private List<Recipe> createDummyRecipes() {
        List<RecipeIngredient> emptyIngredients = Collections.emptyList();
        List<String> emptyInstructions = Collections.emptyList();

        List<Recipe> fixedList = Arrays.asList(
                new Recipe("Recipe 1", "Recipe 1 description", emptyIngredients, emptyInstructions),
                new Recipe("Recipe 2", "Recipe 2 description", emptyIngredients, emptyInstructions)
        );

        return new ArrayList<>(fixedList);
    }

    /**
     * Handles the addition of a new recipe placeholder, increments the counter, and selects the new item.
     */
    public void addRecipe() {
        this.recipeCounter++;

        String newName = "Recipe " + this.recipeCounter;

        List<commons.RecipeIngredient> emptyIngredients = Collections.emptyList();
        List<String> emptyInstructions = Collections.emptyList();
        Recipe newRecipe = new Recipe(newName, "New recipe created by user.", emptyIngredients, emptyInstructions);
        recipeListView.getItems().add(newRecipe);
        recipeListView.getSelectionModel().select(newRecipe);

        System.out.println("Added new recipe: " + newName);
    }

    /**
     * Handles the deletion of the currently selected recipe.
     */
    public void deleteRecipe() {
        System.out.println("Deleting selected recipe.");
    }

    /**
     * Handles the duplication (cloning) of the currently selected recipe.
     */
    public void duplicateRecipe() {
        System.out.println("Cloning selected recipe.");
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