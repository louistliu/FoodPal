package client.scenes;

import commons.Recipe;
import client.scenes.MainCtrl;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import commons.RecipeIngredient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainScreenCtrl {

    private final MainCtrl mainCtrl;
    private Recipe selectedRecipe;

    @FXML private Pane recipeListPane;
    @FXML private TextField searchRecipesField;
    @FXML private ChoiceBox<String> languageChoiceBox;

    // Buttons (linked with fx:id and onAction)
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button duplicateButton;
    @FXML private Button favoritesButton;
    @FXML private Button allButton;
    @FXML private Button printButton;

    @Inject
    public MainScreenCtrl(MainCtrl m) {
        this.mainCtrl = m;
    }

    @FXML
    public void initialize() {
        languageChoiceBox.setItems(FXCollections.observableArrayList(
                "English", "Dutch", "German"
        ));
        languageChoiceBox.getSelectionModel().selectFirst();

        System.out.println("FoodPal Main Screen UI initialized.");
    }

    // Helper method (required for compilation, but not used by buttons)
    private List<Recipe> createDummyRecipes() {
        List<RecipeIngredient> emptyIngredients = Collections.emptyList();
        List<String> emptyInstructions = Collections.emptyList();

        return Arrays.asList(
                new Recipe("Pizza Dough", "Simple base recipe for pizza.", emptyIngredients, emptyInstructions),
                new Recipe("Tomato Sauce", "Quick sauce for pasta or pizza.", emptyIngredients, emptyInstructions)
        );
    }

    public void addRecipe() {
        System.out.println("Handler: Adding new recipe.");
    }

    public void deleteRecipe() {
        System.out.println("Handler: Deleting selected recipe.");
    }

    public void duplicateRecipe() {
        System.out.println("Handler: Cloning selected recipe.");
    }

    public void printRecipe() {
        System.out.println("Handler: Downloading printable version of recipe.");
    }

    public void showFavorites() {
        System.out.println("Handler: Switching view to show only favorite recipes.");
    }

    public void showAllRecipes() {
        System.out.println("Handler: Switching view to show all recipes.");
    }

    public void onLanguageChange() {
        System.out.println("Handler: Language switched to: " + languageChoiceBox.getValue());
    }
}