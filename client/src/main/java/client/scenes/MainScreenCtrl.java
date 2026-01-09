package client.scenes;

import client.Main;
import client.utils.PrintRecipe;
import client.utils.ResponseHandler;
import client.utils.ServerSockets;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
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
    private Recipe selectedRecipe;

    private List<Recipe> allRecipesMaster = new ArrayList<>();
    private boolean showingFavoritesOnly = false;

    private String currentSearchQuery = "";

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
    @FXML
    private Button favoriteButton;

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
        languageChoiceBox.setItems(FXCollections.observableArrayList(
                "English", "Dutch", "German"));
        languageChoiceBox.getSelectionModel().selectFirst();

        // --- Cell Factory and Listeners ---
        recipeListView.setCellFactory(lv -> new ListCell<>() {
            public void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                setText(empty ? null : recipe != null ? recipe.getName() : null);
            }
        });

        searchRecipesField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.currentSearchQuery = newValue.toLowerCase().trim();
            refreshListView();
        });

        searchRecipesField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchRecipesField.clear(); // This triggers our listener to show all recipes
                recipeListView.requestFocus(); // Move focus away from search
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

        serverRecipes.subscribe(ServerSockets.setDestination("/app/recipes/fetch"),
                new ResponseHandler<List<Recipe>>(this::onUpdateRecipeList) {
                });

        serverRecipes.subscribe(ServerSockets.setDestination("/topic/recipes/create"),
                new ResponseHandler<Recipe>(this::onAddRecipe) {
                });
        serverRecipes.subscribe(ServerSockets.setDestination("/topic/recipes/update"),
                new ResponseHandler<Recipe>(this::onUpdateRecipe) {
                });
        serverRecipes.subscribe(ServerSockets.setDestination("/topic/recipes/delete"),
                new ResponseHandler<Recipe>(this::onDeleteRecipe) {
                });

    }

    private void onUpdateRecipeList(List<Recipe> recipes) {
        Platform.runLater(() -> {
            allRecipesMaster.clear();
            allRecipesMaster.addAll(recipes);
            refreshListView();
        });
    }

    /**
     * Refreshes the recipe list view by applying both the favorite filter
     * and the multi-word search filter. [cite: 146, 148]
     */
    private void refreshListView() {
        List<Recipe> toShow = allRecipesMaster.stream()
                .filter(recipe -> {
                    boolean matchesFavorite = !showingFavoritesOnly || recipe.isFavorite();

                    boolean matchesSearch = true;
                    if (!currentSearchQuery.isEmpty()) {
                        String[] keywords = currentSearchQuery.split("\\s+");

                        String searchArea = (recipe.getName() + " " + recipe.getDescription()).toLowerCase();

                        for (String keyword : keywords) {
                            if (!searchArea.contains(keyword)) {
                                matchesSearch = false;
                                break;
                            }
                        }
                    }

                    return matchesFavorite && matchesSearch;
                })
                .toList();

        observableRecipes.setAll(toShow);
        recipeListView.refresh();
    }

    private void onAddRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            if (!allRecipesMaster.contains(recipe)) {
                allRecipesMaster.add(recipe);
            }
            refreshListView();

            if (selectedRecipe == null || !selectedRecipe.equalsNoId(recipe)) {
                return;
            }
            selectRecipe(recipe);
        });
    }

    private void onDeleteRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            allRecipesMaster.removeIf(r -> r.getId() == recipe.getId());
            refreshListView();
        });
    }

    private void onUpdateRecipe(Recipe recipe) {
        Platform.runLater(() -> {
            allRecipesMaster.removeIf(r -> r.getId() == recipe.getId());
            allRecipesMaster.add(recipe);

            if (selectedRecipe != null && selectedRecipe.getId() == recipe.getId()) {
                selectRecipe(recipe);

                favoriteButton.setText(recipe.isFavorite() ? "Unfavorite" : "Favorite");
            }
        });
    }

    private void selectRecipe(Recipe recipe) {
        if (!observableRecipes.contains(recipe)) {
            return;
        }
        recipeListView.getSelectionModel().select(recipe);
    }

    private void setupInstructionDragAndDrop() {
        instructionListView.setCellFactory(param -> new InstructionListCell());
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
                for (RecipeIngredient ri : recipe.getIngredients()) {
                    ingredientListView.getItems().add("Ingredient Item (Placeholder)");
                }
            }
        }
        favoriteButton.setText(recipe.isFavorite() ? "Unfavorite" : "Favorite");
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
        if (selectedRecipe.getId() != 0) {
            serverRecipes.send(ServerSockets.setDestination("/app/recipes/update"), selectedRecipe);
        } else {
            serverRecipes.send(ServerSockets.setDestination("/app/recipes/create"), selectedRecipe);
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

        // List<commons.RecipeIngredient> emptyIngredients = new ArrayList<>();
        // List<String> emptyInstructions = new ArrayList<>();
        // Recipe newRecipe = new Recipe(newName, "", emptyIngredients,
        // emptyInstructions);
        //
        // observableRecipes.add(newRecipe);
        // recipeListView.getSelectionModel().select(newRecipe);
        //
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
        serverRecipes.send(ServerSockets.setDestination("/app/recipes/delete"), selected);
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
        // int index = getMaxId(observableRecipes.stream().map(r ->
        // r.getName()).toList(), recipeName);
        // System.out.println("INDEX: " + index);

        Recipe newRecipe = new Recipe(selectedRecipe.getName() + " Clone "
                + findNextId(recipeName) + " Clone",
                selectedRecipe.getDescription(),
                clonedIngredients,
                clonedInstructions);

        serverRecipes.send(ServerSockets.setDestination("/app/recipes/create"), newRecipe);

        System.out.println("Added new recipe: " + newRecipe.getName());
    }

    /**
     * Helper method to launch a modal window.
     */
    private <T> T launchModal(Class<T> controllerClass, String fxmlFileName, String title) {
        try {
            Pair<T, Parent> pair =
                    Main.getFxml().load(controllerClass, "client", "scenes", fxmlFileName);
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

        AddIngredientScreenCtrl controller =
                launchModal(AddIngredientScreenCtrl.class, "AddIngredientScreen.fxml",
                        "Add New Ingredient");

        if (controller != null) {
            RecipeIngredient newIngredient = controller.getResult();
            if (newIngredient != null) {
                System.out.println("Ingredient added: " + newIngredient.getIngredient().getName());

                selectedRecipe.getIngredients().add(newIngredient);
                showRecipeDetails(selectedRecipe);
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
     * Deletes an ingredient after user confirmation.
     */
    public void deleteIngredient() {
        String selected = ingredientListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Do you really want to delete this ingredient?")) {
                ingredientListView.getItems().remove(selected);
                // In a full implementation, you'd also remove from selectedRecipe.getIngredients()
            }
        }
    }

    /**
     * Deletes an instruction after user confirmation.
     */
    public void deleteInstruction() {
        String selected = instructionListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Delete this instruction step?")) {
                instructionListView.getItems().remove(selected);
                selectedRecipe.getInstructions().remove(selected);
            }
        }
    }

    /**
     * Launches a confirmation modal to warn the user before deletion.
     * @param message The warning message to display.
     * @return true if the user confirmed, false otherwise.
     */
    private boolean showConfirmation(String message) {
        try {
            var pair = Main.getFxml().load(ConfirmationScreenCtrl.class, "client", "scenes", "ConfirmationScreen.fxml");
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(recipeListView.getScene().getWindow());
            stage.setScene(new Scene(pair.getValue()));

            ConfirmationScreenCtrl ctrl = pair.getKey();
            ctrl.setStage(stage);
            ctrl.setMessage(message);

            stage.showAndWait();
            return ctrl.isConfirmed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Handles the request to download a printable version of the recipe.
     */
    public void printRecipe() {

        if (selectedRecipe == null) {
            return;
        }

        PrintRecipe.exportRecipe(
                printButton.getScene().getWindow(),
                selectedRecipe);

        System.out.println("Exported recipe: " + selectedRecipe.getName());
    }

    /**
     * Handles switching the recipe list view to show only favorite recipes.
     */
    public void showFavorites() {
        showingFavoritesOnly = true;
        refreshListView();
        System.out.println("Showing only favorites.");
    }

    /**
     * Handles switching the recipe list view to show all recipes available on the
     * server.
     */
    public void showAllRecipes() {
        showingFavoritesOnly = false;
        refreshListView();
        System.out.println("Showing all recipes.");
    }

    /**
     * Toggles the favorite status of the selected recipe and updates the UI. [cite: 134, 135]
     */
    public void toggleFavorite() {
        if (selectedRecipe == null) return;

        selectedRecipe.setFavorite(!selectedRecipe.isFavorite());

        favoriteButton.setText(selectedRecipe.isFavorite() ? "Unfavorite" : "Favorite");

        serverRecipes.send(ServerSockets.setDestination("/app/recipes/update"), selectedRecipe);

        if (showingFavoritesOnly) {
            refreshListView();
        }
        if (selectedRecipe.isFavorite()) {
            System.out.println("Added " + selectedRecipe.getName() + " to favorites.");
        }
        else {
            System.out.println("Removed " + selectedRecipe.getName() + " from favorites.");
        }
    }

    /**
     * Handles the user changing the selected language in the choice box.
     */
    public void onLanguageChange() {
        System.out.println("Language switched to: " + languageChoiceBox.getValue());
    }
}